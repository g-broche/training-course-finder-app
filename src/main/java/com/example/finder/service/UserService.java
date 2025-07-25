package com.example.finder.service;

import com.example.finder.config.PaginationConfig;
import com.example.finder.controller.UserController;
import com.example.finder.dto.output.DetailedUserDto;
import com.example.finder.exception.entity.UserNotFoundException;
import com.example.finder.model.AppUser;
import com.example.finder.repository.AppUserRepository;
import com.example.finder.response.ApiResponseFactory;
import com.example.finder.response.PaginatedResponse;
import com.example.finder.utils.SanitizerUtil;
import com.example.finder.utils.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class UserService {
    @Autowired
    private Environment environment;
    private final SanitizerUtil sanitizerUtil;
    private final ValidatorUtil validatorUtil;
    private final AppUserRepository userRepository;
    private final PaginationConfig paginationConfig;
    private final AuthenticationManager authManager;

    public UserService(
            SanitizerUtil sanitizerUtil,
            ValidatorUtil validatorUtil,
            AppUserRepository userRepository,
            PaginationConfig paginationConfig,
            AuthenticationManager authManager
    ) {
        this.sanitizerUtil = sanitizerUtil;
        this.validatorUtil = validatorUtil;
        this.userRepository = userRepository;
        this.paginationConfig = paginationConfig;
        this.authManager = authManager;
    }

    public ResponseEntity<?> getAllUsers() {
        try {
            List<AppUser> users = userRepository.findAll();
            Set<DetailedUserDto> usersDto = users.stream()
                    .map(AppUser::toDetailedUserDto)
                    .collect(Collectors.toSet());
            return ApiResponseFactory.success(usersDto);
        } catch (Exception e) {
            return  ApiResponseFactory.internalError();
        }
    }

    public ResponseEntity<?> getPaginatedUsers(int page, int size) {
        try {
            size = Math.min(size, paginationConfig.getMaxResultsPerPage());
            Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
            Page<AppUser> userPage = userRepository.findAll(pageable);
            Page<DetailedUserDto> userDtoPage = userPage.map(DetailedUserDto::new);
            var paginatedResult = PaginatedResponse.from(userDtoPage);
            return ApiResponseFactory.success(paginatedResult);
        } catch (Exception e) {
            return  ApiResponseFactory.internalError();
        }
    }

    public ResponseEntity<?> getUserDetailsById(UUID id) {
        try {
            AppUser user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
            return ApiResponseFactory.success(user.toDetailedUserDto());
        } catch (UserNotFoundException e) {
            return  ApiResponseFactory.badRequest(e.getMessage());
        } catch (Exception e) {
            return  ApiResponseFactory.internalError();
        }
    }
}
