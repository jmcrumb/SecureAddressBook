/*
 * Title:          com.AddressBook
 * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
 * Last Modified:  4/22/20
 * Description:
 * */
package com.AddressBook;

import com.AddressBook.Command.Command;

 public class Authorization {
     
    /**
     * This function is used to make sure whoever is using the system is authorized 
     * to use a command before it is executed.
     * 
     * @param command The command to authorize
     * @return a boolean that is false if authorization fails and true if authorization 
     * is successful
     */
    public static boolean verify(Command command) {
        return User.getInstance().getAuthorization() == command.authRequirement;
    }
 }
