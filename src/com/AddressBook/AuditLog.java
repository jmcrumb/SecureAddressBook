 /*
  * Title:          com.AddressBook
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook;

 import com.AddressBook.Command.Command;

 import java.io.IOException;
 import java.nio.file.Files;
 import java.nio.file.Path;
 import java.nio.file.Paths;
 import java.time.LocalDateTime;
 import java.time.format.DateTimeFormatter;
 import java.time.format.FormatStyle;
 import java.util.ArrayList;
 import java.util.List;

 import static java.nio.file.StandardOpenOption.*;


 public class AuditLog {

     @SuppressWarnings("InnerClassMayBeStatic")
     private final class DataHolder {
         DataHolder(String commandType, String userName) {
             this.currentTime = LocalDateTime.now();
             this.commandType = commandType;
             this.userName = userName;
         }

         DataHolder(String dataString) {
             String[] sa = dataString.split(", ");
             this.currentTime = LocalDateTime.parse(sa[0] + " " + sa[1]);
             this.commandType = sa[2];
             this.userName = sa[3];
         }

         final LocalDateTime currentTime;
         final String commandType;
         final String userName;

         @Override
         public String toString() {
             return this.currentTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) + ", " +
               this.currentTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)) + ", " +
               this.commandType + ", " +
               this.userName;
         }
     }

     private final String LOG_FILE_NAME = ".logHistory";

     private static AuditLog logInstance;
     private final List<DataHolder> fifo = new ArrayList<>();

     private AuditLog() throws IOException {
         fileToList();
     }

     public static AuditLog getInstance() throws IOException {
         if (logInstance == null) {
             logInstance = new AuditLog();
         }
         return logInstance;
     }

     private void fileToList() throws IOException {

        try {
             Path f = Paths.get(LOG_FILE_NAME);
             if (Files.exists(f)) {
                 List<String> ls = Files.readAllLines(Paths.get(LOG_FILE_NAME));
                 if (ls.size() > 0) {
                     ls.forEach((v) -> fifo.add(new DataHolder(v)));
                 }
             }
         }catch (IOException e){
            throw new IOException("failed to read AuditLog");
        }

     }

     private void listToFile() throws IOException {
         List<String> ls = new ArrayList<>();
         fifo.forEach((v) -> ls.add(v.toString()));
         String output = String.join("\n", ls);
         Files.writeString(Paths.get(LOG_FILE_NAME), output, CREATE, WRITE);
     }

     //Logs the command of all user
     //figure out where to put the file, make sure in same directory as program
     //Format is command(input);authorization(Yes/No)
     public void logCommand(Command command, boolean authorized) throws IOException {

         if (command == null) {
             return;
         }

         if (fifo.size() >= 512) {
             fifo.remove(0);
         }


         if (authorized && command.getAuthorizedCode() != null) {
             fifo.add(new DataHolder(command.getAuthorizedCode(), User.getInstance().getUserId()));
         } else if (!authorized && command.getUnauthorizedCode() != null) {
             fifo.add(new DataHolder(command.getUnauthorizedCode(), User.getInstance().getUserId()));
         }
         listToFile();

     }
 }
