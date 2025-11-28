package org.formation.projet_ntousse_tadiha_vanisco.entities;

import jakarta.persistence.Entity;

@Entity
public class CptEpargne extends Compte {
    private Double taux = 0.03;

    public Double getTaux() { return taux; }
    public void setTaux(Double taux) { this.taux = taux; }

    @Override
    public boolean peutDebiter(Double montant) {
        return this.getSolde() >= montant;
    }

    public Double calculInterets() {
        return this.getSolde() * taux;
    }
}
