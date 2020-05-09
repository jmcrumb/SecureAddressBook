 /*
  * Title:          com.AddressBook.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook.Command;

 import com.AddressBook.AuditLog;
 import com.AddressBook.Database.UserDatabase;
 import com.AddressBook.User;

 import java.io.IOException;
 import java.security.GeneralSecurityException;

 public class DisplayAuditLog extends Command {
     @SuppressWarnings("FieldCanBeLocal")
     private final int MAX_SIZE = 16;

     public DisplayAuditLog(String input) {
         super(input.trim(), 2, "DAL", null);
     }

     @Override
     public String execute() throws CommandException, IOException, GeneralSecurityException {
         String[] entries;
         AuditLog al = AuditLog.getInstance();
         if (input.equals("")) {
             entries = al.getArray(User.getInstance()::decrypt);
         } else {
             if (!validateInput(input, MAX_SIZE))
                 throw new CommandException("Invalid userID");
             if (!UserDatabase.getInstance().exists(input)) {
                 throw new CommandException("Account does not exist");
             } else {
                 entries = al.getFilteredArray(input, User.getInstance()::decrypt);
             }
         }
         return String.join("\n", entries);
     }
 }
