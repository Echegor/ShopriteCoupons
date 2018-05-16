package com.archelo.coupons.json;

public class UserInfo {
    private String Email;
    private String FSN;
    private String FirstName;
    private String LastName;
    private String AccountId;
    private String StoreId;
    private String ReturnUrl;
    private String sessId;

    public UserInfo(String email, String fsn, String firstName, String lastName, String accountId, String storeId, String returnUrl, String sessId) {
        Email = email;
        FSN = fsn;
        FirstName = firstName;
        LastName = lastName;
        AccountId = accountId;
        StoreId = storeId;
        ReturnUrl = returnUrl;
        this.sessId = sessId;
    }

    public String getEmail() {
        return Email;
    }

    public String getFSN() {
        return FSN;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getAccountId() {
        return AccountId;
    }

    public String getStoreId() {
        return StoreId;
    }

    public String getReturnUrl() {
        return ReturnUrl;
    }

    public String getSessId() {
        return sessId;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "Email='" + Email + '\'' +
                ", FSN='" + FSN + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", AccountId='" + AccountId + '\'' +
                ", StoreId='" + StoreId + '\'' +
                ", ReturnUrl='" + ReturnUrl + '\'' +
                ", sessId='" + sessId + '\'' +
                '}';
    }
}
