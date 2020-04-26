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
import java.util.Scanner;

import com.AddressBook.AddressEntry;
import com.AddressBook.Encryption;
import com.AddressBook.User;
import com.AddressBook.Command.Command;
import com.AddressBook.Command.CommandException;
import com.AddressBook.Database.AddressDatabase;

public class GetRecord extends Command{

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
    public String execute() throws CommandException, IOException {
        Scanner scanner = new Scanner(input.trim());
        String recordID = scanner.next();
        if(!validateInput(recordID))
            throw new CommandException("Invalid recordID");
        AddressEntry ae = 
            AddressDatabase.getInstance().get(User.getInstance().getUserId(), 
                                              recordID, (String s) -> Encryption.decrypt(s, s));
        
        String queryResult = parseFields(scanner, ae);
        return "OK\n" + queryResult;
    }

    /**
     * Parses the fields passed in via input.
     * 
     * @param scanner Scanner containing input
     * @param ae AddressEntry associated with the recordID in the input
     * @return A String representation of the record in question
     */
    private String parseFields(Scanner scanner, AddressEntry ae) {
        if(!scanner.hasNext())
            return String.format("SN,N,PEM,WEM,PPH,WPH,SA,CITY,STP,CTY,PC\n" + 
                                "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", 
                                ae.SN, ae.GN, ae.PEM, ae.WEM, ae.PPH, ae. WPH, ae.SA, ae.CITY, ae.STP, ae.CTY, ae.PC);
        String queryResult = "";
        String fields = "";
        while(scanner.hasNext()) {
            String field = scanner.next();
            fields += field + ",";
            queryResult += getField(field, ae) + ",";
        }
        return fields+"\n"+queryResult;
    }

    /**
     * Accesses a field of an AddressEntry ae.
     * 
     * @param field Field to be accessed
     * @param ae AddressEntry in question
     * @throws CommandException if the field name is not recognized
     * @return value of field in ae
     */
    private String getField(String field, AddressEntry ae) throws CommandException{
        String val = "";
        switch(field) {
            case "SN":
                val = ae.SN;
                break;
            case "GN":
                val = ae.GN;
                break;
            case "PEM":
                val = ae.PEM;
                break;
            case "WEM":
                val = ae.WEM;
                break;
            case "PPH":
                val = ae.PPH;
                break;
            case "WPH":
                val = ae.WPH;
                break;
            case "SA":
                val = ae.SA;
                break;
            case "CITY":
                val = ae.CITY;
                break;
            case "STP":
                val = ae.STP;
                break;
            case "CTY":
                val = ae.CTY;
                break;
            case "PC":
                val = ae.PC;
                break;
            default:
                throw new CommandException("One or more invalid record data fields");
        }
    }

 }
