package org.formation.projet_ntousse_tadiha_vanisco.entities;


import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
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

    public void ajouterClient(Client client) {
        clients.add(client);
        client.setConseiller(this);
    }

    public void supprimerClient(Client client) {
        clients.remove(client);
        client.setConseiller(null);
    }
}
