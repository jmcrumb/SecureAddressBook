/*
 * Title:          com.AddressBook
 * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
 * Last Modified:  4/22/20
 * Description:
 * */
package com.AddressBook;

import com.AddressBook.UserEntry.*;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Base64;

public class User {

    private static User instance = null;

    private UserEntry entry;
    private String DBKey;


    private User() {
        this.entry = null;
        this.DBKey = null;
    }


    public static User getInstance() {
        if(instance == null)
            return new User();
        return instance;
    }


    public void setUser(UserEntry entry, String DBKey) {
        this.entry = entry;
        this.DBKey = DBKey;
        instance = this;
    }

    public String getUserId() {
        if (entry == null) return null;
        return entry.userId;
    }


    public byte[] encrypt(String data) throws java.security.GeneralSecurityException {
        if (DBKey == null || entry == null) {
            throw new RuntimeException("User is not initialized.");
        }
        return Encryption.encrypt(data, DBKey);
    }

    public String decrypt(byte[] data) throws java.security.GeneralSecurityException  {
        if (DBKey == null || entry == null) {
            throw new RuntimeException("User is not initialized.");
        }
        return Encryption.decrypt(data, DBKey);
    }


    public int getAuthorization() {
        if (entry == null) return 0;
        else if (entry instanceof AdminEntry) return 2;
        else return 1;
    }

    public PublicKey getPublicKey() throws GeneralSecurityException, UnsupportedEncodingException {
        byte[] b = Base64.getDecoder().decode(entry.encryptedPublicKey);
        String k = decrypt(b);
        return Encryption.publicKeyFromB64(k);
    }
}