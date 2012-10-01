package vinna.vinnernews.views;

import vinna.Validation;
import vinna.template.LiquidrodsView;

public class LoginView extends LiquidrodsView {
    public String login;

    public LoginView() {
    }

    public LoginView(String login, Validation validation) {
        this.login = login;
        super.validation = validation;
    }

    public boolean inLogin() {
        return true;
    }
}
