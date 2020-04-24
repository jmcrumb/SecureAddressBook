 /*
  * Title:          com.AddressBook.Database
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook.Database;

 import com.AddressBook.UserEntry.UserEntry;
 import org.jetbrains.annotations.NotNull;
 import org.jetbrains.annotations.Nullable;

 import java.io.IOException;
 import java.nio.file.Files;
 import java.nio.file.Path;
 import java.nio.file.Paths;
 import java.util.*;

 import static java.nio.charset.StandardCharsets.US_ASCII;
 import static java.nio.file.StandardOpenOption.*;

 public class UserDatabase {
     //  getInstance
     private final int MAX_SIZE = 7;

     private final String FILE_NAME = ".users";

     private static UserDatabase userDatabase;

     private Map<String, UserEntry> map;

     private @NotNull List<String> mapToList(Map<String, UserEntry> m) {
         ArrayList<String> a = new ArrayList<>(m.size());
         m.forEach((ignored, v) -> a.add(v.toString()));
         return a;
     }

     private @NotNull Map<String, UserEntry> mapFromList(List<String> l) {
         Map<String, UserEntry> m = new HashMap<>();
         l.forEach(s -> {
             UserEntry u = new UserEntry(s);
             m.put(u.userId, u);
         });
         return m;
     }

     private void writeFile(List<String> toWrite) throws IOException {
         Path path = Paths.get(FILE_NAME);
         try {
             Files.write(path, toWrite, US_ASCII, CREATE, WRITE)
         } catch (IOException e) {
             throw new IOException("User Database Failed to Write!");
         }
     }

     private void deleteFile() throws IOException {
         Path path = Paths.get(FILE_NAME);
         try {
             Files.deleteIfExists(path);
         } catch (IOException e) {
             throw new IOException("Failed to Delete User Database File!");
         }
     }


     private @Nullable List<String> loadFile() throws IOException {
         Path path = Paths.get(".properties");
         if (!Files.exists(path)) {
             return null;
         } else {
             try {
                 return Files.readAllLines(path, US_ASCII);
             } catch (IOException e) {
                 throw new IOException("Database exists and Failed to Read!");
             }
         }
     }

     private void getDbFromFile() throws IOException {
         @Nullable List<String> l = loadFile();
         if (l == null) {
             map = new HashMap<>();
         } else {
             // check to make sure hasn't been modified to be oversized
             if (l.size() > MAX_SIZE) {
                 deleteFile();
                 throw new IllegalArgumentException("Corrupted User Database, db deleted");
             }
             map = mapFromList(l);
         }
     }

     private void updateDbFile() throws IOException {
         writeFile(mapToList(map));
     }
     //constructor
     private UserDatabase() throws IOException {
         getDbFromFile();
     }



     public static UserDatabase getInstance() throws IOException {
         if (userDatabase == null) {
             userDatabase = new UserDatabase();
         }
         return userDatabase;
     }

     public UserEntry get(String userId) {
         return map.get(userId);
     }

     public void set(UserEntry entry) throws IOException {
         // if the max # of accounts isn't reached or just updating account
         if (map.size() < MAX_SIZE || map.containsKey(entry.userId)) {
             map.put(entry.userId, entry);
             updateDbFile();
         } else {
             throw new IllegalArgumentException("Already at max accounts!" +
               " delete one before adding a new one");
         }
     }
 }
