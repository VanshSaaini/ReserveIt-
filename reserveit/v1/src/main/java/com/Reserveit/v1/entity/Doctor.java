package com.Reserveit.v1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /** The clinic this doctor is attached to (a doctor belongs to one clinic in this MVP). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    private String specialization;

    private String qualifications;

    private Integer experienceYears;

    /** Default length of one bookable slot, in minutes, unless overridden per availability window. */
    @Builder.Default
    private Integer defaultSlotMinutes = 30;
}
