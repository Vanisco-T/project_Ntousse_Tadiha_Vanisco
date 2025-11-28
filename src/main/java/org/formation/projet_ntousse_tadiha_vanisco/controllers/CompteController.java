package org.formation.projet_ntousse_tadiha_vanisco.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.formation.projet_ntousse_tadiha_vanisco.entities.Compte;
import org.formation.projet_ntousse_tadiha_vanisco.services.CompteService;
import org.formation.projet_ntousse_tadiha_vanisco.services.VirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comptes")
@Tag(name = "Comptes", description = "API de gestion des opérations bancaires sur les comptes")
public class CompteController {

    @Autowired
    private CompteService compteService;

    @Autowired
    private VirementService virementService;

    @Operation(
            summary = "Créditer un compte",
            description = "Ajoute un montant au solde d'un compte spécifié. " +
                    "Le montant doit être positif. " +
                    "Opération tracée dans les logs de virements."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Compte crédité avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "id": 1,
                          "numero": "CC001",
                          "solde": 1600.0,
                          "dateOuverture": "2025-11-28T10:00:00"
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Opération impossible - compte non trouvé ou montant invalide"
            )
    })
    @PostMapping("/{numero}/crediter")
    public ResponseEntity<Compte> crediter(
            @Parameter(description = "Numéro du compte", example = "CC001")
            @PathVariable String numero,
            @Parameter(description = "Montant à créditer (doit être positif)", example = "100.0")
            @RequestParam Double montant) {
        try {
            Compte compte = compteService.crediter(numero, montant);
            return ResponseEntity.ok(compte);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Débiter un compte",
            description = "Retire un montant du solde d'un compte spécifié. " +
                    "Vérifie que le compte a suffisamment de fonds et respecte les limites de découvert. " +
                    "Pour les comptes courants : découvert maximum de 1000€ autorisé. " +
                    "Pour les comptes épargne : aucun découvert autorisé."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Compte débité avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "id": 1,
                          "numero": "CC001",
                          "solde": 1400.0,
                          "dateOuverture": "2025-11-28T10:00:00"
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Opération impossible - solde insuffisant, compte non trouvé ou montant invalide"
            )
    })
    @PostMapping("/{numero}/debiter")
    public ResponseEntity<Compte> debiter(
            @Parameter(description = "Numéro du compte", example = "CC001")
            @PathVariable String numero,
            @Parameter(description = "Montant à débiter (doit être positif)", example = "50.0")
            @RequestParam Double montant) {
        try {
            Compte compte = compteService.debiter(numero, montant);
            return ResponseEntity.ok(compte);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Effectuer un virement entre comptes",
            description = "Transfère un montant d'un compte source vers un compte destination. " +
                    "**Opération sensible** - Traçage complet dans le fichier logs/virements.log. " +
                    "Vérifications : comptes différents, fonds suffisants, montant positif."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Virement effectué avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "\"Virement effectué avec succès\""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Virement impossible",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "Fonds insuffisants",
                                            value = "Fonds insuffisants sur le compte source pour effectuer le virement"
                                    ),
                                    @ExampleObject(
                                            name = "Compte non trouvé",
                                            value = "Compte source ou destination non trouvé"
                                    ),
                                    @ExampleObject(
                                            name = "Même compte",
                                            value = "Les comptes source et destination doivent être différents"
                                    )
                            }
                    )
            )
    })
    @PostMapping("/virement")
    public ResponseEntity<String> effectuerVirement(
            @Parameter(description = "Numéro du compte source", example = "CC001")
            @RequestParam String source,
            @Parameter(description = "Numéro du compte destination", example = "CC002")
            @RequestParam String destination,
            @Parameter(description = "Montant du virement (doit être positif)", example = "100.0")
            @RequestParam Double montant) {
        try {
            virementService.effectuerVirement(source, destination, montant);
            return ResponseEntity.ok("Virement effectué avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
            summary = "Consulter un compte",
            description = "Récupère les informations détaillées d'un compte par son numéro. " +
                    "Inclut le solde, la date d'ouverture et le type de compte (courant/épargne)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Compte trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Compte courant",
                                            value = """
                            {
                              "id": 1,
                              "numero": "CC001",
                              "solde": 1500.0,
                              "dateOuverture": "2025-11-28T10:00:00",
                              "decouvertMax": 1000.0,
                              "type": "CptCourant"
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "Compte épargne",
                                            value = """
                            {
                              "id": 2,
                              "numero": "CE001",
                              "solde": 5000.0,
                              "dateOuverture": "2025-11-28T10:00:00",
                              "taux": 0.03,
                              "type": "CptEpargne"
                            }
                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Compte non trouvé"
            )
    })
    @GetMapping("/{numero}")
    public ResponseEntity<Compte> getCompte(
            @Parameter(description = "Numéro du compte", example = "CC001")
            @PathVariable String numero) {
        Compte compte = compteService.findByNumero(numero);
        return compte != null ? ResponseEntity.ok(compte) : ResponseEntity.notFound().build();
    }
}