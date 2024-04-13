package client;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CryptoService {
    public static final String transformation = "RSA"; //RSA/ECB/PKCS1Padding
    public static final int bytesInString = 245;
    public static final String delimiter = ".";
    public static final String delimiterDecode = "\\.";

    public static String encryptString(PublicKey publicKey, String stringToEncrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        
        byte[] stringToEncryptInBytes = stringToEncrypt.getBytes(StandardCharsets.UTF_8);
        String[] hashedValueStrings = new String[Math.ceilDiv(stringToEncryptInBytes.length, bytesInString)];
        
        for (int i = 0; i < hashedValueStrings.length; i ++) {
            byte[] valueArray = Arrays.copyOfRange(stringToEncryptInBytes, i*bytesInString, (1+i)*bytesInString);
            Cipher cipher = Cipher.getInstance(transformation);

            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] cipherText = cipher.doFinal(valueArray);
    
            hashedValueStrings[i] = Base64.getEncoder().encodeToString(cipherText);
        }

        return String.join(delimiter, hashedValueStrings);
    }

    public static byte[] decryptString(PrivateKey privateKey, String stringToDecrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String[] encryptedStrings = stringToDecrypt.split(delimiterDecode);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytesInString*encryptedStrings.length);

        for (String encryptedString : encryptedStrings) {
            Cipher decryptCipher = Cipher.getInstance(transformation);
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] decryptedMessageBytes = decryptCipher.doFinal(Base64.getDecoder().decode(encryptedString));
            byteBuffer.put(decryptedMessageBytes);
        }

        return byteBuffer.array();
    }
}
