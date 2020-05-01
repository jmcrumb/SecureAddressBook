/*
 * Title:          com.AddressBook
 * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
 * Last Modified:  4/22/20
 * Description:
 * */
package com.AddressBook;

import com.AddressBook.Command.Command;
import com.AddressBook.Database.UserDatabase;
import com.AddressBook.UserEntry.UserEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.AddressBook.Encryption.*;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class AuditLog {


    @SuppressWarnings("InnerClassMayBeStatic")
    private final class DataEntry {
        DataEntry(String commandType, String userName) {
            this.time = LocalTime.now();
            this.date = LocalDate.now();
            this.commandType = commandType;
            this.userId = (userName != null) ? userName : "NO USER LOGGED IN";
        }

        DataEntry(String dataString) {
            String[] sa = dataString.split(", ");
            this.date = LocalDate.parse(sa[0], DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
            this.time = LocalTime.parse(sa[1], DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
            this.commandType = sa[2];
            this.userId = sa[3];
        }

        final LocalDate date;
        final LocalTime time;

        final String commandType;
        final String userId;

        @Override
        public String toString() {
            return this.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) + ", "
              + this.time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)) + ", " + this.commandType
              + ", " + this.userId;
        }
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    private final class EncryptedEntry {
        public final String encryptedData;
        public String signature;

        EncryptedEntry(String dataString) {
            System.out.println(dataString);
            this.encryptedData = dataString.split(";")[0];
            this.signature = dataString.split(";")[1];
        }

        EncryptedEntry(DataEntry de, EncryptedEntry lastEntry) throws Exception {
            encryptedData = Encryption.encryptWithRSA(publicKey, de.toString());
            String entryHash = Encryption.hashSHA256(de.toString());
            signature = User.getInstance().sign(entryHash + ":" + lastEntry.signature);
        }

        EncryptedEntry(DataEntry de) throws Exception {
            encryptedData = Encryption.encryptWithRSA(publicKey, de.toString());
            String entryHash = Encryption.hashSHA256(de.toString());
            signature = User.getInstance().sign(entryHash + ":");
        }

        @Override
        public String toString() {
            return encryptedData + ";" + signature;
        }
    }

    private final String LOG_FILE_NAME = ".logHistory";
    private static final String PUB_KEY_FILENAME = ".logkey";
    private static final String PRV_KEY_FILENAME = ".auditPrvKey";
    private static PublicKey publicKey;
    private static AuditLog logInstance;


    public static void setPrivateKey(PrivateKey key, Encrypter encrypter) throws GeneralSecurityException, IOException {
        byte[] ba = encrypter.encrypt(keyToB64(key));
        String encryptedKey = bytesToString(ba);
        Path p = Paths.get(PRV_KEY_FILENAME);
        Files.writeString(p, encryptedKey);
    }

    public static PrivateKey getPrivateKey(Decrypter decrypter) throws IOException, GeneralSecurityException {
        Path p = Paths.get(PRV_KEY_FILENAME);
        String encryptedKey = Files.readString(p);
        byte[] decryptedKey = stringToBytes(encryptedKey);
        String b64Key = decrypter.decrypt(decryptedKey);
        return privateKeyFromB64(b64Key);
    }

    public static void setPublicKey(PublicKey key) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        writePublicKey(PUB_KEY_FILENAME, key);
    }


    private AuditLog() throws Exception {
        getPublicKey();
//        fileToList();
    }

    public static AuditLog getInstance() throws Exception {
        if (logInstance == null) {
            logInstance = new AuditLog();
        }
        return logInstance;
    }

    private List<String> fileToList() throws IOException {
        List<String> ls = new LinkedList<>();
        try {
            Path f = Paths.get(LOG_FILE_NAME);
            if (Files.exists(f)) {
                ls = Files.readAllLines(f);
            }
            return ls;
        } catch (IOException e) {
            throw new IOException("failed to read AuditLog");
        }
    }

    private void getPublicKey() throws Exception {
        // if file doesn't exist
        // if admin has logged in throw exception
        // else wait for public key to be passed
        // else
        // set public key

        Path f = Paths.get(PUB_KEY_FILENAME);
        if (!Files.exists(f)) {
            UserEntry admin = UserDatabase.getInstance().get("admin");
            if (!admin.hasLoggedIn()) {
                throw new Exception("Audit log public key has been tampered with.");
            }
            publicKey = null;
            return;
        }

        try {
            publicKey = Encryption.readPublicKey(f.toString());
        } catch (Exception e) { // make this a better exception
            throw new Exception("Audit log public key has been tampered with.");
        }
    }

    private List<DataEntry> decryptEntries(PrivateKey decryptKey) throws Exception {
        List<DataEntry> decrypted = new ArrayList<DataEntry>();
        EncryptedEntry lastEntry = null;
        EncryptedEntry[] encrypted = fileToList()
          .stream()
          .map(EncryptedEntry::new)
          .toArray(EncryptedEntry[]::new);

        for (EncryptedEntry entry : encrypted) {
            String decryptedEntry = Encryption.decryptWithRSA(decryptKey, entry.encryptedData);
            DataEntry data = new DataEntry(decryptedEntry);

            String hashes = Encryption.decryptWithRSA(Encryption.publicKeyFromB64(UserDatabase.getInstance().get(data.userId).publicKey), entry.signature);
            String entryHash = hashes.split(";")[0];
            String lastSignature = hashes.split(";")[1];
            String calculatedHash = Encryption.hashSHA256(decryptedEntry);

            if (!entryHash.equals(calculatedHash)) {
                throw new UserVisibleException("Audit Log is compromised @ " + data.toString());
            } else if (lastEntry != null && !lastEntry.signature.equals(lastSignature)) {
                throw new UserVisibleException("Audit Log is compromised between " + data.toString() + " and " + lastEntry.toString());
            }
            decrypted.add(data);
            lastEntry = entry;
        }
        return decrypted;
    }

    private void logRecovery() throws IOException {
        UserInput.getInstance().sendOutput("Failed to read Audit Log\n"
          + "Please enter the number of the action which you wish to take: \n"
          + "1. Terminate Audit Log read \n2. Reset Log");
        String input = UserInput.getInstance().getNextInput();

        try {
            int i = Integer.parseInt(input.trim());
            if (i == 2)
                Files.delete(Paths.get(LOG_FILE_NAME));
            else {
                UserInput.getInstance().sendOutput("Input not recognized.  Please try again.");
                logRecovery();
            }
        } catch (NumberFormatException e) {
            UserInput.getInstance().sendOutput("Input not recognized.  Please try again.");
            logRecovery();
        }

    }

    private void listToFile(List<String> list) throws IOException {
        String output = String.join("\n", list);
        Files.writeString(Paths.get(LOG_FILE_NAME), output, TRUNCATE_EXISTING, CREATE);
    }

    // Logs the command of all user
    // figure out where to put the file, make sure in same directory as program
    // Format is command(input);authorization(Yes/No)
    public void logCommand(Command command, boolean authorized, String userId) throws Exception {
        if (publicKey == null) {
            throw new Exception("Audit log public key has not been set.");
        }

        if (command == null) {
            return;
        }

        DataEntry data = null;
        if (authorized && command.getAuthorizedCode() != null) {
            data = new DataEntry(command.getAuthorizedCode(), userId);
        } else if (!authorized && command.getUnauthorizedCode() != null) {
            data = new DataEntry(command.getUnauthorizedCode(), userId);
        } 


        EncryptedEntry newEntry = null;
        List<String> entryStringList = fileToList();
        EncryptedEntry lastEntry;
        if (entryStringList.size() > 0) {
            lastEntry = new EncryptedEntry(entryStringList.get(entryStringList.size() - 1));
            newEntry = new EncryptedEntry(data, lastEntry);
        } else {
            newEntry = new EncryptedEntry(data);
        }

        entryStringList.add(newEntry.toString());
        listToFile(entryStringList);
        logInstance = this; // update self across all other instances

    }

    public String[] getFilteredArray(String userId, PrivateKey decryptKey) throws Exception {
        return decryptEntries(decryptKey)
          .stream()
          .filter(e -> e.userId.equals(userId))
          .map(DataEntry::toString).toArray(String[]::new);
    }

    public String[] getArray(PrivateKey decryptKey) throws Exception {
        return decryptEntries(decryptKey)
          .stream()
          .map(DataEntry::toString)
          .toArray(String[]::new);
    }

    public void reEncryptEntries(Decrypter decrypter, Encrypter encrypter) throws Exception {
        PrivateKey pk = getPrivateKey(decrypter);
        setPrivateKey(pk, encrypter);

        List<DataEntry> ls = decryptEntries(pk);
        List<EncryptedEntry> le = new LinkedList<>();
        EncryptedEntry lastEntry = null;
        for (DataEntry entry : ls) {
            EncryptedEntry newEntry;
            if (ls.size() > 0) {
                newEntry = new EncryptedEntry(entry);
            } else {
                newEntry = (new EncryptedEntry(entry, lastEntry));
            }
            le.add(newEntry);
            lastEntry = newEntry;
        }
        listToFile(le.stream().map(EncryptedEntry::toString).collect(Collectors.toList()));

    }

}
