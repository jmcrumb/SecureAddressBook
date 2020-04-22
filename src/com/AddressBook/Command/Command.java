 /*
  * Title:          com.AddressBook.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook.Command;

 import com.AddressBook.User;

 public abstract class Command {
     public final int authRequirement;

     public Command(int authRequirement) {
         this.authRequirement = authRequirement;
     }

     abstract String execute(User user) throws CommandException;
 }

/*
example subclass implementation
 public class AddRecord extends Command {
     AddRecord() {
         super(1);
     }

     @Override
     public String execute(User user) {
        // do your stuff
     }
 }
 */