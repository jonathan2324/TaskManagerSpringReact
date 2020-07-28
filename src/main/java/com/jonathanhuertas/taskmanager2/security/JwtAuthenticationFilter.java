package com.jonathanhuertas.taskmanager2.security;

import com.jonathanhuertas.taskmanager2.domain.User;
import com.jonathanhuertas.taskmanager2.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static com.jonathanhuertas.taskmanager2.security.SecurityConstants.HEADER_STRING;
import static com.jonathanhuertas.taskmanager2.security.SecurityConstants.TOKEN_PREFIX;


/*
This custom filter extends OncePerRequestFilter which means it will be called for each request only once
this will later be used before the UsernamePasswordAuthenticationFilter in spring security config

this loads the User information into the SecurityContext

 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        try{

            String jwt = getJWTFromRequest(httpServletRequest);

            if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)){
                Long userId = tokenProvider.getUserIdFromJWT(jwt);
                User userDetails = customUserDetailsService.loadUserById(userId);

                //UsernamePasswordAuthenticationToken is an instance of Authentication interface
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());

                //Stores additional details about the authentication request. These might be an IP address, certificate serial number etc.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                //System.out.println(authentication);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }

        } catch(Exception ex){

            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }

    private String getJWTFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader(HEADER_STRING);
        //System.out.println(bearerToken);


        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)){
            return bearerToken.substring(7, bearerToken.length()); //cut out Bearer and return the token
        }

        return null;
    }
}
