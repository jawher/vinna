package vinna.vinnernews.views;

import vinna.Model;
import vinna.Validation;
import vinna.template.LiquidrodsView;
import vinna.vinnernews.model.Submission;

import java.util.List;

public class SubmitView extends LiquidrodsView {
    public String title;
    public String link;

    public SubmitView() {
    }

    public SubmitView(String title, String link, Validation validation) {
        this.title = title;
        this.link = link;
        super.validation = validation;
    }

    public boolean inSubmit() {
        return true;
    }
}
