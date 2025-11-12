package com.sylvester.dempproject.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Response {
    private String message;
    private Object data;
    private String jwtToken;
    private String username;
    List<String> roles;

    public Response(String message, Object data,String jwtToken, String username,List<String> roles) {
        this.message = message;
        this.data = data;
        this.jwtToken = jwtToken;
        this.username = username;
        this.roles = roles;

    }
}
