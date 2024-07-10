package com.kahiroshi.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtils {

    private static final String signKey = "kahiroshiempsystem";
    private static final Long expire = 3600000L;

    /**
     * Generate JWT Key
     * @return String JWT
     * */
    public static String generateKey(Map<String, Object> claims) {
        return Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signKey)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
    }

    /**
     * Parse JWT Key
     * @return Claims
     * */
    public static Claims parseKey(String key) {
        return Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(key)
                .getBody();
    }
}
