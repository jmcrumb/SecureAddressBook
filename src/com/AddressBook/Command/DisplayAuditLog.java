 /*
  * Title:          com.AddressBook.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook.Command;

 import java.nio.file.Files;
 import java.nio.file.Paths;

 public class DisplayAuditLog extends Command{

    String DislayAuditLog(){
        Super(null,2,"DAL",null);
    }

    @Override
    public void execute() throws CommandException {
        Path path = Paths.get("logHistory.txt");
        try{
            List contents = Files.readAllLines(path);

            //Read from the stream
            for(String content:contents){//for each line of content in contents
                String[] contentArray = content.split(";");
                //displays the userID and whether command is authorized
                System.out.println(contentArray[0] + " " + contentArray[1]); 
            }
        }
        catch(IOException ex){
            ex.printStackTrace();//handle exception here
        }
    }
 }
