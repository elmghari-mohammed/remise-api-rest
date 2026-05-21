package org.example.tpremise.service;

import org.example.tpremise.DTO.CreateTransactionDTO;
import org.example.tpremise.DTO.UpdateTransactionDTO;
import org.example.tpremise.exceptions.MontantPositifExption;
import org.example.tpremise.models.Remise;
import org.example.tpremise.models.Transaction;
import org.example.tpremise.models.User;
import org.example.tpremise.repositories.RemiseRepository;
import org.example.tpremise.repositories.TransactionRepository;
import org.example.tpremise.service.reductionServices.ReductionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private final ReductionService reductionService;
    private final RemiseRepository remiseRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(@Qualifier("reductionDBService") ReductionService reductionService, RemiseRepository remiseRepository, TransactionRepository transactionRepository) {
        this.reductionService = reductionService;
        this.remiseRepository = remiseRepository;
        this.transactionRepository = transactionRepository;

    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Transaction addTransaction(CreateTransactionDTO dto) {
        double montant = dto.getMontant();
        if (montant <= 0) {
            throw new MontantPositifExption("Montant must be positive"); //
        }
        double remiseAmount = reductionService.calculerRemise(montant);
        Remise remise = remiseRepository.getRemise(montant);
        CreateTransactionDTO.UtilisateurDTO utilisateur = dto.getUtilisateur();
        User user = new User(utilisateur.getNom(), utilisateur.getPrenom());
        Transaction transaction = new Transaction(montant, montant - remiseAmount, remise, user);
        transactionRepository.addTransaction(transaction);
        return transaction;
    }

    public Transaction getTransactionById(int id) {
        return transactionRepository.getTransactionById(id);
    }

    public Transaction updateTransaction(int id, UpdateTransactionDTO dto) {
        Transaction existingTransaction = transactionRepository.getTransactionById(id);
        double montant = dto.getMontant();
        if (montant <= 0) {
            throw new MontantPositifExption("Montant must be positive");
        }
        double remiseAmount = reductionService.calculerRemise(montant);
        Remise remise = remiseRepository.getRemise(montant);

        existingTransaction.setMontantAvant(montant);
        existingTransaction.setMontantApres(montant - remiseAmount);
        existingTransaction.setRemise(remise);

        transactionRepository.updateTransaction(existingTransaction);
        return existingTransaction;
    }

    public void deleteTransaction(int id) {
        transactionRepository.deleteTransaction(id);
    }
}
