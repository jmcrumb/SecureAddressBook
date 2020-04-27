/*
 * Title:          com.AddressBook.Command
 * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
 * Last Modified:  4/25/20
 * Description:
 * This operation is used to delete a record from a database. Several conditions must be
 * met for the operation to succeed:
 * 1. A non-admin user must be currently logged-in.
 * 2. The command must include a valid recordID for a record that exists in the
 * database for the active user.
 * If these conditions are satisfied, the operation succeeds and the record in the database
 * for the currently active user that matches the recordID is deleted. Otherwise, there is
 * no change to the system.
 *
 * Command line: DER <recordID>
 * */
package com.AddressBook.Command;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.AddressBook.Encryption;
import com.AddressBook.User;
import com.AddressBook.Database.AddressDatabase;

public class DeleteRecord extends Command {

    private String recordID;

    @SuppressWarnings("FieldCanBeLocal")
    private final int MAX_RECORD_FIELD_SIZE = 64;

    /**
     * Creates an instance of the DeleteRecord Command.
     *
     * @param input Input from the command line.
     */
    public DeleteRecord(String input) {
        super(input, 1, null, null);
        recordID = null;
    }

    /**
     * This operation is used to delete a record from a database. Several conditions must be
     * met for the operation to succeed: <br>
     * 1. A non-admin user must be currently logged-in.<br>
     * 2. The command must include a valid recordID for a record that exists in the
     * database for the active user.<p>
     * If these conditions are satisfied, the operation succeeds and the record in the database
     * for the currently active user that matches the recordID is deleted. Otherwise, there is
     * no change to the system. <p>
     * <p>
     * Command line: DER recordID
     */
    @Override
    public String execute() throws CommandException, IOException, GeneralSecurityException {
        parseID(input);
        delete();
        return "OK";
    }

    /**
     * Validates and parses the record ID.
     *
     * @param arg record ID field
     * @throws CommandException if field does not match expectations for the record ID
     */
    private void parseID(String arg) throws CommandException {
        if (!validateInput(input, MAX_RECORD_FIELD_SIZE))
            throw new CommandException("Invalid recordID");
        recordID = arg.trim();
    }

    private void delete() throws IOException, GeneralSecurityException {
        User usr = User.getInstance();
        AddressDatabase.getInstance().delete(usr.getUserId(), recordID,
          usr::decrypt, usr::encrypt);
    }
}
