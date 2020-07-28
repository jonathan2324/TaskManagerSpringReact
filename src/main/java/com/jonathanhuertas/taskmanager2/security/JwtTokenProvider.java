package com.jonathanhuertas.taskmanager2.security;

import com.jonathanhuertas.taskmanager2.domain.User;
import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.jonathanhuertas.taskmanager2.security.SecurityConstants.EXPIRATION_TIME;
import static com.jonathanhuertas.taskmanager2.security.SecurityConstants.SECRET;

/*
This class has three main functions:
-generate token
-validate token
-get user Id from token -> use customDetailsService to validate we have the correct user
 */
@Component
public class JwtTokenProvider {
    public String generateToken(Authentication authentication){
        User user = (User) authentication.getPrincipal(); //need to cast because authentication.getPrincipal() is of type UserDetails
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        String userId = Long.toString(user.getId()); //we have to cast because the token is a String but the id is a Long

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", (Long.toString(user.getId())));
        claims.put("username", user.getUsername());
        claims.put("fullName", user.getFullName());

        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    //validate Token
    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        } catch(SignatureException ex){
            System.out.println("Invalid JWT signature");
        } catch(MalformedJwtException ex){
            System.out.println("Invalid JWT token");
        } catch( ExpiredJwtException ex){
            System.out.println("Expired JWT token");
        } catch(UnsupportedJwtException ex){
            System.out.println("Unsupported JWT token");
        } catch(IllegalArgumentException ex){
            System.out.println("JWT claims string is empty");
        }
        return false;
    }

    //Get user id from token
    public Long getUserIdFromJWT(String token){
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();

        String id = (String) claims.get("id");

        return Long.parseLong(id);
    }

}
