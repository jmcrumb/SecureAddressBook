/*
 * Title:          com.AddressBook.Application
 * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
 * Last Modified:  4/24/20
 * Description: This class drives the functionality of the address program and is the class to be called
 * upon boot of the system (Application.main(args)).  It consequentially handles logging, command input,
 * command execution, authorization, and authentication, and error reporting.
 * */
package com.AddressBook;

import com.AddressBook.Command.Command;
import com.AddressBook.Command.CommandException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public class Application {

    /**
     * This function drives the basic functionality of the address book program.
     * Handles logging, command input / execution, authorization, and authentication,
     * and error reporting.
     */
    public static void run() {
        UserInterface ui = new UserInterface();

        while (true) {
            try {
                ui.sendResponse(processInput(ui));
            } catch (CommandException | IOException ce) {
                //TODO: Delete NEXT LINE once debugging is over
                System.out.println("***CommandException | IOException***");
                ui.sendResponse(ce.getMessage());
            } catch (IllegalAccessError ae) {
                ui.sendResponse("The current user is not authorized");
            } catch (Exception e) {
                //TODO: Delete NEXT LINE once debugging is over
                e.printStackTrace();

                ui.sendResponse("An unknown error has occurred");
                break;
            }
        }
    }

    /**
     * Represents a single iteration of processing a command, authorizing, and
     * executing said command.
     *
     * @param ui User Interface instance to interact with the user
     * @return String result of Command execution
     * @throws IllegalAccessError if an unauthorized access attempt occurs
     */
    private static String processInput(UserInterface ui) throws IllegalAccessError, Exception {
        Command command = ui.getNextCommand();
        if (command == null)
            return "Could not find command. Please try again or type 'HLP' for a list of commands\n";
        boolean isAuthorized = Authorization.verify(command);
        String userId = User.getInstance().getUserId();
        String s = "";
        if (isAuthorized)
            s = command.execute();
        else
            throw new IllegalAccessError();
        userId = (User.getInstance().getUserId() == null) ? userId : User.getInstance().getUserId();
        System.out.println(userId);
        AuditLog.getInstance().logCommand(command, isAuthorized, userId);
        return s;
    }

    public static void main(String[] args) throws GeneralSecurityException, UnsupportedEncodingException {
        //initialize system to a state of no user
        User.getInstance().setUser(null, null);
        //execute program
        run();
    }
}
