package org.formation.projet_ntousse_tadiha_vanisco.entities;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public Double getSolde() { return solde; }
    public void setSolde(Double solde) { this.solde = solde; }

    public LocalDateTime getDateOuverture() { return dateOuverture; }
    public void setDateOuverture(LocalDateTime dateOuverture) { this.dateOuverture = dateOuverture; }

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