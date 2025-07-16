package com.example.finder.seeders;

import com.example.finder.model.InteractivityState;
import com.example.finder.model.enums.AvailableInteractivityState;
import com.example.finder.repository.InteractivityStateRepository;
import org.springframework.stereotype.Component;

@Component
public class InteractivityStateSeeder {
    private final InteractivityStateRepository interactivityStateRepository;

    public InteractivityStateSeeder(InteractivityStateRepository interactivityStateRepository) {
        this.interactivityStateRepository = interactivityStateRepository;
    }

    public void fillInteractivityStates(){
        for (AvailableInteractivityState interactivityState : AvailableInteractivityState.values()) {
            String stateName = interactivityState.toString();
            interactivityStateRepository.findByName(stateName)
                    .orElseGet(() -> interactivityStateRepository.save(new InteractivityState(stateName)));
        }
    }
}
