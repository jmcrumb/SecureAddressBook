/*
 * Title:          com.AddressBook.Command
 * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
 * Last Modified:  4/22/20
 * Description:
 * */
package com.AddressBook.Command;

import java.io.IOException;

import com.AddressBook.Encryption;
import com.AddressBook.User;
import com.AddressBook.Database.UserDatabase;

public class ChangePassword extends Command {

    public ChangePassword(String input) {
        super(input, 1, "LS", "LF");
    }

    @Override
    public String execute() throws IOException {
        if (User.getInstance().getUserId() == null) {
            return "No active login session";
        }

        String oldPassword = input.split(" ")[0];
        String curPassword = UserDatabase.getInstance().get(User.getInstance().getUserId()).password;
        if (!Encryption.checkBCrypt(oldPassword, curPassword)) {
            return "Invalid credentials";
        }


        // credentials are correct
        // prompt new password
        // reenter new password
        

        return null;
    }
 }
