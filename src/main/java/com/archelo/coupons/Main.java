package com.archelo.coupons;



import com.archelo.coupons.http.Server;
import com.archelo.coupons.utils.Utils;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Running");

        Utils.setDebugProperties();
        Server server = new Server();
        server.start();
//        Utils.performShopRiteSignIn(handler);




        Utils.performAzureSignIn();
    }
}
