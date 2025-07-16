package com.example.finder.seeders;

import com.example.finder.model.AnnounceStatus;
import com.example.finder.model.enums.AvailableAnnounceStatus;
import com.example.finder.repository.AnnounceStatusRepository;
import org.springframework.stereotype.Component;

@Component
public class AnnounceStatusSeeder {
    private final AnnounceStatusRepository announceStatusRepository;

    public AnnounceStatusSeeder(AnnounceStatusRepository announceStatusRepository) {
        this.announceStatusRepository = announceStatusRepository;
    }

    public void fillAnnounceStatus(){
        for (AvailableAnnounceStatus announceStatus : AvailableAnnounceStatus.values()) {
            String statusName = announceStatus.toString();
            announceStatusRepository.findByName(statusName)
                    .orElseGet(() -> announceStatusRepository.save(new AnnounceStatus(statusName)));
        }
    }
}
