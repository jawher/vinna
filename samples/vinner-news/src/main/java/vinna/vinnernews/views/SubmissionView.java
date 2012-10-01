package vinna.vinnernews.views;

import vinna.template.LiquidrodsView;
import vinna.vinnernews.model.Submission;

public class SubmissionView extends LiquidrodsView {
    public final Submission submission;

    public SubmissionView(Submission submission) {

        this.submission = submission;
    }


}
