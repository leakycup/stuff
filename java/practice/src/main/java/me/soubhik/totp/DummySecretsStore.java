package me.soubhik.totp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by soubhik on 04-10-2016.
 */
public class DummySecretsStore implements SecureStorage {
    private final Map<String, String> userToSecret;

    public DummySecretsStore() {
        this.userToSecret = new HashMap<>();
        this.userToSecret.put("john.doe", "MzI4dTkzMnVmbm9ma2QgMTItPWAzMCAtMDFyIHBva3cgbHNkbXZsZHNtdmxzZG12a2xkZm52amtu");
        this.userToSecret.put("babu.ram", "MjF1ZXVlZm5md3Bka212LHNwbHAxZDJvLTAxbzMyYF0zPTItXWBcMlw5MDI4NDczMjg5MjEwJF4mKihCTk08Pk5ERkdISktMOjxNKSo4OTAtKygpamRtd2UxMjBlMQ==");
        this.userToSecret.put("bad.guy", "not a valid base64 *&^");
    }

    @Override
    public String getSecret(String user) {
        return userToSecret.get(user);
    }
}
