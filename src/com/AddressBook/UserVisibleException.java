 /*
  * Title:          com.AddressBook
  * Authors:        Miles Maloney, Caden Keese
  * Last Modified:  4/30/20
  * Description:
  * */
 package com.AddressBook;

 public class UserVisibleException extends Exception {

     public UserVisibleException(String message) {
         super(message);
     }

     public UserVisibleException(String message, Throwable cause) {
         super(message, cause);
     }
 }
