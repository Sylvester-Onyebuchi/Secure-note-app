package com.sylvester.dempproject.serviceimpl;

import com.sylvester.dempproject.service.TwoFAService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

@Service
public class TwoFAServiceImpl implements TwoFAService {

    private final GoogleAuthenticator googleAuthenticator;

    public TwoFAServiceImpl(GoogleAuthenticator googleAuthenticator) {
        this.googleAuthenticator = googleAuthenticator;
    }
    public TwoFAServiceImpl() {
        this.googleAuthenticator = new GoogleAuthenticator();
    }


    @Override
    public GoogleAuthenticatorKey generateSecret(){
        return googleAuthenticator.createCredentials();
    }

    @Override
    public String getQrCodeURL(GoogleAuthenticatorKey authKey, String username){
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("Secure Notes Application", username, authKey);
    }


    @Override
    public boolean verifyQrCode(String secret, int code){
        return googleAuthenticator.authorize(secret, code);
    }
}
