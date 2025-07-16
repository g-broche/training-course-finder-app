package com.example.finder.repository;

import com.example.finder.exception.InteractivityStateNotFoundException;
import com.example.finder.exception.UserStatusNotFoundException;
import com.example.finder.model.InteractivityState;
import com.example.finder.model.UserStatus;
import com.example.finder.model.enums.AvailableInteractivityState;
import com.example.finder.model.enums.AvailableUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InteractivityStateRepository extends JpaRepository<InteractivityState, Long> {
    Optional<InteractivityState> findByName(String InteractivityStateName);

    default InteractivityState getOpenInteractivityStateOrThrow() {
        return findByName(AvailableInteractivityState.OPEN.toString())
                .orElseThrow(InteractivityStateNotFoundException::new);
    }

    default InteractivityState getCloseInteractivityStateOrThrow() {
        return findByName(AvailableInteractivityState.CLOSE.toString())
                .orElseThrow(InteractivityStateNotFoundException::new);
    }

}
