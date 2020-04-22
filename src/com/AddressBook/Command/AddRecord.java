 /*
  * Title:          com.AddressBook.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook.Command;

 import com.AddressBook.User;

 public class AddRecord extends Command<Void> {
     AddRecord() {
         super(1);
     }

     @Override
     public Void execute(User user) {
         return null;
     }
 }
