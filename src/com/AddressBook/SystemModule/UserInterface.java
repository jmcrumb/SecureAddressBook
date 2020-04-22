 /*
  * Title:          com.AddressBook.System
  * Authors:        Miles Maloney, Caden Keese
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook.SystemModule;

 import com.AddressBook.Command.Command;
 import com.AddressBook.Parser;

 import java.util.Scanner;

 public class UserInterface {
   private final Parser parser;
   private Scanner sc;

  public UserInterface(Parser parser) {
   this.parser = parser;
  }

  public Command getNextCommand(){
   System.out.print("?>");
   sc = new Scanner(System.in);
   String s = sc.nextLine();
   return parser.parseCommand(s);
  }

  void sendResponse(String response){
   System.out.println(response);
  }

 }
