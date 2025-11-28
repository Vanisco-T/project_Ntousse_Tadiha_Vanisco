package org.formation.projet_ntousse_tadiha_vanisco.services;


import org.formation.projet_ntousse_tadiha_vanisco.entities.Compte;
import org.formation.projet_ntousse_tadiha_vanisco.repositories.CompteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CompteService {

    @Autowired
    private CompteRepository compteRepository;

    public Compte crediter(String numeroCompte, Double montant) {
        Compte compte = compteRepository.findByNumero(numeroCompte);
        if (compte != null && montant > 0) {
            compte.crediter(montant);
            return compteRepository.save(compte);
        }
        throw new RuntimeException("Compte non trouvé ou montant invalide");
    }

    public Compte debiter(String numeroCompte, Double montant) {
        Compte compte = compteRepository.findByNumero(numeroCompte);
        if (compte != null && montant > 0 && compte.peutDebiter(montant)) {
            compte.debiter(montant);
            return compteRepository.save(compte);
        }
        throw new RuntimeException("Opération impossible");
    }

    public Compte findByNumero(String numero) {
        return compteRepository.findByNumero(numero);
    }
}
