 /*
  * Title:          com.AddressBook.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description: DeleteUser deletes a user from the address book device
  * */
 package com.AddressBook.Command;

 import com.AddressBook.Authorization;
 import com.AddressBook.Database.UserDatabase;
 import com.AddressBook.UserInput;

 import java.io.IOException;

 public class DeleteUser extends Command {
     private String userid;

     public DeleteUser(String userid)    //the parameter is the userid to be deleted
     {
         super(userid, 2, "DU", null);     //there is no code for failed user deletions
         this.userid = userid;
     }

     public String execute() throws CommandException, IOException {
         boolean currAuth = Authorization.verify(this);
         if (currAuth) {
             UserDatabase userDatabase = UserDatabase.getInstance();
             if (userDatabase.exists(this.userid))  //checks that the user exists before trying to delete it
             {
                 userDatabase.deleteUser(this.userid);
             }
             if (!userDatabase.exists(this.userid))     //handles the admin trying to delete a user that doesn't exist
             {
                 UserInput.getInstance().sendOutput("User not found.\n");
             }
             return this.authorizedCode;  //returns authorized code for the audit log
         } else {
             return this.unauthorizedCode;   //returns null
         }
     }
 }
