 /*
  * Title:          com.AddressBook.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook.Command;

 import com.AddressBook.Database.AddressDatabase;
 import com.AddressBook.User;


 import java.io.IOException;
 import java.nio.file.Files;
 import java.nio.file.InvalidPathException;
 import java.nio.file.Path;
 import java.nio.file.Paths;
 import java.security.GeneralSecurityException;
 import java.util.regex.Pattern;

 public class ImportDatabase extends Command {
     /**
      * Creates a ImportDatabase object
      *
      * @param input            Input for the command
      */
     public ImportDatabase(String input) {
         super(input.trim(), 1, null, null);
     }

     /**
      * Verify that passed user can use this command
      *
      * @param user the user to check
      * @throws CommandException if user cannot access command
      */
     private void verifyUser( User user) throws CommandException {
         if (user.getAuthorization() != this.authRequirement) {
             if (user.getAuthorization() == Command.CODE_ADMIN) {
                 throw new CommandException("Admin not authorized");
             } else if (user.getAuthorization() == Command.CODE_ADMIN) {
                 throw new CommandException("No active login session");
             } else {
                 throw new IllegalArgumentException("Illegal Authorization Code");
             }
         }
     }

     private  Path checkInput() throws CommandException {
         if (input.equals("")) {
             throw new CommandException("No Input_file specified");
         }
         try {
             Path p = Paths.get(input);
             if (!Files.exists(p)) {
                 throw new CommandException("Can’t open " + input);
             }
             return p;
         } catch (InvalidPathException e) {
             throw new CommandException("Can’t open " + input);
         }
     }


     private String readFromFile(Path p) throws IOException {
         return Files.readString(p);
     }

     private void validateFileFormat(String s) throws CommandException {
         if (!Pattern.matches("(\\n*([A-Za-z@.0-9 ]*;){12})+", s)) {
             throw new CommandException(input + " invalid format");
         }
     }


     @Override
     public String execute() throws CommandException, IOException, GeneralSecurityException {
         final User user = User.getInstance();
         verifyUser(user);
         Path p = checkInput();
         AddressDatabase ad = AddressDatabase.getInstance();
         String data = readFromFile(p);
         validateFileFormat(data);
         ad.importDB(user.getUserId(), user::decrypt, user::encrypt, data);

         return "OK";

     }
 }
