
//This file is named CustomerService
(function() {
    window.angular
        .module('couponsApp')
        .factory('customerService', ['$http', '$window', customerService]);

    function customerService($http, $window) {
        var baseUrl = 'https://wfsso.azurewebsites.net/api/v1/sp/sso';
        var authPartUrl =
            '?Authorization=c5ec5f48-9d97-40f2-875c-2af6a1f994c7&returnUrl=';
        var authUrl = baseUrl + authPartUrl + window.location;
        var localStorageKey = 'coupon_sessionid';
        var $ = window.$;

        var service = {
            customer: {
                loggedIn: false
            },
            isLoggedIn: isLoggedIn,
            getCustomerInfo: getCustomerInfo,
            getSSOUrl: getSSOUrl,
            getFrequentShopperNumber: getFrequentShopperNumber,
            redirectToSSO: redirectToSSO
        };

        var doneFetchingAuthData = new Promise(function(resolve) {
            // if there is a session id from a previous login attempt, store it
            handleURLSessionId();

            // request user status from storefront
            checkMWGAuthStatus()
                .then(function(user) {
                    if (user.IsSignedIn) {
                        getSAMLWithUser(user).then(function(authResponse) {
                            handleAuthResponse(authResponse);
                            resolve(authResponse);
                        });
                    } else {
                        var localSessionId = getLocalSessionId();

                        // if we have a previously stored session id
                        // check it to see if it's still valid
                        if (localSessionId) {
                            getSAMLWithSession(localSessionId).then(function(
                                authResponse
                            ) {
                                // if the user is signed in, get their info
                                if (authResponse.Active === 'Y') {
                                    handleAuthResponse(authResponse);
                                    resolve(authResponse);
                                } else {
                                    // delete localstorage key if it's invalid
                                    removeLocalSessionId();

                                    // get new session
                                    getSAMLWithUser(user).then(function(
                                        authResponse
                                    ) {
                                        handleAuthResponse(authResponse);
                                        resolve(authResponse);
                                    });
                                }
                            });
                        } else {
                            // user is not logged in
                            getSAMLWithUser(user).then(function(authResponse) {
                                handleAuthResponse(authResponse);
                                resolve(authResponse);
                            });
                        }
                    }
                })
                .catch(function() {
                    var nullData = {
                        SSOUrl: null,
                        UserInfo: {
                            Email: null,
                            FSN: null,
                            AccountId: null,
                            ReturnUrl: null,
                            sessId: null
                        },
                        Active: null,
                        Message: null
                    };
                    resolve(nullData);
                });
        });

        initializeHeader(doneFetchingAuthData);

        return service;

        function getSAMLWithSession(sessionId) {
            var url =
                baseUrl +
                '/sessId/' +
                sessionId +
                authPartUrl +
                window.location;

            return $http
                .get(url)
                .then(function(res) {
                    return res.data;
                });
        }

        function getSAMLWithUser(user) {
            var userId = user.UserId;
            var flag = user.IsSignedIn ? 1 : 0;
            var url =
                baseUrl +
                '/sessId/' +
                userId +
                flag +
                authPartUrl +
                window.location;

            return $http
                .get(url)
                .then(function(res) {
                    return res.data;
                });
        }

        function checkMWGAuthStatus() {
            return $http
                .get('https://shop.shoprite.com/chain/FBFB139/user/status', {
                    withCredentials: true
                })
                .then(function(res) {
                    return res.data;
                });
        }

        function isLoggedIn() {
            return doneFetchingAuthData.then(function(authResponse) {
                return (
                    authResponse.UserInfo &&
                    authResponse.UserInfo.FSN !== null &&
                    authResponse.UserInfo.FSN !== ''
                );
            });
        }

        function getCustomerInfo() {
            return doneFetchingAuthData.then(function(authResponse) {
                return authResponse.UserInfo;
            });
        }

        function redirectToSSO() {
            getSSOUrl().then(function(url) {
                $window.location.href = url;
            });
        }

        function getSSOUrl() {
            return doneFetchingAuthData.then(function(authResponse) {
                return authResponse.SSOUrl
                    ? decodeURIComponent(authResponse.SSOUrl)
                    : '';
            });
        }

        function getFrequentShopperNumber() {
            return doneFetchingAuthData.then(function(authResponse) {
                return authResponse.UserInfo.FSN;
            });
        }

        // Converts URL params into an object for lookup
        function getURLSearchObject() {
            var rawQuery = $window.location.search.substring(1);

            return rawQuery.split('&').reduce(function(memo, term) {
                var keyValuePair = term.split('=');
                memo[keyValuePair[0]] = keyValuePair[1];
                return memo;
            }, {});
        }

        function handleAuthResponse(authResponse) {
            if (
                authResponse.UserInfo &&
                authResponse.UserInfo.FSN !== null &&
                authResponse.UserInfo.FSN !== ''
            ) {
                service.customer.loggedIn = true;
            }
        }

        function handleURLSessionId() {
            // store session id if it exists in url
            var sessionId = getURLSearchObject().sessId;

            if (sessionId) {
                storeSessionId(sessionId);
            }

            // remove search param from url
            var newurl =
                window.location.protocol +
                '//' +
                window.location.host +
                window.location.pathname;

            // check in case browser doesn't support history
            if (window.history) {
                window.history.pushState({ path: newurl }, '', newurl);
            }

            return sessionId;
        }

        function storeSessionId(id) {
            localStorage.setItem(localStorageKey, id);
        }

        function getLocalSessionId() {
            return localStorage.getItem(localStorageKey);
        }

        function removeLocalSessionId() {
            localStorage.removeItem(localStorageKey);
        }

        function initializeHeader(doneFetchingAuthData) {
            doneFetchingAuthData.then(function(authResponse) {
                var name = authResponse.UserInfo.FirstName;
                if (name) {
                    $(function() {
                        var welcomeMessage = getWelcomeMessage(name);
                        var signoutLink = getSignoutLink();
                        $('.authenticationControls__wrapper').html(
                            '<div class="header-content">' +
                                welcomeMessage +
                                ' ' +
                                signoutLink +
                                '</div>'
                        );
                        $('.signout-btn').click(onSignOutClick);
                    });
                } else if (authResponse.UserInfo && authResponse.UserInfo.FSN) {
                    $(function() {
                        $('.authenticationControls__wrapper').html(
                            getSignoutLink()
                        );
                        $('.signout-btn').click(onSignOutClick);
                    });
                } else {
                    $(function() {
                        $('.menuItem__button').click(redirectToSSO);
                    });
                }
            });

            function getWelcomeMessage(name) {
                return 'Welcome, ' + name + '!';
            }

            function getSignoutLink() {
                return '<button class="menuItem__button signout-btn" role="button" aria-label="Sign Out">Sign Out</a>';
            }

            function onSignOutClick() {
                removeLocalSessionId();

                // Create iframe of MWG storefront logout
                var logoutFrame = document.createElement('iframe');
                logoutFrame.setAttribute(
                    'src',
                    'https://shop.shoprite.com/chain/FBFB139/User/SignOut'
                );
                logoutFrame.setAttribute(
                    'style',
                    'width: 0; height: 0; border: 0; border: none; position: absolute;'
                );
                logoutFrame.onload = function() {
                    window.location.reload();
                };
                document.body.appendChild(logoutFrame);
            }
        }
    }
})();
