 /*
  * Title:          com.AddressBook.UserEntry
  * Authors:        Miles Maloney, Caden Keese
  * Last Modified:  4/24/20
  * Description:
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
