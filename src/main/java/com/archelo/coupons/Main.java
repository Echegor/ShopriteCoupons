package com.archelo.coupons;



import com.archelo.coupons.utils.Utils;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Running");

        Utils.setDebugProperties();
//        Utils.performShopRiteSignIn(handler);




        Utils.performAzureSignIn();
    }
}
