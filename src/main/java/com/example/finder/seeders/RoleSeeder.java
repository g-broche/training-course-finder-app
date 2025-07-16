package com.example.finder.seeders;

import com.example.finder.model.Role;
import com.example.finder.model.enums.AvailableRoles;
import com.example.finder.repository.RoleRepository;
import org.springframework.stereotype.Component;

@Component
public class RoleSeeder {
    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void fillRoles(){
        for (AvailableRoles availableRole : AvailableRoles.values()) {
            String roleName = availableRole.toString();
            roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(new Role(roleName)));
        }
    }
}
