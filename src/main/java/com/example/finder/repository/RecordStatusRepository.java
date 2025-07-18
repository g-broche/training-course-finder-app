package com.example.finder.repository;

import com.example.finder.exception.entity.RecordStatusNotFoundException;
import com.example.finder.model.RecordStatus;
import com.example.finder.model.enums.AvailableRecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordStatusRepository extends JpaRepository<RecordStatus, Long> {
    Optional<RecordStatus> findByName(String recordStatusName);

    default RecordStatus getShownRecordStatusOrThrow() {
        return findByName(AvailableRecordStatus.SHOWN.toString())
                .orElseThrow(RecordStatusNotFoundException::new);
    }

    default RecordStatus getHiddenRecordStatusOrThrow() {
        return findByName(AvailableRecordStatus.HIDDEN.toString())
                .orElseThrow(RecordStatusNotFoundException::new);
    }

    default RecordStatus getToDeleteRecordStatusOrThrow() {
        return findByName(AvailableRecordStatus.TO_DELETE.toString())
                .orElseThrow(RecordStatusNotFoundException::new);
    }
}
