package org.example.tpremise.service.reductionServices;

import org.example.tpremise.exception.RemiseException;
import org.springframework.stereotype.Service;

@Service
public class ReductionVarService implements ReductionService {
    @Override
    public double calculerRemise(double montant) {
        if (montant <= 0) {
            throw new RemiseException("Le montant doit être supérieur à 0");
        }
        if(montant < 1000) {
            return 0;
        } else if(montant < 10000) {
            return montant * 0.02;
        } else {
            return montant * 0.05;
        }
    }
}

