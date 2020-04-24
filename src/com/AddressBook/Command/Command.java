 /*
  * Title:          com.AddressBook.Command.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/24/20
  * Description: Abstract superclass representation of a Command.  All subclasses will serve to 
  * represent commands usable by individuals in at least one level of authorization.
  * */
 package com.AddressBook.Command;

 public abstract class Command {
     public final int authRequirement;
     protected String input;
     protected String authorizedCode;
     protected String unauthorizedCode;

     private final int maxPriviledge = 2;

     /**
      * Creates a Command object
      *
      * @param input Input for the command
      * @param authRequirement The authorization level requirement {0:none, 1:user, 2:admin}
      * @param authorizedCode String representation of authorization for the log
      * @param unauthorizedCode String representation of unauthorization for the log
      */
     public Command(String input, int authRequirement, String authorizedCode, String unauthorizedCode){
        this.authRequirement = authRequirement;
        this.input = input;
        this.authorizedCode = authorizedCode;
        this.unauthorizedCode = unauthorizedCode;
     }

     /**
      * Accessor for convenient retrieval of Command specific log codes for the Audit Log
      *
      * @param isAuthorized Boolean value representing whether or not the user has 
      * the authorization to use this Command
      * @return A two character String code for the Audit Log, as defined in the command 
      * line interface.
      */
     public String getLogCode(boolean isAuthorized) {
        if(isAuthorized)
            return authorizedCode;
        else
            return unauthorizedCode;
     }

     /**
      * Executes command represented by this object.  This class is abstract and thus 
      * must be overloaded in all subclass implementations of this class.
      *
      * @return String to be determined by implementation
      * @throws CommandException
      */
      abstract String execute() throws CommandException;

 }
