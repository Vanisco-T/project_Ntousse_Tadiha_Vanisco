package org.formation.projet_ntousse_tadiha_vanisco.entities;


import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdClient() { return idClient; }
    public void setIdClient(String idClient) { this.idClient = idClient; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public CptCourant getCourant() { return courant; }
    public void setCourant(CptCourant courant) { this.courant = courant; }

    public CptEpargne getEpargne() { return epargne; }
    public void setEpargne(CptEpargne epargne) { this.epargne = epargne; }

    public Conseiller getConseiller() { return conseiller; }
    public void setConseiller(Conseiller conseiller) { this.conseiller = conseiller; }

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