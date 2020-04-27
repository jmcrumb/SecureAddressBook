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
                ui.sendResponse(ce.getMessage());
            } catch (IllegalAccessError ae) {
                ui.sendResponse("The current user is not authorized");
            } catch(Exception e) {
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
     * @throws IllegalAccessError if an unauthorized access attempt occurs
     * @return String result of Command execution
     */
    private static String processInput(UserInterface ui) throws IllegalAccessError, Exception {
        Command command = ui.getNextCommand();
        if(command == null)
            return "Could not find command. Please try again or type 'HLP' for a list of commands\n";                       
        boolean isAuthorized = Authorization.verify(command);
        //TODO: Comment in once implemented
  //      AuditLog.getInstance().logCommand(command, isAuthorized);
        if(isAuthorized)
            return command.execute();
        else
            throw new IllegalAccessError();
    }

    public static void main(String[] args) {
        //initialize system to a state of no user
        User.getInstance().setUser(null, null);
        //execute program
        run();
    }
}
