package client;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class KeyStore {
    private Map<String, PublicKey> publicKeys;

    public KeyStore() {
        this.publicKeys = new HashMap<>();
    }

    public synchronized void addPublicKey(String userName, PublicKey publicKey) {
        publicKeys.put(userName, publicKey);
    }

    public synchronized PublicKey getPublicKeyForUser(String username) {
        return publicKeys.get(username);
    }

    public synchronized void removePublicKeyOfUser(String username) {
        publicKeys.remove(username);
    }

    public synchronized Map<String, PublicKey> getMap() {
        return publicKeys;
    }
}
