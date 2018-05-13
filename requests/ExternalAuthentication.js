var logOn = {
  StoreGroupId: "",
  Email: "",
  Password: "",
  ExternalUserAuthenticationToken: "",
  ExternalUserAuthenticationProviderKey: "",
  ExternalUserFirstName: "",
  __RequestVerificationToken: "",
  ReturnUrl: "",
  ExternalProfileImageUrl: "",
  AuthenticationType : ""
};

var pressEnterToSignIn = false;

var externalAuthenticationData = {
  // Used if the user is redirected from the SignIn page and has already chosen to register with external authentication provider
  registerWithApplication: null,

  // Object where we can store the appId and the name of the object containing the external authentication provider functions
  externalAuthenticationPlugins: {}
};
window.ExternalAuthenticationData = externalAuthenticationData;

var Authenticate = {
  ariaHide: function (selector) {
    $(selector).attr('aria-hidden', 'true').attr('role', 'presentation');
  },
  ariaShow: function (selector) {
    $(selector).attr('aria-hidden', 'false').removeAttr('role');
  },
  readElement: function (selector) {
    $(selector).attr('role', 'alert').attr('aria-live', 'assertive');
    $(selector).hide().show();
    $(selector).removeAttr('role').removeAttr('aria-live');
  },
  validateFields: function (email, password) {
    var hasError = false;
    /* jshint ignore:start */
    var emailRegEx = /^(([^<>()\[\]\\.,;:\s@\"]+(\.[^<>()\[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[(2([0-4]\d|5[0-5])|1?\d{1,2})(\.(2([0-4]\d|5[0-5])|1?\d{1,2})){3} \])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/i;
    /* jshint ignore:end */
    $('.dynamic').hide();
    if (email === "") {
      $('#EmailValidation').html($('#Email').attr('data-val-required'));
      $('#EmailValidation').show();
      hasError = true;
    }
    else if (!emailRegEx.exec(email)) {
      $('#EmailValidation').html($('#Email').attr('data-val-regex'));
      $('#EmailValidation').show();
      hasError = true;
    }
    if (password === "") {
      $('#PasswordValidation').html($('#Password').attr('data-val-required'));
      $('#PasswordValidation').show();
      hasError = true;
    }
    if (hasError) {
      return false;
    }
    return true;

  },

  handleValidatePasswordTrueResponse: function (email) {
    $("#ReAuthEmail").val(email);
    $("#ReAuthPassword").val("");
    $("#ReAuthEmail").prop('disabled', true);
    $('.without-email').hide();
    $('.with-email').show();

    pressEnterToSignIn = true;
  },

  handeValidatePasswordFalseResponse: function () {
    $('.without-email').show();
    $('.with-email').hide();
    document.getElementById("ReAuthPassword").value = "";
    document.getElementById("ReAuthEmail").disabled = false;

    pressEnterToSignIn = false;
  },

  handlePasswordRequiredResponse: function(response, imageUrl, email) {
    if (response.PasswordValidationRequired === true) {
      Authenticate.handleValidatePasswordTrueResponse(email);
    }
    else {
      Authenticate.handeValidatePasswordFalseResponse(response);
    }
  },

  displayResponseMessage: function (response, imageUrl) {
    var alertSelector = null;

    if (response.Message != null) {
      $("#Message").html(response.Message);
    }

    $("#Profile").attr('src', imageUrl);

    if (response.DisplayMessageAsError === true) {
      if (response.ErrorMessage != null) {
        $('#ErrorValidation').html(response.ErrorMessage);
        $('#ErrorValidation').show();
      }
      alertSelector = '#ErrorValidation';
    }
    else {
      $("#Message").removeClass('validation-summary-errors');
      alertSelector = '#Message';
    }

    Authenticate.showExternalAuthDialog(alertSelector);
  },

  postToken: function (provider, email, token, password, imageUrl) {

    /* This adds information about which external authentication provider is currently used for loggin in
     *  The provider name is then sent as part of the url, when redirecting to the registration page
     */
    $('#externalRegisterUrl').data('authenticationprovider', logOn.ExternalUserAuthenticationProviderKey);

    logOn.Email = email;
    logOn.Password = password;
    logOn.ExternalUserAuthenticationToken = token;
    logOn.__RequestVerificationToken = $('[name="__RequestVerificationToken"]').first().val();
    logOn.StoreGroupId = $('#StoreGroupId').val();
    logOn.ReturnUrl = $('#ReturnUrl').val();
    logOn.AuthenticationType = "";

    var authUrl = window.configuration.applicationRoot + "User/AuthenticateWithToken";
    if ($('.without-email').css('display') !== 'block') {
      authUrl += "?emailFound=true";
    }

    $.ajax({
      type: "POST",
      data: logOn,
      url: authUrl,
      success: function (response) {
        if (response.RedirectUrl != null) {
          window.location = response.RedirectUrl;
          return;
        }

        if (response.PasswordRequired === true) {
          Authenticate.handlePasswordRequiredResponse(response, imageUrl, email);
        }

        Authenticate.displayResponseMessage(response, imageUrl);
      },
      error: function (jqXHR, textStatus, errorThrown) {
        window.location = window.configuration.applicationRoot + "Error";
      }
    });
  },

  postReauthenticateToken: function (provider, email, token, password, imageUrl) {
    logOn.Email = $("#Email").val();
    logOn.Password = password;
    logOn.ExternalUserAuthenticationProviderKey = provider;
    logOn.ExternalUserAuthenticationToken = token;
    logOn.__RequestVerificationToken = $('[name="__RequestVerificationToken"]').first().val();
    logOn.StoreGroupId = $('#StoreGroupId').val();
    logOn.ReturnUrl = $('#ReturnUrl').val();
    logOn.AuthenticationType = "reauthenticate";
    $('.validation-summary-errors').remove();

    $.ajax({
      type: "POST",
      data: logOn,
      url: window.configuration.applicationRoot + "User/AuthenticateWithToken",
      success: function (response) {
        if (response.RedirectUrl != null) {
          window.location = response.RedirectUrl;
          return;
        }

        if (!response.AuthenticationSucceeded) {

          $("#ExternalAuthContainer").after(function () {
            var errorElement = $("<div class='validation-summary-errors reauthenticate-box'></div>").text(response.ErrorMessage);
            return errorElement;
          });
          return;
        }

        alert('A.OK');
      },
      error: function (jqXHR, textStatus, errorThrown) {
        window.location = window.configuration.applicationRoot + "Error";
      }
    });
  },

  closeExternalAuthDialog: function () {
    $('.dynamic').hide();
    $('#ErrorValidation').html("");
    $("#ReAuthEmail").val("");
    $('#LinkWithExternalIdDialog').dialog('close');

    Authenticate.ariaShow('#RegionHeader, #RegionContent, #RegionFooter');
    Authenticate.ariaHide('#LinkWithExternalIdDialog');

    pressEnterToSignIn = false;
  },

  showExternalAuthDialog: function (alertSelector) {

    $('#LinkWithExternalIdDialog').dialog({
        dialogClass: 'no-close',
        modal: true,
        resizable: false,
        draggable: false,
        width: 'auto',
        closeOnEscape: false
      });
      $(".ui-dialog-titlebar").hide();

      Authenticate.ariaHide('#RegionHeader, #RegionContent, #RegionFooter');
      Authenticate.ariaShow('#LinkWithExternalIdDialog');

      if (alertSelector !== undefined) {
        Authenticate.readElement(alertSelector);
      }
  },

  reAuthenticate: function () {
    var reAuthEmail = $('#ReAuthEmail').val();
    var reAuthPassword = $('#ReAuthPassword').val();

    if (Authenticate.validateFields(reAuthEmail, reAuthPassword)) {
      Authenticate.postToken(logOn.ExternalUserAuthenticationProviderKey,
                             reAuthEmail,
                             logOn.ExternalUserAuthenticationToken,
                             reAuthPassword,
                             logOn.ExternalProfileImageUrl);
    }
  }
};

function raiseInitSuccessEvent(message) {
  var event = $.Event('externalAuthenticationInitSuccess');

  // The value of message is returned from within the 'plugin.login()' function
  event.state = message;
  $(document).trigger(event);
}

function externalAuthError() {
  // TODO: implement external authentication error
};

function externalAuthSuccess(provider, email, firstName, lastName, picUrl, token) {
  logOn.ExternalUserAuthenticationProviderKey = provider;
  logOn.ExternalUserFirstName = firstName;

  // The picUrl is stored here so that it may be reused by functions which are not passed the picUrl as a param (ex: reAuthenticate)
  logOn.ExternalProfileImageUrl = picUrl;

  Authenticate.postToken(provider, email, token, "", logOn.ExternalProfileImageUrl);
};

function externalReauthError() {
  // Do nothing for now. The user will cancel the operation
};

function externalReauthSuccess(provider, email, firstName, lastName, picUrl, token) {
  Authenticate.postReauthenticateToken(provider, email, token, "", logOn.ExternalProfileImageUrl);
};

function externalAuthLogin(plugin) {
  plugin.login(externalAuthError, externalAuthSuccess);
};

function externalReauthLogin(plugin) {
  plugin.reLogin(externalReauthError, externalReauthSuccess);
};

function externalAuthInit(plugin, appId) {

  // The raiseInitSuccessEvent will be raised after the plugin.init event has finished
  plugin.init(appId, raiseInitSuccessEvent);
};

$(document).ready(function () {
  // Here we initialize all the external authentication plugins
  var externalAuthPlugins = window.ExternalAuthenticationData.externalAuthenticationPlugins;

  for (var plugin in externalAuthPlugins) {
    externalAuthInit(externalAuthPlugins[plugin]["jsPluginVar"], externalAuthPlugins[plugin]["appId"]);
  }
});

$(document).on('click', '#reAuthenticate', function () {
  Authenticate.reAuthenticate();
});

$(document).on('click', '#ReAuthBoxContent .cancel-button', function () {
  Authenticate.closeExternalAuthDialog();
  location.reload();
});

$(document).on('click', '#externalRegisterUrl', function () {
  var provider = $(this).data('authenticationprovider').toLowerCase();
  var baseRegisterUrl = $(this).data('baseregisterurl');

  location.href = baseRegisterUrl + "&application=" + provider;
});

$(document).on('click', '.ExternalAuthButton', function () {
  // First we get the name of the provider plugin
  var providerName = $(this).data('authenticationprovider');
  // Then we get the actual provider plugin object
  var provider = window[providerName];

  externalAuthLogin(provider);
});

$(document).on('click', '.ExternalReauthButton', function () {
  // First we get the name of the provider plugin
  var providerName = $(this).data('authenticationprovider');
  // Then we get the actual provider plugin object
  var provider = window[providerName];

  externalReauthLogin(provider);
});

$(document).on('input', '#ReAuthBoxContent input', function() {
  var validationField = $(this).parent().parent().find('.field-validation-error');
  var inputLenght = $(this).val().length;

  if (inputLenght > 0) {
    validationField.hide();
  }
  else {
    validationField.show();
  };
});

$(window).resize(function() {
  $('#LinkWithExternalIdDialog').dialog("option", "position", ['center', 'center']);
});

$(document).on('keydown', '.signInWithEnter', function (event) {
  if (event.keyCode == 13) {
    Authenticate.reAuthenticate();
  }
});