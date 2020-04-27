/*
 * Title:          com.AddressBook.Command
 * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
 * Last Modified:  4/22/20
 * Description:
 * */
package com.AddressBook.Command;

import com.AddressBook.Database.UserDatabase;
import com.AddressBook.UserEntry.UserEntry;

import java.io.IOException;

import com.AddressBook.Encryption;
import com.AddressBook.User;

public class Login extends Command {

    public Login(String input) {
        super(input, 0, "LS", "LF");
    }

    @Deprecated
    public String execute() throws CommandException, IOException {
        // case 1 (user already logged in)
        if (User.getInstance().getUserId() != null) {
            return "An account is currently active; logout before proceeding";
        }

        String[] splitInput = input.split(" ");
        if (splitInput.length < 2) {
            throw new CommandException("A username and password must be provided.");
        }
        String userid = splitInput[0];
        String password = Encryption.hashBCrypt(splitInput[1]);

        UserDatabase database = UserDatabase.getInstance();

        // case 2 (user doesn't exist)
        if (!database.exists(userid)) return "Invalid Credentials";


        UserEntry entry = database.get(userid);

        if (entry == null) {
            throw new CommandException("Unknown Error");
        } else if (entry.password != password) {
            // case 3 (invalid password)
            return "Invalid Credentials";
        } else if (entry.password != null) {
            // case 4 (correct username and password)
            User.getInstance().setUser(entry, password);
        } else if (entry.password == null) {
            // case 5 (first time login)
            database.set(new UserEntry(userid, password));
        } else {
            throw new CommandException("Unknown Error");
        }
        return "OK";


    }
}
