 /*
  * Title:          com.AddressBook
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description: The UserEntry class holds all of the fields , constructors, and a toString method for each user's login data
  * */
 package com.AddressBook.UserEntry;

public class UserEntry
{
    final public String userid;
    final public String pwhash;
    final public int authLevel;
    final public boolean hasLoggedIn;

    public UserEntry(String userid, String pwhash, int authLevel)
    {
        this.userid = userid;
        this.pwhash = pwhash;
        this.authLevel = authLevel;
        this.hasLoggedIn = false;
    }

    public UserEntry(String userEntryString)    //uncertain about this: both constructors just take strings
    {
        String fields[] = userEntryString.split(";");
        if(fields.length < 2)
        {
            sendResponse("invalid user entry string \n");
            this.userid = null;
            this.pwhash = null;
            this.hasLoggedIn = null;
        }
 
//       else if(fields.length > 2)
//        {
//            int counter = 0;
//            String incuserid = "";
//            String pass = "";
//           while(!exists(incuserid))   //gets the userid if it contains ; characters by checking whether the user exists at each stage ***could give a shorter userid than what is intended if multiple ~ characters are involved and one userid is a substring of another
//            {
//                incuserid += fields[counter];
//                counter++;
//            }
//            counter++;  //starts the password after the userid
//            while(counter < fields.length - 1)    //gets the password if it contains ; characters by taking the rest of the strings in the array and concatenating them together
//            {
//                pass += fields[counter];
//               counter++;
//          }
//         this.userid = incuserid;
//         this.pwhash = pass; 
//        }
        else    //fields.length = 2
        {
            this.userid = fields[0];
            this.pwhash = fields[1];
        }
    }

    public String toString()
    {
        String userEntry = this.userid + ";" + this.pwhash;  //this should either include the password or the fromString constructor should be removed
        return userEntry;
    }
}
