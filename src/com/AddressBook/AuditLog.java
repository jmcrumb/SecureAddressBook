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

import com.AddressBook.Database.UserDatabase;

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
        try{
            if(command == null){
                return;
            }
            if(!logHistory.exists()){
                logHistory = new File("logHistory.txt");
            }
            
            String authorization;
            if(authorized){
                authorization = "Yes";
            }
            else{
                authorization = "No";
            }

            //Encrypt so only admin would be able to access eventually
            String adminPassword = UserDatabase.get(admin).password; 
            
            FileWriter FW = new FileWriter("logHistory.txt");
            //Can someone check if this is correct
            FW.write(Encyption.encrypt(command.input,adminPassword) + ";" + Encryption.encrypt(authorization));
            FW.close();
        }

        catch (IOException e){
            System.out.println("IOException");
        }
    }
 }
