package com.example.finder.repository;

import com.example.finder.model.Discussion;
import com.example.finder.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID>{
}
