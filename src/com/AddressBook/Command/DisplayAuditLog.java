 /*
  * Title:          com.AddressBook.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook.Command;

 import com.AddressBook.AuditLog;
 import com.AddressBook.User;

 import java.security.PrivateKey;

 public class DisplayAuditLog extends Command {
     @SuppressWarnings("FieldCanBeLocal")
     private final int MAX_SIZE = 16;

     public DisplayAuditLog(String input) {
         super(input.trim(), 2, "DAL", null);
     }

     @Override
     public String execute() throws Exception {
         String[] entries;
         AuditLog al = AuditLog.getInstance();
         PrivateKey key = AuditLog.getPrivateKey(User.getInstance()::decrypt);
         if (input.equals("")) {
             entries = al.getArray(key);
         } else {
             if (!validateInput(input, MAX_SIZE))
                 throw new CommandException("Invalid userID");
             entries = al.getFilteredArray(input, key);
             if (entries.length == 0) {
                 throw new CommandException("Account does not exist");
             }
         }

         return String.join("\n", entries);
     }
 }
