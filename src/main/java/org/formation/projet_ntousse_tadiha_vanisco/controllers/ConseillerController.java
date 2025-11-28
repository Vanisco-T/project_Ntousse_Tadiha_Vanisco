package org.formation.projet_ntousse_tadiha_vanisco.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.formation.projet_ntousse_tadiha_vanisco.entities.Conseiller;
import org.formation.projet_ntousse_tadiha_vanisco.repositories.ConseillerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conseillers")
@Tag(name = "Conseillers", description = "API de gestion des conseillers bancaires")
public class ConseillerController {

    @Autowired
    private ConseillerRepository conseillerRepository;

    @Operation(
            summary = "Lister tous les conseillers",
            description = "Retourne la liste complète de tous les conseillers avec leurs informations " +
                    "et la liste des clients associés à chaque conseiller."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des conseillers récupérée avec succès",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                    [
                      {
                        "id": 1,
                        "idConseiller": "CONS001",
                        "nom": "Dupont",
                        "prenom": "Marie",
                        "clients": [
                          {
                            "id": 1,
                            "idClient": "CLI001",
                            "nom": "Martin",
                            "prenom": "Jean"
                          }
                        ]
                      },
                      {
                        "id": 2,
                        "idConseiller": "CONS002",
                        "nom": "Martin",
                        "prenom": "Pierre",
                        "clients": []
                      }
                    ]
                    """
                    )
            )
    )
    @GetMapping
    public List<Conseiller> getAllConseillers() {
        return conseillerRepository.findAll();
    }

    @Operation(
            summary = "Obtenir un conseiller par ID",
            description = "Récupère les informations détaillées d'un conseiller spécifique " +
                    "incluant sa liste complète de clients associés."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conseiller trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "id": 1,
                          "idConseiller": "CONS001",
                          "nom": "Dupont",
                          "prenom": "Marie",
                          "clients": [
                            {
                              "id": 1,
                              "idClient": "CLI001",
                              "nom": "Martin",
                              "prenom": "Jean",
                              "adresse": "123 Rue de la Paix",
                              "codePostal": "75001",
                              "ville": "Paris",
                              "telephone": "0123456789"
                            }
                          ]
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Conseiller non trouvé"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Conseiller> getConseiller(
            @Parameter(description = "ID du conseiller", example = "1")
            @PathVariable Long id) {
        return conseillerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Créer un nouveau conseiller",
            description = "Crée un nouveau conseiller avec ses informations d'identification. " +
                    "L'idConseiller doit être unique dans le système."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conseiller créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "id": 3,
                          "idConseiller": "CONS003",
                          "nom": "Bernard",
                          "prenom": "Alice",
                          "clients": []
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides - vérifiez que l'idConseiller est unique"
            )
    })
    @PostMapping
    public Conseiller createConseiller(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet conseiller à créer",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                            {
                              "idConseiller": "CONS003",
                              "nom": "Bernard",
                              "prenom": "Alice"
                            }
                            """
                            )
                    )
            )
            @RequestBody Conseiller conseiller) {
        return conseillerRepository.save(conseiller);
    }

    @Operation(
            summary = "Modifier un conseiller existant",
            description = "Met à jour les informations d'un conseiller existant. " +
                    "Tous les champs peuvent être modifiés sauf la liste des clients " +
                    "(gérée automatiquement par les associations)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conseiller modifié avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "id": 1,
                          "idConseiller": "CONS001_MOD",
                          "nom": "DUPONT",
                          "prenom": "MARIE-CLAIRE",
                          "clients": [
                            {
                              "id": 1,
                              "idClient": "CLI001",
                              "nom": "Martin",
                              "prenom": "Jean"
                            }
                          ]
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Conseiller non trouvé"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Conseiller> updateConseiller(
            @Parameter(description = "ID du conseiller à modifier", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations du conseiller",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                            {
                              "idConseiller": "CONS001_MOD",
                              "nom": "DUPONT",
                              "prenom": "MARIE-CLAIRE"
                            }
                            """
                            )
                    )
            )
            @RequestBody Conseiller conseillerDetails) {
        return conseillerRepository.findById(id)
                .map(conseiller -> {
                    conseiller.setIdConseiller(conseillerDetails.getIdConseiller());
                    conseiller.setNom(conseillerDetails.getNom());
                    conseiller.setPrenom(conseillerDetails.getPrenom());

                    Conseiller updatedConseiller = conseillerRepository.save(conseiller);
                    return ResponseEntity.ok(updatedConseiller);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}