package org.formation.projet_ntousse_tadiha_vanisco.services;


import org.formation.projet_ntousse_tadiha_vanisco.entities.Compte;
import org.formation.projet_ntousse_tadiha_vanisco.repositories.CompteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VirementService {

    @Autowired
    private CompteRepository compteRepository;

    public void effectuerVirement(String numeroSource, String numeroDestination, Double montant) {
        Compte source = compteRepository.findByNumero(numeroSource);
        Compte destination = compteRepository.findByNumero(numeroDestination);

        if (source == null || destination == null) {
            throw new RuntimeException("Compte source ou destination non trouvé");
        }

        if (montant <= 0) {
            throw new RuntimeException("Montant du virement doit être positif");
        }

        if (!source.peutDebiter(montant)) {
            throw new RuntimeException("Fonds insuffisants pour effectuer le virement");
        }

        source.debiter(montant);
        destination.crediter(montant);

        compteRepository.save(source);
        compteRepository.save(destination);
    }
}
