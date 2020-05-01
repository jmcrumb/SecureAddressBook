package com.AddressBook;

import java.io.Console;

public class UserInput {

    private static UserInput instance;

    private final Console cnsl;

    private UserInput() {
        cnsl = System.console();
    }


    public static UserInput getInstance() {
        if (instance == null) instance = new UserInput();
        return instance;
    }

    public String getNextPassword() {
        return new String(cnsl.readPassword());
    }

    public String getNextInput() {
        return cnsl.readLine();
    }

    public void sendOutput(String output) {
        cnsl.printf(output);
    }

}
