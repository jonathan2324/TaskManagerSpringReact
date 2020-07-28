package com.jonathanhuertas.taskmanager2.security;

import com.jonathanhuertas.taskmanager2.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.jonathanhuertas.taskmanager2.security.SecurityConstants.H2_URL;
import static com.jonathanhuertas.taskmanager2.security.SecurityConstants.SIGN_UP_URLS;

/*
WebSecurityConfigurerAdapter- a class that implements WebSecurityConfigurer interface
//AuthenticationManagerBuilder is used to build the AuthenticationManager which makes sure the user has the correct username and password
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired//we created this bean in the main application file
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean//we never said that this is a component, therefore it is just a regular java class so we need an @Bean here to make an instance
    public JwtAuthenticationFilter jwtAuthenticationFilter() { return new JwtAuthenticationFilter();}





    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)//so it is statless and doesnt save cookies because we are using jwt-server doesnt have to hold session
                .and()
                .headers().frameOptions().sameOrigin()//enables h2 database
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/",
                        "/favicon.ico",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                ).permitAll()
                .antMatchers(SIGN_UP_URLS).permitAll()
                .antMatchers(H2_URL).permitAll()
                .anyRequest().authenticated();//anything other than above will need to be authenticated

        //this says use this custom filter before the UsernamePasswordAuthenticationFilter class is used
        //the UsernamePasswordAuthenticationFilter  is the filter that allows the user to authenticate
        //UsernamePasswordAuthenticationFilter calls the AuthenticationManager to process each authentication request
        //if Authentication is successful, the resuling Authentication object is placed in the SecurityContextHolder
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    //create a bean for authentication manager-> This allows us to autowire authenticationManager in the application
    @Override
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
