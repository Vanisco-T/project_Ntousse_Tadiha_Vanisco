package org.formation.projet_ntousse_tadiha_vanisco.entities;


import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String idClient;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    private String adresse;
    private String codePostal;
    private String ville;
    private String telephone;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "courant_id")
    private CptCourant courant;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "epargne_id")
    private CptEpargne epargne;

    @ManyToOne
    @JoinColumn(name = "conseiller_id")
    private Conseiller conseiller;

    public List<Compte> getComptes() {
        List<Compte> comptes = new ArrayList<>();
        if (courant != null) comptes.add(courant);
        if (epargne != null) comptes.add(epargne);
        return comptes;
    }

    public void supprimerComptes() {
        this.courant = null;
        this.epargne = null;
    }
}
