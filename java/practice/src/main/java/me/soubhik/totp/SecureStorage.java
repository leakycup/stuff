package me.soubhik.totp;

/**
 * Created by soubhik on 04-10-2016.
 */
public interface SecureStorage {
    public String getSecret(String user);
}
