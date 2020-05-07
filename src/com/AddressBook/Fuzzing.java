package com.AddressBook;

import java.util.HashMap;
import java.util.Random;
import java.awt.Robot;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.AWTException;
import java.awt.event.KeyEvent;

public class Fuzzing {

    public static HashMap<String, String> users;
    public static Random rand;
    public static Output out = null;
    public static final String ADMIN_PASSWORD = "wordpass";
    public static final String LOG_FILEPATH = "fuzzlog.csv";
    public static FileWriter log;

    private class Output {
        private Robot r;
        private int delay;

        public Output(int delay) throws AWTException {
            r = new Robot();
        }

        public void enterString(String message, int keyDelay) {
            for (int i = 0; i < message.length(); i++) {
                char c = message.charAt(i);
                if (Character.isUpperCase(c)) {
                    r.keyPress(KeyEvent.VK_SHIFT);
                }
                r.keyPress(Character.toUpperCase(c));
                r.keyRelease(Character.toUpperCase(c));

                if (Character.isUpperCase(c)) {
                    r.keyRelease(KeyEvent.VK_SHIFT);
                }
                if (keyDelay > 0)
                    r.delay(keyDelay);
            }
            r.keyPress(KeyEvent.VK_ENTER);
            r.keyRelease(KeyEvent.VK_ENTER);
            r.delay(delay);
        }
    }

    public static void main(String[] args) {
        users = new HashMap<String, String>();
        rand = new Random();
        try {
            log = new FileWriter(LOG_FILEPATH);
            out = new Output(50);
            
            //testing
            lin();
            for(int userPopulation = 0; userPopulation < 10; userPopulation++) {
                createValidUserID();
            }
            for(int passes = 0; passes < 20; passes++) {
                
            }


            
        } catch (AWTException e) {
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                log.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Get userID's that already exists in the address book
    public static String getValidUserID() {
        if (users.size() == 0)
            throw new IllegalArgumentException("No users");
        int index = getRandom(0, users.size());
        String[] ids = (String[]) users.keySet().toArray();
        return ids[index];
    }

    //Generates a new valid UserID
    public static String createValidUserID() {
        final int MAX_SIZE = 16;
        final char[] goodChars = getValidChars();
        int length = getRandom(0, MAX_SIZE);
        StringBuilder userid = new StringBuilder(length);
        
        for(int i = 0; i<length; i++){
            char randChar = goodChars[getRandom(0,goodChars.length)];
            userid.append(randChar);
        }
        loginAdmin();
        out.enterString("ADU "+userid.toString(), 0);
        assertion("ADU " + userid.toString(), "OK");
        logout();
        out.enterString("LIN "+userid.toString(), 0);
        String pwd = getValidInput(8, 16);
        out.enterString(pwd, 0);
        out.enterString(pwd, 0);
        assertion("Make password" + pwd, "OK");
        logout();
        addUser(userid.toString(), pwd);
        return userid.toString();
    }

    public static int ID_EMPTY = 1, ID_LONG = 2, ID_BAD_CHARS = 4, ID_RAND_BYTES = 8;
    
    public static String getInvalidUserID(int flags) {
        final int MAX_SIZE = 16;
        final char[] goodChars = getValidChars();
        char[] badChars = new char[32]; int c = 0;
        for (char i = 33; i < 127; ++i) {
            if (!((i >= '0' && i <= '9') || (i >= 'A' && i <= 'Z') || (i >= 'a' && i <= 'z')))
                badChars[c++] = i;
        }
        StringBuilder userid = null; int len = 0;

        if ((flags & ID_EMPTY) == 1) return "";
        if ((flags & ID_LONG) == 1) {
            len = getRandom(MAX_SIZE + 1, MAX_SIZE * 2);
        }
        else len = MAX_SIZE;
        userid = new StringBuilder(len);

        if ((flags & ID_BAD_CHARS) == 1) {
            for (int i = 0; i < len; ++i) {
                userid.append(badChars[getRandom(0, 32)]);
            }
        }
        else if ((flags & ID_RAND_BYTES) == 1) {
            for (int i = 0; i < len; ++i) {
                userid.append((char)getRandom(0, 256));
            }
        }
        else {
            for (int i = 0; i < len; ++i) {
                userid.append(goodChars[getRandom(0, 62)]);
            }
        }


        return userid.toString();
    }

    public static void addUser(String userID, String password) {
        users.put(userID, password);
    }

    public static String getPassword(String userID) {
        return users.get(userID);
    }

    public static void loginAdmin() {
        out.enterString("LIN admin", 0);
        out.enterString("ADMIN_PASSWORD", 0);
        assertion("Admin login", "OK");
    }

    public static String loginUser() {
        String userID = getValidUserID();
        out.enterString("LIN " + userID, 0);
        out.enterString(getPassword(userID), 0);
        assertion("LIN "+userID, "OK");
        return userID;
    }

    public static String loginUser(String userID) {
        out.enterString("LIN " + userID, 0);
        out.enterString(getPassword(userID), 0);
        assertion("LIN "+userID, "OK");
        return userID;
    }

    public static void logout() {
        out.enterString("LOU", 0);
        assertion("LOU", "OK");
    }

    public static int getRandom(int min, int max) {
        return (rand.nextInt() % (max - min)) + min;
    }

    public static void assertion(String input, String expected) {
        try {
            log.write(input + "," + "," + expected);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static char[] getInvalidChars() {
        char[] badChars = new char[32]; int c = 0;
        for (char i = 33; i < 127; ++i) {
            if (!((i >= '0' && i <= '9') || (i >= 'A' && i <= 'Z') || (i >= 'a' && i <= 'z')))
                badChars[c++] = i;
        }
        return badChars;
    }

    public static char[] getValidChars() {
        return "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    }
    
    public static String getValidInput(int minLength, int maxLength) {
        char[] validChars = getValidChars();
        String validInput = "";
        for(int i = 0; i < getRandom(0, maxLength - 1); i++)
        {
            validInput += validChars[getRandom(0,validChars.length - 1)];
        }
        validInput.trim();
        return validInput;
    }

    public static String getInvalidInput(int minLength, int maxLength) {
        char[] invalidChars = getInvalidChars();
        String invalidInput = "";
        for(int i = 0; i < getRandom(minLength, maxLength - 1); i++) 
        {
            invalidInput += invalidChars[getRandom(0, invalidChars.length - 1)];
        }
        invalidInput.trim();
        return invalidInput;
    }

    public static String getInvalidPassword() {
        String s = "";
        char[] valids = getValidChars();
        char[] invalids = getInvalidChars();
        for(int i = 0; i < getRandom(0, 25); i++) {
            if(getRandom(0, 2) < 1)
                s += valids[getRandom(0, valids.length)];
            else
                s += invalids[getRandom(0, invalids.length)];
        }
        return s;
    }

    public static void addSpecificUser(String userID) {
        loginAdmin();
        test("ADU "+ userID, "OK");
        logout();
    }

    public static String getValidRecordID(String userid) {
        String recordID = getValidInput(1, 16);
        out.enterString("ADR" + recordID, 0);
        return recordID;
    }
    
    public static void lin() {
        //admin first time
        out.enterString("LIN admin", 0);
        out.enterString(getInvalidPassword(), 0);
        assertion("Admin invalid password", "Invalid Password");
        //admin valid 
        out.enterString("LIN admin", 0);
        out.enterString("ADMIN_PASSWORD", 0);
        out.enterString("ADMIN_PASSWORD", 0);
        assertion("ADMIN_PASSWORD", "OK");
        logout();
        //valid login
        loginHelper();
        String pwd = getValidInput(8,24);
        out.enterString(pwd, 0);
        out.enterString(pwd, 0);
        assertion("Make password " + pwd, "OK");
        logout();
        //invalid password
        loginHelper();
        out.enterString(getInvalidInput(8, 24), 0);
        assertion("Make password " + pwd, "Invalid Password");
        //passwords do not match
        loginHelper();
        out.enterString(pwd, 0);
        out.enterString(getInvalidInput(8, 24), 0);
        assertion("Not matching passwords " + pwd, "Passwords don\'t match");
        //Invalid username
        test("LIN " + getInvalidInput(1, 16), "Invalid userID");
        //Subsequent logins
        testSubsequentLogins("admin", "ADMIN_PASSWORD");
        String userID = getValidUserID();
        testSubsequentLogins(userID, users.get(userID));
        //already logged in
        out.enterString("LIN " + userID, 0);
        test(users.get(userID), "OK");
        test("LIN " + userID, "User already logged in");
    }

    public static void testSubsequentLogins(String userID, String password) {
        //admin bad password
        out.enterString("LIN " + userID, 0);
        test(getInvalidInput(8, 24), "Invalid Password");
        //admin wrong password 
        out.enterString("LIN " + userID, 0);
        test(getValidInput(8, 24), "Invalid Password");  
        //admin right password
        out.enterString("LIN " + userID, 0);
        test(password, "OK");
        logout();
    }

    public static String loginHelper() {
        String userID = getValidInput(1, 16);
        loginAdmin();
        test("ADU "+ userID, "OK");
        logout();
        return userID;
    }

    public static void lou() {
        //default user test
        test("LOU","No active login session");
        //admin test
        loginAdmin();   
        test("LOU","OK");
        test("LOU" + getValidInput(1, 25),"OK");
        test("LOU" + getInvalidInput(1, 25),"OK");
        logout();
        //user test
        loginUser();    //not necessarily needed, but throws compiler error otherwise
        test("LOU","OK");
        test("LOU" + getValidInput(1, 25),"OK");
        test("LOU" + getInvalidInput(1, 25),"OK");
        logout();
    }


    public static void chp() {  //need to get the user
        test("CHP", "No active login session");
        String tempUserID = loginUser();
        test("CHP" + users.get)
        
        
        
    }

    public static void adu() {
        //default user test
        test("ADU", "No active login session");
        test("ADU" + getValidInput(1, 16), "No active login session");
        test("ADU" + getInvalidInput(1, 16), "No active login session");
        //user test
        loginUser();
        test("ADU", "Invalid input");
        test("ADU" + getValidInput(1, 16), "User not authorized");
        test("ADU" + getInvalidInput(1, 16), "User not authorized");
        logout();
        //admin test
        loginAdmin();
        test("ADU", "Invalid input");
        test("ADU" + getValidInput(1, 16), "OK");
        test("ADU" + getInvalidInput(1, 16), "Invalid input");
        logout();
    }

    public static void deu() {
        //default user test
        test("DEU", "No active login session");
        String dUID = getValidInput(1,16);
        deleteUserTest(dUID, "No active login session");
        dUID = getInvalidInput(1,16);
        deleteUserTest(dUID, "No active login session");
        //admin test
        loginAdmin();
        test("DEU", "Invalid input");
        dUID = getValidInput(1,16);
        deleteUserTest(dUID, "OK");
        dUID = getInvalidInput(1,16);
        deleteUserTest(dUID, "Invalid input");
        logout();
        //user test
        loginUser();
        test("DEU", "User not authorized");
        dUID = getValidInput(1,16);
        deleteUserTest(dUID, "User not authorized");
        dUID = getInvalidInput(1,16);
        deleteUserTest(dUID, "User not authorized");
        logout();
    }

    public static void deleteUserTest(String deleteUserID, String output){
        addSpecificUser(deleteUserID);
        test("DEU" + deleteUserID, output);
    }

    public static void dal() throws InterruptedException {
        test("DAL", "No active login session");
        loginUser();
        test("DAL", "User not authorized");
        logout();
        loginAdmin();
        test("DAL", "CHECK MANUALLY");
        test("DAL " + getValidInput(1, 24), "CHECK MANUALLY");
        logout();
    }

    public static void adr() {
        //no log in
        test("ADR " + getValidInput(1, 16), "No active login session");
        //user logged in
        String userID = createValidUserID();
        loginUser(userID);
        test("ADR", "Invalid input");
        test("ADR " + getInvalidInput(0, 64), "Invalid input");
        String addressID = getValidInput(1, 16);
        test("ADR " + addressID, "OK");
        test("ADR " + addressID, "Record already exists");
        //param testing
        test("ADR " + getValidInput(1, 16) + generateRecordParameters(getRandom(0, 11), true), "OK");
        test("ADR " + getValidInput(1, 16) + generateRecordParameters(getRandom(0, 11), false), "Invalid input");
        test("ADR " + getValidInput(1, 16) + generateRecordParameters(getRandom(11, 24), true), "OK");
        logout();
        //admin
        loginAdmin();
        test("ADR " + getValidInput(1, 16) + generateRecordParameters(getRandom(0, 11), true), "User not authorized");
        logout();

        
    }

    public static String generateRecordParameters(int numberParams, boolean isValid) {
        String s = "";
        for(int i = 0; i < numberParams; i++) {
            switch (getRandom(0, 11)) {
                case 0:
                    s += "SN=";
                    break;
                case 1:
                    s += "GN=";
                    break;
                case 2:
                    s += "PEM=";
                    break;
                case 3:
                    s += "WEM=";
                    break;
                case 4:
                    s += "PPH=";
                    break;
                case 5:
                    s += "WPH=";
                    break;
                case 6:
                    s += "SA=";
                    break;
                case 7:
                    s += "CITY=";
                    break;
                case 8:
                    s += "STP=";
                    break;
                case 9:
                    s += "CTY=";
                    break;
                case 10:
                    s += "PC=";
                    break;
                default:
                    break;
            }
            String input = isValid ? getValidInput(1, 64) : getInvalidInput(0, 64);
            s += " " + input + " ";
        }
        return s;
    }

    public static void der() {
        //default user test
        test("DER", "No active login session");
        String dRID = getValidInput(1, 16);
        testDeleteRecord(dRID, "No active login session");
        dRID = getInvalidInput(1, 16);
        testDeleteRecord(dRID, "No active login session");
        //admin test
        loginAdmin();
        test("DER", "User not authorized");
        dRID = getValidInput(1, 16);
        testDeleteRecord(dRID, "User not authorized");
        dRID = getInvalidInput(1, 16);
        testDeleteRecord(dRID, "User not authorized");
        logout();
        //user test
        loginUser();
        test("DER", "Invalid input");
        dRID = getValidInput(1, 16);
        testDeleteRecord(dRID, "OK");
        dRID = getInvalidInput(1, 16);
        testDeleteRecord(dRID, "Invalid input");
        logout();
    }

    public static void testDeleteRecord(String recordID, String output)
    {
        addSpecificRecord(recordID);
        test("DER" + recordID, output);
    }
    public static void addSpecificRecord(String recordID)
    {
        out.enterString("ADR" + recordID, 0);
    }

    public static void edr() {
        //defaut user test
        
    }

    public static void rer() {
        
    }

    public static void imd() {
        
    }

    public static void exd() {
        
    }

    public static void ext() {
        
    }
    
    public static void hlp() {
        
    }

    public static void test(String fullInput, String expectedOutput)
    {
        out.enterString(fullInput, 0);
        assertion(fullInput, expectedOutput);
    }
}