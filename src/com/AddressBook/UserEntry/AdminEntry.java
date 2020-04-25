 /*
  * Title:          com.AddressBook.UserEntry
  * Authors:        Miles Maloney, Caden Keese
  * Last Modified:  4/24/20
  * Description: AdminEntry class intended to hold information for the admin account of the address book
  * */
 package com.AddressBook.UserEntry;

 public class AdminEntry {
    final String adminid;
    final String adminpw;
    public AdminEntry(UserEntry entry)
    {
        this.adminid = entry.userid;
        this.adminpw = entry.password;
    }
 }
