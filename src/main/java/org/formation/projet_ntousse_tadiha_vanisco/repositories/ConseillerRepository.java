package org.formation.projet_ntousse_tadiha_vanisco.repositories;


import org.formation.projet_ntousse_tadiha_vanisco.entities.Conseiller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConseillerRepository extends JpaRepository<Conseiller, Long> {
    Conseiller findByIdConseiller(String idConseiller);
}