package com.AddressBook;

import java.io.Console;

public class UserInput {

    private static UserInput instance;

    private Console cnsl;

    private UserInput(boolean isLogIn) {
        if(isLogIn)
        {
            return cnsl.readPassword();
        }
        cnsl = System.console();
    }


    public static UserInput getInstance(boolean isLogIn) {
        if (instance == null) instance = new UserInput(boolean isLogIn);
        return instance;
    }


    public String getNextInput(boolean isLogIn) {
        if(isLogIn)
        {
            return cnsl.readPassword();
        }
        return cnsl.readLine();
    }

    public void sendOutput(String output) {
        cnsl.printf(output);
    }

}
