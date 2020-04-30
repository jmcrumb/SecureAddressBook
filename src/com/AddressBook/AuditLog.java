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
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class AuditLog {

    @SuppressWarnings("InnerClassMayBeStatic")
    private final class DataHolder {
        DataHolder(String commandType, String userName) {
            this.time = LocalTime.now();
            this.date = LocalDate.now();
            this.commandType = commandType;
            this.userId = (userName != null) ? userName : "NO USER LOGGED IN";
        }

        DataHolder(String dataString) {
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

    private final class EncryptedEntry {
        public final String encryptedData;
        public String signature;
        
        EncryptedEntry(String dataString) {
            this.encryptedData = dataString.split(";")[0];
            this.signature = dataString.split(";")[1];
        }

        EncryptedEntry(String encryptedData, String signature) {
            this.encryptedData = encryptedData;
            this.signature = signature;
        }

        @Override
        public String toString() {
            return encryptedData + ";" + signature;
        }
    }

    private final String LOG_FILE_NAME = ".logHistory";
    private final String PUBLIC_KEY_NAME = ".logkey";

    private static PublicKey publicKey;
    
    private static AuditLog logInstance;
    private static List<EncryptedEntry> fifo;

    private AuditLog() throws IOException, Exception {
        getPublicKey();
        fileToList();
    }

    public static AuditLog getInstance() throws IOException, Exception {
        if (logInstance == null) {
            logInstance = new AuditLog();
        }
        return logInstance;
    }

    private void fileToList() throws IOException {
        try {
            Path f = Paths.get(LOG_FILE_NAME);
            if (Files.exists(f)) {
                List<String> ls = Files.readAllLines(Paths.get(LOG_FILE_NAME));
                if (ls.size() > 0) {
                    ls.forEach((v) -> fifo.add(new EncryptedEntry(v)));
                }
            }
        } catch (IOException e) {
            throw new IOException("failed to read AuditLog");
        }
    }

    private void getPublicKey() throws IOException, Exception {
        // if file doesn't exist
            // if admin has logged in throw exception
            // else wait for public key to be passed
        // else
            // set public key 
        
        Path f = Paths.get(PUBLIC_KEY_NAME);
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
    
    private List<DataHolder> decryptEntries(PrivateKey decryptKey) throws IOException {
        List<DataHolder> decrypted = new ArrayList<DataHolder>();
        EncryptedEntry lastEntry = null;
        for (EncryptedEntry entry : fifo) {
            String decryptedEntry = Encryption.decryptWithRSA(decryptKey, entry.encryptedData);
            DataHolder data = new DataHolder(decryptedEntry);

            String hashes = Encryption.decryptWithRSA(UserDatabase.getInstance().get(data.userId).publicKey, entry.signature);
            String entryHash = hashes.split(";")[0];
            String lastSignature = hashes.split(";")[1];
            String calculatedHash = Encryption.hashSHA256(decryptedEntry);

            if (!entryHash.equals(calculatedHash) || !lastEntry.signature.equals(lastSignature)) {
                // Uh-oh!
            }
            decrypted.add(data);
            lastEntry = entry;
        }
        return decrypted;
    }

    private void listToFile() throws IOException {
        List<String> ls = new ArrayList<>();
        fifo.forEach((v) -> ls.add(v.toString()));
        String output = String.join("\n", ls);
        Files.writeString(Paths.get(LOG_FILE_NAME), output, CREATE, WRITE);
    }

    // Logs the command of all user
    // figure out where to put the file, make sure in same directory as program
    // Format is command(input);authorization(Yes/No)
    public void logCommand(Command command, boolean authorized) throws IOException, Exception {
        if (publicKey == null) {
            throw new Exception("Audit log public key has not been set.");
        }

        if (command == null) {
            return;
        }

        if (fifo.size() >= 512) {
            fifo.remove(0);
        }

        EncryptedEntry hd = null;
        EncryptedEntry lastEntry = fifo.get(fifo.size() - 1);
        DataHolder data = null;
        if (authorized && command.getAuthorizedCode() != null) {
            data = new DataHolder(command.getAuthorizedCode(), User.getInstance().getUserId());
        } else if (!authorized && command.getUnauthorizedCode() != null) {
            data = new DataHolder(command.getUnauthorizedCode(), User.getInstance().getUserId());
        } else {
            throw new Exception("An unknown error occured.");
        }
        String encryptedData = Encryption.encryptWithRSA(publicKey, data.toString());
        String entryHash = Encryption.hashSHA256(data.toString());
        String signature = User.getInstance().sign(entryHash + ":" + lastEntry.signature);
        hd = new EncryptedEntry(encryptedData, signature);
        fifo.add(hd);
        listToFile();

        logInstance = this; // update self across all other instances

    }

    public String[] getFilteredArray(String userId, PrivateKey decryptKey) throws IOException {
        return decryptEntries(decryptKey).stream().filter(e -> e.userId.equals(userId)).map(DataHolder::toString).toArray(String[]::new);
    }

    public String[] getArray(PrivateKey decryptKey) throws IOException {
        return decryptEntries(decryptKey).stream().map(DataHolder::toString).toArray(String[]::new);
    }

}
