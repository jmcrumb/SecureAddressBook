package com.AddressBook.Command;

public class Help extends Command{

    public Help(String input) {
        super(input, 0, null, null);
    }

    @Override
    public String execute() throws CommandException {
        return helpHandler(input);
    }

    private String helpHandler(String hlpinput) {
        String s = "";
        switch (hlpinput) {
            case "LIN":
                s="Login: LIN <userID> <password>\n";
                break;
            case "LOU":
                s="Logout: LOU\n";
                break;
            case "CHP":
                s="Change password: CHP <old password>\n";
                break;
            case "ADU":
                s="Add User: ADU <userID>\n";
                break;
            case "DEU":
                s="Delete User: DEU <userID>\n";
                break;
            case "DAL":
                s="Dsiplay Audit Log: DAL <userID>\n";
                break;
            case "ADR":
                s="Add Record: ADR <recordID> [<field1 = value1> <field2 = value2>...]\n"; //**NEED TO DEAL WITH THIS SYNTAX
                break;
            case "DER":
                s="Delete Record: DER <recordID>\n";
                break;
            case "EDR":
                s="Edit Record: EDR <recordID> [<field1 = value1> <field2 = value2>...]\n";
                break;
            case "RER":
                s="Read Record: RER <recordID> [<fieldname> ...]\n";
                break;
            case "IMD":
                s="Import Database: IMD <Input_File>\n";
                break;
            case "EXD":
                s="Export Database: EXD <Output_File>\n";
                break;
            case "HLP":
                s="Help: HLP [<command name>]\n";
                break;
            default:
                s= "Login: LIN <userID> <password>\n"
                +"Logout: LOU\n"
                +"Change Password: CHP <old password>\n"
                +"Add User: ADU <userID>\n"
                +"Delete User: DEU <userID>\n"
                +"Display Audit Log: DAL <userID>\n"
                +"Add Record: ADR <recordID> [<field1 = value1> <field2 = value2>...]\n"
                +"Delete Record: DER <recordID>\n"
                +"Edit Record: EDR <recordID> [<field1 = value1> <field2 = value2>...]\n"
                +"Read Record: RER <recordID> [<fieldname> ...]\n"
                +"Import Database: IMD <Input_File>\n"
                +"Export Database: EXD <Output_File>\n"
                +"Exit Application: EXT\n"
                +"Help: HLP [<command name>]\n";
        }
        return s;
    }

}