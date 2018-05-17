package com.archelo.coupons.urls;

public class AzureUrls {

    public static final String SESSION = "https://wfsso.azurewebsites.net/api/v1/sp/sso/sessId/";
    public static final String SIGN_IN = "https://wfsso.azurewebsites.net/SRSSO/SignIn";
    public static final String USER_INFO = "https://wfsso.azurewebsites.net/SRSSO/GetUserInfo";
    public static final String RETURN_FROM_SIGN_IN = "https://wfsso.azurewebsites.net/SRSSO/ReturnFromSignIn?success=True?binding=urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
    public static final String AVAILABLE_COUPONS = "https://stagingcouponswest.azure-mobile.net/tables/ecry_cp_couponid_a_c_for_card";
    public static final String COUPONS_METADATA = "https://stagingcouponswest.azure-mobile.net/tables/ecry_cp_couponid_a_c_for_card";
    public static final String COUPONS_ADD = "https://stagingcouponswest.azure-mobile.net/tables/ecry_cp_query_add_to_card";
    public static final String SIGN_IN_SUCCESS = "https://wfsso.azurewebsites.net/SRSSO/ReturnFromSignIn?success=True?binding=urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";


}
