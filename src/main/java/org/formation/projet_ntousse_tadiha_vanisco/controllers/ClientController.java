package org.formation.projet_ntousse_tadiha_vanisco.controllers;

import org.formation.projet_ntousse_tadiha_vanisco.entities.Client;
import org.formation.projet_ntousse_tadiha_vanisco.services.ClientService;
import org.formation.projet_ntousse_tadiha_vanisco.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;

    @GetMapping
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(@PathVariable Long id) {
        return clientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Client createClient(@RequestBody Client client) {
        return clientRepository.save(client);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client clientDetails) {
        return clientRepository.findById(id)
                .map(client -> {
                    client.setNom(clientDetails.getNom());
                    client.setPrenom(clientDetails.getPrenom());
                    client.setAdresse(clientDetails.getAdresse());
                    client.setCodePostal(clientDetails.getCodePostal());
                    client.setVille(clientDetails.getVille());
                    client.setTelephone(clientDetails.getTelephone());
                    return ResponseEntity.ok(clientRepository.save(client));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
        return clientRepository.findById(id)
                .map(client -> {
                    try {
                        clientService.supprimerClient(id);
                        return ResponseEntity.ok().build();
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoints supplémentaires pour vérifier l'état du client
    @GetMapping("/{id}/solde-total")
    public ResponseEntity<Map<String, Double>> getSoldeTotalClient(@PathVariable Long id) {
        try {
            Double soldeTotal = clientService.getSoldeTotalClient(id);
            return ResponseEntity.ok(Map.of("soldeTotal", soldeTotal));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/decouvert")
    public ResponseEntity<Map<String, Object>> getInfoDecouvert(@PathVariable Long id) {
        try {
            boolean estEnDecouvert = clientService.estEnDecouvert(id);
            Double montantDecouvert = clientService.getMontantDecouvert(id);

            Map<String, Object> response = Map.of(
                    "estEnDecouvert", estEnDecouvert,
                    "montantDecouvert", montantDecouvert
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/peut-supprimer")
    public ResponseEntity<Map<String, Boolean>> peutSupprimerClient(@PathVariable Long id) {
        try {
            Client client = clientRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));

            boolean peutSupprimer = clientService.peutEtreSupprime(client);
            return ResponseEntity.ok(Map.of("peutSupprimer", peutSupprimer));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}