package org.example.tpremise.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.example.tpremise.models.Remise;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
@Profile("hibernate")
public class RemiseHibernateDao implements RemiseDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Remise> findByMontant(Double montant) {
        TypedQuery<Remise> query = entityManager.createQuery(
                "FROM Remise r WHERE :montant BETWEEN r.montantMin AND r.montantMax ORDER BY r.montantMin DESC", Remise.class);
        query.setParameter("montant", montant);
        query.setMaxResults(1);
        return query.getResultStream().findFirst();
    }

    @Override
    public Optional<Remise> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Remise.class, id));
    }

    @Override
    public Remise save(Remise remise) {
        entityManager.persist(remise);
        return remise;
    }

    @Override
    public Remise update(Remise remise) {
        if (remise.getId() == null) {
            throw new IllegalArgumentException("Remise id must not be null for update");
        }
        return entityManager.merge(remise);
    }

    @Override
    public void deleteById(Long id) {
        Remise remise = entityManager.getReference(Remise.class, id);
        entityManager.remove(remise);
    }
}
