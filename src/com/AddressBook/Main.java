package com.AddressBook;


import com.AddressBook.SystemModule.UserInterface;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        UserInterface ui = new UserInterface(new Parser());
    }
}
