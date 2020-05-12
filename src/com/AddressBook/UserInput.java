package com.AddressBook;

import java.util.Scanner;

public class UserInput {

    private static UserInput instance;

    private Scanner scanner;

    private UserInput() {
        scanner = new Scanner(System.in);
    }


    public static UserInput getInstance() {
        if (instance == null) instance = new UserInput();
        return instance;
    }

    public String getNextPassword() {
        return scanner.nextLine();
    }
    public String getNextInput() {
        return scanner.nextLine();
    }

    public void sendOutput(String output) {
        System.out.printf(output);
    }

}
