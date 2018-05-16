package com.archelo.coupons;


import com.archelo.coupons.utils.HeaderUtils;
import com.archelo.coupons.utils.Utils;


public class Main {
    public static void main(String[] args) {
        System.out.println("Running");

        Utils.setDebugProperties();
        HTTPHandler handler = new HTTPHandler(HeaderUtils.getStandardHeaders());

//        Utils.performShopRiteSignIn(handler);
        Utils.performAzureSignIn(handler);
    }
}
