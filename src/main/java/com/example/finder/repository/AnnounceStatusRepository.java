package com.example.finder.repository;

import com.example.finder.exception.entity.AnnounceStatusNotFoundException;
import com.example.finder.model.AnnounceStatus;
import com.example.finder.model.enums.AvailableAnnounceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnnounceStatusRepository extends JpaRepository<AnnounceStatus, Long> {
    Optional<AnnounceStatus> findByName(String InteractivityStateName);

    default AnnounceStatus getSolvedAnnounceStatusOrThrow() {
        return findByName(AvailableAnnounceStatus.SOLVED.toString())
                .orElseThrow(AnnounceStatusNotFoundException::new);
    }

    default AnnounceStatus getUnsolvedAnnounceStatusOrThrow() {
        return findByName(AvailableAnnounceStatus.UNSOLVED.toString())
                .orElseThrow(AnnounceStatusNotFoundException::new);
    }

}
