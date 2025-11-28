package org.formation.projet_ntousse_tadiha_vanisco.controllers;

import org.formation.projet_ntousse_tadiha_vanisco.entities.Compte;
import org.formation.projet_ntousse_tadiha_vanisco.services.CompteService;
import org.formation.projet_ntousse_tadiha_vanisco.services.VirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comptes")
public class CompteController {

    @Autowired
    private CompteService compteService;

    @Autowired
    private VirementService virementService;

    @PostMapping("/{numero}/crediter")
    public ResponseEntity<Compte> crediter(@PathVariable String numero, @RequestParam Double montant) {
        try {
            Compte compte = compteService.crediter(numero, montant);
            return ResponseEntity.ok(compte);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{numero}/debiter")
    public ResponseEntity<Compte> debiter(@PathVariable String numero, @RequestParam Double montant) {
        try {
            Compte compte = compteService.debiter(numero, montant);
            return ResponseEntity.ok(compte);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/virement")
    public ResponseEntity<String> effectuerVirement(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam Double montant) {
        try {
            virementService.effectuerVirement(source, destination, montant);
            return ResponseEntity.ok("Virement effectué avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{numero}")
    public ResponseEntity<Compte> getCompte(@PathVariable String numero) {
        Compte compte = compteService.findByNumero(numero);
        return compte != null ? ResponseEntity.ok(compte) : ResponseEntity.notFound().build();
    }
}
