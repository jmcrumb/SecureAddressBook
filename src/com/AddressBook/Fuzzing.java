package com.AddressBook;

import java.util.HashMap;
import java.util.Random;
import java.awt.Robot;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.AWTException;
import java.awt.event.KeyEvent;

public class Fuzzing {

    public static HashMap<String, String> users;
    public static Random rand;
    public static Output out = null;
    public static final String ADMIN_PASSWORD = "wordpass";
    public static final String LOG_FILEPATH = "FuzzFiles/fuzzlogs/fuzzlog.csv";
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
            startup();
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

    //starts up the addressbook application
    public static void startup() {
        out.enterString("java Application", 0);
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
        for(int i = 0; i < getRandom(minLength, maxLength - 1); i++) {
            validInput += validChars[getRandom(0,validChars.length - 1)];
        }
        validInput.trim();
        return validInput;
    }

    public static String getInvalidInput(int minLength, int maxLength) {
        char[] invalidChars = getInvalidChars();
        String invalidInput = "";
        for(int i = 0; i < getRandom(minLength, maxLength - 1); i++)  {
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
        louTest(0);
        //user test
        louTest(1);
        //admin test
        louTest(2);
    }

    public static void louTest(int user) {
        quickLIN(user);
        String expectedOutput = "OK";
        if(user == 0) {
            expectedOutput = "No active login session";
        }
        test("LOU", expectedOutput);  //no params
        quickLIN(user);
        test("LOU" + getValidInput(1, 25), expectedOutput);  //valid param
        quickLIN(user);
        test("LOU" + getInvalidInput(1, 25), expectedOutput);    //invalid param
        //function will end with default user regardless of user parameter
    }


    public static void chp() {
        //default user test
        chpTest(0);
        //user test
        chpTest(1);
        //admin test
        chpTest(2);
    }

    public static void chpTest(int user) {
        String expectedOutput = "No active login session";
        String oldpw = "";
        String currUser = "";
        if(user == 0) {
            test("CHP", expectedOutput);    //no params
            test("CHP" + getValidInput(8, 24), expectedOutput); //valid param
            test("CHP" + getInvalidInput(8, 24), expectedOutput);   //invalid param
            return; 
        }
        if(user == 1) {
            oldpw = getPassword(loginUser());
            currUser = "User";
        }
        else if(user == 2) {
            loginAdmin();
            oldpw = "ADMIN_PASSWORD";
            currUser = "Admin";
        }
        //no params
        test("CHP", "Invalid input");
        //valid/invalid param (not old pw)
        test("CHP" + getValidInput(8, 24), "Incorrect password");
        test("CHP" + getInvalidInput(8, 24), "Invalid input");
        //using old pw
        out.enterString("CHP" + oldpw, 0);  //invalid new pw
        out.enterString(getInvalidInput(8, 24), 0);
        assertion("CHP" + currUser + "invalid new pw", "Invalid password");
        out.enterString("CHP" + oldpw, 0);   //valid non-matching new pw
        out.enterString(getValidInput(8, 24), 0);
        out.enterString(getValidInput(8, 24), 0);
        assertion("CHP" + currUser + "valid non-matching new pw", "Passwords don\'t match");
        out.enterString("CHP" + oldpw, 0);  //valid matching new pw
        String newpw = getValidInput(8, 24);
        out.enterString(newpw, 0);
        out.enterString(newpw, 0);
        assertion("CHP" + currUser + "valid matching new pw", "OK");
        if(user == 2) { //reset admin pw
            out.enterString("CHP" + newpw, 0);
            out.enterString(oldpw, 0);
            out.enterString(oldpw, 0);
        }
        if(user != 0) {
            logout();
        }
    }


    public static chpHelper(String oldpw)
    {
        out.enterString("CHP" + oldpw);
    }


    public static void adu() {
        //default user test
        aduTest(0);
        //user test
        aduTest(1);
        //admin test
        aduTest(2);
    }

    public static void aduTest(int user) {
        quickLIN(user);
        String expectedOutput = ""; //changed to be no active login session and user not authorized for default user and user respectively
        if(user == 0) {
            expectedOutput = "No active login session";
        }
        else if(user == 1) {
            expectedOutput = "User not authorized";
        }
        else if(user == 2) {
            test("ADU", "Invalid input");   //no params
            test("ADU" + getValidInput(1, 16), "OK");   //valid param
            test("ADU" + getInvalidInput(1, 16), "Invalid input");  //invalid param
            test("ADU" + getValidUserID(), "User already exists");  //param already exists 
            logout();
            return;
        }
        test("ADU", expectedOutput);    //no params
        test("ADU" + getValidInput(1, 16), expectedOutput); //valid param
        test("ADU" + getInvalidInput(1, 16), expectedOutput);  //invalid param
        test("ADU" + getValidUserID(), expectedOutput);  //param already exists
        if(user != 0) {
            logout();
        }
    }


    public static void deu() { 
        //default user test
        deuTest(0);
        //user test
        deuTest(1);
        //admin test
        deuTest(2);
    }

    public static void deuTest(int user) {
        quickLIN(user);
        String expectedOutput = "";
        if(user == 0) {
            expectedOutput = "No active login session";
        }
        else if(user == 1) {
            expectedOutput = "User not authorized";
        }
        else if(user == 2) {
            test("DEU", "Invalid input");   //no params
            dUID = getValidInput(1,16);
            deleteUserTest(dUID, "OK");     //valid param
            dUID = getInvalidInput(1,16);
            deleteUserTest(dUID, "Invalid input");  //invalid param
            logout();
            return;
        }
        test("DEU", expectedOutput);    //no params
        test("DEU" + getValidInput(1, 16), expectedOutput); //valid param (non-user)
        test("DEU" + getInvalidInput(1, 16), expectedOutput); //invalid param (non-user)
        test("DEU" + getValidUserID(), expectedOutput); //valid param (user); won't delete it due to authorization blocks hopefully
        if(user != 0) {
            logout();   //logs out the user or admin
        }
    }

    public static void deleteUserTest(String deleteUserID, String output) {
        out.enterString("ADU "+ deleteUserID, 0);
        test("DEU" + deleteUserID, output);
    }



    public static void dal() throws InterruptedException {  //needs manual checking
        //default user test
        dalTest(0);
        //user test
        dalTest(1);
        //admin test
        dalTest(2);
    }

    public static void dalTest(int user) {
        quickLIN(user);
        String expectedOutput = "";
        if(user == 0) {
            expectedOutput = "No active login session";
        }
        else if(user == 1) {
            expectedOutput = "User not authorized";
        }
        else if(user == 2) {
            expectedOutput = "CHECK MANUALLY";
        }
        test("DAL", expectedOutput);
        test("DAL" + getValidInput(1, 16), expectedOutput);
        test("DAL" + getInvalidInput(1, 16), expectedOutput);
        if(user != 0) {
            logout();   //logs out the user or admin
        }
    }

    public static void adr() {
        //default user test
        adrTest(0);
        //user test
        adrTest(1);
        //admin test
        adrTest(2);        
    }

    public static void adrTest(int user) {
        //Logins handled in if statements for sake of retaining user test code
        String expectedOutput = ""; //changed to be no active login session and user not authorized for default user and user respectively
        if(user == 0) {
            expectedOutput = "No active login session";
        }
        else if(user == 1) {
            String userID = createValidUserID();
            loginUser(userID);
            test("ADR", "Invalid input");   //no params
            test("ADR " + getInvalidInput(0, 64), "Invalid input"); //invalid param
            String addressID = getValidInput(1, 16);
            test("ADR " + addressID, "OK"); //valid param
            test("ADR " + addressID, "Record already exists");  //param already exists
            //param testing
            test("ADR " + getValidInput(1, 16) + generateRecordParameters(getRandom(0, 11), true), "OK");   //valid multiple params
            test("ADR " + getValidInput(1, 16) + generateRecordParameters(getRandom(0, 11), false), "Invalid input");   //invalid multiple params
            test("ADR " + getValidInput(1, 16) + generateRecordParameters(getRandom(11, 24), true), "OK");  //valid extra params
            logout();
            return;
        }
        else if(user == 2) {
            loginAdmin();
            expectedOutput = "User not authorized";
        }
        test("ADR", expectedOutput);   //no params
        test("ADR " + getInvalidInput(0, 64), expectedOutput); //invalid param
        String addressID = getValidInput(1, 16);
        test("ADR " + addressID, expectedOutput); //valid param
        test("ADR " + addressID, expectedOutput);  //param already exists
        //param testing
        test("ADR " + getValidInput(1, 16) + generateRecordParameters(getRandom(0, 11), true), expectedOutput);   //valid multiple params
        test("ADR " + getValidInput(1, 16) + generateRecordParameters(getRandom(0, 11), false), expectedOutput);   //invalid multiple params
        test("ADR " + getValidInput(1, 16) + generateRecordParameters(getRandom(11, 24), true), expectedOutput);  //valid extra params
        if(user != 0) {
            logout();
        }
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
        derTest(0);
        //user test
        derTest(1);
        //admin test
        derTest(2);
    }

    public static void derTest(int user) {
        quickLIN(user);
        String expectedOutput = "";
        if(user == 0) {
            expectedOutput = "No active login session";
        }
        else if(user == 1) {
            test("DER", "Invalid input");   //no params
            test("DER" + getValidInput(1, 16), "Record not found"); //valid param (non-record)
            test("DER" + getInvalidInput(1, 16), "Invalid input"); //invalid param (can't be record)
            testDeleteRecord(getValidInput(1, 16), "OK");     //valid param (record); testDeleteRecord will create the record then test deleting it
            logout();
            return;   
        }
        else if(user == 2) {
            expectedOutput = "User not authorized";
        }
        test("DER", expectedOutput);    //no params
        test("DER" + getValidInput(1, 16), expectedOutput); //valid param (non-record)
        test("DER" + getInvalidInput(1, 16), expectedOutput); //invalid param (can't be record)
        test("DER" + getValidRecordID(getValidUserID()), expectedOutput); //valid param (record); won't delete it due to authorization blocks hopefully
        if(user != 0) {
            logout();   //logs out the user or admin
        }
    }

    public static void testDeleteRecord(String recordID, String output) {
        out.enterString("ADR" + recordID, 0);
        test("DER" + recordID, output);
    }

    public static void edr() {
        //default user test
        edrTest(0);
        //user test
        edrTest(1);
        //admin test (tests same record ID to ensure no access)
        edrTest(2);
    }

    public static void edrTest(int user) {
        quickLIN(user);
        String expectedOutput = "";
        if(user == 0) {
            expectedOutput = "No active login session";
        }
        else if(user == 1) {
            eRID = getValidInput(1,16); //record ID to be tested on
            out.enterString("ADR" + eRID + generateRecordParameters(getRandom(0, 11), true));   //creates a generic recordID with any number of fields
            //no record ID
            test("EDR", "Invalid input");
            //non-existing record ID w/ valid chars
            expectedOutput = "Record not found";
            test("EDR" + getValidInput(1,16), expectedOutput); //no params
            test("EDR" + getValidInput(1,16) + generateRecordParameters(getRandom(0, 11), true), expectedOutput); //valid params
            test("EDR" + getValidInput(1,16) + generateRecordParameters(getRandom(0, 11), false), expectedOutput); //invalid params
            test("EDR" + getValidInput(1,16) + generateRecordParameters(getRandom(12, 24), true), expectedOutput); //extra parameters
            //non-existing record ID w/ invalid chars
            expectedOutput = "Invalid input";
            test("EDR" + getInvalidInput(1,16), expectedOutput);   //no params
            test("EDR" + getInvalidInput(1,16) + generateRecordParameters(getRandom(0, 11), true), expectedOutput); //valid params
            test("EDR" + getInvalidInput(1,16) + generateRecordParameters(getRandom(0, 11), false), expectedOutput); //invalid params
            test("EDR" + getInvalidInput(1,16) + generateRecordParameters(getRandom(12, 24), true), expectedOutput); //extra parameters
            //valid existing recordID      
            test("EDR" + eRID, "Invalid input"); //no params
            test("EDR" + eRID + generateRecordParameters(getRandom(0,11), true), "OK"); //valid params
            test("EDR" + eRID + generateRecordParameters(getRandom(0,11), false), "Invalid input"); //invalid params
            test("EDR" + eRID + generateRecordParameters(getRandom(12, 24), true), "OK"); //extra params
            logout();
            return;
        }
        else if(user == 2){
            expectedOutput = "User not authorized";
        }
        //no record ID
        test("EDR", expectedOutput);
        //non-existing record ID w/ valid chars
        test("EDR" + getValidInput(1,16), expectedOutput); //no params
        test("EDR" + getValidInput(1,16) + generateRecordParameters(getRandom(0, 11), true), expectedOutput); //valid params
        test("EDR" + getValidInput(1,16) + generateRecordParameters(getRandom(0, 11), false), expectedOutput); //invalid params
        test("EDR" + getValidInput(1,16) + generateRecordParameters(getRandom(12, 24), true), expectedOutput); //extra parameters
        //non-existing record ID w/ invalid chars
        test("EDR" + getInvalidInput(1,16), expectedOutput);   //no params
        test("EDR" + getInvalidInput(1,16) + generateRecordParameters(getRandom(0, 11), true), expectedOutput); //valid params
        test("EDR" + getInvalidInput(1,16) + generateRecordParameters(getRandom(0, 11), false), expectedOutput); //invalid params
        test("EDR" + getInvalidInput(1,16) + generateRecordParameters(getRandom(12, 24), true), expectedOutput); //extra parameters
        //valid existing recordID      
        test("EDR" + eRID, expectedOutput); //no params
        test("EDR" + eRID + generateRecordParameters(getRandom(0,11), true), expectedOutput); //valid params
        test("EDR" + eRID + generateRecordParameters(getRandom(0,11), false), expectedOutput); //invalid params
        test("EDR" + eRID + generateRecordParameters(getRandom(12, 24), true), expectedOutput); //extra params
        if(user != 0) {
            logout();
        }
    }



    public static void rer() {
        //default user test
        rerTest(0);
        //user test
        rerTest(1);
        //admin test
        rerTest(2);
    }

    public static void rerTest(int user) {
        quickLIN(user);
        String expectedOutput = "";
        if(user == 0) {
            expectedOutput = "No active login session";
        }
        else if(user == 1) {
            rRID = getValidInput(1,16); //record ID to be used for testing
            out.enterString("ADR" + rRID + generateRecordParameters(getRandom(0, 11), true));   //creates a generic recordID with any number of fields initialized
            //no record ID
            test("RER", "Invalid input");
            //non-existing record
            test("RER" + getValidInput(1, 16), "Record not found"); //valid param (non-record)
            test("RER" + getInvalidInput(1, 16), "Invalid input");  //invalid param (can't be record)
            //existing record
            test("RER" + rRID, "OK"); //valid param
            test("RER" + rRID + getValidInput(1, 16), "OK");    //extra params
            logout();
            return;
        }
        else if(user == 2) {
            expectedOutput = "User not authorized";
        }
        test("RER", expectedOutput);
        //non-existing record
        test("RER" + getValidInput(1, 16), expectedOutput); //valid param
        test("RER" + getInvalidInput(1, 16), expectedOutput);  //invalid param
        //existing record
        test("RER" + getValidRecordID(getValidUserID()), expectedOutput); //valid param
        test("RER" + rRID + getValidInput(1, 16), expectedOutput);    //extra params
        if(user != 0) {
            logout();
        }
    }



    public static void imd() {
        //default user test
        test("IMD notloggedin", "No active login session");
        //user test
        loginUser();
        test("IMD", "Invalid input");
        test("IMD" + getInvalidInput(1, 24), "Invalid input");
        test("IMD ~/" + getValidInput(1, 24) +"/"+getValidInput(1, 24), "Input file invalid format");
        randomNoiseFile("FuzzFiles/testfiles/randomdatabase.csv");
        test("IMD FuzzFiles/testfiles/emptydatabase.csv", "Invalid file");
        test("IMD FuzzFiles/testfiles/randomdatabase.csv", "Invalid file");
        //TODO: populate corruptdatabase file with modified database export
        test("IMD FuzzFiles/testfiles/corruptdatabase.csv", "Invalid file");
        //TODO: populate corruptdatabase file with valid database export
        test("IMD FuzzFiles/testfiles/gooddatabase.csv", "OK");
        logout();
        //admin test
        loginAdmin();
        //TODO: populate corruptdatabase file with modified database export
        test("IMD FuzzFiles/testfiles/gooddatabase.csv", "User not authorized");
        logout();
    }

    public static void randomNoiseFile(String filepath) {
        byte[] barr = new byte[getRandom(64, 1024)];
        rand.nextBytes(barr);
        try (FileOutputStream stream = new FileOutputStream(filepath)) {
            stream.write(barr);
            stream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void exd() {
        //default user
        test("EXD", "No active login session"); //no params
        //admin
        loginAdmin();
        test("EXD", "No active login session");
        logout();
        //user
        loginUser();
        test("EXD", "Invalid input");    //no params
        test("EXD" + getInvalidInput(1, 16), "Invalid input");   //invalid params
        test("EXD " + getValidInput(1, 24) +"/"+getValidInput(1, 24), "Output file invalid format");
        test("EXD FuzFiles/testfiles/export_" + getValidInput(1, 16) + getValidInput(1, 16), "OK");   //extra params
        test("EXD FuzFiles/testfiles/export_" + getValidInput(1, 16), "OK");    //valid param
        logout();          
    }

    public static void ext() {
        //default user test
        extTest(0);
        //user test
        extTest(1);
        //admin test
        extTest(2);
    }

    public static void extTest(int user) {   //function to test full fucntionality of exit function for a type of user
        quickLIN(user);
        String expectedOutput = "OK";
        test("EXT", expectedOutput);
        startup();
        quickLIN(user);
        test("EXT " + getValidInput(1, 24), expectedOutput);
        startup();
        quickLIN(user);
        test("EXT " + getInvalidInput(1, 24), expectedOutput);
        startup();
    }

    public static void quickLIN(int user) {  //function to take in a code and log in that user (0 = default, 1 = user, 2 = admin)
        if(user == 1) {
            loginUser();
        }
        else if(user == 2) {
            loginAdmin();
        }
    }
    
    public static void hlp() {
        //default user test
        hlpTest(0);
        //user test
        hlpTest(1);
        //admin test
        hlpTest(2);
    }

    public static void hlpTest(int user) {
        quickLIN(user);
        hlpHelper();
        if(user != 0) {
            logout();
        }
    }

    public static void hlpHelper() {
        String[] commands = {
            "LIN", "LOU", "CHP", "ADU", "DEU", "DAL", "ADR", "DER", "EDR", "RER", "IMD", "EXD", "EXT", "HLP"
        };

        test("HLP " + getValidInput(1, 24), "Unrecognized command");
        test("HLP " + getInvalidInput(1, 24), "Invalid input");
        test("HLP " + commands[getRandom(0, 15)], "OK");
        test("HLP " + commands[getRandom(0, 15)] + " " + getValidInput(1, 24), "OK");
        String s = "";
        for(int i = 0; i < getRandom(2, 15); i++) {
            s += commands[getRandom(0, 15)] + " ";
        }
        test("HLP " + s, "OK");
    }

    public static void test(String fullInput, String expectedOutput) {
        out.enterString(fullInput, 0);
        assertion(fullInput, expectedOutput);
    }
}
