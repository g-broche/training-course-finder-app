package com.example.finder.repository;

import com.example.finder.exception.entity.AnnounceTypeNotFoundException;
import com.example.finder.model.AnnounceType;
import com.example.finder.model.enums.AvailableAnnounceTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnnounceTypeRepository extends JpaRepository<AnnounceType, Long> {
    Optional<AnnounceType> findByName(String InteractivityStateName);

    default AnnounceType getFoundAnnounceTypeOrThrow() {
        return findByName(AvailableAnnounceTypes.FOUND.toString())
                .orElseThrow(AnnounceTypeNotFoundException::new);
    }

    default AnnounceType getLostAnnounceTypeOrThrow() {
        return findByName(AvailableAnnounceTypes.LOST.toString())
                .orElseThrow(AnnounceTypeNotFoundException::new);
    }

}
