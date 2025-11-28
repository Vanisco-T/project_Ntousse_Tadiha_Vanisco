package org.formation.projet_ntousse_tadiha_vanisco.repositories;


import org.formation.projet_ntousse_tadiha_vanisco.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByIdClient(String idClient);
    List<Client> findByConseillerId(Long conseillerId);
}
