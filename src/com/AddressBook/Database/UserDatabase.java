 /*
  * Title:          com.AddressBook.Database
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook.Database;

 import com.AddressBook.UserEntry.UserEntry;

 import java.io.IOException;
 import java.nio.file.Files;
 import java.nio.file.Path;
 import java.nio.file.Paths;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 import static java.nio.charset.StandardCharsets.US_ASCII;
 import static java.nio.file.StandardOpenOption.CREATE;
 import static java.nio.file.StandardOpenOption.WRITE;

 /**
  * Singleton Object to hold the user database
  */
 public class UserDatabase {
     /**
      * the maximum # of accounts that can be created including admin
      */
     private final static int MAX_ACCOUNTS = 7;

     private final static String FILE_NAME = ".users";

     private static UserDatabase userDatabase;

     /**
      * map used for storing user entries
      */
     private Map<String, UserEntry> map;

     private List<String> mapToList(Map<String, UserEntry> m) {
         ArrayList<String> a = new ArrayList<>(m.size());
         m.forEach((ignored, v) -> a.add(v.toString()));
         return a;
     }

     private Map<String, UserEntry> mapFromList(List<String> l) {
         Map<String, UserEntry> m = new HashMap<>();
         l.forEach(s -> {
             UserEntry u = new UserEntry(s);
             m.put(u.userId, u);
         });
         return m;
     }

     /**
      * used to write data to the Database file {@value #FILE_NAME}
      *
      * @param toWrite the data to write
      * @throws IOException when something goes wrong
      */
     private void writeFile(List<String> toWrite) throws IOException {
         Path path = Paths.get(FILE_NAME);
         try {
             Files.write(path, toWrite, US_ASCII, CREATE, WRITE);
         } catch (IOException e) {
             throw new IOException("User Database Failed to Write!");
         }
     }

     /**
      * deletes database file {@value FILE_NAME}
      *
      * @throws IOException if something goes wrong
      */
     private void deleteFile() throws IOException {
         Path path = Paths.get(FILE_NAME);
         try {
             Files.deleteIfExists(path);
         } catch (IOException e) {
             throw new IOException("Failed to Delete User Database File!");
         }
     }


     /**
      * reads database file {@value FILE_NAME}
      *
      * @return List of lines from a file or null if file doesn't exist
      * @throws IOException if something goes wrong reading file
      */
     private List<String> readFile() throws IOException {
         Path path = Paths.get(FILE_NAME);
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

     /**
      * Reads {@value #FILE_NAME} file to set {@link #map}
      *
      * @throws IllegalArgumentException if # of accounts is greater than {@value #MAX_ACCOUNTS}
      * @throws IOException              from {@link #readFile()}
      */
     private void getDbFromFile() throws IOException {
         List<String> l = readFile();
         if (l == null) {
             map = new HashMap<>();
             map.put("admin", new UserEntry("admin", null));
         } else {
             // check to make sure hasn't been modified to be oversized
             if (l.size() > MAX_ACCOUNTS) {
                 deleteFile();
                 throw new IllegalArgumentException("Corrupted User Database, db deleted");
             }
             map = mapFromList(l);
         }
     }

     /**
      * saves {@link #map} to file named {@value FILE_NAME}
      *
      * @throws IOException because it calls {@link #writeFile}
      */
     private void updateDbFile() throws IOException {
         writeFile(mapToList(map));
     }

     /**
      * @throws IOException because it calls {@link #getDbFromFile} to load the database
      */
     private UserDatabase() throws IOException {
         getDbFromFile();
     }


     /**
      * Used to get the one instance of the singleton UserDatabase
      *
      * @return The user database
      * @throws IOException if fails to load Database file {@value FILE_NAME}
      */
     public static UserDatabase getInstance() throws IOException {
         if (userDatabase == null) {
             userDatabase = new UserDatabase();
         }
         return userDatabase;
     }

     /**
      * @param userId the user to check for
      * @return If a user is in the database
      */
     public boolean exists(String userId) {
         return map.containsKey(userId);
     }

     /**
      * @return if the database is full
      */
     public boolean isFull() {
         return !(map.size() < MAX_ACCOUNTS);
     }

     /**
      * @param userId User to delete
      * @throws IOException if fails to find account
      */
     public void deleteUser(String userId) throws IOException {
         if (exists(userId)) {
             map.remove(userId);
             updateDbFile();
         } else {
             throw new IOException("Account doesn't exist");
         }
     }

     /**
      * Get an entry from the database
      *
      * @param userId the id of the entry to retrieve
      * @return an entry or null if user doesn't exist
      */
     public UserEntry get(String userId) {
         return map.get(userId);
     }

     /**
      * Save an entry in the database
      *
      * @param entry the entry to be saved in the database
      * @throws IOException if at #{@value MAX_ACCOUNTS} accounts
      */
     public void set(UserEntry entry) throws IOException {
         // if the max # of accounts isn't reached or just updating account that exists
         if (!isFull() || exists(entry.userId)) {
             map.put(entry.userId, entry);
             updateDbFile();
         } else {
             throw new IOException("Already at max accounts!" +
               " delete one before adding a new one");
         }
     }
 }
