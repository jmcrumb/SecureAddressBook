/*
 * Title:          com.AddressBook.Command
 * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
 * Last Modified:  4/22/20
 * Description: DeleteUser deletes a user from the address book device
 * */
package com.AddressBook.Command;

import com.AddressBook.Authorization;
import com.AddressBook.User;
import com.AddressBook.Database.UserDatabase;
import com.AddressBook.UserInput;

import java.io.IOException;

public class DeleteUser extends Command {

    public DeleteUser(String input) { // the parameter is the userid to be deleted
        super(input.trim(), 2, "DU", null); // there is no code for failed user deletions
    }

    public String execute() throws CommandException, IOException {
        if (User.getInstance().getUserId() == null) return "No active login session";

        boolean currAuth = Authorization.verify(this);
        if (currAuth) {
            if (!validateInput(input)) return "Invalid userID";

            UserDatabase userDatabase = UserDatabase.getInstance();
            if (userDatabase.exists(input)) { // checks that the user exists before trying to delete it
                userDatabase.deleteUser(input);
                return "OK";
            }
            else { // handles the admin trying to delete a user that doesn't exist
                return "Account does not exist";
            }
        } else {
            return "Admin not active";
        }
    }
}
