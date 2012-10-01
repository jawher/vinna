package vinna.vinnernews;

import vinna.Session;
import vinna.Vinna;

public class VinnerApp extends Vinna {
    @Override
    protected Session newSession() {
        return new VinnerSession();
    }
}
