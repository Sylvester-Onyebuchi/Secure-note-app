package com.sylvester.dempproject.config;


import com.sylvester.dempproject.models.Role;
import com.sylvester.dempproject.models.Roles;
import com.sylvester.dempproject.models.User;
import com.sylvester.dempproject.repository.RoleRepository;
import com.sylvester.dempproject.security.jwt.JwtUtils;
import com.sylvester.dempproject.security.service.UserDetailsImpl;
import com.sylvester.dempproject.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private final UserService userService;

    @Autowired
    private final JwtUtils jwtUtils;

    @Autowired
    RoleRepository roleRepository;


    String username;
    String idAttributeKey;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        if ("github".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()) || "google".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = principal.getAttributes();
            String email = attributes.getOrDefault("email", "").toString();
            String name = attributes.getOrDefault("name", "").toString();
            if ("github".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
                username = attributes.getOrDefault("login", "").toString();
                idAttributeKey = "id";
            } else if ("google".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
                username = email.split("@")[0];
                idAttributeKey = "sub";
            } else {
                username = "";
                idAttributeKey = "id";
            }
            System.out.println("HELLO OAUTH: " + email + " : " + name + " : " + username);

            userService.findByEmail(email).ifPresentOrElse(user -> {
                        DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                                List.of(new SimpleGrantedAuthority(user.getRoles().getRoleType().name())),
                                attributes,
                                idAttributeKey
                        );
                        Authentication securityAuth = new OAuth2AuthenticationToken(
                                oauthUser,
                                List.of(new SimpleGrantedAuthority(user.getRoles().getRoleType().name())),
                                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
                        );
                        SecurityContextHolder.getContext().setAuthentication(securityAuth);
                    }, () -> {
                        User newUser = new User();
                        Optional<Roles> userRole = roleRepository.findByRoleType(Role.ROLE_USER);
                        if (userRole.isPresent()) {
                            newUser.setRoles(userRole.get());
                        } else {

                           throw new RuntimeException("Role not found");
                        }
                        newUser.setEmail(email);
                        newUser.setUsername(username);
                        userService.registerUser(newUser);
                        DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                                List.of(new SimpleGrantedAuthority(newUser.getRoles().getRoleType().name())),
                                attributes,
                                idAttributeKey
                        );
                        Authentication securityAuth = new OAuth2AuthenticationToken(
                                oauthUser,
                                List.of(new SimpleGrantedAuthority(newUser.getRoles().getRoleType().name())),
                                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
                        );
                        SecurityContextHolder.getContext().setAuthentication(securityAuth);
                    });
        }
        this.setAlwaysUseDefaultTargetUrl(true);


        DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();


        String email = (String) attributes.get("email");
        System.out.println("OAuth2LoginSuccessHandler: " + username + " : " + email);

        Set<SimpleGrantedAuthority> authorities = oauth2User.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority())).collect(Collectors.toSet());
        User user = userService.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found"));
        authorities.add(new SimpleGrantedAuthority(user.getRoles().getRoleType().name()));


        UserDetailsImpl userDetails = new UserDetailsImpl(
                null,
                username,
                email,
                null,
                false,
                authorities
        );


        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);


        String frontendUrl = "http://localhost:3000";
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                .queryParam("token", jwtToken)
                .build().toUriString();
        this.setDefaultTargetUrl(targetUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}