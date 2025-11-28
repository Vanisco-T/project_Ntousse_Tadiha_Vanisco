package org.formation.projet_ntousse_tadiha_vanisco.services;


import org.formation.projet_ntousse_tadiha_vanisco.entities.Compte;
import org.formation.projet_ntousse_tadiha_vanisco.repositories.CompteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuditService {

    @Autowired
    private CompteRepository compteRepository;

    public Map<String, Object> faireAudit() {
        List<Compte> comptes = compteRepository.findAll();

        double totalCredits = comptes.stream()
                .filter(c -> c.getSolde() > 0)
                .mapToDouble(Compte::getSolde)
                .sum();

        double totalDebits = comptes.stream()
                .filter(c -> c.getSolde() < 0)
                .mapToDouble(Compte::getSolde)
                .sum();

        long nbComptesCrediteurs = comptes.stream()
                .filter(c -> c.getSolde() > 0)
                .count();

        long nbComptesDebiteurs = comptes.stream()
                .filter(c -> c.getSolde() < 0)
                .count();

        Map<String, Object> audit = new HashMap<>();
        audit.put("totalCredits", totalCredits);
        audit.put("totalDebits", Math.abs(totalDebits));
        audit.put("soldeNet", totalCredits - Math.abs(totalDebits));
        audit.put("nbComptesCrediteurs", nbComptesCrediteurs);
        audit.put("nbComptesDebiteurs", nbComptesDebiteurs);
        audit.put("totalComptes", comptes.size());

        return audit;
    }
}
