package com.sylvester.dempproject.security.service;


import com.sylvester.dempproject.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;




public class UserDetailsImpl implements UserDetails {


    User user;

    public UserDetailsImpl(Object user, String username, String email, Object o, boolean b, Set<SimpleGrantedAuthority> authorities) {

    }

    public UserDetailsImpl(User user) {
        this.user = user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       return List.of(new SimpleGrantedAuthority(user.getRoles().getRoleType().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    public String getDisplayName(){
        return user.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return user.isAccountEnabled();
    }
}
