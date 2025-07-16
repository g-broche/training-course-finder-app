package com.example.finder.config;

import com.example.finder.model.*;
import com.example.finder.model.enums.*;
import com.example.finder.repository.*;
import com.example.finder.seeders.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements ApplicationRunner{

    private final RoleSeeder roleSeeder;
    private final UserStatusSeeder userStatusSeeder;
    private final RecordStatusSeeder recordStatusSeeder;
    private final InteractivityStateSeeder interactivityStateSeeder;
    private final AnnounceTypeSeeder announceTypeSeeder;
    private final AnnounceStatusSeeder announceStatusSeeder;
    private final CategorySeeder categorySeeder;

    public DatabaseSeeder(
            RoleSeeder roleSeeder,
            UserStatusSeeder userStatusSeeder,
            RecordStatusSeeder recordStatusSeeder,
            InteractivityStateSeeder interactivityStateSeeder,
            AnnounceTypeSeeder announceTypeSeeder,
            AnnounceStatusSeeder announceStatusSeeder,
            CategorySeeder categorySeeder
    ) {
        this.roleSeeder = roleSeeder;
        this.userStatusSeeder = userStatusSeeder;
        this.recordStatusSeeder = recordStatusSeeder;
        this.interactivityStateSeeder = interactivityStateSeeder;
        this.announceTypeSeeder = announceTypeSeeder;
        this.announceStatusSeeder = announceStatusSeeder;
        this.categorySeeder = categorySeeder;
    }

    @Override
    public void run(ApplicationArguments args) {
        this.roleSeeder.fillRoles();
        this.userStatusSeeder.fillUserStatus();
        this.recordStatusSeeder.fillRecordStatus();
        this.interactivityStateSeeder.fillInteractivityStates();
        this.announceTypeSeeder.fillAnnounceTypes();
        this.announceStatusSeeder.fillAnnounceStatus();
        this.categorySeeder.fillDefaultCategories();
    }
}
