package client;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class User {

    private String name;
    private String email;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private KeyStore keyStore;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.privateKey = null;
        this.publicKey = null;
        this.keyStore = new KeyStore();

        KeyPair keyPair;
        try {
            keyPair = KeyGenerator.generateKeyPair();
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public KeyStore getKeyStore() {
        return this.keyStore;
    }
}