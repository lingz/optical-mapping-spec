package edu.nyu.opticalMapping;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import org.apache.commons.codec.binary.Base64;

/**
 * Created by ling on 2/10/14.
 */
public class Utils {
    private static byte[] salt = {0, 1, 2, 3, 4, 5, 6, 7};
    private static NumberFormat doubleFormatter = new DecimalFormat("#0.00000");
    private static final String encryptionKey = "9021nkds8xnewrq023";
    private static final byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private static final IvParameterSpec ivspec = new IvParameterSpec(iv);

    public static String joinDoubles(List<Double> input) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Double cut : input) {
            stringBuilder.append(doubleFormatter.format(cut));
            stringBuilder.append(" ");
        }
        // remove trailing whitespace
        stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public static String encryptString(String plainText) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException, InvalidAlgorithmParameterException {

        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);

        byte[] raw = cipher.doFinal(plainText.getBytes());

        return Base64.encodeBase64String(raw);
    }

    public static String decryptString(String cipherText) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException, InvalidAlgorithmParameterException {

        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);

        byte[] raw = Base64.decodeBase64(cipherText);

        String plainText = new String(cipher.doFinal(raw), "UTF-8");
        return plainText;
    }

    private static Cipher getCipher(int mode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, InvalidKeySpecException, InvalidAlgorithmParameterException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(encryptionKey.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, secret, ivspec);
        return cipher;
    }
}
