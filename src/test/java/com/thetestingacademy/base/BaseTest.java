package com.thetestingacademy.base;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

public class BaseTest {
    // This is common to all test cases.

    @BeforeTest
    public void setup() {
        System.out.println("Start the Test!");

    }

    @AfterTest
    public void tearDown() {
        System.out.println("Finished the Test!");
    }

    public  String getToken(){
        return null;
    }

}
