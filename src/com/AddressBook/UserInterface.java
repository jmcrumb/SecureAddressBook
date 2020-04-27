 /*
  * Title:          com.AddressBook.System
  * Authors:        Miles Maloney, Caden Keese
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook;

 import com.AddressBook.Command.*;

 import java.io.Console;

 public class UserInterface {

     public UserInterface() {
     }

     public Command getNextCommand() {
         try {
             Console cnsl = System.console();
             while (true) {
                 String fullString = cnsl.readLine();   //recieves the next command line
                //  String cmdString = fullString.substring(0, 3); //recieves the first 3 characters of the command line (the command codes)
                 return commandParse(fullString);
             }
         } catch (Exception e) {
             throw new RuntimeException("Error getting next command.");
         }
     }

     public void sendResponse(String response) {
         System.out.println(response);
     }

     public Command commandParse(String commandString) {
//         String sub = "LIN";
         int divider = Integer.min(3, commandString.length());
         String sub = commandString.substring(0, divider);
         switch (sub) {
             case "LIN": //username + ";" + plain text pw

                 return new Login(commandString.substring(divider).trim());

             case "LOU":
                 return new Logout();

             case "CHP": //username + ";" + plain text pw
                 return new ChangePassword(commandString.substring(divider).trim());

             case "ADU":
                 return new AddUser(commandString.substring(divider).trim());

             case "DEU":
                 return new DeleteUser(commandString.substring(divider).trim());
             case "DAL":
                 return new DisplayAuditLog(commandString.substring(3));
             case "ADR":
                 return new AddRecord(commandString.substring(divider).trim());

             case "DER":
                 return new DeleteRecord(commandString.substring(divider).trim());

             case "EDR":
                 return new EditRecord(commandString.substring(divider).trim());

             case "RER":
                 return new GetRecord(commandString.substring(divider).trim());

             case "IMD":
                 return new ImportDatabase(commandString.substring(divider).trim());

             case "EXD":
                 return new ExportDatabase(commandString.substring(divider).trim());

             case "HLP":
                 try {
                    return new Help(commandString.substring(divider).trim());
                 } catch(IndexOutOfBoundsException e) {
                    return new Help("");
                 }
                 
            case "EXT":
                return new Exit(commandString.substring(divider).toLowerCase());
             default:
                return null;
         }

     }


     

     
 }

