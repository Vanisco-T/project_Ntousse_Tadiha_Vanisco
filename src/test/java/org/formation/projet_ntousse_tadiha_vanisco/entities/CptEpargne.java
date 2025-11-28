package org.formation.projet_ntousse_tadiha_vanisco.entities;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class CptEpargne extends Compte {
    private Double taux = 0.03;

    @Override
    public boolean peutDebiter(Double montant) {
        return getSolde() >= montant;
    }

    public Double calculInterets() {
        return getSolde() * taux;
    }
}
