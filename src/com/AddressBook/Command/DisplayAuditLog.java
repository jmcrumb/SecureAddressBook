 /*
  * Title:          com.AddressBook.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook.Command;

 public class DisplayAuditLog extends Command {

     public DisplayAuditLog(String input) {
         super(input, 1, "", "");
     }

     @Override
     public String execute() {
         return "TEST LOG";
     }
 }
