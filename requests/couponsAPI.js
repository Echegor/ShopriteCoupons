/**
 * This file is replaced during the build process with separate
 * files for servers 1,3 and 2,4
 Request URL: http://coupons.shoprite.com/js/couponsAPI/couponsAPI.js?t=1525878033494
 Request Method: GET
 Status Code: 200 OK (from memory cache)
 Remote Address: 69.25.71.53:80
 Referrer Policy: no-referrer-when-downgrade
 Accept-Ranges: bytes
 Content-Length: 904
 Content-Type: application/x-javascript
 Date: Sun, 13 May 2018 01:59:15 GMT
 ETag: "0951d7ba6e7d31:0"
 Last-Modified: Wed, 09 May 2018 15:00:34 GMT
 Server: Microsoft-IIS/7.5
 X-Powered-By: ASP.NET
 Accept: \*
 Accept-Encoding: gzip, deflate
 Accept-Language: en-US,en;q=0.9
 Connection: keep-alive
 Host: coupons.shoprite.com
 Referer: http://coupons.shoprite.com/
 User-Agent: Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Mobile Safari/537.36
 t: 1525878033494
 */
(function() {
    // Servers 1, 3
    // var url = 'https://todolisthq.azure-mobile.net/';
    // var key = 'floSujxejWbvUVqthQqscVPMMBUoHe75';
    // var userTable = 'CP_Control_User';

    // Servers 2, 4
    var url = 'https://stagingcouponswest.azure-mobile.net/';
    var key = 'mUJXkgoWiGZvlhUylskMCadqeKPMhj49';
    var userTable = 'Staging_CP_Control_User';

    // Mobile
    // var url = 'https://couponprodwest.azure-mobile.net/';
    // var key = 'noowhTBIYfzVrXOcFrNSwIFbkMoqRh19';
    // var userTable = 'CP_Control_User';

    var WindowsAzure = window.WindowsAzure;

    window._client = {
        client: new WindowsAzure.MobileServiceClient(url, key),
        connectionDetails: {
            url: url,
            key: key,
            userTable: userTable
        }
    };
})();
