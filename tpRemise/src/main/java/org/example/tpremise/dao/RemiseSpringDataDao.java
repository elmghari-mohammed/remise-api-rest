package org.example.tpremise.dao;

import org.example.tpremise.exception.RemiseException;
import org.example.tpremise.models.Remise;
import org.example.tpremise.repositories.RemiseRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
@Profile("!hibernate")
public class RemiseSpringDataDao implements RemiseDao {

    private final RemiseRepository remiseRepository;

    public RemiseSpringDataDao(RemiseRepository remiseRepository) {
        this.remiseRepository = remiseRepository;
    }

    @Override
    public Optional<Remise> findByMontant(Double montant) {
        return remiseRepository.findByMontant(montant);
    }

    @Override
    public Optional<Remise> findById(Long id) {
        return remiseRepository.findById(id);
    }

    @Override
    public Remise save(Remise remise) {
        return remiseRepository.save(remise);
    }

    @Override
    public Remise update(Remise remise) {
        if (!remiseRepository.existsById(remise.getId())) {
            throw new RemiseException("Remise not found with id: " + remise.getId());
        }
        return remiseRepository.save(remise);
    }

    @Override
    public void deleteById(Long id) {
        if (!remiseRepository.existsById(id)) {
            throw new RemiseException("Remise non trouvée avec l'id: " + id);
        }
        remiseRepository.deleteById(id);
    }
}
