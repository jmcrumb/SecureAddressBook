/*
 * Title:          com.AddressBook.Database
 * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
 * Last Modified:  4/22/20
 * Description:
 * */
package com.AddressBook.Database;

import com.AddressBook.AddressEntry;
import com.AddressBook.Encryption;
import com.AddressBook.UserVisibleException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

public class AddressDatabase {


    /**
     * The maximum number of records a single user can have
     */
    private static final int MAX_RECORDS = 256;
    /**
     * the folder where all record files are stored
     */
    private static final String FOLDER_NAME = ".addresses";
    /**
     * the prefix of the file is combined with the userId to create the file name
     * where the record is stored
     */
    private static final String FILE_PREFIX = "u_";
    /**
     * this is used to store the current userId to check if the user has changed
     */
    private String currentUserId;
    /**
     * this is used to store the records in memory
     */
    private Map<String, AddressEntry> map;

    /**
     * Read the user record file
     *
     * @param userId the user of the file to read
     * @return the encrypted contents of the user record
     * @throws IOException if it fails to read the file
     */
    private byte[] readFile(String userId) throws IOException {
        Path path = Paths.get(FOLDER_NAME, FILE_PREFIX + userId);
        if (!Files.exists(path)) {
            return null;
        } else {
            try {
                return Files.readAllBytes(path);
                // Files.readString(path, UTF_8);
            } catch (IOException e) {
                throw new IOException("Database exists and Failed to Read!", e);
            }
        }
    }

    private void deleteFile(String userId) throws IOException {
        Files.delete(Paths.get(FOLDER_NAME, FILE_PREFIX + userId));
    }

    /**
     * @param userId the user of the file to write
     * @param data   the encrypted data to write to the file
     * @throws IOException if if fails to write the file
     */
    private void writeFile(String userId, byte[] data) throws IOException {
        Path path = Paths.get(FOLDER_NAME, FILE_PREFIX + userId);
        try {
            if (Files.notExists(Paths.get(FOLDER_NAME)))
                Files.createDirectory(Paths.get(FOLDER_NAME));
            Files.write(path, data);
            // Files.writeString(path, data, UTF_8, CREATE, WRITE);
        } catch (IOException e) {
            throw new IOException("User Database Failed to Write!", e);
        }
    }

    /**
     * @param userId    the user of the file to access
     * @param decrypter function to decrypt the data
     * @return map of the data loaded from the file
     * @throws IOException if fails to read file or misformatted
     */
    private Map<String, AddressEntry> getMapFromFile(String userId, Encryption.Decrypter decrypter)
      throws IOException, GeneralSecurityException {
        byte[] encypted = readFile(userId);
        if (encypted == null) {
            return new HashMap<>();
        } else {
            String data = decrypter.decrypt(encypted);
            return getMapFromString(data);
        }

    }

    /**
     * get map from csv string
     *
     * @param data the database data in csv format
     * @return a map of the data passed in
     * @throws IOException if there are duplicates
     */
    private Map<String, AddressEntry> getMapFromString(String data) throws IOException {
        String[] lines = data.split("\n");
        Map<String, AddressEntry> m = new HashMap<>(lines.length);
        for (String line : lines) {
            AddressEntry ae = new AddressEntry(line);
            if (m.containsKey(ae.recordID)) {
                throw new IOException("Duplicate Records when loading Address Database!");
            }
            m.put(ae.recordID, ae);
        }
        return m;
    }

    /**
     * @param userId    the user of the file to access
     * @param encrypter function to encrypt data
     * @param m         the map of data to store in the file
     * @throws IOException if fails to write
     */
    private void setFileFromMap(String userId, Encryption.Encrypter encrypter, Map<String, AddressEntry> m)
      throws IOException, GeneralSecurityException {
        StringBuilder sb = new StringBuilder();
        m.forEach((k, v) -> {
            sb.append(v.toString()).append("\n");
        });
        writeFile(userId, encrypter.encrypt(sb.toString()));
    }

    /**
     * @param userId    user associate with the data to load
     * @param decrypter function to decrypt the data
     * @throws IOException if fails to read db file
     */
    private void instantiateMapIfNeeded(String userId, Encryption.Decrypter decrypter)
      throws IOException, GeneralSecurityException {
        if (map == null || currentUserId == null || !currentUserId.equals(userId)) {
            map = getMapFromFile(userId, decrypter);
            currentUserId = userId;
        }
    }

    /**
     * Get a record
     *
     * @param userId    the user whose record to get
     * @param recordId  the id of the record to get
     * @param decrypter function to decrypt records
     * @return the record with the passed id
     * @throws IOException if database fails to load from file
     */

    public AddressEntry get(String userId, String recordId, Encryption.Decrypter decrypter)
      throws IOException, GeneralSecurityException {

        instantiateMapIfNeeded(userId, decrypter);
        return map.get(recordId);
    }

    /**
     * @param userId    user associated with record to remove
     * @param recordId  the record to remove
     * @param decrypter function to decrypt data
     * @param encrypter function to encrypt data
     * @throws IOException              on failure to load or store database file
     * @throws GeneralSecurityException if encryption fails
     */
    public void delete(String userId, String recordId, Encryption.Decrypter decrypter, Encryption.Encrypter encrypter)
      throws IOException, GeneralSecurityException, UserVisibleException {
        instantiateMapIfNeeded(userId, decrypter);
        if (!map.containsKey(recordId)) {
            throw new UserVisibleException("RecordID Not Found");
        } else {
            map.remove(recordId);
            if (map.size() == 0) {
                deleteFile(userId);
            } else {
                setFileFromMap(userId, encrypter, map);
            }
        }
    }

    /**
     * Set the value of a record
     *
     * @param userId    the user whose record to set
     * @param entry     the entry to set
     * @param decrypter function to decrypt records
     * @param encrypter function to encrypt records
     * @throws IOException if database fails to load from file or save to file
     */
    public void set(String userId, AddressEntry entry, Encryption.Decrypter decrypter, Encryption.Encrypter encrypter)
      throws IOException, GeneralSecurityException, UserVisibleException {
        instantiateMapIfNeeded(userId, decrypter);
        if (!isFull(userId, decrypter) || map.containsKey(entry.recordID)) {
            map.put(entry.recordID, entry);
            setFileFromMap(userId, encrypter, map);
        } else {
            throw new UserVisibleException("Number of records exceeds maximum");
        }
    }

    /**
     * Check if a record exists
     *
     * @param userId    the user whose records to check
     * @param decrypter function to decrypt records
     * @return if the record exists
     * @throws IOException if database fails to load from file
     */
    public boolean exists(String userId, Encryption.Decrypter decrypter) throws IOException, GeneralSecurityException {
        instantiateMapIfNeeded(userId, decrypter);
        return map.containsKey(userId);
    }

    /**
     * Check if database is full
     *
     * @param userId    user of the file to access
     * @param decrypter function to decrypt data
     * @return if the database is full
     * @throws IOException if fails to read db file
     */
    public boolean isFull(String userId, Encryption.Decrypter decrypter) throws IOException, GeneralSecurityException {
        instantiateMapIfNeeded(userId, decrypter);
        return !(map.size() < MAX_RECORDS);
    }

    /**
     * Export database as CSV string
     *
     * @param userId    user of data to access
     * @param decrypter function to decrypt data
     * @return CSV string of user's database
     * @throws IOException if failed to read db file
     */
    public String exportDB(String userId, Encryption.Decrypter decrypter) throws IOException, GeneralSecurityException {
        byte[] raw = readFile(userId);
        return decrypter.decrypt(raw);
    }

    /**
     * Import CSV string into database
     *
     * @param userId    user of data to access
     * @param encrypter function to encrypt data
     * @param data      CSV data to add
     * @throws IOException if fails to read or write db file
     */
    public void importDB(String userId, Encryption.Decrypter decrypter, Encryption.Encrypter encrypter, String data)
      throws IOException, GeneralSecurityException, UserVisibleException {
        instantiateMapIfNeeded(userId, decrypter);
        Map<String, AddressEntry> m = getMapFromString(data);
        for (Map.Entry<String, AddressEntry> entry : m.entrySet()) {
            if (map.containsKey(entry.getKey())) {
                throw new UserVisibleException("Duplicate recordID");
            } else {
                map.put(entry.getKey(), entry.getValue());
                setFileFromMap(userId, encrypter, map);
            }
        }

    }

    public void reEncrypt(String userId, Encryption.Decrypter decrypterOld, Encryption.Encrypter encrypterNew) throws IOException, GeneralSecurityException {
        instantiateMapIfNeeded(userId, decrypterOld);
        setFileFromMap(userId, encrypterNew, map);
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