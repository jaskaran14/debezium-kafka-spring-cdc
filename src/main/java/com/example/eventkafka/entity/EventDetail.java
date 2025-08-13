package com.example.eventkafka.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "event_details")
public class EventDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private String detail;
}
