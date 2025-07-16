package com.example.finder.seeders;

import com.example.finder.model.RecordStatus;
import com.example.finder.model.enums.AvailableRecordStatus;
import com.example.finder.repository.RecordStatusRepository;
import org.springframework.stereotype.Component;

@Component
public class RecordStatusSeeder {
    private final RecordStatusRepository recordStatusRepository;

    public RecordStatusSeeder(RecordStatusRepository recordStatusRepository) {
        this.recordStatusRepository = recordStatusRepository;
    }

    public void fillRecordStatus(){
        for (AvailableRecordStatus recordStatus : AvailableRecordStatus.values()) {
            String statusName = recordStatus.toString();
            recordStatusRepository.findByName(statusName)
                    .orElseGet(() -> recordStatusRepository.save(new RecordStatus(statusName)));
        }
    }
}
