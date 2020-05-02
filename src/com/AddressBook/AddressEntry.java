 /*
  * Title:          com.AddressBook
  * Authors:        Miles Maloney, Caden Keese
  * Last Modified:  4/24/20
  * Description:
  * */
 package com.AddressBook;

 import java.util.*;


 public class AddressEntry {

     public final String recordID;
     public final String SN;
     public final String GN;
     public final String PEM;
     public final String WEM;
     public final String PPH;
     public final String WPH;
     public final String SA;
     public final String CITY;
     public final String STP;
     public final String CTY;
     public final String PC;

     public AddressEntry(String recordID, String surname, String givenName, String perEmail, String wrkEmail, String perPhone, String wrkPhone, String address, String city, String state, String country, String postalCode) {
         this.recordID = recordID;
         SN = surname;
         GN = givenName;
         PEM = perEmail;
         WEM = wrkEmail;
         PPH = perPhone;
         WPH = wrkPhone;
         SA = address;
         CITY = city;
         STP = state;
         CTY = country;
         PC = postalCode;
     }

     public AddressEntry(String stringAddressEntry) {
         StringTokenizer splitString = new StringTokenizer(stringAddressEntry, ";");

         recordID = splitString.nextToken();
         SN = splitString.nextToken();
         GN = splitString.nextToken();
         PEM = splitString.nextToken();
         WEM = splitString.nextToken();
         PPH = splitString.nextToken();
         WPH = splitString.nextToken();
         SA = splitString.nextToken();
         CITY = splitString.nextToken();
         STP = splitString.nextToken();
         CTY = splitString.nextToken();
         PC = splitString.nextToken();
     }

     public String toString() {

         String sb = recordID + ";" +
           SN + ";" +
           GN + ";" +
           PEM + ";" +
           WEM + ";" +
           PPH + ";" +
           WPH + ";" +
           SA + ";" +
           CITY + ";" +
           STP + ";" +
           CTY + ";" +
           PC + ";";
         return sb;
     }

 }
