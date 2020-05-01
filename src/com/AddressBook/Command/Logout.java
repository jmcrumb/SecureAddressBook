 /*
  * Title:          com.AddressBook.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook.Command;

 import com.AddressBook.User;

 import java.io.UnsupportedEncodingException;
 import java.security.GeneralSecurityException;

 public class Logout extends Command {
     /**
      * Creates a Logout object
      */
     public Logout() {
         super(null, 0, "LO", "LO");
     }

     @Override
     public String execute() throws CommandException, GeneralSecurityException, UnsupportedEncodingException {
         if (User.getInstance().getAuthorization() < authRequirement) {
             throw new CommandException("No active login session");
         } else {
             User.getInstance().setUser(null, null);
             return "OK";
         }
     }
 }
