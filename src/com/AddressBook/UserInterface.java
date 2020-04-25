 /*
  * Title:          com.AddressBook.System
  * Authors:        Miles Maloney, Caden Keese
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook;

 import com.AddressBook.Command.Command;

 import java.util.Scanner;
 import java.util.regex.Pattern;

 public class UserInterface {
     private final Parser parser;
     private Scanner sc;

     public UserInterface(Parser parser) {
         this.parser = parser;
     }

     void sendResponse(String response) {
         System.out.println(response);
     }

     public Command getNextCommand() {
         Command command = null;
         while (command == null) {
             String raw = System.console().readLine();
             if (!validateTotalCharSet(raw)) ;
             String cmdStr = raw.substring(0, 3);
             switch (cmdStr) {
                 case "LIN":
                     return command = Login(raw);
                     break;
                 case "LOU":
                     return command = Logout(raw);
                     break;
                 case "CHP":
                     return command = ChangePassword(raw);
                     break;
                 case "ADU":
                     return command = AddUser(raw);
                     break;
                 case "DEU":
                     return command = DeleteUser(raw);
                     break;
                 case "DAL":
                     return command = DisplayAuditLog(raw);
                     break;
                 case "ADR":
                     return command = AddRecord(raw);
                     break;
                 case "DER":
                     return command = DeleteRecord(raw);
                     break;
                 case "EDR":
                     return command = EditRecord(raw);
                     break;
                 case "RER":
                     return command = ReadRecord(raw);
                     break;
                 case "IMD":
                     return command = ImportDatabase(raw);
                     break;
                 case "EXD":
                     return command = ExportDatabase(raw);
                     break;
                 case "HLP":
                     return command = HLP(raw);
                     break;
                 default:
                     break;
             }
         }
         return null;
     }

//     private boolean authorizeForChangePassword(){
////         System.console().printf("Current Password?\n");
////         char[] password = System.console().readPassword();
////
//
//     }


     private boolean validateTotalCharSet(String input) {
         return Pattern.matches("[0-9A-Za-z\\.@\\-\\(\\)]+", input);
     }


     //  * HLP [<command name>]
     private boolean HLP(String s) {
         return Pattern.matches(
           "HLP (LIN|LOU|CHP|ADU|DEU|DAL|ADR|DER|EDR|RER|IMD|EXD)?"
           , s);
     }

 }

 /*
  * Syntax
  * LIN <userID> <password>
  * LOU
  * CHP <old password>
  * ADU <userID>
  * DEU <userID>
  * DAL [<userID>]
  * ADR <recordID> [<field1=value1> <field2=value2> ...]
  * DER <recordID>
  * EDR <recordID> <field1=value1> [<field2=value2> ...]
  * RER [<recordID>] [<fieldname> ...]
  * IMD <Input_File>
  * EXD <Output_file>
  * HLP [<command name>]
  * */
