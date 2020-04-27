 /*
  * Title:          com.AddressBook.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * This operation is used to add a new record to a database. Several conditions must be
  * met for the operation to succeed:
  * 1. A non-admin user must be currently logged-in.
  * 2. The record data provided must include a recordID.
  * 3. All record field identifiers are valid.
  * 4. All record fields must satisfy formatting requirements.
  * 5. There must not already be a record in the database with that recordID.
  * If these conditions are satisfied, the operation succeeds and a record with the input
  * data is created in the database for the currently active user. Otherwise, there is no
  * change to the system.
  * */
 package com.AddressBook.Command;

 import java.io.IOException;
import java.util.Scanner;

import com.AddressBook.User;
import com.AddressBook.Command.CommandException;
import com.AddressBook.AddressEntry;
import com.AddressBook.Database.AddressDatabase;
import com.AddressBook.Encryption;

 public class AddRecord extends Command{

    protected String recordID;
    protected String SN;
    protected String GN;
    protected String PEM;
    protected String WEM;
    protected String PPH;
    protected String WPH;
    protected String SA;
    protected String CITY;
    protected String STP;
    protected String CTY;
    protected String PC;

    private final int MAX_RECORD_FIELD_SIZE = 64;

    public AddRecord(String input) {
        super(input, 1, null, null);
        recordID = null;
        SN = null;
        GN = null;
        PEM = null;
        WEM = null;
        PPH = null;
        WPH = null;
        SA = null;
        CITY = null;
        STP = null;
        CTY = null;
        PC = null;
    }

    /**
     * This operation is used to add a new record to a database. Several conditions must be
     * met for the operation to succeed:<br>
     * 1. A non-admin user must be currently logged-in.<br>
     * 2. The record data provided must include a recordID.<br>
     * 3. All record field identifiers are valid.<br>
     * 4. All record fields must satisfy formatting requirements.<br>
     * 5. There must not already be a record in the database with that recordID.<p>
     * If these conditions are satisfied, the operation succeeds and a record with the input
     * data is created in the database for the currently active user. Otherwise, there is no
     * change to the system.
     */
    @Override
    public String execute() throws CommandException, IOException {
        parseInput();
        writeToDatabase();
        return "OK";
    }

    /**
     * Parses input for the command.
     * 
     * @throws CommandException if expectations are violated
     */
    protected void parseInput() throws CommandException {
        Scanner scanner = new Scanner(input);
        parseID(scanner.next());
        while(scanner.hasNext()) {
            parseArg(scanner.next());
        }
        scanner.close();    
    }

    /**
     * Validates and parses the record ID. 
     * 
     * @param arg record ID field
     * @throws CommandException if field does not match expectations for the record ID
     * @throws IOException if there is an issue interacting with the database
     */
    protected void parseID(String arg) throws CommandException, IOException {
        if(!validateInput(input, MAX_RECORD_FIELD_SIZE))
            throw new CommandException("Invalid recordID");
        recordID = arg.trim();
        AddressEntry ae = AddressDatabase.getInstance().get(User.getInstance().getUserId(), 
                          recordID, (String s) -> Encryption.decrypt(s));
        if(ae != null)
            throw new CommandException("Duplicate recordID");
    }

    /**
     * Parses a single argument of the form "field=value".
     * 
     * @param arg argument to be parsed
     * @throws CommandException if expectations are violated.
     */
    protected void parseArg(String arg) throws CommandException{
        int eqIndex = arg.indexOf('=');
        if(eqIndex == -1)
            throw new CommandException("One or more invalid record data fields");
        String val = arg.substring(eqIndex + 1).trim();
        if(!validateInput(val, MAX_RECORD_FIELD_SIZE))
            throw new CommandException("One or more invalid record data fields");
        switch(arg.substring(0, eqIndex)) {
            case "SN":
                SN = val;
                break;
            case "GN":
                GN = val;
                break;
            case "PEM":
                PEM = val;
                break;
            case "WEM":
                WEM = val;
                break;
            case "PPH":
                PPH = val;
                break;
            case "WPH":
                WPH = val;
                break;
            case "SA":
                SA = val;
                break;
            case "CITY":
                CITY = val;
                break;
            case "STP":
                STP = val;
                break;
            case "CTY":
                CTY = val;
                break;
            case "PC":
                PC = val;
                break;
            default:
                throw new CommandException("One or more invalid record data fields");
        }
    }

    /**
     * Writes the fields into an AddressEntry to be passed to the Address Database for handling.
     */
    protected void writeToDatabase() throws IOException{
        AddressEntry ae = new AddressEntry(SN, GN, PEM, WEM, PPH, WPH, SA, CITY, STP, CTY, PC);
        AddressDatabase.getInstance().set(User.getInstance().getUserId(), ae,
                                        (String s) -> Encryption.decrypt(s),
                                        (String s) -> Encryption.encrypt(s));
    }
 }
