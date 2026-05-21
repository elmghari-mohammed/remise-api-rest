package org.example.tpremise.repositories;

import org.example.tpremise.exception.TransactionNotFoundException;
import org.example.tpremise.mappers.TransactionMapper;
import org.example.tpremise.models.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionRepository {
    private final JdbcTemplate jdbcTemplate;

    public TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Transaction> findAll() {
        String sql = "SELECT t.*, t.user_nom, t.user_prenom FROM TRANSACTION t";
        return jdbcTemplate.query(sql, new TransactionMapper());
    }

    public void addTransaction(Transaction transaction) {
        jdbcTemplate.update(
                "INSERT INTO TRANSACTION (date, montant_avant, montant_apres, remise_id, user_nom, user_prenom) VALUES (?, ?, ?, ?, ?, ?)",
                transaction.getDate(),
                transaction.getMontantAvant(),
                transaction.getMontantApres(),
                transaction.getRemise().getId(),
                transaction.getUser().getNom(),
                transaction.getUser().getPrenom()
        );
    }

    public Transaction getTransactionById(int id) {
        List<Transaction> list = jdbcTemplate.query(
                "SELECT t.*, t.user_nom, t.user_prenom FROM TRANSACTION t WHERE t.id = ?",
                new TransactionMapper(),
                id
        );

        if (list.isEmpty()) {
            throw new TransactionNotFoundException(id);
        }
        return list.get(0);
    }

    public void updateTransaction(Transaction transaction) {
        jdbcTemplate.update(
                "UPDATE TRANSACTION SET date = ?, montant_avant = ?, montant_apres = ?, remise_id = ?, user_nom = ?, user_prenom = ? WHERE id = ?",
                transaction.getDate(),
                transaction.getMontantAvant(),
                transaction.getMontantApres(),
                transaction.getRemise().getId(),
                transaction.getUser().getNom(),
                transaction.getUser().getPrenom(),
                transaction.getId()
        );
    }

    public void deleteTransaction(int id) {
        jdbcTemplate.update("DELETE FROM TRANSACTION WHERE id = ?", id);
    }
}
