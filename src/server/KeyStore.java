package server;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class KeyStore {
    private Map<String, PublicKey> publicKeys;

    public KeyStore() {
        this.publicKeys = new HashMap<>();
    }

    public void addPublicKey(String userName, PublicKey publicKey) {
        publicKeys.put(userName, publicKey);
    }

    public PublicKey getPublicKeyForUser(String username) {
        return publicKeys.get(username);
    }

    public void removePublicKeyOfUser(String username) {
        publicKeys.remove(username);
    }

    public Map<String, PublicKey> getMap() {
        return publicKeys;
    }
}
