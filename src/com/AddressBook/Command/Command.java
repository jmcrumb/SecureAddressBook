 /*
  * Title:          com.AddressBook.Command
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook.Command;

 import com.AddressBook.User;

 public abstract class Command<T> {
     public final int authRequirement;


     private User user;

     protected User getUser() {
         return this.user;
     }

     protected void setUser(User m_user) {
         this.user = m_user;
     }

     public Command(int authRequirement, User user) {
         this.authRequirement = authRequirement;
         this.user = user;
     }

     abstract T execute(User user) throws CommandException;
 }

/*
example subclass implementation
 public class AddRecord extends Command<Void> {
     AddRecord() {
         super(1);
     }

     @Override
     public Void execute(User user) {
        // do your stuff
     }
 }
 */