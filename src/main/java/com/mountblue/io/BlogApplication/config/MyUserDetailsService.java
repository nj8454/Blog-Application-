package com.mountblue.io.BlogApplication.config;

import com.mountblue.io.BlogApplication.entities.User;
import com.mountblue.io.BlogApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private UserRepository userRepo;

    @Autowired
    public MyUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    private UserPrincipal userPrincipal;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmailIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("UserName Not Found"));

        return new UserPrincipal(user);
    }
}
