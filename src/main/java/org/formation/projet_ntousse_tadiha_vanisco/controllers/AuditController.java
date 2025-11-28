package org.formation.projet_ntousse_tadiha_vanisco.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.formation.projet_ntousse_tadiha_vanisco.services.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/audit")
@Tag(name = "Audit", description = "API d'audit et de rapports financiers")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @Operation(
            summary = "Générer un rapport d'audit complet",
            description = "Génère un rapport détaillé sur l'état financier de tous les comptes de la banque. " +
                    "Inclut les totaux des crédits, débits, soldes nets et statistiques sur les comptes."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rapport d'audit généré avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                          "totalCredits": 12500.0,
                          "totalDebits": 300.0,
                          "soldeNet": 12200.0,
                          "nbComptesCrediteurs": 8,
                          "nbComptesDebiteurs": 2,
                          "totalComptes": 10
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne lors de la génération du rapport"
            )
    })
    @GetMapping
    public Map<String, Object> faireAudit() {
        return auditService.faireAudit();
    }
}