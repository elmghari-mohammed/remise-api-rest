package org.example.tpremise.service;

import org.example.tpremise.service.reductionServices.ReductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class FacturationService {
    private final ReductionService rs;

    @Autowired
    public FacturationService(@Qualifier("reductionDBService") ReductionService reductionService) {
        this.rs = reductionService;
    }

    public double calculerFacture(double montant) {
        return montant - rs.calculerRemise(montant);
    }
}
