package com.example.finder.repository;

import com.example.finder.model.Announce;
import com.example.finder.model.AppUser;
import com.example.finder.model.enums.AvailableAnnounceTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnnounceRepository extends JpaRepository<Announce, UUID> {
    List<Announce> findByAuthor(AppUser user);

    List<Announce> findAllByType_Name(String typeName);

    Page<Announce> findAllByType_Name(String typeName, Pageable pageable);

    default List<Announce> findAllFoundAnnounces() {
        return findAllByType_Name(AvailableAnnounceTypes.FOUND.toString());
    }

    default Page<Announce> findAllFoundAnnounces(Pageable pageable) {
        return findAllByType_Name(AvailableAnnounceTypes.FOUND.toString(), pageable);
    }

    default List<Announce> findAllLostAnnounces() {
        return findAllByType_Name(AvailableAnnounceTypes.LOST.toString());
    }

    default Page<Announce> findAllLostAnnounces(Pageable pageable) {
        return findAllByType_Name(AvailableAnnounceTypes.LOST.toString(), pageable);
    }
}
