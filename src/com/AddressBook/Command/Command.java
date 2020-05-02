 /*
  * Title:          com.AddressBook.Command.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/24/20
  * Description: Abstract superclass representation of a Command.  All subclasses will serve to 
  * represent commands usable by individuals in at least one level of authorization.
  * */
 package com.AddressBook.Command;

 public abstract class Command {
  
     protected static final int CODE_NONE = 0;
     protected static final int CODE_USER = 1;
     protected static final int CODE_ADMIN = 2;

  
     public final int authRequirement;
     protected String input;

     public String getAuthorizedCode() {
         return authorizedCode;
     }

     protected String authorizedCode;

     public String getUnauthorizedCode() {
         return unauthorizedCode;
     }

     protected String unauthorizedCode;

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
     * Helper method for validating input conforms with requirements 
     * outlined in design document:
     * i) Is no larger than the Maximum size of input for an input 
     * ii) Is alphanumeric
     * iii) Is nonempty
     * 
     * @param input Input to be validated
     * @param maxSize maximum size of input
     * @return if the input is valid
     */
    protected boolean validateInput(String input, int maxSize) {
        if(input == null || input.length() > maxSize)
            return false;
        return input.matches("^[a-zA-Z0-9]+$");
    }

    /**
     * Helper method for validating input conforms with requirements 
     * outlined in design document:
     * i) Is alphanumeric
     * ii) Is nonempty
     * 
     * @param input Input to be validated
     * @return if the input is valid
     */
    protected boolean validateInput(String input) {
        if(input == null)
            return false;
        return input.matches("^[a-zA-Z0-9]+$");
    }

     /**
      * Executes command represented by this object.  This class is abstract and thus 
      * must be overloaded in all subclass implementations of this class.
      *
      * @return String to be determined by implementation
      * @throws CommandException
      */
      abstract public String execute() throws Exception;

 }
