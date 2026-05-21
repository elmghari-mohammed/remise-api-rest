package org.example.tpremise.service.reductionServices;

import org.example.tpremise.exception.RemiseException;
import org.springframework.stereotype.Service;

@Service
public class ReductionFixService implements ReductionService {
    private final double TAUX = 0.04;

    @Override
    public double calculerRemise(double montant) {
        if (montant <= 0) {
            throw new RemiseException("Le montant doit être supérieur à 0");
        }
        return montant * TAUX;
    }
}