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

 import static java.nio.file.StandardOpenOption.*;

 public class ExportDatabase extends Command {

     /**
      * Creates a Command object
      *
      * @param input Input for the command
      */
     public ExportDatabase(String input) {
         super(input.trim(), 1, null, null);
     }

     /**
      * Verify that passed user can use this command
      *
      * @param user the user to check
      * @throws CommandException if user cannot access command
      */
     private void verifyUser(User user) throws CommandException {
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

     /**
      * check if the input is valid
      *
      * @throws CommandException if invalid
      */
     private Path checkInput() throws CommandException {
         if (input.equals("")) {
             throw new CommandException("No Output_file specified");
         }
         try {
             Path p = Paths.get(input);
             if (Files.exists(p)) {
                 throw new CommandException("Can’t open " + input);
             }
             return p;
         } catch (InvalidPathException e) {
             throw new CommandException("Can’t open " + input);
         }
     }

     private void writeToFile(Path p, String data) throws IOException {
         Files.writeString(p, data, CREATE, WRITE);
     }


     @Override
     public String execute() throws CommandException {
         final User user = User.getInstance();
         verifyUser(user);
         Path p = checkInput();
         AddressDatabase ad = AddressDatabase.getInstance();
         try {
             String data = ad.exportDB(user.getUserId(), user::decrypt);
             writeToFile(p, data);
             return "OK";
         } catch (IOException | GeneralSecurityException e) {
             throw new CommandException("Error writing " + input);
         }

     }

 }
