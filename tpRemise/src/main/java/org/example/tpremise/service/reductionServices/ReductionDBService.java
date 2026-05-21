package org.example.tpremise.service.reductionServices;

import org.example.tpremise.exception.RemiseException;
import org.example.tpremise.repositories.RemiseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReductionDBService implements ReductionService {
    private final RemiseRepository remiseRepository;

    public ReductionDBService(RemiseRepository remiseRepository) {
        this.remiseRepository = remiseRepository;
    }

    @Override
    @Transactional
    public double calculerRemise(double montant) {
        if (montant <= 0) {
            throw new RemiseException("Le montant doit etre superieur a 0");
        }

        Double taux = remiseRepository.getTaux(montant);

        if (taux == null) {
            throw new RemiseException("Aucune tranche de remise trouve pour le montant: " + montant);
        }

        return montant * taux;
    }
}
