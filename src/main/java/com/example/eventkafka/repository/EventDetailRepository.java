package com.example.eventkafka.repository;

import com.example.eventkafka.entity.EventDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDetailRepository extends JpaRepository<EventDetail, Long> {
}
