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
        super(input, 0, "", "");
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


        // case 2 (user doesn't exist)
        if (!UserDatabase.getInstance().exists(userid)) return "Invalid Credentials";

        // case 4 (correct username and password)
        UserEntry entry = UserDatabase.getInstance().get(userid);
        if (entry != null && (entry.password == password)) {
            User.getInstance().setUser(entry, password);
            return "OK";
        }
        else {
            // case 3 (invalid password)
            return "Invalid Credentials";
        }

        // case 5 (first time login)
        // TO-DO

        return null;
    }
 }
