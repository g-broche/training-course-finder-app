package com.example.finder.service;

import com.example.finder.dto.input.RequestLogin;
import com.example.finder.dto.input.RequestRegister;
import com.example.finder.dto.output.ErrorDto;
import com.example.finder.dto.JwtDto;
import com.example.finder.exception.entity.UserNotFoundException;
import com.example.finder.response.enums.AuthError;
import com.example.finder.exception.action.ActivationTokenGenerationException;
import com.example.finder.exception.action.UserNotCreatedException;
import com.example.finder.model.AppUser;
import com.example.finder.model.RecordStatus;
import com.example.finder.model.Role;
import com.example.finder.model.UserStatus;
import com.example.finder.repository.AppUserRepository;
import com.example.finder.repository.RecordStatusRepository;
import com.example.finder.repository.RoleRepository;
import com.example.finder.repository.UserStatusRepository;
import com.example.finder.response.ApiResponseFactory;
import com.example.finder.utils.ActivationTokenUtil;
import com.example.finder.utils.SanitizerUtil;
import com.example.finder.utils.ValidatorUtil;
import com.example.finder.utils.jwt.JwtUtil;
import com.example.finder.utils.logger.Printer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@Service
public class AuthService {
    @Autowired
    private Environment environment;
    private final SanitizerUtil sanitizerUtil;
    private final ValidatorUtil validatorUtil;
    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserStatusRepository userStatusRepository;
    private final RecordStatusRepository recordStatusRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthService(
            SanitizerUtil sanitizerUtil,
            ValidatorUtil validatorUtil,
            AppUserRepository userRepository,
            RoleRepository roleRepository,
            UserStatusRepository userStatusRepository,
            RecordStatusRepository recordStatusRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authManager,
            JwtUtil jwtUtil
    ) {
        this.sanitizerUtil = sanitizerUtil;
        this.validatorUtil = validatorUtil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userStatusRepository = userStatusRepository;
        this.recordStatusRepository = recordStatusRepository;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Creates and persists a new user from signup data and returns a response
     * with JWT containing the newly created user data
     *
     * @param registerData data provided to create the new user
     * @return Response entity corresponding to the result of the request
     */
    public ResponseEntity<?> registerNewUser(RequestRegister registerData) {
        try {
            if (userRepository.existsByEmail(registerData.getEmail())) {
                return ApiResponseFactory.badRequest(
                        AuthError.EMAIL_ALREADY_USED.getErrorMessage()
                );
            }
            RequestRegister sanitizedRequest = sanitizerUtil.sanitizeRegisterInputs(registerData);
            List<ErrorDto> validationErrors = validatorUtil.validateRegisterInputs(sanitizedRequest);
            boolean isRequestInvalid = !validationErrors.isEmpty();
            if (isRequestInvalid) {
                for (ErrorDto errorDto : validationErrors){
                    System.out.println(errorDto.getErrorMessage());
                }
                return ApiResponseFactory.badRequest(
                        AuthError.INVALID_REGISTER_DATA.getErrorMessage(),
                        validationErrors
                );
            }
            AppUser createdUser = createNewUserFromData(registerData);
            String token = jwtUtil.generateToken(createdUser);
            return ApiResponseFactory.success(new JwtDto(token));
        } catch (Exception e) {
            return  ApiResponseFactory.internalError();
        }
    }

    /**
     * Validate given credentials match a user and in such case returns
     * a response with JWT containing user data.
     * @param request request containing credentials data
     * @return Response entity corresponding to the result of the request
     */
    public ResponseEntity<?> logUser(RequestLogin request) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            UserDetails userDetails = (UserDetails) auth.getPrincipal();

            AppUser user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
            String token = jwtUtil.generateToken(user);
            return ApiResponseFactory.success(new JwtDto(token));
        } catch (AuthenticationException e) {
            return ApiResponseFactory.unauthorized(AuthError.INVALID_CREDENTIALS.getErrorMessage());
        } catch (Exception e) {
            return  ApiResponseFactory.internalError();
        }
    }


    /**
     * Given a token, will search for a user having the same activationToken and if
     * such user exists it will be set to active and its existing token set back to null
     *
     * @param token registration issued token to verify user mail
     * @return Response entity corresponding to the result of the request
     */
    @Transactional
    public ResponseEntity<?> validateRegistrationToken(String token) {
        try {
            AppUser user = userRepository.findByActivationToken(token)
                    .orElseThrow(UserNotFoundException::new);
            user.setIsVerified(true);
            user.setActivationToken(null);
            userRepository.save(user);
            return ApiResponseFactory.success("Email has been verified successfully");
        }catch (UserNotFoundException e) {
            return ApiResponseFactory.badRequest(AuthError.INVALID_VERIFICATION_TOKEN.getErrorMessage());
        } catch (Exception e) {
            Printer.printErrorLogWithDetails(e);
            return ApiResponseFactory.internalError();
        }
    }

    /**
     * Generates a random token to be used to validate registration. Ensures the token
     * don't already exist beforehand
     *
     * @return String
     */
    private String generateRegistrationToken() {
        Set<String> existingTokens = userRepository.findAllNonNullActivationTokens();
        String activationToken = ActivationTokenUtil.generateToken();
        boolean mustCreateToken = true;
        int maxAttempts = 10;
        int currentAttempt = 1;
        // prevent collision with other users existing token
        while (mustCreateToken && currentAttempt <= maxAttempts) {
            if (!existingTokens.contains(activationToken)) {
                mustCreateToken = false;
            } else {
                currentAttempt++;
                activationToken = ActivationTokenUtil.generateToken();
            }
        }
        if (mustCreateToken) {
            throw new ActivationTokenGenerationException();
        }
        return activationToken;
    }

    /**
     * Creates a new user in database based on provided register data
     * @param registerData data to create the new user from
     * @return newly created user
     */
    private AppUser createNewUserFromData(RequestRegister registerData) {
        AppUser newUser = new AppUser();
        newUser.setFirstName(registerData.getFirstName());
        newUser.setLastName(registerData.getLastName());
        newUser.setDisplayName(registerData.getDisplayName());
        newUser.setEmail(registerData.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerData.getPassword()));

        Role userRole = roleRepository.getUserRoleOrThrow();
        newUser.setRoles(Set.of(userRole));
        String activationToken = generateRegistrationToken();
        newUser.setActivationToken(activationToken);
        UserStatus defaultStatus = userStatusRepository.getAllowedUserStatusOrThrow();
        newUser.setUserStatus(defaultStatus);
        RecordStatus defaultRecordStatus = recordStatusRepository.getShownRecordStatusOrThrow();
        newUser.setRecordStatus(defaultRecordStatus);
        userRepository.save(newUser);
        return userRepository.findByEmail(newUser.getEmail())
                .orElseThrow(UserNotCreatedException::new);
    }
}
