 /*
  * Title:          com.AddressBook.Application
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/24/20
  * Description: This class drives the functionality of the address program and is the class to be called
  * upon boot of the system (Application.main(args)).  It consequentially handles logging, command input, 
  * command execution, authorization, and authentication, and error reporting.
  * */
 package com.AddressBook;

import java.util.Scanner;

import com.AddressBook.User;
import com.Addressbook.UserInterface;
import com.AddressBook.Authorization;

public class Application {
  
    /**
     * This function drives the basic functionality of the address book program. 
     * Handles logging, command input / execution, authorization, and authentication, 
     * and error reporting.
     */
    public static void run(){
        UserInterface ui = new UserInterface();

        while(true) {
            try{
                ui.sendResponse(processInput(ui));
            } catch(CommandException ce) {
                ui.sendResponse(ce.getMessage());
            } catch(IllegalAccessError ae) {
                ui.sendResponse("The current user is not authorized");
            } catch(Exception e) {
                ui.sendResponse("An unknown error has occurred");
            }
        }
    }

    /**
     * Represents a single iteration of processing a command, authorizing, and 
     * executing said command.
     * 
     * @param ui User Interface instance to interact with the user
     * @throws IllegalAccessError if an unauthorized access attempt occurs
     */
    private static String processInput(UserInterface ui) throws IllegalAccessError{
        Command command = ui.getNextCommand();                         
        boolean isAuthorized = Authorization.verify(command);
        AuditLog.getInstance().logCommand(command, isAuthorized);
        if(isAuthorized)
            command.execute();
        else
            throw new IllegalAccessError();      
    }

    public static void main(String[] args) {
        //initialize system to a state of no user
        User.setUser(null);
        //execute program
        run();
    }
 }
