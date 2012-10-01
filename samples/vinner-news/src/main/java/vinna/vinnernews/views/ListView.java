package vinna.vinnernews.views;

import vinna.template.LiquidrodsView;
import vinna.vinnernews.model.Submission;
import vinna.vinnernews.util.Seo;

import java.util.Date;
import java.util.List;

public class ListView extends LiquidrodsView {
    private final List<Submission> submissions;
    private int index;
    private final int nextPage;

    public ListView(List<Submission> submissions, int offset, int nextPage) {
        this.submissions = submissions;
        this.index = offset + 1;
        this.nextPage = nextPage;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public int index() {
        return index++;
    }

    public int nextPage() {
        return nextPage;
    }

    public int prevPage() {
        return nextPage - 2;
    }

    public boolean showPrev() {
        return nextPage > 1;
    }

    public String submissionLocation(Submission s) {
        return Seo.submissionLocation(s);
    }

    public String niceDate(Submission s) {
        return "fuck off";
    }

    public String niceDate(Date date) {
        return "xxxxxXxxxxx";
    }
}
