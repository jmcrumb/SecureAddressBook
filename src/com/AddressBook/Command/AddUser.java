 /*
  * Title:          com.AddressBook.Command.AddUser
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/24/20
  * Description: Adds a new user to the User Database.  Requires Administrative privilege to use.
  * */
 package com.AddressBook.Command;

 import com.AddressBook.User;
 import com.AddressBook.UserEntry.UserEntry;

 public class AddUser extends Command{
    private String userID;
    //Maximum size of userID
    private final int MAX_SIZE = 16;

    /**
     * Creates an AddUser Command object.
     * 
     * @param input A String of input in the form "<userID>""
     */
    public AddUser(String input) {
        super(input, 2, "AU", null);
        userID = input.trim();
    }

    /**
     * Adds a new user to the User Database.  Requires Administrative privilege.
     * 
     * @return A status message reflecting the state resulting from this action
     * @throws CommandException
     */
    @Override
    public String execute() throws CommandException {
        if(!validateInput())
            return "Invalid userID";
        else if(UserDatabase.get(userID) != null)
            return "Account already exists";
        else {
            //Creates a new user associated with the userID
            UserDatabase.set(new UserEntry(userID));
            return "OK";
        }
    }

    /**
     * Helper method for validating input conforms with requirements 
     * outlined in design document:
     * i) Is no larger than the Maximum size of input for a userID 
     * ii) Is alphanumeric
     * 
     * @return if the input is valid
     */
    private boolean validateInput() {
        if(userID == null || userID.length() > MAX_SIZE)
            return false;
        return userID.matches("^[a-zA-Z0-9]+$");
    }
 }
