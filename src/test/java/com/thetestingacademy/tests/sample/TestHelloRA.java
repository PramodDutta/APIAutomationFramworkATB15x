package com.thetestingacademy.tests.sample;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestHelloRA {

    @Test
    public void test_hello_world_positive(){
        Assert.assertEquals("pramod","pramod");
    }

    @Test
    public void test_hello_world_negative(){
        Assert.assertEquals("pramod","Pramod");

    }
}
