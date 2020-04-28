 /*
  * Title:          com.AddressBook
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description: The UserEntry class holds all of the fields , constructors, and a toString method for each user's login data
  * */
 package com.AddressBook.UserEntry;

 public class UserEntry {
     final public String userId;
     final public String passwordHash;


     public UserEntry(String userId, String passwordHash) {
         this.userId = userId;
         this.passwordHash = passwordHash;
     }

     protected UserEntry(UserEntry ue) {
         this.userId = ue.userId;
         this.passwordHash = ue.passwordHash;
     }

     public UserEntry(String userEntryString)    //uncertain about this: both constructors just take strings
     {
         String[] fields = userEntryString.split(";");
         if (fields.length != 2) {
             throw new RuntimeException("invalid user entry string \n");

         } else {
             this.userId = fields[0];
             this.passwordHash = fields[1];
         }
     }

     public int getAuthLevel() {
         return (userId.equals("admin")) ? 2 : 1;
     }

     public String toString() {
         return this.userId + ";" + this.passwordHash;
     }

     public boolean hasLoggedIn() {
         return this.passwordHash == null;
     }
 }
