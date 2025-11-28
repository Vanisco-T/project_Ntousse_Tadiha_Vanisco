package org.formation.projet_ntousse_tadiha_vanisco.config;


import org.formation.projet_ntousse_tadiha_vanisco.entities.*;
import org.formation.projet_ntousse_tadiha_vanisco.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ConseillerRepository conseillerRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CompteRepository compteRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists to avoid duplicates
        if (conseillerRepository.count() > 0) {
            System.out.println("=== Données déjà initialisées ===");
            return;
        }

        System.out.println("=== Initialisation des données... ===");

        Conseiller conseiller1 = new Conseiller();
        conseiller1.setIdConseiller("CONS001");
        conseiller1.setNom("Dupont");
        conseiller1.setPrenom("Marie");
        conseiller1 = conseillerRepository.save(conseiller1);

        Conseiller conseiller2 = new Conseiller();
        conseiller2.setIdConseiller("CONS002");
        conseiller2.setNom("Martin");
        conseiller2.setPrenom("Pierre");
        conseiller2 = conseillerRepository.save(conseiller2);

        Client client1 = new Client();
        client1.setIdClient("CLI001");
        client1.setNom("Martin");
        client1.setPrenom("Jean");
        client1.setAdresse("123 Rue de la Paix");
        client1.setCodePostal("75001");
        client1.setVille("Paris");
        client1.setTelephone("0123456789");
        client1.setConseiller(conseiller1);

        CptCourant courant1 = new CptCourant();
        courant1.setNumero("CC001");
        courant1.setSolde(1500.0);
        client1.setCourant(courant1);

        CptEpargne epargne1 = new CptEpargne();
        epargne1.setNumero("CE001");
        epargne1.setSolde(5000.0);
        client1.setEpargne(epargne1);

        clientRepository.save(client1);

        Client client2 = new Client();
        client2.setIdClient("CLI002");
        client2.setNom("Bernard");
        client2.setPrenom("Sophie");
        client2.setAdresse("456 Avenue des Champs");
        client2.setCodePostal("69002");
        client2.setVille("Lyon");
        client2.setTelephone("0987654321");
        client2.setConseiller(conseiller1);

        CptCourant courant2 = new CptCourant();
        courant2.setNumero("CC002");
        courant2.setSolde(800.0);
        client2.setCourant(courant2);

        CptEpargne epargne2 = new CptEpargne();
        epargne2.setNumero("CE002");
        epargne2.setSolde(3000.0);
        client2.setEpargne(epargne2);

        clientRepository.save(client2);

        Client client3 = new Client();
        client3.setIdClient("CLI003");
        client3.setNom("Dubois");
        client3.setPrenom("Luc");
        client3.setAdresse("789 Boulevard Central");
        client3.setCodePostal("13001");
        client3.setVille("Marseille");
        client3.setTelephone("0654321987");
        client3.setConseiller(conseiller2);

        CptCourant courant3 = new CptCourant();
        courant3.setNumero("CC003");
        courant3.setSolde(200.0);
        client3.setCourant(courant3);

        clientRepository.save(client3);

        System.out.println("=== Données initialisées avec succès ===");
        System.out.println("2 conseillers créés");
        System.out.println("3 clients créés");
        System.out.println("3 comptes courants créés");
        System.out.println("2 comptes épargne créés");
    }
}