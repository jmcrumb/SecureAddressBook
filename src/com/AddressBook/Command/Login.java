/*
 * Title:          com.AddressBook.Command
 * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
 * Last Modified:  4/22/20
 * Description:
 * */
package com.AddressBook.Command;

import com.AddressBook.Database.UserDatabase;
import com.AddressBook.UserEntry.AdminEntry;
import com.AddressBook.UserEntry.UserEntry;

import java.io.IOException;

import com.AddressBook.Encryption;
import com.AddressBook.User;
import com.AddressBook.UserInput;

public class Login extends Command {

    public Login(String input) {
        super(input, 0, "LS", "LF");
    }


    private boolean isAlphanumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) return false;
        }
        return true;
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
        }
        else if (entry.pwhash == null) {
            // case 5 (first time login)
            UserInput.getInstance().sendOutput("This is the first time the account is being used. You must create a new password. Passwords may contain 1-24 upper- or lower-case letters or numbers. Choose an uncommon password that would be difficult to guess.\n");
            
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

            int authLevel = (entry instanceof AdminEntry) ? 2 : 1;
            database.set(new UserEntry(userid, newPassword, authLevel));
        }
        else if (entry.pwhash != password) {
            // case 3 (invalid password)
            return "Invalid Credentials";
        } else if (entry.pwhash != null) {
            // case 4 (correct username and password)
            User.getInstance().setUser(entry, password);
        } else {
            throw new CommandException("Unknown Error");
        }
        
        return "OK";
    }
}
