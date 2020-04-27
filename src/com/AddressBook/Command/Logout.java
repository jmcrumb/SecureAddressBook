 /*
  * Title:          com.AddressBook.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook.Command;

 import com.AddressBook.User;

 public class Logout extends Command {
  /**
   * Creates a Command object
   *
   * @param input            Input for the command
   * @param authRequirement  The authorization level requirement {0:none, 1:user, 2:admin}
   * @param authorizedCode   String representation of authorization for the log
   * @param unauthorizedCode String representation of unauthorization for the log
   */
  public Logout(String input, int authRequirement, String authorizedCode, String unauthorizedCode) {
   super(input, 1, "LO", "LO");
  }

  @Override
  public String execute() throws CommandException {
   if(User.getInstance().getAuthorization() < authRequirement){
    throw new CommandException("No active login session");
   }else{
    User.getInstance().setUser(null, null);
    return "OK";
   }
  }
 }
