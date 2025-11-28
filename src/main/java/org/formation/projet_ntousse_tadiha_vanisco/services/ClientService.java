package org.formation.projet_ntousse_tadiha_vanisco.services;

import org.formation.projet_ntousse_tadiha_vanisco.entities.Client;
import org.formation.projet_ntousse_tadiha_vanisco.entities.Compte;
import org.formation.projet_ntousse_tadiha_vanisco.entities.CptCourant;
import org.formation.projet_ntousse_tadiha_vanisco.exceptions.SuppressionImpossibleException;
import org.formation.projet_ntousse_tadiha_vanisco.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public boolean peutEtreSupprime(Client client) {
        List<Compte> comptes = client.getComptes();

        if (comptes.isEmpty()) {
            return true; // Pas de comptes = suppression possible
        }

        for (Compte compte : comptes) {
            // Vérifier si le compte a de l'argent
            if (compte.getSolde() > 0) {
                return false;
            }

            // Vérifier si le compte est à découvert
            if (compte.getSolde() < 0) {
                return false;
            }
        }

        return true;
    }

    public void supprimerClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'ID: " + clientId));

        // Vérifier si le client peut être supprimé
        if (!peutEtreSupprime(client)) {
            StringBuilder message = new StringBuilder();
            message.append("Impossible de supprimer le client '")
                    .append(client.getPrenom()).append(" ").append(client.getNom())
                    .append("' car :\n");

            List<Compte> comptes = client.getComptes();
            for (Compte compte : comptes) {
                if (compte.getSolde() > 0) {
                    message.append("- Le compte ").append(compte.getNumero())
                            .append(" a un solde positif de ").append(compte.getSolde()).append(" €\n");
                } else if (compte.getSolde() < 0) {
                    message.append("- Le compte ").append(compte.getNumero())
                            .append(" est à découvert de ").append(Math.abs(compte.getSolde())).append(" €\n");
                }
            }

            throw new SuppressionImpossibleException(message.toString());
        }

        // Si toutes les vérifications passent, supprimer le client
        clientRepository.delete(client);
    }

    public Double getSoldeTotalClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'ID: " + clientId));

        return client.getComptes().stream()
                .mapToDouble(Compte::getSolde)
                .sum();
    }

    public boolean estEnDecouvert(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'ID: " + clientId));

        return client.getComptes().stream()
                .anyMatch(compte -> compte.getSolde() < 0);
    }

    public Double getMontantDecouvert(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'ID: " + clientId));

        return Math.abs(client.getComptes().stream()
                .filter(compte -> compte.getSolde() < 0)
                .mapToDouble(Compte::getSolde)
                .sum());
    }
}
