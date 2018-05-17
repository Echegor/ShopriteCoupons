package com.archelo.coupons.utils;

import com.archelo.coupons.http.Get;
import com.archelo.coupons.http.Post;
import com.archelo.coupons.json.AzureToken;
import com.archelo.coupons.json.AzureUserInfo;
import com.archelo.coupons.json.CouponsArray;
import com.archelo.coupons.urls.AzureUrls;
import com.archelo.coupons.urls.ShopriteURLS;
import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Utils {


    public static void writeToFile(String file, String entity) {
        try {
            Files.write(Paths.get("logs", file), entity.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void performAzureSignIn() throws IOException {
        HttpHost proxy = new HttpHost("10.120.30.19", 8080, "http");
        RequestConfig globalConfig = RequestConfig
                .custom()
                .setProxy(proxy)
                .setCircularRedirectsAllowed(false)
                .build();
        HttpClientContext context;
        context = HttpClientContext.create();
        context.setCookieStore(new BasicCookieStore());
        context.setRequestConfig(globalConfig);
        context.setAuthCache(new BasicAuthCache());

        String status = new Get.GetBuilder()
                .url(ShopriteURLS.STATUS)
                .headers(HeaderUtils.getStatus())
                .httpClientContext(context)
                .build().doGet();

        logString("status",status,"status.html",false);

        String samlRequest = new Get.GetBuilder()
                .url(AzureUrls.SIGN_IN)
                .headers(HeaderUtils.getAzureSignInStatus())
                .encodeQueryParams(false)
                .queryParams(ParamUtils.getAzureSignInStatus(status))
                .httpClientContext(context)
                .build().doGet();

        logString("samlRequest",samlRequest,"samlRequest.html",false);

        String authenticate3601 = new Post.PostBuilder()
                .url(ShopriteURLS.AUTHENTICATE3601)
                .headers(HeaderUtils.getAzureAuthenticationHeaders3601(status))
                .queryParams(ParamUtils.buildAzureSamlRequestQueryParameters())
                .dataParams(ParamUtils.buildSamlRequestDataParameters(samlRequest))
                .contentType(ContentType.APPLICATION_FORM_URLENCODED)
                .httpClientContext(context)
                .encodeQueryParams(true)
                .build().doPost();

        logString("authenticate3601",authenticate3601,"authenticate3601.html",false);

        String postToken = ParamUtils.getRequestVerificationToken(authenticate3601);
        System.out.println("postToken:" +postToken);

        String samlResponse = new Post.PostBuilder()
                .url(ShopriteURLS.AUTHENTICATE)
                .headers(HeaderUtils.getAuthenticationHeaders())
                .dataParams(ParamUtils.getAuthenticationInfo(postToken))
                .contentType(ContentType.APPLICATION_FORM_URLENCODED)
                .httpClientContext(context)
                .build().doPost();

        logString("samlResponse",samlResponse,"samlResponse.html",false);

        String returnFromSignIn = new Post.PostBuilder()
                .url(AzureUrls.RETURN_FROM_SIGN_IN)
                .headers(HeaderUtils.getReturnFromSignInHeaders())
                .dataParams(ParamUtils.buildSamlResponseDataParameter(samlResponse))
                .contentType(ContentType.APPLICATION_FORM_URLENCODED)
                .httpClientContext(context)
                .build().doPost();

        logString("returnFromSignIn",returnFromSignIn,"returnFromSignIn.html",false);

        String webJS = new Get.GetBuilder()
                .url(ShopriteURLS.WEB_JS)
                .headers(HeaderUtils.getWebJsHeaders())
                .httpClientContext(context)
                .build().doGet();

        logString("webJS",webJS,"webJS.html",false);

        AzureToken azureTokens = ParamUtils.buildAzureToken(webJS, status);

        System.out.println("azureTokens:" +azureTokens);
        System.out.println();

        String azureSession = new Get.GetBuilder()
                .url(AzureUrls.SESSION + azureTokens.getUserID())
                .headers(HeaderUtils.getAzureStatus())
                .queryParams(ParamUtils.buildAzureStatusQueryParam(azureTokens))
                .encodeQueryParams(false)
                .httpClientContext(context)
                .build().doGet();

        System.out.println("azureSession:" +azureSession);
        logString("azureSession",azureSession,"azureSession.html",true);

        AzureUserInfo userInfo = new Gson().fromJson(azureSession, AzureUserInfo.class);

        System.out.println("userInfo:" +userInfo);
        System.out.println();

        String availableCoupons = new Post.PostBuilder()
                .url(AzureUrls.AVAILABLE_COUPONS)
                .headers(HeaderUtils.getAvailableCoupons(azureTokens))
                .jsonObject(ParamUtils.buildPPCJSON(userInfo))
                .contentType(ContentType.APPLICATION_JSON)
                .httpClientContext(context)
                .build().doPost();

        System.out.println("availableCoupons:" +availableCoupons);
        System.out.println();
        logString("availableCoupons",availableCoupons,"availableCoupons.json",true);


        CouponsArray couponsArray = new Gson().fromJson(availableCoupons, CouponsArray.class);

        System.out.println("couponsArray:" +couponsArray);
        System.out.println();

        List<Header> couponHeader = HeaderUtils.getAvailableCoupons(azureTokens);
        for (String coupon : couponsArray.getAvailable_ids_array()) {
            String response = new Post.PostBuilder()
                    .url(AzureUrls.COUPONS_ADD)
                    .headers(couponHeader)
                    .jsonObject(ParamUtils.buildCouponJSON(coupon, userInfo))
                    .contentType(ContentType.APPLICATION_JSON)
                    .httpClientContext(context)
                    .build().doPost();
            System.out.println("Response: " + response);
        }

    }

    public static void logString(String request,String string, String filename, boolean print){
        if(print){
            System.out.println(request+" " + string);
            System.out.println();
        }

        writeToFile(filename,string);
    }

    public static void setDebugProperties() {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
//        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http","DEBUG");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
//        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.impl.conn","DEBUG");
//        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.impl.client","DEBUG");
//        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.client", "DEBUG");

    }
}
