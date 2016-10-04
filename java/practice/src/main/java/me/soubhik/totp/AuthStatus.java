package me.soubhik.totp;

/**
 * Created by soubhik on 04-10-2016.
 */
public class AuthStatus {
    final String user;
    final long timestamp;

    public AuthStatus(String user, long timestamp) {
        this.user = user;
        this.timestamp = timestamp;
    }
}
