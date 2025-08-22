package com.example.finder.utils.validator;

import com.example.finder.exception.entity.UserNotFoundException;
import com.example.finder.model.AppUser;
import com.example.finder.repository.AppUserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class ValidatorAuth {
    private final AppUserRepository appUserRepository;

    public ValidatorAuth(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser getUserFromSecurityContext(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        return appUserRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }
}
