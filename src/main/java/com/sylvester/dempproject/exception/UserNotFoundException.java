package com.sylvester.dempproject.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userAlreadyExist) {
        super(userAlreadyExist);
    }
}
