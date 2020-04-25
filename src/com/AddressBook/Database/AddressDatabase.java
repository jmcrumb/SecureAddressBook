 /*
  * Title:          com.AddressBook.Database
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook.Database;

 import com.AddressBook.AddressEntry;
 import org.jetbrains.annotations.NotNull;
 import org.jetbrains.annotations.Nullable;

 import java.io.IOException;
 import java.nio.file.Files;
 import java.nio.file.Path;
 import java.nio.file.Paths;
 import java.util.HashMap;
 import java.util.Map;

 import static java.nio.charset.StandardCharsets.US_ASCII;
 import static java.nio.file.StandardOpenOption.CREATE;
 import static java.nio.file.StandardOpenOption.WRITE;


 public class AddressDatabase {
     public interface Encrypter {
         String encrypt(String plainText);
     }

     public interface Decrypter {
         String decrypt(String encrypted);
     }

     private static final int MAX_RECORDS = 256;
     private static final String FOLDER_NAME = ".addresses";
     private static final String FILE_PREFIX = "u_";
     private String currentUserId;
     private Map<String, AddressEntry> map;

     private @Nullable String readFile(String userId) throws IOException {
         Path path = Paths.get(FOLDER_NAME, FILE_PREFIX + userId);
         if (!Files.exists(path)) {
             return null;
         } else {
             try {
                 return Files.readString(path, US_ASCII);
             } catch (IOException e) {
                 throw new IOException("Database exists and Failed to Read!");
             }
         }
     }

     private void writeFile(String userId, String data) throws IOException {
         Path path = Paths.get(FOLDER_NAME, FILE_PREFIX + userId);
         try {
             Files.writeString(path, data, US_ASCII, CREATE, WRITE);
         } catch (IOException e) {
             throw new IOException("User Database Failed to Write!");
         }
     }

     private @NotNull Map<String, AddressEntry> getMapFromFile(String userId, Decrypter decrypter) throws IOException {
         String encypted = readFile(userId);
         if (encypted == null) {
             return new HashMap<>();
         }
         String raw = decrypter.decrypt(encypted);
         String[] lines = raw.split("\n");
         Map<String, AddressEntry> m = new HashMap<>(lines.length);
         for (String line : lines) {
             AddressEntry ae = new AddressEntry(line);
             m.put(ae.recordId, ae);
         }
         return m;
     }


     private void setFileFromMap(String userId, @NotNull Encrypter encrypter, @NotNull Map<String, AddressEntry> m) throws IOException {
         StringBuilder sb = new StringBuilder();
         m.forEach((k, v) -> {
             sb.append(v.toString()).append(":");
         });
         sb.deleteCharAt(sb.lastIndexOf(":"));
         writeFile(userId, encrypter.encrypt(sb.toString()));
     }


     private void instantiateMapIfNeeded(String userId, Decrypter decrypter) throws IOException {
         if (map == null || currentUserId == null || !currentUserId.equals(userId)) {
             map = getMapFromFile(userId, decrypter);
             currentUserId = userId;
         }
     }

     public AddressEntry get(String userId, String recordId, Decrypter decrypter) throws IOException {
         instantiateMapIfNeeded(userId, decrypter);
         return map.get(recordId);
     }

     public void set(String userId, AddressEntry entry, Decrypter decrypter, Encrypter encrypter) throws IOException {
         instantiateMapIfNeeded(userId, decrypter);
         map.put(entry.recordId, entry);
         setFileFromMap(userId, encrypter, map);
     }

     public boolean exists(String userId, Decrypter decrypter) throws IOException {
         instantiateMapIfNeeded(userId, decrypter);
         return map.containsKey(userId);
     }

     public boolean isFull(String userId, Decrypter decrypter) throws IOException {
         instantiateMapIfNeeded(userId, decrypter);
         return !(map.size() < MAX_RECORDS);
     }

     public static AddressDatabase getInstance() {
         if (addressDatabase == null) {
             addressDatabase = new AddressDatabase();
         }
         return addressDatabase;
     }

     private static AddressDatabase addressDatabase;

     private AddressDatabase() {

     }
 }

