package com.byebug.automation.properties;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ByeBugPropertiesTest {

    @Test
    public static void getSetPropertiesTest(String[] args) {
        String mall_host = "10.58.176.210";
        ByeBugProperties.set("mall_host", mall_host);
        Assert.assertEquals(ByeBugProperties.get("mall_host"), mall_host);
    }

}
