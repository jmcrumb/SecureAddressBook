 /*
  * Title:          com.AddressBook.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook.Command;
 import java.nio.file.Files;
 import java.nio.file.Paths;

 import java.io.IOException;
 import java.nio.file.Files;
 import java.nio.file.Path;
 import java.nio.file.Paths;
 import java.util.List;

 public class DisplayAuditLog extends Command {

     public DisplayAuditLog(String input) {
         super(null, 2, "DAL", null);
     }

     @Override
     public String execute() throws CommandException {
         Path path = Paths.get("logHistory.txt");
         try {
             List<String> contents = Files.readAllLines(path);
             return String.join("\n", contents);
         } catch (IOException ex) {
             throw new CommandException("Failed to open AuditLog");
         }
     }
 }
