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
    private String commands[] = new String[13];
    public UserInterface()         //initializes the commands array with all possible commands
    {
        commands[0] = "LIN";
        commands[1] = "LOU";
        commands[2] = "CHP";
        commands[3] = "ADU";
        commands[4] = "DEU";
        commands[5] = "DAL";
        commands[6] = "ADR";
        commands[7] = "DER";
        commands[8] = "EDR";
        commands[9] = "RER";
        commands[10] = "IMD";
        commands[11] = "EXD";
        commands[12] = "HLP";
    }

    public Command getNextCommand()
    {
        boolean commandFound = false;
        Console cnsl = null;
        try{
            cnsl = System.console();
            String pass = "";
            while(!commandFound && cnsl != null)
            {
 
                String fullString = cnsl.readLine();   //recieves the next command line
                String cmdString = fullString.substring(0,2); //recieves the first 3 characters of the command line (the command codes)
                for(int i = 0; i < commands.length - 1; i++)
                {
                    if(cmdString.equalsIgnoreCase(commands[i])) //case: command is found (case insensitive)
                    {
                        commandFound = true;
                        return commandParse(fullString);
                    }
                
                }
                sendResponse("Could not find command. Please try again or type 'help' for a list of commands\n"); //user inputs invalid command
            }
        }
        catch(Exception e)
        {
            System.out.println("Error getting next command.\n");
        }

        return "error"; //the code should never reach this if it is working properly: this can be vetted in the app loop
    }

    public void sendResponse(String response)
    {
        System.out.println(response);
    }

    public Command commandParse(String commandString)
    {
        String sub = commandString.substring(0,2);
        switch(sub)
        {
            case "LIN": //username + ";" + plain text pw
                String[] linargs = commandString.split(" ");   //*will add if args.length < 3
                String linuser = UserInterface.removeBrackets(linargs[1]);
                String linpw = UserInterface.removeBrackets(linargs[2]);
                String lininput = linuser + ";" + linpw;
                Login logIn = new Login(lininput);
                return logIn;

            case "LOU":
                Logout logOut = new Logout();
                return logOut;

            case "CHP": //username + ";" + plain text pw
                ChangePassword changePassword = null;
                String[] chpargs = commandString.split(" ");
                String oldpw = UserInterface.removeBrackets(chpargs[1]); // *need to verify current password (waiting to see how login recieves pw from hash)
            
                Console cnsl = null;
                String newPw = "";
                try{
                    cnsl = System.console();
                    boolean confirmed = false;
                    while(!confirmed)
                    {
                        System.out.println("Please enter your new password");
                        newPw = cnsl.readLine();
                        System.out.println("Please confirm your new password");
                        if(cnsl.readLine().equals(newPw))
                        {
                            confirmed = true;
                        }
                    }
                    String chpuser = null;//*User.getInstance().getID();
                    String chpinput = chpuser + ";" + newPw;
                    changePassword = new ChangePassword(chpinput);
                }
                catch(Exception e)
                {
                    System.out.println("Error parsing command through console.\n");
                }
                return changePassword;

            case "ADU":
                String aduinput = parseBasicCommand(commandString);
                AddUser addUser = new AddUser(aduinput);
                return addUser;

            case "DEU":
                String deuinput = parseBasicCommand(commandString);
                DeleteUser deleteUser = new DeleteUser(deuinput);
                return deleteUser;
            case "DAL":
                String dalinput = parseBasicCommand(commandString);
                DisplayAuditLog displayAuditLog = new DisplayAuditLog(dalinput);
                return displayAuditLog;

            case "ADR":
                String adrinput = parseBasicCommand(commandString);
                AddRecord addRecord = new AddRecord(adrinput);
                return deleteUser;
            
            case "DER":
                String derinput = parseBasicCommand(commandString);
                DeleteRecord deleteRecord = new DeleteRecord(derinput);
                return deleteRecord;

            case "EDR":
                String edrinput = UserInterface.parseBasicCommand(commandString);
                EditRecord editRecord = new EditRecord(edrinput);
                return editRecord;

            case "RER": 
                String rerinput = UserInterface.parseBasicCommand(commandString);
                ReadRecord readRecord = new ReadRecord(rerinput);
                return readRecord;

            case "IMD":
                String imdinput = UserInterface.parseBasicCommand(commandString);
                ImportDatabase importDatabase = new ImportDatabase(imdinput);
                return readRecord;
            
            case "EXD":
                String exdinput = parseBasicCommand(commandString);
                ExportDatabase exportDatabase = new ExportDatabase(exdinput);
                return exportDatabase;

            case "HLP":
                String hlpinput = parseBasicCommand(commandString);
                UserInterface.helpHandler(hlpinput);
        }

    }

    public static String removeBrackets(String s)   //function to remove brackets from original syntax
    {
        s.replace("<","");
        s.replace(">","");
        return s;
    }

    public static String parseBasicCommand(String cmdString)    //function to get the input string of the command easily
    {
        String[] cmdargs = cmdString.split(" ");
        return cmdargs[1];
    }

    public static void helpHandler(String hlpinput)
    {
        if(hlpinput == null)
        {
            System.out.println("LIN <userID> <password>\n");
            System.out.println("LOU\n");
            System.out.println("CHP <old password>\n");
            System.out.println("ADU <userID>\n");
            System.out.println("DEU <userID>\n");
            System.out.println("DAL <userID>\n");
            System.out.println("ADR <recordID> [<field1 = value1> <field2 = value2>...]\n"); //**NEED TO DEAL WITH THIS SYNTAX
            System.out.println("DER <recordID>\n");
            System.out.println("EDR <recordID> [<field1 = value1> <field2 = value2>...]\n");
            System.out.println("RER <recordID> [<fieldname> ...]\n");
            System.out.println("IMD <Input_File>\n");
            System.out.println("EXD <Output_File>\n");
            System.out.println("HLP [<command name>]\n"); 
        }
        switch(hlpinput)
        {
            case "LIN":
                System.out.println("LIN <userID> <password>\n");
                break;
            case "LOU":
                System.out.println("LOU\n");
                break;
            case "CHP":
                System.out.println("CHP <old password>\n");
                break;
            case "ADU":
                System.out.println("ADU <userID>\n");
                break;
            case "DEU":
                System.out.println("DEU <userID>\n");
                break;
            case "DAL":
                System.out.println("DAL <userID>\n");
                break;
            case "ADR":
                System.out.println("ADR <recordID> [<field1 = value1> <field2 = value2>...]\n"); //**NEED TO DEAL WITH THIS SYNTAX
                break;
            case "DER":
                System.out.println("DER <recordID>\n");
                break;
            case "EDR":
                System.out.println("EDR <recordID> [<field1 = value1> <field2 = value2>...]\n");
                break;
            case "RER":
                System.out.println("RER <recordID> [<fieldname> ...]\n");
                break;
            case "IMD":
                System.out.println("IMD <Input_File>\n");
                break;
            case "EXD":
                System.out.println("EXD <Output_File>\n");
                break;
            case "HLP":
                System.out.println("HLP [<command name>]\n"); 
                break;
        }
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
