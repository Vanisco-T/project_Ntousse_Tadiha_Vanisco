package org.formation.projet_ntousse_tadiha_vanisco.repositories;


import org.formation.projet_ntousse_tadiha_vanisco.entities.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {
    Compte findByNumero(String numero);
}
