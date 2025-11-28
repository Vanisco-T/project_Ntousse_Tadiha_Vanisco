package org.formation.projet_ntousse_tadiha_vanisco.entities;


import jakarta.persistence.Entity;

@Entity
public class CptCourant extends Compte {
    private Double decouvertMax = 1000.0;

    public Double getDecouvertMax() { return decouvertMax; }
    public void setDecouvertMax(Double decouvertMax) { this.decouvertMax = decouvertMax; }

    @Override
    public boolean peutDebiter(Double montant) {
        return (this.getSolde() - montant) >= -decouvertMax;
    }

    public boolean checkDecouvert() {
        return this.getSolde() < 0;
    }
}
