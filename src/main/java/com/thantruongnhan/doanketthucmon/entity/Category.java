package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    private String description;
    private String imageUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}