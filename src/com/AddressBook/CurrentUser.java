 /*
  * Title:          com.AddressBook
  * Authors:        Miles Maloney, Caden Keese
  * Last Modified:  4/22/20
  * Description:
  * */
 package com.AddressBook;

 public class CurrentUser {
     private static CurrentUser obj;

     private User user;

     private CurrentUser() {
     }

     public static CurrentUser getInstance() {
         if (obj == null)
             obj = new CurrentUser();
         return obj;
     }

     public User getUser() {
         return user;
     }

     public void setUser(User user) {
         this.user = user;
     }
 }

