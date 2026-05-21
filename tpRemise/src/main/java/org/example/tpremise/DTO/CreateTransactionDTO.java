package org.example.tpremise.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTransactionDTO {
    private double montant;
    private UtilisateurDTO utilisateur;

    @Getter
    @Setter
    public static class UtilisateurDTO {
        private String nom;
        private String prenom;
    }
}
