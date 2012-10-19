package vinna.vinnernews.controllers;

import vinna.Validation;
import vinna.VinnaContext;
import vinna.response.Redirect;
import vinna.response.Response;
import vinna.response.StringResponse;
import vinna.template.LiquidrodsResponse;
import vinna.template.LiquidrodsView;
import vinna.vinnernews.VinnerSession;
import vinna.vinnernews.model.DummyRepository;
import vinna.vinnernews.model.Submission;
import vinna.vinnernews.util.Seo;
import vinna.vinnernews.views.*;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;

public class VinnerControler {
    private static final int page = 10;
    private static final DummyRepository dummyRepository = new DummyRepository();

    public Response index() {
        return new ListView(dummyRepository.range(0, page), 0, 1);
    }

    public Response index(int p) {
        return new ListView(dummyRepository.range(p * page, page), p * page, p + 1);
    }

    public Response submission(Long id, String title) {
        Submission submission = dummyRepository.get(id);
        if (submission == null) {
            return new NotFoundView();
        } else {
            if (!submission.getSeoTitle().equals(title)) {
                return Redirect.moved(Seo.submissionLocation(submission));
            } else {
                return new SubmissionView(submission);
            }
        }
    }

    public Response submitForm() {
        return new SubmitView();
    }

    public Response submit(String title, String link) {
        Validation validation = new Validation();
        validation.required(title, "title").longerThan(title, 20, "title")
                .required(link, "link");
        validateUrl("link", link, validation);
        if (validation.hasErrors()) {
            return new SubmitView(title, link, validation);
        } else {
            Submission submission = new Submission(title, link, null, "djo");
            dummyRepository.post(submission);
            return Redirect.found(Seo.submissionLocation(submission));
        }
    }

    public Response loginForm() {
        if (getSession().exists()) {
            return Redirect.found("/");//FIXME: maybe add a message explaining why
        } else {
            return new LoginView();
        }
    }

    public Response login(String login, String password, String rememberMe) {
        Validation validation = new Validation();
        validation.required(login, "login")
                .required(password, "password");
        validateAuth(login, password, validation);

        if (validation.hasErrors()) {
            return new LoginView(login, validation);
        } else {
            //validate login/password
            getSession().create();
            getSession().login = login;
            return Redirect.found("/");
        }
    }

    public Response logout() {
        VinnerSession session = getSession();
        if (session.exists()) {
            session.delete();
        }
        return Redirect.found("/");
    }


    private VinnerSession getSession() {
        return (VinnerSession) VinnaContext.get().session;
    }

    private static void validateAuth(String value, String expected, Validation validation) {
        if (!expected.equals(value)) {
            validation.addError("general", "Invalid credentials");
        }

    }


    private static void validateUrl(String name, String value, Validation validation) {
        if (validation.hasErrors(name)) {
            return;
        }
        try {
            URI uri = new URI(value);
            if (!uri.isAbsolute()) {
                validation.addError(name, "Invalid link");
            }
            } catch (URISyntaxException e) {
            validation.addError(name, "Invalid link");


        }
    }
}