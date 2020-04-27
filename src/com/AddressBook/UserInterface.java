 /*
  * Title:          com.AddressBook.System
  * Authors:        Miles Maloney, Caden Keese
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook;

 import com.AddressBook.Command.*;

 import java.io.Console;
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.Set;

 public class UserInterface {


     private final Set<String> commands = new HashSet<>(Arrays.asList("LIN", "LOU", "CHP", "ADU", "DEU", "DAL", "ADR", "DER", "EDR", "RER", "IMD", "EXD", "HLP"));

     public UserInterface() {
     }

     public Command getNextCommand() {
         try {
             Console cnsl = System.console();
             while (true) {
                 String fullString = cnsl.readLine();   //recieves the next command line
                 String cmdString = fullString.substring(0, 2); //recieves the first 3 characters of the command line (the command codes)

                 if (commands.contains(cmdString.toUpperCase())) {
                     Command c = commandParse(fullString);
                     if (c != null) {
                         return c;
                     }
                 }
                 sendResponse("Could not find command. Please try again or type 'help' for a list of commands\n"); //user inputs invalid command

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
         String sub = commandString.substring(0, 3);
         switch (sub) {
             case "LIN": //username + ";" + plain text pw

                 return new Login(commandString.substring(3));

             case "LOU":
                 return new Logout();

             case "CHP": //username + ";" + plain text pw
                 return new ChangePassword(commandString.substring(3));

             case "ADU":
                 return new AddUser(commandString.substring(3));

             case "DEU":
                 return new DeleteUser(commandString.substring(3));
             case "DAL":
                 return new DisplayAuditLog(commandString.substring(3));

             case "ADR":
                 return new AddRecord(commandString.substring(3));

             case "DER":
                 return new DeleteRecord(commandString.substring(3));

             case "EDR":
                 return new EditRecord(commandString.substring(3));

             case "RER":
                 return new GetRecord(commandString.substring(3));

             case "IMD":
                 return new ImportDatabase(commandString.substring(3));

             case "EXD":
                 return new ExportDatabase(commandString.substring(3));

             case "HLP":
                 String hlpinput = parseBasicCommand(commandString);
                 UserInterface.helpHandler(hlpinput);
             default:
                 return null;
         }

     }


     public static String parseBasicCommand(String cmdString)    //function to get the input string of the command easily
     {
         String[] cmdargs = cmdString.split(" ");
         return cmdargs[1];
     }

     public static void helpHandler(String hlpinput) {
         if (hlpinput == null) {
             System.out.println("Login: LIN <userID> <password>\n");
             System.out.println("Logout: LOU\n");
             System.out.println("Change Password: CHP <old password>\n");
             System.out.println("Add User: ADU <userID>\n");
             System.out.println("Delete User: DEU <userID>\n");
             System.out.println("Display Audit Log: DAL <userID>\n");
             System.out.println("Add Record: ADR <recordID> [<field1 = value1> <field2 = value2>...]\n"); //**NEED TO DEAL WITH THIS SYNTAX
             System.out.println("Delete Record: DER <recordID>\n");
             System.out.println("Edit Record: EDR <recordID> [<field1 = value1> <field2 = value2>...]\n");
             System.out.println("Read Record: RER <recordID> [<fieldname> ...]\n");
             System.out.println("Import Database: IMD <Input_File>\n");
             System.out.println("Export Database: EXD <Output_File>\n");
             System.out.println("Help: HLP [<command name>]\n");
         } else {
             switch (hlpinput) {
                 case "LIN":
                     System.out.println("Login: LIN <userID> <password>\n");
                     break;
                 case "LOU":
                     System.out.println("Logout: LOU\n");
                     break;
                 case "CHP":
                     System.out.println("Change password: CHP <old password>\n");
                     break;
                 case "ADU":
                     System.out.println("Add User: ADU <userID>\n");
                     break;
                 case "DEU":
                     System.out.println("Delete User: DEU <userID>\n");
                     break;
                 case "DAL":
                     System.out.println("Dsiplay Audit Log: DAL <userID>\n");
                     break;
                 case "ADR":
                     System.out.println("Add Record: ADR <recordID> [<field1 = value1> <field2 = value2>...]\n"); //**NEED TO DEAL WITH THIS SYNTAX
                     break;
                 case "DER":
                     System.out.println("Delete Record: DER <recordID>\n");
                     break;
                 case "EDR":
                     System.out.println("Edit Record: EDR <recordID> [<field1 = value1> <field2 = value2>...]\n");
                     break;
                 case "RER":
                     System.out.println("Read Record: RER <recordID> [<fieldname> ...]\n");
                     break;
                 case "IMD":
                     System.out.println("Import Database: IMD <Input_File>\n");
                     break;
                 case "EXD":
                     System.out.println("Export Database: EXD <Output_File>\n");
                     break;
                 case "HLP":
                     System.out.println("Help: HLP [<command name>]\n");
                     break;
                 default:
                     System.out.println("Command not found.\n");
             }
         }
     }
 }

