package org.formation.projet_ntousse_tadiha_vanisco.controllers;


import org.formation.projet_ntousse_tadiha_vanisco.entities.Conseiller;
import org.formation.projet_ntousse_tadiha_vanisco.repositories.ConseillerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conseillers")
public class ConseillerController {

    @Autowired
    private ConseillerRepository conseillerRepository;

    @GetMapping
    public List<Conseiller> getAllConseillers() {
        return conseillerRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conseiller> getConseiller(@PathVariable Long id) {
        return conseillerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Conseiller createConseiller(@RequestBody Conseiller conseiller) {
        return conseillerRepository.save(conseiller);
    }
}
