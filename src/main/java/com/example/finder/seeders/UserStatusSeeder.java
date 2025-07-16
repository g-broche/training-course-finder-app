package com.example.finder.seeders;

import com.example.finder.model.UserStatus;
import com.example.finder.model.enums.AvailableUserStatus;
import com.example.finder.repository.UserStatusRepository;
import org.springframework.stereotype.Component;

@Component
public class UserStatusSeeder {
    private final UserStatusRepository userStatusRepository;

    public UserStatusSeeder(UserStatusRepository userStatusRepository) {
        this.userStatusRepository = userStatusRepository;
    }

    public void fillUserStatus(){
        for (AvailableUserStatus userStatus : AvailableUserStatus.values()) {
            String statusName = userStatus.toString();
            userStatusRepository.findByName(statusName)
                    .orElseGet(() -> userStatusRepository.save(new UserStatus(statusName)));
        }
    }
}
