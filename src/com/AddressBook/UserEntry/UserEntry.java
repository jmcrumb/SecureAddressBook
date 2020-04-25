 /*
  * Title:          com.AddressBook
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description: The UserEntry class holds all of the fields , constructors, and a toString method for each user's login data
  * */
 package com.AddressBook.UserEntry;

 public class UserEntry {
    //fields
    final String userid;
    final String password;
    private static boolean needsPWChange;

    public UserEntry(String userid)
    {
        this.userid = userid;
        needsPWChange = True; //flag to mark that the user must change passwords on next login
    }

    public UserEntry(String userEntryString)    //uncertain about this: both constructors just take strings
    {
        String fields[] = userEntryString.split(".");
        if (fields.length == 1)         //merges the two constructors
        {
            this.userid = userid;
            needsPWChange = true;
        }
        else
        {
            this.userid = fields[0];
            this.password = fields[1];
        }
    }

    public String toString()
    {
        String userEntry = this.userid + "." + this.password;  //this should either include the password or the fromString constructor should be removed
        return userEntry;
    }
 }
