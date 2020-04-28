/*
  * Title:          com.AddressBook.Command.Exit
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/26/20
  * Description: This command terminates the program
  */

package com.AddressBook.Command;

public class Exit extends Command {

    public Exit(String input) {
        super(input, 0, null, null);
    }

    @Override
    public String execute() {
        System.exit(0);
        return "OK"; // java doesn't like when this line isn't here
    }
}