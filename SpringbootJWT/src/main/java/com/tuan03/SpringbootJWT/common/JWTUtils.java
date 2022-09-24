package com.tuan03.SpringbootJWT.common;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.tuan03.SpringbootJWT.service.UserDetailsImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTUtils {
        @Value("${bezkoder.app.jwtSecret}")
        private String jwtSecret;

        @Value("${bezkoder.app.jwtExpirationMs}")
        private int jwtExpirationMs;

        public String genJWTToken(Authentication authentication) {
                UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
                return Jwts.builder().setSubject(userDetailsImpl.getUsername())
                                .setIssuedAt(new Date())
                                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                                .compact();
        }

        public String getUserNameFromJWTToken(String token) {
                return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
        }

        public boolean validateJWTToken(String authToken) throws Exception {

                try {
                        Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
                        return true;
                } catch (Exception e) {
                        System.err.println(e.getMessage());
                }
                // } catch (SignatureException e) {
                // System.err.printf("Invalid JWT signature: ", e.getMessage());
                // } catch (MalformedJwtException e) {
                // System.err.printf("Invalid JWT token: ", e.getMessage());
                // } catch (ExpiredJwtException e) {
                // System.err.printf("]WT token is expired: ", e.getMessage());
                // } catch (UnsupportedJwtException e) {
                // System.err.printf("JWT token is unsupported: ", e.getMessage());
                // } catch (IllegalArgumentException e) {
                // System.err.printf("JWT claims string is empty: ", e.getMessage());
                // }
                return false;
        }
}
