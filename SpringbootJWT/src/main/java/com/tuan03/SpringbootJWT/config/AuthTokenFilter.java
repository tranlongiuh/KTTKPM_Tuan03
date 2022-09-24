package com.tuan03.SpringbootJWT.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tuan03.SpringbootJWT.common.JWTUtils;
import com.tuan03.SpringbootJWT.service.impl.UserDetailsServiceImpl;

public class AuthTokenFilter extends OncePerRequestFilter {

        @Autowired
        private JWTUtils jwtUtils;

        @Autowired
        private UserDetailsServiceImpl userDetailsServiceImpl;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                        FilterChain filterChain) throws ServletException, IOException {
                try {
                        String jwt = parseJWT(request);
                        if (jwt != null && jwtUtils.validateJWTToken(jwt)) {
                                String username = jwtUtils.getUserNameFromJWTToken(jwt);
                                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
                                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                                userDetails, null, userDetails.getAuthorities());
                                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                                SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                } catch (Exception e) {
                        System.err.println(e.getMessage());
                }
                filterChain.doFilter(request, response);

        }

        private String parseJWT(HttpServletRequest request) {
                String headerAuth = request.getHeader("Authorization");
                if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
                        return headerAuth.substring(7, headerAuth.length());
                }
                return null;
        }

}
