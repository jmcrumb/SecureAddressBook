/*
 * Title:          com.AddressBook.Command.AddUser
 * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
 * Last Modified:  4/24/20
 * Description: Adds a new user to the User Database.  Requires Administrative privilege to use.
 * */
package com.AddressBook.Command;

import java.io.IOException;

import com.AddressBook.User;
import com.AddressBook.Database.UserDatabase;
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
    public String execute() throws CommandException, IOException {
        if(!validateInput(userID, MAX_SIZE))
            throw new CommandException("Invalid userID");
        else if(UserDatabase.getInstance().get(userID) != null)
            throw new CommandException("Account already exists");
        else {
            //Creates a new user associated with the userID
            UserDatabase.getInstance().set(new UserEntry(userID));
            return "OK";
        }
    }
 }
