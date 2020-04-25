 /*
  * Title:          com.AddressBook.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description: DeleteUser deletes a user from the address book device
  * */
 package com.AddressBook.Command;

 public class DeleteUser {
    private String userid;
  
    public DeleteUser(String userid)
    {
        this.userid = userid;
    }

    public String execute() throws CommandException
    {
        currAuth = verify(this);
        if(currAuth)
        {
            if(exists(this.userid))
            {
                userToDelete = new UserEntry(this.userid); //need decent way to remove user from user database
                addressDatabase.set(userToDelete)
            }
            if(!exists(this.userid))
            {
                sendResponse("User not found.\n");
            }
            return authorizedCode;
        }
        else
        {
            return unauthorizedCode;
        }
    }
 }
