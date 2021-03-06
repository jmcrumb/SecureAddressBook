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
 import java.security.GeneralSecurityException;
 import java.util.Scanner;

 import com.AddressBook.User;
 import com.AddressBook.AddressEntry;
 import com.AddressBook.Database.AddressDatabase;

 public class AddRecord extends Command {

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
      * This operation is used to add a new record to a database. Several conditions
      * must be met for the operation to succeed:<br>
      * 1. A non-admin user must be currently logged-in.<br>
      * 2. The record data provided must include a recordID.<br>
      * 3. All record field identifiers are valid.<br>
      * 4. All record fields must satisfy formatting requirements.<br>
      * 5. There must not already be a record in the database with that recordID.
      * <p>
      * If these conditions are satisfied, the operation succeeds and a record with
      * the input data is created in the database for the currently active user.
      * Otherwise, there is no change to the system.
      */
     @Override
     public String execute() throws CommandException, IOException, GeneralSecurityException {
         if (input.trim().equals("")) return "No recordID";
         parseInput();
         writeToDatabase();
         return "OK";
     }

     /**
      * Parses input for the command.
      *
      * @throws CommandException if expectations are violated
      */
     protected void parseInput() throws CommandException, IOException, GeneralSecurityException {
         Scanner scanner = new Scanner(input);
         parseID(scanner.next(), true);
         while (scanner.hasNext()) {
             parseArg(scanner.next());
         }
         scanner.close();
     }

     /**
      * Validates and parses the record ID.
      *
      * @param arg record ID field
      * @param checkDuplicate if the method should check for preexisting record
      * @throws CommandException if field does not match expectations for the record
      *                          ID
      * @throws IOException      if there is an issue interacting with the database
      */
     protected void parseID(String arg, boolean checkDuplicate) throws CommandException, IOException, GeneralSecurityException {
         if (!validateInput(arg, MAX_RECORD_FIELD_SIZE))
             throw new CommandException("Invalid recordID");
         recordID = arg.trim();
         User usr = User.getInstance();
         AddressEntry ae = AddressDatabase.getInstance().get(usr.getUserId(),
           recordID, usr::decrypt);
         if (checkDuplicate && ae != null)
             throw new CommandException("Duplicate recordID");
     }

     /**
      * Parses a single argument of the form "field=value".
      *
      * @param arg argument to be parsed
      * @throws CommandException if expectations are violated.
      */
     protected void parseArg(String arg) throws CommandException {
         int eqIndex = arg.indexOf('=');
         if (eqIndex == -1)
             throw new CommandException("One or more invalid record data fields");
         String val = arg.substring(eqIndex + 1).trim();
         if (!validateInput(val, MAX_RECORD_FIELD_SIZE))
             throw new CommandException("One or more invalid record data fields");
         switch (arg.substring(0, eqIndex)) {
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
     protected void writeToDatabase() throws IOException, GeneralSecurityException {
//        AddressEntry ae = new AddressEntry()
         AddressEntry ae = new AddressEntry(recordID, SN, GN, PEM, WEM, PPH, WPH, SA, CITY, STP, CTY, PC);
         User usr = User.getInstance();
         AddressDatabase.getInstance().set(User.getInstance().getUserId(), ae, usr::decrypt, usr::encrypt);
     }

     /**
     * Helper method for validating input conforms with requirements 
     * outlined in design document:
     * i) Is no larger than the Maximum size of input for an input 
     * ii) Is alphanumeric
     * iii) Is nonempty
     * 
     * @param input Input to be validated
     * @param maxSize maximum size of input
     * @return if the input is valid
     */
    @Override
    protected boolean validateInput(String input, int maxSize) {
        if(input == null || input.length() > maxSize)
            return false;
        return input.matches("[A-Za-z@.0-9 ]+");
    }
 }
