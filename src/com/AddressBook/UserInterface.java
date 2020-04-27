 /*
  * Title:          com.AddressBook.System
  * Authors:        Miles Maloney, Caden Keese
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook;

 import com.AddressBook.Command.Command;


import java.io.Console;

 public class UserInterface {
{
    private boolean isLoggedIn;
    private String commands[] = new String[13];
    public UserInterface()         //initializes the commands array with all possible commands
    {
        this.isLoggedIn = false;
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
                Console lincnsl = null;
                String[] linargs = commandString.split(" ");   //*will add if args.length < 3
                String linuser = linargs[1];
                String linpw = linargs[2];
                UserDatabase ud = UserDatabase.getInstance();
                if(!ud.get(linuser).getLoggedIn())
                {
                    try
                    {
                        lincnsl = System.console();
                        boolean cnfrm = false;
                        while(!cnfrm)
                        {
                            System.out.println("Please confirm your desired password:\n");
                            if(lincnsl.readLine().equals(linpw))
                            {
                                cnfrm = true;
                            }
                        }
                    }
                    catch(Exception e)
                    {
                        System.out.println("Error parsing command through console\n");
                    }
                }
                String lininput = linuser + ";" + linpw;
                Login logIn = new Login(lininput);
                return logIn;

            case "LOU":
                Logout logOut = new Logout();
                return logOut;

            case "CHP": //username + ";" + plain text pw
                ChangePassword changePassword = null;
                String[] chpargs = commandString.split(" ");
                String oldpw = chpargs[1]; // *need to verify current password (waiting to see how login recieves pw from hash)
            
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
                break;
            case default: 
                System.out.println("Command not found.\n");
        }

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
        }
        switch(hlpinput)
        {
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
            case default: 
                System.out.println("Command not found.\n");
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
