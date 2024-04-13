package client;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyGenerator {
    public static final int keysize = 2048;
    public static final String algorithm = "RSA";

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(algorithm);

        keyPairGen.initialize(keysize);

        KeyPair pair = keyPairGen.generateKeyPair();

        return pair;
    }

    public static PublicKey getPublicKeyFrom(String keyString){
        try{
            byte[] byteKey = Base64.getDecoder().decode(keyString);
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance(algorithm);
    
            return kf.generatePublic(X509publicKey);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static PrivateKey getPrivateKeyFrom(String keyString) {
        try{
            byte[] byteKey = Base64.getDecoder().decode(keyString);
            X509EncodedKeySpec X509privateKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance(algorithm);
    
            return kf.generatePrivate(X509privateKey);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
