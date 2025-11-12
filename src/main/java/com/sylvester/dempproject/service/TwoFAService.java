package com.sylvester.dempproject.service;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

public interface TwoFAService {


    GoogleAuthenticatorKey generateSecret();

    String getQrCodeURL(GoogleAuthenticatorKey authKey, String username);

    boolean verifyQrCode(String secret, int code);
}
