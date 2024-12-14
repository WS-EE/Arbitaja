package com.arbitaja.backend.security;

import com.arbitaja.backend.users.dataobjects.Permission;
import com.arbitaja.backend.users.dataobjects.User;
import com.arbitaja.backend.users.repositories.PermissionRepository;
import com.arbitaja.backend.users.repositories.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private static final Logger log = LogManager.getLogger(MyUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        log.debug("Found user: {}", user.getUsername());
        Collection<GrantedAuthority> authorities = getAuthorities(user);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getSalted_password(),
                authorities
        );
    }

    private Collection<GrantedAuthority> getAuthorities(User user) {
        List<Permission> permissions = permissionRepository.findPermissionsByUserId(user.getId());

        return permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toSet());
    }
}
