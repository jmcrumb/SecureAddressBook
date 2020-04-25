 /*
  * Title:          com.AddressBook
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook;

 import com.AddressBook.Database.UserDatabase;
 import com.AddressBook.UserEntry.UserEntry;

 public class User {
  private UserEntry entry;
  User(UserEntry ue){
   this.entry = ue;
  }
  User(String username, String password){
   this.entry = UserDatabase.get(username, password);
  }

  String sign(){
   return null;
  }

  String encrypt(){
   return null;
  }
  String decrypt(){
   return null;
  }
  String getID(){
   return null;
  }

  public int getAuthorization() {
      return null;
  }

 }
