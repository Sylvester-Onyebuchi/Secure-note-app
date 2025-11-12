package com.sylvester.dempproject.service;


import com.sylvester.dempproject.dto.*;
import com.sylvester.dempproject.models.User;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    Response createUser(UserRequest userRequest);
    Response getUser(Long id);
    Response updateUserRole(Long id, UserRoleUpdate roleUpdate);
    Response deleteUser(Long id);
    Response updateUser(Long id,UserRequest userRequest);
    Response verifyUser(VerifyUserRequest verifyUser, Long userId);
    Response checkIfVerified(Long userId);
    Response generateNewVerificationCode(ResendVerificationRequest verificationRequest);

    GoogleAuthenticatorKey generate2FASecret(Long userId);

    boolean validate2FACode(Long userId, int code);

    void enable2FA(Long userId);

    void disable2FA(Long userId);

    User loggedInUser();

    Optional<User> findByEmail(String email);

    User registerUser(User user);

    void updatePassword(Long userId, String password);

    void generatePasswordResetToken(String email);

    void resetPassword(String token, String newPassword);
}
