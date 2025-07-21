package com.example.finder.controller;

import com.example.finder.model.AppUser;
import com.example.finder.model.Role;
import com.example.finder.repository.AppUserRepository;
import com.example.finder.repository.RecordStatusRepository;
import com.example.finder.repository.RoleRepository;
import com.example.finder.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class UserRelatedTest {
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;
    @Autowired
    private RecordStatusRepository recordStatusRepository;

    protected AppUser createTestUser(
            String firstName,
            String lastName,
            String displayName,
            String email,
            String password
    ) {
        AppUser newUser = new AppUser(
                firstName,
                lastName,
                displayName,
                email,
                passwordEncoder.encode(password)
        );
        Role userRole = roleRepository.getUserRoleOrThrow();
        newUser.setRoles(Set.of(userRole));
        newUser.setUserStatus(userStatusRepository.getAllowedUserStatusOrThrow());
        newUser.setRecordStatus(recordStatusRepository.getShownRecordStatusOrThrow());
        return userRepository.save(newUser);
    }

    protected AppUser createTestAdmin(
            String firstName,
            String lastName,
            String displayName,
            String email,
            String password
    ) {
        AppUser newUser = new AppUser(
                firstName,
                lastName,
                displayName,
                email,
                passwordEncoder.encode(password)
        );
        Role userRole = roleRepository.getAdminRoleOrThrow();
        newUser.setRoles(Set.of(userRole));
        newUser.setUserStatus(userStatusRepository.getAllowedUserStatusOrThrow());
        newUser.setRecordStatus(recordStatusRepository.getShownRecordStatusOrThrow());
        return userRepository.save(newUser);
    }

    protected List<AppUser> createTestUserList(int amount){
        List<AppUser> testUsers = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            AppUser newTestUser = createTestUser(
                    "User"+i,
                    "Doe"+i,
                    "user"+i+" .D",
                    "user"+i+"@test.test",
                    "User"+i+"Test!"
            );
            testUsers.add(newTestUser);
        }
        return testUsers;
    }

}
