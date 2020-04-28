/*
 * Title:          com.AddressBook.Command
 * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
 * Last Modified:  4/24/20
 * Description:
 * This operation is used to change field values in a record in the currently active user’s
 * database. Several conditions must be met for the operation to succeed:
 * 1. A non-admin user must be currently logged-in.
 * 2. The command must include a valid recordID for a record that exists in the database for
 * the active user.
 * 3. All of the new fields and field values must be valid and in a valid size and format.
 * If these conditions are satisfied, the operation succeeds and the record in the database
 * for the currently active user that matches the recordID is deleted. Otherwise, there is
 * no change to the system.
 *
 * Command line call: EDR <recordID> <field1=value1> [<field2=value2> ...]
 * */

package com.AddressBook.Command;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Scanner;

import com.AddressBook.Command.AddRecord;
import com.AddressBook.Command.CommandException;
import com.AddressBook.AddressEntry;
import com.AddressBook.Database.AddressDatabase;
import com.AddressBook.User;
import com.AddressBook.Encryption;

public class EditRecord extends AddRecord {

    /**
     * Constructs an EditRecord object.
     *
     * @param input Input from the command line
     */
    public EditRecord(String input) {
        super(input);
    }

    /**
     * This operation is used to change field values in a record in the currently active user’s
     * database. Several conditions must be met for the operation to succeed:
     * 1. A non-admin user must be currently logged-in. <br>
     * 2. The command must include a valid recordID for a record that exists in the database for
     * the active user. <br>
     * 3. All of the new fields and field values must be valid and in a valid size and format.
     * If these conditions are satisfied, the operation succeeds and the record in the database
     * for the currently active user that matches the recordID is deleted. Otherwise, there is
     * no change to the system. <p>
     * <p>
     * Command line call: EDR recordID field1=value1 [field2=value2 ...]
     *
     * @throws CommandException if expectations are violated.  A specific error message will be
     *                          associated
     */
    @Override
    public String execute() throws CommandException, IOException, GeneralSecurityException {
        parseInput();
        writeToDatabase();
        return "OK";
    }

    @Override
    protected void parseInput() throws CommandException, IOException, GeneralSecurityException {
        Scanner scanner = new Scanner(input);
        parseID(scanner.next());
        readInAddressEntry();
        while (scanner.hasNext()) {
            parseArg(scanner.next());
        }
        scanner.close();
    }

    /**
     * Reads in an AddressEntry into this class so that the fields may be
     * edited.
     */
    private void readInAddressEntry() throws IOException, GeneralSecurityException {
        User usr = User.getInstance();
        AddressEntry ae =
          AddressDatabase.getInstance().get(usr.getUserId(), recordID,
            usr::decrypt);
        if(ae == null){
            throw new IOException("RecordID not found");
        }
        SN = ae.SN;
        GN = ae.GN;
        PEM = ae.PEM;
        WEM = ae.WEM;
        PPH = ae.PPH;
        WPH = ae.WPH;
        SA = ae.SA;
        CITY = ae.CITY;
        STP = ae.STP;
        CTY = ae.CTY;
        PC = ae.PC;
    }
}
