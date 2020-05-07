package com.encryption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EncryptionTest {

    /*
     * generate crypto random key, k for each entry
     * 
     * previous entries encrypted as string = pe
     * 
     * encrypt(pe + # + newEntry)
     * 
     * rsaEncrypt(k) -> ledger key1|key2^b64EncodedAESentries
     */

    public static void main(String[] args) throws Exception {
        String record = "this is a record, trust me";

        KeyPair rkeys = Encryption.generatePublicPrivateKeys();

        String encrypted = "";
        for (int i = 0; i < 10; ++i) {
            String key = genRandomKey();
            addKey("keyfile", key, rkeys.getPrivate());
            encrypted = encryptNewEntry(encrypted, record, key);
        }

        List<String> entries = decryptEntries(encrypted, getKeys("keyfile", rkeys.getPublic()));
        for (String entry : entries) {
            System.out.println(entry);
        }
    }

    public static String encryptNewEntry(String oldEntries, String newEntry, String key) throws Exception {
        byte[] ba = Encryption.encrypt(newEntry + "#" + oldEntries, key);
        return new String(Base64.getEncoder().encode(ba), "UTF-8");
    }
 
    public static List<String> decryptEntries(String oldEntries, List<String> keyList) throws Exception {
        List<String> entries = new ArrayList<>();
        Collections.reverse(keyList);// reverse so that newest key is first instead of last
        for (String key : keyList) {
            byte[] ba = Base64.getDecoder().decode(oldEntries);
            String temp = Encryption.decrypt(ba, key);
            String[] sa = temp.split("#");
            if(sa.length > 1){
               oldEntries = sa[1];
            }
            entries.add(sa[0]);
        }
        return entries;
    }

    public static List<String> getKeys(String filename, PublicKey key) throws Exception {
        String input = Files.readString(Paths.get(filename));
        String[] lines = input.trim().split("\n");
        List<String> out = new ArrayList<String>();
        for(String encryptedKey : lines){
            out.add(Encryption.decryptWithRSA(key, encryptedKey));
        }
        
        return out;
    }
     

    public static void addKey(String filename, String key, PrivateKey pk) throws Exception {
        Files.writeString(Paths.get(filename), Encryption.encryptWithRSA(pk, key) + "\n", StandardOpenOption.CREATE,
            StandardOpenOption.APPEND);
    }


    public static String genRandomKey() {
        char[] b64chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+/".toCharArray();
        Random rand = new Random();
        StringBuilder rstr = new StringBuilder(256);
        for (int i = 0; i < 256; ++i) {
            int rindex = rand.nextInt(b64chars.length);
            rstr.append(b64chars[rindex]);
        }

        return rstr.toString();
    }


    /* private static String encryptLog(String encrypted, String newEntry, PrivateKey pk) throws Exception {
        if (newEntry.contains("#")) {
            throw new IllegalArgumentException("entry contains invalid character #");
        }
        encrypted += "#" + newEntry;
        return Encryption.encryptWithRSA(pk, encrypted);
    }

    private static List<String> decryptLog(String encrypted, PublicKey pk) throws Exception {
        List<String> entries = new ArrayList<String>();
        boolean thereAreMoreEntries = true;
        while (thereAreMoreEntries) {
            String temp = Encryption.decryptWithRSA(pk, encrypted);
            String[] ta = temp.split("#");
            if (ta.length == 1) {
                thereAreMoreEntries = false;
                entries.add(ta[0]);
            } else {
                entries.add(ta[1]);
                encrypted = ta[0];
            }
        }

        return entries;
    } */
}