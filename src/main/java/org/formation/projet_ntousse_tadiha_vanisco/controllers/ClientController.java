package org.formation.projet_ntousse_tadiha_vanisco.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Clients", description = "API de gestion des clients bancaires")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;

    @Operation(
            summary = "Lister tous les clients",
            description = "Retourne la liste complète de tous les clients avec leurs informations et comptes associés"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des clients récupérée avec succès"
    )
    @GetMapping
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Operation(
            summary = "Obtenir un client par ID",
            description = "Récupère les informations détaillées d'un client spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Client trouvé"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Client non trouvé"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(
            @Parameter(description = "ID du client", example = "1")
            @PathVariable Long id) {
        return clientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Créer un nouveau client",
            description = "Crée un nouveau client avec ses informations personnelles. " +
                    "Les comptes peuvent être associés lors de la création."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Client créé avec succès",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                    {
                      "id": 4,
                      "idClient": "CLI004",
                      "nom": "Durand",
                      "prenom": "Paul",
                      "adresse": "123 Rue Example",
                      "codePostal": "75001",
                      "ville": "Paris",
                      "telephone": "0123456789"
                    }
                    """
                    )
            )
    )
    @PostMapping
    public Client createClient(@RequestBody Client client) {
        return clientRepository.save(client);
    }

    @Operation(
            summary = "Modifier un client",
            description = "Met à jour les informations personnelles d'un client existant"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Client modifié avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Client non trouvé"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(
            @Parameter(description = "ID du client", example = "1")
            @PathVariable Long id,
            @RequestBody Client clientDetails) {
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

    @Operation(
            summary = "Supprimer un client",
            description = "Supprime un client uniquement si tous ses comptes ont un solde nul. " +
                    "Impossible de supprimer un client ayant de l'argent ou des dettes."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Client supprimé avec succès"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Suppression impossible - le client a de l'argent ou des dettes",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "Impossible de supprimer le client 'Jean Martin' car : - Le compte CC001 a un solde positif de 1500.0 €"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Client non trouvé"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClient(
            @Parameter(description = "ID du client", example = "1")
            @PathVariable Long id) {
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

    @Operation(
            summary = "Obtenir le solde total d'un client",
            description = "Calcule la somme des soldes de tous les comptes (courant et épargne) d'un client"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Solde total calculé",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"soldeTotal\": 6500.0}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Client non trouvé"
            )
    })
    @GetMapping("/{id}/solde-total")
    public ResponseEntity<Map<String, Double>> getSoldeTotalClient(
            @Parameter(description = "ID du client", example = "1")
            @PathVariable Long id) {
        try {
            Double soldeTotal = clientService.getSoldeTotalClient(id);
            return ResponseEntity.ok(Map.of("soldeTotal", soldeTotal));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Vérifier l'état de découvert d'un client",
            description = "Indique si un client est à découvert et le montant total du découvert"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Informations de découvert récupérées",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                        {
                          "estEnDecouvert": true,
                          "montantDecouvert": 150.0
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Client non trouvé"
            )
    })
    @GetMapping("/{id}/decouvert")
    public ResponseEntity<Map<String, Object>> getInfoDecouvert(
            @Parameter(description = "ID du client", example = "1")
            @PathVariable Long id) {
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

    @Operation(
            summary = "Vérifier si un client peut être supprimé",
            description = "Vérifie si un client respecte les conditions de suppression : " +
                    "tous ses comptes doivent avoir un solde nul (ni argent, ni dettes)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vérification effectuée",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"peutSupprimer\": false}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Client non trouvé"
            )
    })
    @GetMapping("/{id}/peut-supprimer")
    public ResponseEntity<Map<String, Boolean>> peutSupprimerClient(
            @Parameter(description = "ID du client", example = "1")
            @PathVariable Long id) {
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