package com.siaas.academic;

import jakarta.persistence.*;
import lombok.Getter;
import java.util.UUID;

@Entity
@Table(name = "subjects")
@Getter
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "semester_number", nullable = false)
    private int semesterNumber;

    @Column(nullable = false)
    private int credits;
}
