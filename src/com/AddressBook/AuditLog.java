 /*
  * Title:          com.AddressBook
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook;
 import java.io.File;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.nio.file.Files;
 import java.nio.file.Paths;

 import com.AddressBook.Command.Command;
 import com.AddressBook.Database.UserDatabase;
 import com.AddressBook.UserEntry.UserEntry;

 public class AuditLog {

    private static AuditLog logInstance;
    File logHistory;

    public static AuditLog getInstance(){
        if(logInstance == null){
            logInstance = new AuditLog();
        }
        return logInstance;
    }

    //Logs the command of all user
    //figure out where to put the file, make sure in same directory as program
    //Format is command(input);authorization(Yes/No)
    void logCommand(Command command, boolean authorized){
        try {
            if (command == null) {
                return;
            }
            if (!logHistory.exists()) {
                logHistory = new File("logHistory.txt");
            }


            //Encrypt so only admin would be able to access eventually
            UserEntry adm = UserDatabase.getInstance().get("admin");
            String adminPassword;
            if (adm != null) {
                adminPassword = adm.passwordHash;
            } else {
                throw new RuntimeException("Admin doesn't exist");
            }
            if (authorized && command.getAuthorizedCode() != null) {
                Files.writeString(Paths.get("logHistory.txt"), "" + User.getInstance().getUserId() + ";" + command.getAuthorizedCode());
            } else if (!authorized && command.getUnauthorizedCode() != null) {
                Files.writeString(Paths.get("logHistory.txt"), "" + User.getInstance().getUserId() + ";" + command.getUnauthorizedCode());

            }
        }

        catch (IOException e){
            System.out.println("IOException");
        }
    }
 }
