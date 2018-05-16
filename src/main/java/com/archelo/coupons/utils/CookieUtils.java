package com.archelo.coupons.utils;

import com.archelo.coupons.json.AzureToken;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

public class CookieUtils {
    public static Cookie getSessionClientCookie(AzureToken azureToken) {
        BasicClientCookie cookie = new BasicClientCookie("_ssosignin_=sessId", azureToken.getUserID() + "&returnUrl=http://coupons.shoprite.com/");
        cookie.setPath("/");
        cookie.setDomain("wfsso.azurewebsites.net");
        return cookie;
    }

    public static Cookie getLinkCookie(AzureToken azureToken) {
        BasicClientCookie cookie = new BasicClientCookie("sessId=SessId", azureToken.getUserID());
        cookie.setPath("/");
        cookie.setDomain("wfsso.azurewebsites.net");
        return cookie;
    }


}
