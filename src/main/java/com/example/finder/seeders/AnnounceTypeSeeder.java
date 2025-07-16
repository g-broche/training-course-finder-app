package com.example.finder.seeders;

import com.example.finder.model.AnnounceType;
import com.example.finder.model.enums.AvailableAnnounceTypes;
import com.example.finder.repository.AnnounceTypeRepository;
import org.springframework.stereotype.Component;

@Component
public class AnnounceTypeSeeder {
    private final AnnounceTypeRepository announceTypeRepository;

    public AnnounceTypeSeeder(AnnounceTypeRepository announceTypeRepository) {
        this.announceTypeRepository = announceTypeRepository;
    }

    public void fillAnnounceTypes(){
        for (AvailableAnnounceTypes announceType : AvailableAnnounceTypes.values()) {
            String typeName = announceType.toString();
            announceTypeRepository.findByName(typeName)
                    .orElseGet(() -> announceTypeRepository.save(new AnnounceType(typeName)));
        }
    }
}
