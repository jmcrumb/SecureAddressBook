 /*
  * Title:          com.AddressBook.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook.Command;

 public class CommandException extends Exception {
   private static final long serialVersionUID = 6273558234152975777L;

     public CommandException(String message) {
         super(message);
     }

     public CommandException(String message, Throwable cause) {
         super(message, cause);
     }
 }

