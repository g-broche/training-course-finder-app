package com.example.finder.config;

import com.example.finder.model.Role;
import com.example.finder.model.enums.AvailableRoles;
import com.example.finder.repository.RoleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements ApplicationRunner{

    private final RoleRepository roleRepository;

    public DatabaseSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        this.fillRoles();
    }
    
    private void fillRoles(){
        for (AvailableRoles availableRole : AvailableRoles.values()) {
            String roleName = availableRole.toString();
            roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(new Role(roleName)));
        }
    }
}
