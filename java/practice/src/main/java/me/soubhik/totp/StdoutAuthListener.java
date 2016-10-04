package me.soubhik.totp;

/**
 * Created by soubhik on 04-10-2016.
 */
public class StdoutAuthListener implements AuthListener {
    @Override
    public void notify(AuthStatus authStatus) {
        System.out.println("Auth successful: " + authStatus.user + ", " + authStatus.timestamp);
    }
}
