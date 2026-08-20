package com.Reserveit.v1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * A service a clinic offers (e.g. "General Consultation", "Dental Cleaning"),
 * optionally attached to appointments. Named ServiceOffering to avoid clashing
 * with Spring's own @Service stereotype.
 */
@Entity
@Table(name = "service_offerings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @Column(nullable = false)
    private String name;

    private String description;

    private Integer durationMinutes;

    private BigDecimal price;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}
