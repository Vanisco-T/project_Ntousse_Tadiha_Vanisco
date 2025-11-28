package org.formation.projet_ntousse_tadiha_vanisco.entities;


import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class CptCourant extends Compte {
    private Double decouvertMax = 1000.0;

    @Override
    public boolean peutDebiter(Double montant) {
        return (getSolde() - montant) >= -decouvertMax;
    }

    public boolean checkDecouvert() {
        return getSolde() < 0;
    }
}
