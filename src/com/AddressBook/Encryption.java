/*
 * Title:          com.AddressBook.Command
 * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
 * Last Modified:  4/22/20
 * Description:
 * */
package com.AddressBook;

import org.mindrot.BCrypt;

import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

    public static String hashBCrypt(String data) {
        return BCrypt.hashpw(data, BCrypt.gensalt());
    }

    public static String hashSHA256(String data) throws java.security.NoSuchAlgorithmException, java.io.UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] hash = md.digest(data.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(hash);
    }

    public static boolean checkBCrypt(String unhashed, String hashed) {
        return BCrypt.checkpw(unhashed, hashed);
    }

    public static String encrypt(String data, String key) throws java.security.GeneralSecurityException, java.io.UnsupportedEncodingException {
        Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] encodeKey = Base64.getDecoder().decode(key.getBytes("UTF-8"));
        SecretKey aesKey = new SecretKeySpec(encodeKey, 0, encodeKey.length, "AES");

        aes.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] cipherText = aes.doFinal(data.getBytes("UTF-8"));

        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String data, String key) throws java.security.GeneralSecurityException, java.io.UnsupportedEncodingException {
        Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] decodeKey = Base64.getDecoder().decode(key.getBytes("UTF-8"));
        SecretKey aesKey = new SecretKeySpec(decodeKey, 0, decodeKey.length, "AES");

        aes.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] plainText = aes.doFinal(data.getBytes("UTF-8"));

        return new String(plainText, "UTF-8");
    }
}
