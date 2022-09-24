package com.tuan03.SpringbootJWT.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tuan03.SpringbootJWT.entity.User;
import com.tuan03.SpringbootJWT.repository.UserRepository;
import com.tuan03.SpringbootJWT.service.UserDetailsImpl;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

        @Autowired
        UserRepository userRepository;

        @Override
        @Transactional
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("Not found user " + username));
                return UserDetailsImpl.build(user);
        }

}
