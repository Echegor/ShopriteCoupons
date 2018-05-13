http://coupons.shoprite.com/scripts/web.js
window.angular.module("couponsApp", []);
!function() {
    window.angular.module("couponsApp").factory("couponControllerService", function() {
        return {
            getCategoriesSet: function(n) {
                var e = n.reduce(function(n, e) {
                    return n[e.Category] = n[e.Category] ? ++n[e.Category] : 1,
                    n
                }, {})
                  , r = [];
                for (var t in e)
                    r.push({
                        name: t,
                        count: e[t]
                    });
                return r.sort(function(n, e) {
                    return n.name < e.name ? -1 : n.name > e.name ? 1 : 0
                }),
                r
            },
            getBrandsSet: function(n) {
                var e = n.reduce(function(n, e) {
                    return n[e.brand_name] = n[e.brand_name] ? ++n[e.brand_name] : 1,
                    n
                }, {})
                  , r = [];
                for (var t in e)
                    r.push({
                        name: t,
                        count: e[t]
                    });
                return r.sort(function(n, e) {
                    return n.name < e.name ? -1 : n.name > e.name ? 1 : 0
                }),
                r
            },
            calculateTotalPages: function(n, e) {
                return Math.ceil(n.length / e)
            },
            calculateTotalSavings: function(n) {
                return n.reduce(function(n, e) {
                    return n + Number(e.value)
                }, 0)
            }
        }
    })
}();
!function() {
    window.angular.module("couponsApp").factory("couponService", ["$http", "customerService", function(e, n) {
        var r = window._client.client
          , t = (window._client.connectionDetails,
        r.getTable("cp_query_read_metadata"))
          , o = null;
        function i(e) {
            var t = r.getTable("ecry_cp_query_coupon_history");
            return n.getFrequentShopperNumber().then(function(n) {
                return t.insert({
                    ppc_number: n,
                    coupon_type: e
                }).then(function(e) {
                    return Array.isArray(e) ? u(e) : []
                })
            })
        }
        function u(e) {
            return e.map(function(e) {
                return e.Category = c(e.Category),
                e
            })
        }
        function c(e) {
            return e.split(" ").map(function(e) {
                return (n = e)[0].toUpperCase() + n.slice(1).toLowerCase();
                var n
            }).join(" ")
        }
        return {
            getCoupons: function() {
                return n.isLoggedIn().then(function(n) {
                    return n ? e() : i()
                });
                function e() {
                    var e = r.getTable("ecry_cp_couponid_a_c_for_card");
                    return n.getFrequentShopperNumber().then(function(n) {
                        return window.promiseAll([t.insert({
                            ppc_number: n
                        }), e.insert({
                            ppc_number: n
                        })]).then(function(e) {
                            return function(e) {
                                o = e[0];
                                var n = e[1].available_ids_array || []
                                  , r = e[1].clipped_active_ids_array || []
                                  , t = o.reduce(function(e, n) {
                                    return e[n.coupon_id] = n,
                                    e
                                }, {});
                                return n.map(function(e) {
                                    var n = t[e];
                                    return n.clipped = !1,
                                    n.Category = c(n.Category),
                                    n
                                }).concat(r.map(function(e) {
                                    var n = t[e];
                                    return n.clipped = !0,
                                    n.Category = c(n.Category),
                                    n
                                })).filter(function(e) {
                                    return null !== e
                                })
                            }(e)
                        })
                    })
                }
                function i() {
                    var e = r.getTable("ecry_cp_couponid_for_card_new");
                    return new Promise(function(n) {
                        window.promiseAll([t.insert({
                            ppc_number: "all"
                        }), e.insert({
                            ppc_number: "all"
                        })]).then(function(e) {
                            var r = function(e) {
                                o = e[0];
                                var n = function(e) {
                                    if (e)
                                        return e.reduce(function(e, n) {
                                            return e[n] = n,
                                            e
                                        }, {});
                                    return []
                                }(e[1]);
                                return u(o.filter(function(e) {
                                    return n.hasOwnProperty(e.coupon_id)
                                }))
                            }(e);
                            n(r)
                        })
                    }
                    )
                }
            },
            getActiveClippedCoupons: function() {
                return i("clipped_active_ids")
            },
            getExpiredClippedCoupons: function() {
                return i("clipped_expired_ids")
            },
            getRedeemedClippedCoupons: function() {
                return i("clipped_redeemed_ids")
            },
            addToCard: function(e, n) {
                var t = r.getTable("ecry_cp_query_add_to_card");
                return new Promise(function(r, o) {
                    t.insert({
                        ppc_number: e,
                        coupon_id: n,
                        clip_source: "Web_SR"
                    }).done(function(e) {
                        !function(e, n, r) {
                            if (!0 === e.result)
                                n(e);
                            else {
                                var t = "";
                                switch (e.codes[0].code) {
                                case "000":
                                    t = "Application error.";
                                    break;
                                case "4100":
                                    t = "Coupon already on card or redeemed.";
                                    break;
                                case "4103":
                                    t = "Coupon has hit download limit.";
                                    break;
                                case "1205":
                                    t = "Card is invalid.";
                                    break;
                                case "1208":
                                    t = "Unable to find coupons for ID's provided.";
                                    break;
                                case "1210":
                                    t = "Unable to add coupons to card."
                                }
                                r("Error - " + t)
                            }
                        }(e, r, o)
                    }, function(e) {
                        var n, r, t;
                        r = o,
                        t = (n = e) + (n.request ? " - " + n.request.status : ""),
                        r("Error - " + t)
                    })
                }
                )
            },
            removeFromCard: function(n, r) {
                var t = "https://stagingcouponswest.azure-mobile.net/api/removeCouponFromPPC?ppc_number=" + n + "&coupon_id=" + r;
                return e.post(t, {}, {
                    headers: {
                        "X-ZUMO-APPLICATION": "mUJXkgoWiGZvlhUylskMCadqeKPMhj49",
                        "Content-Type": "Application/json"
                    }
                })
            }
        }
    }
    ])
}();
!function() {
    window.angular.module("couponsApp").factory("customerService", ["$http", "$window", function(n, t) {
        var o = "https://wfsso.azurewebsites.net/api/v1/sp/sso"
          , e = "?Authorization=c5ec5f48-9d97-40f2-875c-2af6a1f994c7&returnUrl="
          , r = (window.location,
        "coupon_sessionid")
          , i = window.$
          , u = {
            customer: {
                loggedIn: !1
            },
            isLoggedIn: function() {
                return c.then(function(n) {
                    return n.UserInfo && null !== n.UserInfo.FSN && "" !== n.UserInfo.FSN
                })
            },
            getCustomerInfo: function() {
                return c.then(function(n) {
                    return n.UserInfo
                })
            },
            getSSOUrl: l,
            getFrequentShopperNumber: function() {
                return c.then(function(n) {
                    return n.UserInfo.FSN
                })
            },
            redirectToSSO: a
        }
          , c = new Promise(function(i) {
            !function() {
                var n = t.location.search.substring(1).split("&").reduce(function(n, t) {
                    var o = t.split("=");
                    return n[o[0]] = o[1],
                    n
                }, {}).sessId;
                n && (o = n,
                localStorage.setItem(r, o));
                var o;
                var e = window.location.protocol + "//" + window.location.host + window.location.pathname;
                window.history && window.history.pushState({
                    path: e
                }, "", e)
            }(),
            n.get("https://shop.shoprite.com/chain/FBFB139/user/status", {
                withCredentials: !0
            }).then(function(n) {
                return n.data
            }).then(function(t) {
                if (t.IsSignedIn)
                    s(t).then(function(n) {
                        f(n),
                        i(n)
                    });
                else {
                    var u = localStorage.getItem(r);
                    u ? (c = u,
                    a = o + "/sessId/" + c + e + window.location,
                    n.get(a).then(function(n) {
                        return n.data
                    })).then(function(n) {
                        "Y" === n.Active ? (f(n),
                        i(n)) : (d(),
                        s(t).then(function(n) {
                            f(n),
                            i(n)
                        }))
                    }) : s(t).then(function(n) {
                        f(n),
                        i(n)
                    })
                }
                var c, a
            }).catch(function() {
                i({
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
                })
            })
        }
        );
        return function(n) {
            function t() {
                d();
                var n = document.createElement("iframe");
                n.setAttribute("src", "https://shop.shoprite.com/chain/FBFB139/User/SignOut"),
                n.setAttribute("style", "width: 0; height: 0; border: 0; border: none; position: absolute;"),
                n.onload = function() {
                    window.location.reload()
                }
                ,
                document.body.appendChild(n)
            }
            n.then(function(n) {
                var o = n.UserInfo.FirstName;
                o ? i(function() {
                    var n = "Welcome, " + o + "!"
                      , e = '<button class="menuItem__button signout-btn" role="button" aria-label="Sign Out">Sign Out</a>';
                    i(".authenticationControls__wrapper").html('<div class="header-content">' + n + " " + e + "</div>"),
                    i(".signout-btn").click(t)
                }) : n.UserInfo && n.UserInfo.FSN ? i(function() {
                    i(".authenticationControls__wrapper").html('<button class="menuItem__button signout-btn" role="button" aria-label="Sign Out">Sign Out</a>'),
                    i(".signout-btn").click(t)
                }) : i(function() {
                    i(".menuItem__button").click(a)
                })
            })
        }(c),
        u;
        function s(t) {
            var r = t.UserId
              , i = t.IsSignedIn ? 1 : 0
              , u = o + "/sessId/" + r + i + e + window.location;
            return n.get(u).then(function(n) {
                return n.data
            })
        }
        function a() {
            l().then(function(n) {
                t.location.href = n
            })
        }
        function l() {
            return c.then(function(n) {
                return n.SSOUrl ? decodeURIComponent(n.SSOUrl) : ""
            })
        }
        function f(n) {
            n.UserInfo && null !== n.UserInfo.FSN && "" !== n.UserInfo.FSN && (u.customer.loggedIn = !0)
        }
        function d() {
            localStorage.removeItem(r)
        }
    }
    ])
}();
!function() {
    window.angular.module("couponsApp").controller("CouponController", ["$scope", "$timeout", "$rootScope", "$filter", "couponControllerService", "couponService", "customerService", function(e, t, n, o, r, a, s) {
        e.COUPONS_PER_PAGE = 12,
        e.customer = s.customer,
        e.initialCoupons = null,
        e.tabCoupons = null,
        e.coupons = null,
        e.categories = [],
        e.brands = [],
        e.totalSavings = function() {
            return r.calculateTotalSavings(e.coupons || []) || 0
        }
        ,
        e.tabs = [{
            title: "All Coupons",
            fn: function() {
                return !0
            }
        }, {
            title: "For You",
            auth: !0,
            fn: function(e) {
                return e.targeting_buckets.indexOf("ForYou") > -1
            }
        }, {
            title: "In Circular",
            fn: function(e) {
                return "Y" === e.featured
            }
        }],
        e.mobileTabs = e.tabs.concat([{
            title: "History - Clipped Coupons",
            auth: !0,
            query: "clipped_active_ids"
        }, {
            title: "History - Redeemed Coupons",
            auth: !0,
            query: "clipped_redeemed_ids"
        }, {
            title: "History - Expired Coupons",
            auth: !0,
            query: "clipped_expired_ids"
        }]),
        e.selectedTab = e.tabs[0],
        e.onTabChange = function(t) {
            t ? e.selectedTab !== t && (e.selectedTab = t,
            l(e.initialCoupons.filter(t.fn))) : e.selectedTab.fn ? l(e.initialCoupons.filter(e.selectedTab.fn)) : e.handleClippedCouponsTab(e.selectedTab.query)
        }
        ,
        e.handleClippedCouponsTab = function(t) {
            switch (e.selectedTab.query || (e.selectedTab = "Coupon History"),
            e.coupons = null,
            e.categories = null,
            e.brands = null,
            t) {
            case "clipped_active_ids":
                a.getActiveClippedCoupons().then(function(t) {
                    var n = t.map(function(e) {
                        return e.clipped = !0,
                        e
                    });
                    l(n),
                    e.$apply()
                });
                break;
            case "clipped_redeemed_ids":
                a.getRedeemedClippedCoupons().then(function(t) {
                    l(t),
                    e.$apply()
                });
                break;
            case "clipped_expired_ids":
                a.getExpiredClippedCoupons().then(function(t) {
                    l(t),
                    e.$apply()
                })
            }
        }
        ,
        e.searchText = "",
        e.searchCoupons = function(t) {
            t.preventDefault(),
            e.clearCategories(),
            e.clearBrands(),
            t.target.children[1].children[0].children[0] ? t.target.children[1].children[0].children[0].blur() : t.target.children[1].children[1].children[0].blur(),
            e.coupons = o("filter")(e.tabCoupons, e.searchText),
            e.currentPage = 0,
            e.totalPages = r.calculateTotalPages(e.coupons, e.numPerPage),
            n.$emit("couponsDisplayed")
        }
        ,
        e.numPerPage = e.COUPONS_PER_PAGE,
        e.currentPage = -1,
        e.totalPages = 0,
        e.firstPage = function(t) {
            t.preventDefault(),
            0 !== e.currentPage && (e.currentPage = 0,
            n.$emit("couponsDisplayed"))
        }
        ,
        e.previousPage = function(t) {
            t.preventDefault(),
            e.currentPage > 0 && (e.currentPage--,
            n.$emit("couponsDisplayed"))
        }
        ,
        e.nextPage = function(t) {
            t.preventDefault(),
            e.currentPage < e.totalPages - 1 && (e.currentPage++,
            n.$emit("couponsDisplayed"))
        }
        ,
        e.onShowAll = function(t) {
            t.preventDefault(),
            e.clearCategories(),
            e.clearBrands(),
            e.searchText = "",
            e.coupons = e.tabCoupons,
            e.numPerPage = e.initialCoupons.length,
            e.currentPage = 0,
            e.totalPages = 1,
            n.$emit("couponsDisplayed")
        }
        ,
        e.onReset = function(t) {
            t.preventDefault(),
            e.clearCategories(),
            e.clearBrands(),
            e.searchText = "",
            e.coupons = e.tabCoupons,
            e.numPerPage = e.COUPONS_PER_PAGE,
            e.currentPage = 0,
            e.totalPages = r.calculateTotalPages(e.coupons, e.COUPONS_PER_PAGE),
            n.$emit("couponsDisplayed")
        }
        ,
        e.sorts = [{
            name: "Brand",
            orderBy: "brand_name"
        }, {
            name: "Most Popular",
            orderBy: "total_downloads/1",
            reverse: !0
        }, {
            name: "Most Recent",
            orderBy: "pos_live_date",
            reverse: !0
        }, {
            name: "Expiration Date",
            orderBy: function(e) {
                return new Date(e.expiration_date)
            }
        }, {
            name: "Amount",
            orderBy: "value/1",
            reverse: !0
        }, {
            name: "Category",
            orderBy: "Category"
        }];
        var c = [{
            name: "Most Relevant"
        }].concat(e.sorts);
        e.selectedSort = e.sorts[0],
        s.isLoggedIn().then(function(t) {
            t && (e.sorts = c,
            e.selectedSort = e.sorts[0])
        }),
        e.onSortChange = function() {
            e.currentPage = 0,
            n.$emit("couponsDisplayed")
        }
        ;
        var u = {}
          , i = {};
        function l(t) {
            e.categories = r.getCategoriesSet(t).map(function(e) {
                return e.selected = !1,
                e
            }),
            e.brands = r.getBrandsSet(t).map(function(e) {
                return e.selected = !1,
                e
            }),
            e.totalPages = r.calculateTotalPages(t, e.numPerPage),
            e.coupons = t,
            e.tabCoupons = t,
            e.searchText = "",
            e.currentPage = 0,
            n.$emit("couponsDisplayed")
        }
        function p(t, o) {
            e.coupons = e.tabCoupons.filter(function(e) {
                return Object.keys(t).length && Object.keys(o).length ? t.hasOwnProperty(e.Category) || o.hasOwnProperty(e.brand_name) : Object.keys(t).length ? t.hasOwnProperty(e.Category) : !Object.keys(o).length || o.hasOwnProperty(e.brand_name)
            }),
            e.totalPages = r.calculateTotalPages(e.coupons, e.numPerPage),
            e.currentPage = 0,
            n.$emit("couponsDisplayed")
        }
        e.onCategoryChange = function() {
            u = e.categories.filter(function(e) {
                return e.selected
            }).reduce(function(e, t) {
                return e[t.name] = !0,
                e
            }, {}),
            e.searchText = "",
            p(u, i)
        }
        ,
        e.onBrandChange = function() {
            i = e.brands.filter(function(e) {
                return e.selected
            }).reduce(function(e, t) {
                return e[t.name] = !0,
                e
            }, {}),
            e.searchText = "",
            p(u, i)
        }
        ,
        e.clearFiltersAndSearch = function() {
            e.clearCategories(),
            e.clearBrands(),
            e.searchText = "",
            e.coupons = e.tabCoupons
        }
        ,
        e.clearCategories = function() {
            Object.keys(u).length && (u = {},
            e.categories = e.categories.map(function(e) {
                return e.selected = !1,
                e
            }),
            p(u, i))
        }
        ,
        e.clearBrands = function() {
            Object.keys(i).length && (i = {},
            e.brands = e.brands.map(function(e) {
                return e.selected = !1,
                e
            }),
            p(u, i))
        }
        ,
        e.isDatePassed = function(e) {
            return new Date(e) <= Date.now()
        }
        ,
        e.filterAuthTabs = function(t) {
            return !t.auth || t.auth === e.loggedIn
        }
        ,
        e.closeMobileFilters = function() {
            window.$(".mobile-sort-filter-options-container").fadeOut(400),
            window.$("body").removeClass("modal-open")
        }
        ,
        a.getCoupons().then(function(t) {
            e.initialCoupons = t,
            l(t),
            e.$apply()
        })
    }
    ])
}();
window.angular.module("couponsApp").component("loadToCardBtn", {
    bindings: {
        coupon: "<",
        initialCoupons: "<"
    },
    controller: ["$scope", "$window", "couponService", "customerService", function(o, n, c, d) {
        var i = this;
        o.customer = d.customer,
        o.navigateToSSOUrl = d.redirectToSSO,
        o.addToCard = function(a) {
            i.coupon.addingToCard = !0,
            d.getFrequentShopperNumber().then(function(d) {
                c.addToCard(d, a).then(function() {
                    o.$apply(function() {
                        i.coupon.addingToCard = !1,
                        i.coupon.clipped = !0
                    })
                }).catch(function(c) {
                    o.$apply(function() {
                        i.coupon.addingToCard = !1,
                        n.alert(c)
                    })
                })
            })
        }
        ,
        o.removeFromCard = function(o) {
            i.coupon.addingToCard = !0,
            d.getFrequentShopperNumber().then(function(d) {
                c.removeFromCard(d, o).then(function() {
                    i.coupon.addingToCard = !1,
                    i.coupon.clipped = !1,
                    i.initialCoupons.filter(function(n) {
                        return n.coupon_id === o
                    })[0].clipped = !1
                }).catch(function(o) {
                    i.coupon.addingToCard = !1,
                    i.coupon.clipped = !0,
                    n.alert(o)
                })
            })
        }
    }
    ],
    template: function() {
        return '<div ng-if="customer.loggedIn" ng-hide="{{ $ctrl.coupon.clipped === undefined }}">                    <a class="available-to-clip" ng-hide="$ctrl.coupon.clipped || $ctrl.coupon.addingToCard" ng-click="addToCard($ctrl.coupon.coupon_id)">                        Load to Card                    </a>                    <a class="processing-clip" ng-show="$ctrl.coupon.addingToCard" href="">                        Loading...                    </a>                    <div class="already-clipped" ng-show="$ctrl.coupon.clipped" ng-hide="$ctrl.coupon.addingToCard || !$ctrl.coupon.clipped" >                        <span>Loaded - </span>                        <a class="already-clipped-unclip" ng-click="removeFromCard($ctrl.coupon.coupon_id)">Unclip</a>                    </div>                </div>                <a ng-if="!customer.loggedIn" class="login-to-load" ng-click="navigateToSSOUrl()">Login to Load</a>'
    }
});
!function() {
    window.angular.module("couponsApp").run(["$rootScope", "$timeout", function(o, n) {
        o.$on("couponsDisplayed", function() {
            n(function() {
                window.$('[data-toggle="popover"]').popover({
                    trigger: "hover"
                })
            })
        })
    }
    ])
}();
window.angular.module("couponsApp").component("scrollToTopBtn", {
    controller: ["$element", function(o) {
        o.on("click", function() {
            window.$("html, body").animate({
                scrollTop: 0
            }, "slow")
        })
    }
    ],
    template: function() {
        return '<button class="scroll-to-top-btn">↑</button>'
    }
});
window.angular.module("couponsApp").component("openMobileFiltersBtn", {
    controller: ["$element", function(n) {
        n.on("click", function() {
            window.$("html, body").animate({
                scrollTop: 0
            }, function() {
                window.$(".mobile-sort-filter-options-container").fadeIn(400, function() {
                    window.$(".inner-options-container").animate({
                        scrollTop: 0
                    }, "fast")
                })
            }),
            window.$("body").addClass("modal-open")
        })
    }
    ],
    template: function() {
        return '<div class="mobile-filters-btn-container">                        <button class="open-button"><h1>Filters</h1></button>                    </div>'
    }
});
!function() {
    window.angular.module("couponsApp").filter("trusted", ["$sce", function(t) {
        var n = document.createElement("div");
        return function(e) {
            return n.innerHTML = e,
            t.trustAsHtml(n.textContent)
        }
    }
    ])
}();
//# sourceMappingURL=web.js.map
