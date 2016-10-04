package me.soubhik.totp;

/**
 * Created by soubhik on 04-10-2016.
 */
public interface AuthListener {
    public void notify(AuthStatus authStatus);
}
