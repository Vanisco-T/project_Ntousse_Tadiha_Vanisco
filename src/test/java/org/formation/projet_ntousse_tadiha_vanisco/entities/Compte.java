package org.formation.projet_ntousse_tadiha_vanisco.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public abstract class Compte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numero;

    @Column(nullable = false)
    private Double solde = 0.0;

    @Column(nullable = false)
    private LocalDateTime dateOuverture;

    @PrePersist
    protected void onCreate() {
        dateOuverture = LocalDateTime.now();
    }

    public void crediter(Double montant) {
        if (montant > 0) {
            this.solde += montant;
        }
    }

    public void debiter(Double montant) {
        if (montant > 0) {
            this.solde -= montant;
        }
    }

    public abstract boolean peutDebiter(Double montant);
}
