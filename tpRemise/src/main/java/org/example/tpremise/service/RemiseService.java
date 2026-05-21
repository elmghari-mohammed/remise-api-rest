package org.example.tpremise.service;

import org.example.tpremise.dao.RemiseDao;
import org.example.tpremise.exceptions.MontantPositifExption;
import org.example.tpremise.exception.RemiseException;
import org.example.tpremise.models.Remise;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class RemiseService {

    private final RemiseDao remiseDao;

    public RemiseService(RemiseDao remiseDao) {
        this.remiseDao = remiseDao;
    }

    @Transactional(readOnly = true)
    public Optional<Remise> findByMontant(Double montant) {
        return remiseDao.findByMontant(montant);
    }

    public Remise create(Remise remise) {
        if (remise.getMontantMin() <= 0) {
            throw new MontantPositifExption("Le montant minimum doit être positif");
        }
        if (remise.getMontantMax() <= 0) {
            throw new MontantPositifExption("Le montant maximum doit être positif");
        }
        if (remise.getId() != null) {
            throw new RemiseException("L'ID de la remise doit être nul pour une création");
        }
        return remiseDao.save(remise);
    }

    public Remise update(Long id, Remise remise) {
        if (remise.getMontantMin() <= 0) {
            throw new MontantPositifExption("Le montant minimum doit être positif");
        }
        if (remise.getMontantMax() <= 0) {
            throw new MontantPositifExption("Le montant maximum doit être positif");
        }
        remise.setId(id);
        return remiseDao.update(remise);
    }

    public void delete(Long id) {
        remiseDao.deleteById(id);
    }
}
