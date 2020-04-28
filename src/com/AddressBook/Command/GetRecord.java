/*
 * Title:          com.AddressBook.Command
 * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
 * Last Modified:  4/25/20
 * Description:
 * This operation is used to display a record in the database of the currently active user. If
 * no recordID is specified then every record in the database for the currently active user
 * will be displayed. Several conditions must be met for the operation to succeed:
 * 1. A non-admin user must be currently logged-in.
 * 2. If the command includes a recordID, the recordID must be valid and identify a
 * record that exists in the active user’s database.
 * 3. All specified fieldnames must be valid.
 * If these conditions are satisfied, the operation succeeds and the record in the database
 * for the currently active user that matches the recordID is displayed, or else the entire
 * database is displayed if there is no input recordID. The output uses CSV format.
 * There is no change to the system whether the operation succeeds or not.
 * */

package com.AddressBook.Command;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Scanner;

import com.AddressBook.AddressEntry;
import com.AddressBook.User;
import com.AddressBook.Command.Command;
import com.AddressBook.Database.AddressDatabase;

public class GetRecord extends Command {

    /**
     * Construts a GetRecord object.
     *
     * @param input input from the command line
     */
    public GetRecord(String input) {
        super(input, 1, null, null);
    }

    /**
     * This operation is used to display a record in the database of the currently active user. If
     * no recordID is specified then every record in the database for the currently active user
     * will be displayed. Several conditions must be met for the operation to succeed:<br>
     * 1. A non-admin user must be currently logged-in.<br>
     * 2. If the command includes a recordID, the recordID must be valid and identify a
     * record that exists in the active user’s database.
     * 3. All specified fieldnames must be valid.<p>
     * If these conditions are satisfied, the operation succeeds and the record in the database
     * for the currently active user that matches the recordID is displayed, or else the entire
     * database is displayed if there is no input recordID. The output uses CSV format.
     * There is no change to the system whether the operation succeeds or not.
     */
    @Override
    public String execute() throws CommandException, IOException, GeneralSecurityException {
        if (input.trim().equals("")) return "Invalid recordID";
        Scanner scanner = new Scanner(input.trim());
        String recordID = scanner.next();
        if (!validateInput(recordID))
            throw new CommandException("Invalid recordID");
        User usr = User.getInstance();
        AddressEntry ae =
          AddressDatabase.getInstance().get(usr.getUserId(),
            recordID, usr::decrypt);

        if (ae == null) {
            return "RecordID not found";
        }
        String queryResult = parseFields(scanner, ae);
        return "OK\n" + queryResult;
    }

    private String hlpme(String s) {
        if (s == null) return "none";
        return s;
    }

    /**
     * Parses the fields passed in via input.
     *
     * @param scanner Scanner containing input
     * @param ae      AddressEntry associated with the recordID in the input
     * @return A String representation of the record in question
     */
    private String parseFields(Scanner scanner, AddressEntry ae) throws CommandException {
        if (!scanner.hasNext())
            return String.format("SN,GN,PEM,WEM,PPH,WPH,SA,CITY,STP,CTY,PC\n" +
                "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                hlpme(ae.SN), hlpme(ae.GN), hlpme(ae.PEM), hlpme(ae.WEM), hlpme(ae.PPH), hlpme(ae.WPH), hlpme(ae.SA), hlpme(ae.CITY), hlpme(ae.STP), hlpme(ae.CTY), hlpme(ae.PC));
        StringBuilder queryResult = new StringBuilder();
        StringBuilder fields = new StringBuilder();
        while (scanner.hasNext()) {
            String field = scanner.next();
            fields.append(field).append(",");
            queryResult.append(getField(field, ae)).append(",");
        }
        return fields + "\n" + queryResult;
    }

    /**
     * Accesses a field of an AddressEntry ae.
     *
     * @param field Field to be accessed
     * @param ae    AddressEntry in question
     * @return value of field in ae
     * @throws CommandException if the field name is not recognized
     */
    private String getField(String field, AddressEntry ae) throws CommandException {
        String val = "";
        switch (field) {
            case "SN":
                val = hlpme(ae.SN);
                break;
            case "GN":
                val = hlpme(ae.GN);
                break;
            case "PEM":
                val = hlpme(ae.PEM);
                break;
            case "WEM":
                val = hlpme(ae.WEM);
                break;
            case "PPH":
                val = hlpme(ae.PPH);
                break;
            case "WPH":
                val = hlpme(ae.WPH);
                break;
            case "SA":
                val = hlpme(ae.SA);
                break;
            case "CITY":
                val = hlpme(ae.CITY);
                break;
            case "STP":
                val = hlpme(ae.STP);
                break;
            case "CTY":
                val = hlpme(ae.CTY);
                break;
            case "PC":
                val = hlpme(ae.PC);
                break;
            default:
                throw new CommandException("One or more invalid record data fields");
        }
        return val;
    }

}
