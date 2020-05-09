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
 import org.jetbrains.annotations.NotNull;

 import java.io.IOException;
 import java.nio.charset.StandardCharsets;
 import java.nio.file.Files;
 import java.nio.file.Path;
 import java.nio.file.Paths;
 import java.security.GeneralSecurityException;
 import java.security.KeyPair;
 import java.security.PrivateKey;
 import java.security.PublicKey;
 import java.time.LocalDate;
 import java.time.LocalDateTime;
 import java.time.LocalTime;
 import java.time.format.DateTimeFormatter;
 import java.time.format.FormatStyle;
 import java.util.*;
 import java.util.stream.Collectors;

 import static java.nio.file.StandardOpenOption.*;
/*
AuditLogClass
    for files
        USER LOGS
            .log/<userid>/entries.log = the entries
            .log/<userid>/keys.log = the keyfile
            .log/nouser/entries.log = file for when no user signed in
        Admin stuff
            .log/.admin_data/<userid>.private = the rsa key for each user, encrypted with admin encrypter


when user is created
    create keypair and save public with user, save private in .log/admin/<userid>.key
    create .log/<userid>/entries.log and .log/<userid>/keys.log

to read user log
    access private key of user who's log it is
    decrypt all keys for every log entry
    use those keys to undo AES encryption of entries
to save user log
    create random AES encryption key
    encrypt key with RSA and store in keyfile for user

*/

 public class AuditLog {

     //TODO implement no user entries logging

     @SuppressWarnings("InnerClassMayBeStatic")
     private final class DataHolder implements Comparable<DataHolder> {
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
             return this.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) + ", " +
               this.time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)) + ", " +
               this.commandType + ", " +
               this.userId;
         }

         @Override
         public int compareTo(@NotNull AuditLog.DataHolder o) {
             return (LocalDateTime.of(date, time)).compareTo(LocalDateTime.of(o.date, o.time));
         }
     }

     private static final String MAIN_DIR = ".logs";
     private static final String KEY_FILE_NAME = "keys.log";
     private static final String ENTRY_FILE_NAME = "entries.log";
     private static final String ADMIN_DATA = "_admin_data";
     private static final Path NO_USER_ENTRIES = Paths.get(MAIN_DIR, "nouser", ENTRY_FILE_NAME);

     private static final Path TEMP_ADMIN_PUBLIC_KEY = Paths.get(MAIN_DIR, ADMIN_DATA, "temp.public");
     private static final Path TEMP_ADMIN_PRIVATE_ENCRYPTED_KEY = Paths.get(MAIN_DIR, ADMIN_DATA, "temp.private");
     private static final Path TEMP_KEY_FILE = Paths.get(MAIN_DIR, ADMIN_DATA, "temp.log");

     private static AuditLog logInstance;

     private void initTempKeys(Encryption.Encrypter adminEncrypter) throws GeneralSecurityException, IOException {
         KeyPair kp = Encryption.generatePublicPrivateKeys();
         //save public key
         Files.createDirectories(TEMP_ADMIN_PUBLIC_KEY.getParent());
         Files.writeString(TEMP_ADMIN_PUBLIC_KEY, Encryption.keyToB64(kp.getPublic()), CREATE);
         //encrypt private key
         byte[] encryptedPrivKeyBytes = adminEncrypter.encrypt(Encryption.keyToB64(kp.getPrivate()));
         //save private key
         Files.createDirectories(TEMP_ADMIN_PRIVATE_ENCRYPTED_KEY.getParent());
         Files.writeString(TEMP_ADMIN_PRIVATE_ENCRYPTED_KEY, Base64.getEncoder().encodeToString(encryptedPrivKeyBytes), CREATE);
     }

     private PrivateKey getTempPrivateKey(Encryption.Decrypter adminDecrypter) throws GeneralSecurityException, IOException {
         String encryptedPrivateKeyB64 = Files.readString(TEMP_ADMIN_PRIVATE_ENCRYPTED_KEY);
         byte[] encryptedPrivateKeyB64Bytes = Base64.getDecoder().decode(encryptedPrivateKeyB64);
         String privateKeyB64 = adminDecrypter.decrypt(encryptedPrivateKeyB64Bytes);
         return Encryption.privateKeyFromB64(privateKeyB64);
     }

     private PublicKey getTempPublicKey() throws IOException, GeneralSecurityException {
         return Encryption.publicKeyFromB64(Files.readString(TEMP_ADMIN_PUBLIC_KEY));
     }

     private void saveUserPrivateKey(String userId, PrivateKey keyToSave) throws GeneralSecurityException, IOException {
         PublicKey pub = getTempPublicKey();
         String randomAESKey = genRandomKey();
         String encryptedAESKey = Encryption.encryptWithRSA(pub, randomAESKey);
         byte[] ba = Encryption.encrypt(Encryption.keyToB64(keyToSave), randomAESKey);
         String encryptedRSAKey = Base64.getEncoder().encodeToString(ba);

         if (!Files.exists(TEMP_KEY_FILE)) {
             Files.createDirectories(TEMP_KEY_FILE.getParent());
             Files.writeString(TEMP_KEY_FILE, userId + "#" + encryptedRSAKey + "#" + encryptedAESKey, CREATE);
         } else {
             Files.writeString(TEMP_KEY_FILE, "\n" + userId + "#" + encryptedRSAKey + "#" + encryptedAESKey, APPEND);
         }

     }

     // used when admin logs in to add all public keys from users who had a first login
     private void updateUserPrivateKeys(Encryption.Decrypter adminDecrypter, Encryption.Encrypter adminEncrypter) throws IOException, GeneralSecurityException {
         PrivateKey tempKey = getTempPrivateKey(adminDecrypter);
         List<String> lines = Files.readAllLines(TEMP_KEY_FILE);
         for (String line : lines) {
             String[] sa = line.split("#");
             String id = sa[0];
             String encryptedRSAKey = sa[1];
             String encryptedAESKey = sa[2];
             String keyAES = Encryption.decryptWithRSA(tempKey, encryptedAESKey);

             String privateKeyB64 = Encryption.decrypt(Base64.getDecoder().decode(encryptedRSAKey), keyAES);

             setUserPrivateKey(id, Encryption.privateKeyFromB64(privateKeyB64), adminEncrypter);
         }
         Files.delete(TEMP_KEY_FILE);
     }

     public void onAdminFirstLogin(Encryption.Encrypter adminEncrypter) throws GeneralSecurityException, IOException {
         initTempKeys(adminEncrypter);
     }

     public void onAdminLogin(Encryption.Decrypter adminDecrypter, Encryption.Encrypter adminEncrypter) throws IOException, GeneralSecurityException {
         updateUserPrivateKeys(adminDecrypter, adminEncrypter);
     }

     public void onUserFirstLogin(String userId, PrivateKey keyToSave) throws GeneralSecurityException, IOException {
         saveUserPrivateKey(userId, keyToSave);
     }


     private Path getUserKeyPath(String userId) {
         return Paths.get(MAIN_DIR, userId, KEY_FILE_NAME);
     }

     private Path getUserEntryPath(String userId) {
         return Paths.get(MAIN_DIR, userId, ENTRY_FILE_NAME);
     }

     private Path getUserPrivateKeyPath(String userId) {
         return Paths.get(MAIN_DIR, ADMIN_DATA, userId + ".private");
     }

     private PrivateKey getUserPrivateKey(String userId, Encryption.Decrypter decrypter) throws IOException, GeneralSecurityException {
         String keyB64Encrypted = Files.readString(getUserPrivateKeyPath(userId));

         byte[] keyB64EncryptedBytes = Base64.getDecoder().decode(keyB64Encrypted);
         String keyB64 = decrypter.decrypt(keyB64EncryptedBytes);

         return Encryption.privateKeyFromB64(keyB64);
     }

     private void setUserPrivateKey(String userId, PrivateKey key, Encryption.Encrypter adminEncrypter) throws IOException, GeneralSecurityException {
         String keyB64 = Encryption.keyToB64(key);

         byte[] keyB64EncryptedBytes = adminEncrypter.encrypt(keyB64);

         String keyB64Encrypted = Base64.getEncoder().encodeToString(keyB64EncryptedBytes);

         if (!Files.exists(getUserPrivateKeyPath(userId))) {
             Files.createDirectories(getUserPrivateKeyPath(userId).getParent());
             Files.writeString(getUserPrivateKeyPath(userId), keyB64Encrypted, CREATE);
         } else {
             throw new IOException("PrivateKey exists for user already!");
         }

     }

     private String getEncryptedEntriesFileContent(String userId) throws IOException {
         if (!Files.exists(getUserEntryPath(userId))) {
             return "";
         }
         return Files.readString(getUserEntryPath(userId));
     }

     private void setEncryptedEntriesFileContent(String userId, String encryptedEntries) throws IOException {
         Files.createDirectories(getUserPrivateKeyPath(userId).getParent());
         Files.writeString(getUserEntryPath(userId), encryptedEntries, CREATE, TRUNCATE_EXISTING);
     }

     private void addToNoUserEntries(String data) throws IOException {
         if (!Files.exists(NO_USER_ENTRIES)) {
             Files.createDirectories(NO_USER_ENTRIES.getParent());
             Files.writeString(NO_USER_ENTRIES, data, CREATE);
         } else {
             Files.writeString(NO_USER_ENTRIES, "\n" + data, APPEND);
         }
     }

     private static String encryptNewEntry(String oldEntries, String newEntry, String key) throws GeneralSecurityException {
         byte[] ba = Encryption.encrypt(newEntry + "#" + oldEntries, key);
         return new String(Base64.getEncoder().encode(ba), StandardCharsets.UTF_8);
     }

     private static List<String> decryptEntries(String oldEntries, List<String> keyList) throws GeneralSecurityException {
         List<String> entries = new ArrayList<>();
         Collections.reverse(keyList);// reverse so that newest key is first instead of last
         for (String key : keyList) {
             byte[] ba = Base64.getDecoder().decode(oldEntries);
             String temp = Encryption.decrypt(ba, key);
             String[] sa = temp.split("#");
             if (sa.length > 1) {
                 oldEntries = sa[1];
             }
             entries.add(sa[0]);
         }
         return entries;
     }

     private static List<String> getKeys(Path pathToFile, PrivateKey key) throws IOException, GeneralSecurityException {
         String input = Files.readString(pathToFile);
         String[] lines = input.trim().split("\n");
         List<String> out = new ArrayList<>();
         for (String encryptedKey : lines) {
             out.add(Encryption.decryptWithRSA(key, encryptedKey));
         }

         return out;
     }

     private static void addKey(Path pathToFile, String key, PublicKey pk) throws GeneralSecurityException, IOException {
         Files.createDirectories(pathToFile.getParent());
         Files.writeString(pathToFile, Encryption.encryptWithRSA(pk, key) + "\n", CREATE, APPEND);
     }

     private static String genRandomKey() {
         char[] b64chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+/".toCharArray();
         Random rand = new Random();
         StringBuilder rstr = new StringBuilder(256);
         for (int i = 0; i < 256; ++i) {
             int rindex = rand.nextInt(b64chars.length);
             rstr.append(b64chars[rindex]);
         }

         return rstr.toString();
     }

     private AuditLog() {
     }

     public static AuditLog getInstance() {
         if (logInstance == null) {
             logInstance = new AuditLog();
         }
         return logInstance;
     }

     private void logRecovery() throws IOException {
//        UserInput.getInstance().sendOutput("Failed to read Audit Log\n"
//        + "Please enter the number of the action which you wish to take: \n"
//        + "1. Terminate Audit Log read \n2. Reset Log");
//        String input = UserInput.getInstance().getNextInput();
//
//        try {
//            int i = Integer.parseInt(input.trim());
//            if(i == 1)
//                return;
//            else if(i == 2)
//                Files.delete(Paths.get(LOG_FILE_NAME));
//            else {
//                UserInput.getInstance().sendOutput("Input not recognized.  Please try again.");
//                logRecovery();
//            }
//        } catch (NumberFormatException e) {
//            UserInput.getInstance().sendOutput("Input not recognized.  Please try again.");
//            logRecovery();
//        }

     }

     public void logCommand(Command command, boolean authorized) throws GeneralSecurityException, IOException {
         DataHolder data;
         // get the right auth code
         if (authorized && command.getAuthorizedCode() != null) {
             data = new DataHolder(command.getAuthorizedCode(), User.getInstance().getUserId());
         } else if (!authorized && command.getUnauthorizedCode() != null) {
             data = new DataHolder(command.getUnauthorizedCode(), User.getInstance().getUserId());
         } else {
             // if auth code is null don't record in audit log
             return;
         }
         //get current user
         User user = User.getInstance();
         String userId = user.getUserId();
         if (userId != null) {
             // get a random key for AES
             String key = genRandomKey();
             //add key to users key file, encrypted by public key for user
             addKey(getUserKeyPath(userId), key, user.getPublicKey());
             //get the previous entries that are an encrypted string
             String oldEntries = getEncryptedEntriesFileContent(userId);
             //add the new entry and encrypt
             String entries = encryptNewEntry(oldEntries, data.toString(), key);
             //save that to the entries log file for the user
             setEncryptedEntriesFileContent(userId, entries);
         } else {
             addToNoUserEntries(data.toString());
         }
     }

     public List<DataHolder> getLogOfUser(String userId, Encryption.Decrypter adminDecrypter) throws IOException, GeneralSecurityException {
         //get private key associated with user
         PrivateKey userPrivateKey = getUserPrivateKey(userId, adminDecrypter);
         //get all the entries for user
         String encryptedEntries = getEncryptedEntriesFileContent(userId);
         //get the keys for the entries
         List<String> keys = getKeys(getUserKeyPath(userId), userPrivateKey);
         // decrypt those entries
         List<String> entries = decryptEntries(encryptedEntries, keys);
         // turn entries into objects
         List<DataHolder> r = entries.stream().map(DataHolder::new).collect(Collectors.toList());
         Collections.sort(r);
         return r;
     }

     public List<DataHolder> getLogOfNoUser() throws IOException {
         return Files.readAllLines(NO_USER_ENTRIES)
           .stream()
           .map(DataHolder::new)
           .collect(Collectors.toList());
     }

     public List<DataHolder> getAllLogs(Encryption.Decrypter adminDecrypter) throws IOException, GeneralSecurityException {
         List<UserEntry> users = UserDatabase.getInstance().getAll();
         List<DataHolder> total = new ArrayList<>();
         for (UserEntry u : users) {
             total.addAll(getLogOfUser(u.userId, adminDecrypter));
         }
         total.addAll(getLogOfNoUser());
         Collections.sort(total);
         return total;
     }

     public String[] getFilteredArray(String userId, Encryption.Decrypter adminDecrypter) throws IOException, GeneralSecurityException {
         return getLogOfUser(userId, adminDecrypter).stream().map(DataHolder::toString).toArray(String[]::new);
     }

     public String[] getArray(Encryption.Decrypter adminDecrypter) throws IOException, GeneralSecurityException {
         return getAllLogs(adminDecrypter).stream().map(DataHolder::toString).toArray(String[]::new);
     }


 }
