 /*
  * Title:          com.AddressBook.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook.Command;

 import java.util.Scanner;

import com.AddressBook.User;
import com.AddressBook.Command.CommandException;
import com.AddressBook.AddressEntry;
import com.AddressBook.Database.AddressDatabase;

 public class AddRecord extends Command{

    private String recordID;
    private String SN;
    private String GN;
    private String PEM;
    private String WEM;
    private String PPH;
    private String WPH;
    private String SA;
    private String CITY;
    private String STP;
    private String CTY;
    private String PC;

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

    public String execute() throws CommandException {
        parseInput();
        writeToDatabase();
    }

    private String parseInput() throws CommandException {
        Scanner scanner = new Scanner(input);
        parseID(scanner.next());
        while(scanner.hasNext()) {
            parseArg(scanner.next());
        }
        scanner.close();    
    }

    private void parseID(String arg) throws CommandException{
        if(!validateInput(input))
            throw new CommandException("Invalid recordID");
        recordID = arg.trim();
    }

    private void parseArg(String arg) throws CommandException{
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
                break;
        }
    }

    private void writeToDatabase() {
        AddressEntry ae = new AddressEntry(SN, GN, PEM, WEM, PPH, WPH, SA, CITY, STP, CTY, PC);
        AddressDatabase.getInstance.set(User.getInstance().getUserID(), ae, (String s) -> decrypt(s),
                                        (String s) -> encrypt(s));
    }
 }
