package org.formation.projet_ntousse_tadiha_vanisco.entities;


import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Conseiller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String idConseiller;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @OneToMany(mappedBy = "conseiller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Client> clients = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdConseiller() { return idConseiller; }
    public void setIdConseiller(String idConseiller) { this.idConseiller = idConseiller; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public List<Client> getClients() { return clients; }
    public void setClients(List<Client> clients) { this.clients = clients; }

    public void ajouterClient(Client client) {
        clients.add(client);
        client.setConseiller(this);
    }

    public void supprimerClient(Client client) {
        clients.remove(client);
        client.setConseiller(null);
    }
}