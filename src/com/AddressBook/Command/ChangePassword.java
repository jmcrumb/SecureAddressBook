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
import com.AddressBook.UserInput;
import com.AddressBook.Database.UserDatabase;
import com.AddressBook.UserEntry.UserEntry;

public class ChangePassword extends Command {

    public ChangePassword(String input) {
        super(input, 1, "LS", "LF");
    }


    private boolean isAlphanumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) return false;
        }
        return true;
    }

    @Override
    public String execute() throws IOException {
        if (User.getInstance().getUserId() == null) {
            return "No active login session";
        }

        String oldPassword = input.split(" ")[0];
        String curPassword = UserDatabase.getInstance().get(User.getInstance().getUserId()).pwhash;
        if (!Encryption.checkBCrypt(oldPassword, curPassword)) {
            return "Invalid credentials";
        }

        String newPassword;
        UserInput.getInstance().sendOutput("Enter a password: ");
        newPassword = UserInput.getInstance().getNextInput();
        UserInput.getInstance().sendOutput("Reenter the same password: ");

        if (newPassword.equals(UserInput.getInstance().getNextInput())) {
            if (!isAlphanumeric(newPassword)) {
                return "Password contains illegal characters";
            }
            // TO-DO
            else if (false) { // somehow check if password is too easy to guess
                return "Password is too easy to guess";
            }
        }
        else {
            return "Passwords do not match";
        }

        newPassword = Encryption.hashBCrypt(newPassword);
        UserEntry updatedEntry = new UserEntry(User.getInstance().getUserId(), newPassword, User.getInstance().getAuthorization());
        UserDatabase.getInstance().set(updatedEntry);
        User.getInstance().setUser(updatedEntry, newPassword);

        return "OK";
    }
 }
