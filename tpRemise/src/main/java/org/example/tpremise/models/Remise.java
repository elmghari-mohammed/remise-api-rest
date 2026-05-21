package org.example.tpremise.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "REMISE")
public class Remise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "montant_min", nullable = false)
    private double montantMin;

    @Column(name = "montant_max", nullable = false)
    private double montantMax;

    @Column(name = "taux", nullable = false)
    private double taux;
}
