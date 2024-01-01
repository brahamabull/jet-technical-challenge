package com.coding.challenge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Employee implements Serializable {

    @Id
    private String uuid;

    @Column(unique = true)
    private String email;

    private String fullName;

    private LocalDate birthday;

    @ElementCollection
    private List<String> hobbies;
}
