package com.jonathanhuertas.taskmanager2.web;


import com.jonathanhuertas.taskmanager2.domain.User;
import com.jonathanhuertas.taskmanager2.payload.JWTLoginRequestSuccessResponse;
import com.jonathanhuertas.taskmanager2.payload.LoginRequest;
import com.jonathanhuertas.taskmanager2.security.JwtTokenProvider;
import com.jonathanhuertas.taskmanager2.services.MapValidationErrorService;
import com.jonathanhuertas.taskmanager2.services.UserService;
import com.jonathanhuertas.taskmanager2.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.jonathanhuertas.taskmanager2.security.SecurityConstants.TOKEN_PREFIX;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result){
        //validate passwords match and password length
        userValidator.validate(user, result); //-> pass user and result

        //Any validation errors will show up here
        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);

        //This will also kill the entire process
        if(errorMap != null) return errorMap;

        User newUser = userService.saveUser(user);

        return new ResponseEntity<User>(newUser, HttpStatus.CREATED);
        //
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result){
        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);

        if(errorMap != null) return errorMap;

        /*AuthenticationManager is a container for authentication providers
        When we call the authenticate method, we are passing the UsernamePasswordAuthenticationToken to the AuthenticationManager we build in spring security
        config. This will use our customUserDetailsService and the Bcrypt password encoder as configured.

        This is basically asking the authenticationManager if the loginRequest that is coming in (which contains a username and password) is
        a user that is registered by calling the authenticationManager authenticate method.

        if the authenticationManager can authenticate, then we setAuthentication in the SecurityContextHolder

        SecurityContext holds the currently logged in user-> this allows us to check if the current user is authenticated  using
        securityContext.getAuthentication().isAuthenticated()

         */

        //username and password are obtained and combined into an instance of UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken authReq =  new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );
        //This authReq is called a "token" which can be confusing when implementing jwt but it is just referring to the object itself
        //This authReq has a few fields: Principal which contains the username, Credentials which contains the password; Authenticated boolean which
        //states if the user is authenticated or not, and Details for granted authorities like roles etc
        //System.out.println(authReq.toString());

        //this token is then passed to the authenticationManager for validation
        //The AuthenticationManager returns a fully populated Authentication instance on successful authentication.
        //All this does is turn our Principal field (Which only contained the username when we created the authReq above) to the actual User object
        //with all the fields fetched from the database and all the other fields (Credentials, Authenticated, Details) remain the same except for
        //Authenticated which is now true
        /*
        The principal is actually populated because of the UserDetails and UserDetailsService.
        Remember that the UserDetails represents a principal but in an extensible and application specific way -> Adapter between your user database
        and what Spring Security needs inside the SecurityContextHolder

        So when we call authenticate on the authenticationManager, it will delegate to a List of AuthenticationProvider instances
        -A list of AuthenticationProviders will be successively tried until an AuthenticationProvider indicates it is capable of authenticating the type
        of Authentication object passed -> authentication will then be attempted with that AuthenticationProvider

        In this case, the AuthenticationProvider used is of type DaoAuthenticationProvider
        which is used when working with UsernamePasswordAuthenticationToken instances

        DaoAuthenticationProvider will look up UserDetails (on User Object) and UserDetailsService (In security Config -> CustomUserDetailsService)

        DaoAuthenticationProvider uses the password encoder provided from the SecurityConfig when we created the AuthenticationManager,
        it does this to validate the password on the UserDetails that was returned from fetching using the CustomUserDetailsService we provided



         */
        Authentication authentication = authenticationManager.authenticate(authReq);
        //System.out.println(authentication instanceof UsernamePasswordAuthenticationToken); -> true
        //System.out.println(authentication.getPrincipal() instanceof UserDetails); -> true
        //System.out.println(authentication.toString()); //if authentication fails, this wont even print out to console


        //The security context is established by calling SecurityContextHolder.getContext().setAuthentication(...), passing in the returned authentication object.
       /*
        SecurityContextHolder is where we store details of the present security context of the application, which includes details of the principal currently using the application.
        By default the SecurityContextHolder uses a ThreadLocal to store these details, which means that the security context is
        always available to methods in the same thread of execution, even if the security context is not explicitly passed around as an argument to those
        methods.

        SpringSecurity uses an Authentication Object to represent the information about the principal currently interacting with the application
        in the SecurityContextHolder

        most authentication mechanisms withing Spring Security return an instance of UserDetails as the principal
        */
        SecurityContextHolder.getContext().setAuthentication(authentication);

        /*
        Once we have set the authentication in the securityContext, we create an jwt token using the JwtTokenProvider class
        it takes the Authentication object created and extracts the principal which is the User object (as stated above)
         */
        //System.out.println(authentication.getPrincipal()); -> will show the user that is authenticated
        String jwt = TOKEN_PREFIX + tokenProvider.generateToken(authentication);//-> generates to token to send in res body

        return ResponseEntity.ok(new JWTLoginRequestSuccessResponse(true, jwt));
    }
}