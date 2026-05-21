package org.example.tpremise.repositories;

import org.example.tpremise.models.Remise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RemiseRepository extends JpaRepository<Remise, Long> {
    @Query(value = "SELECT * FROM remise r WHERE :montant BETWEEN r.montant_min AND r.montant_max ORDER BY r.montant_min DESC LIMIT 1", nativeQuery = true)
    Optional<Remise> findByMontant(@Param("montant") Double montant);

    @Query(value = "SELECT r.taux FROM remise r WHERE :montant BETWEEN r.montant_min AND r.montant_max ORDER BY r.montant_min DESC LIMIT 1", nativeQuery = true)
    Optional<Double> findTauxByMontant(@Param("montant") Double montant);

    default Remise getRemise(double montant) {
        return findByMontant(montant).orElse(null);
    }

    default Double getTaux(double montant) {
        return findTauxByMontant(montant).orElse(null);
    }
}
