/*
 * Title:          com.AddressBook.Command
 * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
 * Last Modified:  4/22/20
 * Description:
 * */
package com.AddressBook;

import com.AddressBook.Database.AddressDatabase;
import org.mindrot.BCrypt;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class Encryption {

    public static String hashBCrypt(String data) {
        String hashedData = BCrypt.hashpw(data, BCrypt.gensalt());
        return hashedData;
    }

    public static String hashSHA256(String data) throws java.security.NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    public static boolean checkBCrypt(String unhashed, String hashed) {
        return BCrypt.checkpw(unhashed, hashed);
    }

    public static byte[] encrypt(String data, String key) throws java.security.GeneralSecurityException, java.io.UnsupportedEncodingException {
        byte[] encodeKey = Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8));
        encodeKey = Arrays.copyOf(encodeKey, 16);
        SecretKeySpec aesKey = new SecretKeySpec(encodeKey, "AES");

        Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");

        aes.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(new byte[16]));
        byte[] cipherText = aes.doFinal(data.getBytes(StandardCharsets.UTF_8));

        return cipherText;
    }

    public static String decrypt(byte[] data, String key) throws java.security.GeneralSecurityException, java.io.UnsupportedEncodingException {
        byte[] decodeKey = Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8));
        decodeKey = Arrays.copyOf(decodeKey, 16);
        SecretKeySpec aesKey = new SecretKeySpec(decodeKey, "AES");

        Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");

        aes.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(new byte[16]));
        byte[] plainText = aes.doFinal(data);

        return new String(plainText, StandardCharsets.UTF_8);
    }


    public static KeyPair generatePublicPrivateKeys() throws NoSuchAlgorithmException {
        // from https://www.novixys.com/blog/how-to-generate-rsa-keys-java/
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        return kp;
    }



    public static String decryptWithRSA(Key key, String encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String encryptWithRSA(Key key, String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    public static String keyToB64(Key k){
        return Base64.getEncoder().encodeToString(k.getEncoded());
    }
    public static PrivateKey privateKeyFromB64(String b64key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = Base64.getDecoder().decode(b64key);
        /* Generate private key. */
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(ks);
    }

    public static PublicKey publicKeyFromB64(String b64key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] bytes = Base64.getDecoder().decode(b64key);
        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(ks);
    }

    public static PrivateKey readPrivateKey(String filename, AddressDatabase.Decrypter decrypter) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        /* Read all bytes from the private key file */
        Path path = Paths.get(filename);
        byte[] bytes = Files.readAllBytes(path);
        /* Generate private key. */
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(ks);
    }

    public static PublicKey readPublicKey(String filename) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        // from https://www.novixys.com/blog/how-to-generate-rsa-keys-java/

        Path path = Paths.get(filename);
        byte[] bytes = Files.readAllBytes(path);

        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(ks);
    }


    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToString(byte[] data) {
        char[] str = new char[data.length * 2];
        for (int j = 0; j < data.length; j++) {
            int v = data[j] & 0xFF;
            str[j * 2] = HEX_ARRAY[v >>> 4];
            str[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(str);
    }

    public static byte[] stringToBytes(String data) {
        int len = data.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(data.charAt(i), 16) << 4) + Character.digit(data.charAt(i+1), 16));
        }
        return bytes;
    }
}