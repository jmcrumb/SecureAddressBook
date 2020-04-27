 /*
  * Title:          com.AddressBook
  * Authors:        Miles Maloney, Caden Keese
  * Last Modified:  4/24/20
  * Description:
  * */
package com.AddressBook;

import javax.crypto.Cipher;
import java.util.*;


public class AddressEntry {

    final String recordID; 
    final String SN; 
    final String GN;
    final String PEM;
    final String WEM;
    final String PPH;
    final String WPH;
    final String SA;
    final String CITY;
    final String STP;
    final String CTY;
    final String PC;

    public AddressEntry(String recordID, String surname, String givenName, String perEmail, String wrkEmail, String perPhone, String wrkPhone, String address, String city, String state, String country, String postalCode){
        recordID = recID;
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

    public AddressEntry(String stringAddressEntry){
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

    public String toString(){

        String outputString = (recordID + ";");
        outputString = outputString.concat(SN + ";");
        outputString = outputString.concat(GN + ";");
        outputString = outputString.concat(PEM + ";");
        outputString = outputString.concat(WEM + ";");
        outputString = outputString.concat(PPH + ";");
        outputString = outputString.concat(WPH + ";");
        outputString = outputString.concat(SA + ";");
        outputString = outputString.concat(CITY + ";");
        outputString = outputString.concat(STP + ";");
        outputString = outputString.concat(CTY + ";");
        outputString = outputString.concat(PC + ";");

        return outputString;
    }

 }
