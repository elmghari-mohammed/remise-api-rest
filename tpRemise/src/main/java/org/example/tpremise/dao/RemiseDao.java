package org.example.tpremise.dao;

import org.example.tpremise.models.Remise;

import java.util.Optional;

public interface RemiseDao {
    Optional<Remise> findByMontant(Double montant);
    Optional<Remise> findById(Long id);
    Remise save(Remise remise);
    Remise update(Remise remise);
    void deleteById(Long id);
}
