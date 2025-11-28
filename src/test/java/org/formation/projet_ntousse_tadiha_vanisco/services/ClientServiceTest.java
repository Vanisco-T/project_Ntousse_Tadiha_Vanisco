package org.formation.projet_ntousse_tadiha_vanisco.services;

import org.formation.projet_ntousse_tadiha_vanisco.entities.Client;
import org.formation.projet_ntousse_tadiha_vanisco.entities.Compte;
import org.formation.projet_ntousse_tadiha_vanisco.entities.CptCourant;
import org.formation.projet_ntousse_tadiha_vanisco.entities.CptEpargne;
import org.formation.projet_ntousse_tadiha_vanisco.exceptions.SuppressionImpossibleException;
import org.formation.projet_ntousse_tadiha_vanisco.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires - ClientService")
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client clientAvecComptes;
    private Client clientSansComptes;
    private Compte compteCourantAvecSolde;
    private Compte compteEpargneAvecSolde;
    private Compte compteCourantDecouvert;

    @BeforeEach
    void setUp() {
        // Configuration des comptes de test
        compteCourantAvecSolde = new CptCourant();
        compteCourantAvecSolde.setId(1L);
        compteCourantAvecSolde.setNumero("CC001");
        compteCourantAvecSolde.setSolde(1500.0);

        compteEpargneAvecSolde = new CptEpargne();
        compteEpargneAvecSolde.setId(2L);
        compteEpargneAvecSolde.setNumero("CE001");
        compteEpargneAvecSolde.setSolde(5000.0);

        compteCourantDecouvert = new CptCourant();
        compteCourantDecouvert.setId(3L);
        compteCourantDecouvert.setNumero("CC002");
        compteCourantDecouvert.setSolde(-200.0);

        // Configuration du client avec comptes
        clientAvecComptes = new Client();
        clientAvecComptes.setId(1L);
        clientAvecComptes.setNom("Martin");
        clientAvecComptes.setPrenom("Jean");
        clientAvecComptes.setCourant((CptCourant) compteCourantAvecSolde);
        clientAvecComptes.setEpargne((CptEpargne) compteEpargneAvecSolde);

        // Configuration du client sans comptes
        clientSansComptes = new Client();
        clientSansComptes.setId(2L);
        clientSansComptes.setNom("Durand");
        clientSansComptes.setPrenom("Marie");
    }

    @Test
    @DisplayName("PEUT_ETRE_SUPPRIME - Client sans comptes doit pouvoir être supprimé")
    void peutEtreSupprime_ClientSansComptes_ShouldReturnTrue() {
        // When
        boolean result = clientService.peutEtreSupprime(clientSansComptes);

        // Then
        assertTrue(result, "Un client sans comptes doit pouvoir être supprimé");
    }

    @Test
    @DisplayName("PEUT_ETRE_SUPPRIME - Client avec comptes à solde nul doit pouvoir être supprimé")
    void peutEtreSupprime_ClientAvecComptesSoldeNul_ShouldReturnTrue() {
        // Given
        Compte compteNul1 = new CptCourant();
        compteNul1.setSolde(0.0);
        Compte compteNul2 = new CptEpargne();
        compteNul2.setSolde(0.0);

        Client client = new Client();
        client.setCourant((CptCourant) compteNul1);
        client.setEpargne((CptEpargne) compteNul2);

        // When
        boolean result = clientService.peutEtreSupprime(client);

        // Then
        assertTrue(result, "Un client avec comptes à solde nul doit pouvoir être supprimé");
    }

    @Test
    @DisplayName("PEUT_ETRE_SUPPRIME - Client avec compte à solde positif ne doit pas pouvoir être supprimé")
    void peutEtreSupprime_ClientAvecCompteSoldePositif_ShouldReturnFalse() {
        // Given
        Compte comptePositif = new CptCourant();
        comptePositif.setSolde(500.0);

        Client client = new Client();
        client.setCourant((CptCourant) comptePositif);

        // When
        boolean result = clientService.peutEtreSupprime(client);

        // Then
        assertFalse(result, "Un client avec compte à solde positif ne doit pas pouvoir être supprimé");
    }

    @Test
    @DisplayName("PEUT_ETRE_SUPPRIME - Client avec compte à découvert ne doit pas pouvoir être supprimé")
    void peutEtreSupprime_ClientAvecCompteDecouvert_ShouldReturnFalse() {
        // Given
        Compte compteDecouvert = new CptCourant();
        compteDecouvert.setSolde(-300.0);

        Client client = new Client();
        client.setCourant((CptCourant) compteDecouvert);

        // When
        boolean result = clientService.peutEtreSupprime(client);

        // Then
        assertFalse(result, "Un client avec compte à découvert ne doit pas pouvoir être supprimé");
    }

    @Test
    @DisplayName("PEUT_ETRE_SUPPRIME - Client avec compte positif et compte découvert ne doit pas pouvoir être supprimé")
    void peutEtreSupprime_ClientAvecComptePositifEtDecouvert_ShouldReturnFalse() {
        // Given
        Compte comptePositif = new CptCourant();
        comptePositif.setSolde(1000.0);
        Compte compteDecouvert = new CptEpargne();
        compteDecouvert.setSolde(-500.0);

        Client client = new Client();
        client.setCourant((CptCourant) comptePositif);
        client.setEpargne((CptEpargne) compteDecouvert);

        // When
        boolean result = clientService.peutEtreSupprime(client);

        // Then
        assertFalse(result, "Un client avec au moins un compte non nul ne doit pas pouvoir être supprimé");
    }

    @Test
    @DisplayName("SUPPRIMER_CLIENT - Doit supprimer un client sans comptes avec succès")
    void supprimerClient_ClientSansComptes_ShouldDeleteClient() {
        // Given
        when(clientRepository.findById(2L)).thenReturn(Optional.of(clientSansComptes));

        // When
        clientService.supprimerClient(2L);

        // Then
        verify(clientRepository).findById(2L);
        verify(clientRepository).delete(clientSansComptes);
    }

    @Test
    @DisplayName("SUPPRIMER_CLIENT - Doit supprimer un client avec comptes à solde nul avec succès")
    void supprimerClient_ClientAvecComptesSoldeNul_ShouldDeleteClient() {
        // Given
        Compte compteNul1 = new CptCourant();
        compteNul1.setSolde(0.0);
        Compte compteNul2 = new CptEpargne();
        compteNul2.setSolde(0.0);

        Client client = new Client();
        client.setId(3L);
        client.setCourant((CptCourant) compteNul1);
        client.setEpargne((CptEpargne) compteNul2);

        when(clientRepository.findById(3L)).thenReturn(Optional.of(client));

        // When
        clientService.supprimerClient(3L);

        // Then
        verify(clientRepository).findById(3L);
        verify(clientRepository).delete(client);
    }

    @Test
    @DisplayName("SUPPRIMER_CLIENT - Doit échouer quand le client a des comptes avec solde positif")
    void supprimerClient_ClientAvecComptesSoldePositif_ShouldThrowException() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(clientAvecComptes));

        // When & Then
        SuppressionImpossibleException exception = assertThrows(
                SuppressionImpossibleException.class,
                () -> clientService.supprimerClient(1L)
        );

        assertTrue(exception.getMessage().contains("Impossible de supprimer le client 'Jean Martin'"));
        assertTrue(exception.getMessage().contains("solde positif"));
        verify(clientRepository).findById(1L);
        verify(clientRepository, never()).delete(any(Client.class));
    }

    @Test
    @DisplayName("SUPPRIMER_CLIENT - Doit échouer quand le client n'existe pas")
    void supprimerClient_ClientInexistant_ShouldThrowException() {
        // Given
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> clientService.supprimerClient(999L)
        );

        assertEquals("Client non trouvé avec l'ID: 999", exception.getMessage());
        verify(clientRepository).findById(999L);
        verify(clientRepository, never()).delete(any(Client.class));
    }

    @Test
    @DisplayName("GET_SOLDE_TOTAL_CLIENT - Doit calculer le solde total correctement")
    void getSoldeTotalClient_ClientAvecComptes_ShouldReturnSum() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(clientAvecComptes));

        // When
        Double result = clientService.getSoldeTotalClient(1L);

        // Then
        assertEquals(6500.0, result); // 1500 + 5000
        verify(clientRepository).findById(1L);
    }

    @Test
    @DisplayName("GET_SOLDE_TOTAL_CLIENT - Doit retourner 0 pour un client sans comptes")
    void getSoldeTotalClient_ClientSansComptes_ShouldReturnZero() {
        // Given
        when(clientRepository.findById(2L)).thenReturn(Optional.of(clientSansComptes));

        // When
        Double result = clientService.getSoldeTotalClient(2L);

        // Then
        assertEquals(0.0, result);
        verify(clientRepository).findById(2L);
    }

    @Test
    @DisplayName("GET_SOLDE_TOTAL_CLIENT - Doit échouer quand le client n'existe pas")
    void getSoldeTotalClient_ClientInexistant_ShouldThrowException() {
        // Given
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> clientService.getSoldeTotalClient(999L)
        );

        assertEquals("Client non trouvé avec l'ID: 999", exception.getMessage());
        verify(clientRepository).findById(999L);
    }

    @Test
    @DisplayName("EST_EN_DECOUVERT - Doit retourner true quand au moins un compte est à découvert")
    void estEnDecouvert_ClientAvecDecouvert_ShouldReturnTrue() {
        // Given
        Client clientAvecDecouvert = new Client();
        clientAvecDecouvert.setId(4L);
        clientAvecDecouvert.setCourant((CptCourant) compteCourantDecouvert);

        when(clientRepository.findById(4L)).thenReturn(Optional.of(clientAvecDecouvert));

        // When
        boolean result = clientService.estEnDecouvert(4L);

        // Then
        assertTrue(result);
        verify(clientRepository).findById(4L);
    }

    @Test
    @DisplayName("EST_EN_DECOUVERT - Doit retourner false quand aucun compte n'est à découvert")
    void estEnDecouvert_ClientSansDecouvert_ShouldReturnFalse() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(clientAvecComptes));

        // When
        boolean result = clientService.estEnDecouvert(1L);

        // Then
        assertFalse(result);
        verify(clientRepository).findById(1L);
    }

    @Test
    @DisplayName("GET_MONTANT_DECOUVERT - Doit calculer le montant total du découvert")
    void getMontantDecouvert_ClientAvecDecouverts_ShouldReturnSum() {
        // Given
        Compte compteDecouvert1 = new CptCourant();
        compteDecouvert1.setSolde(-300.0);
        Compte compteDecouvert2 = new CptEpargne();
        compteDecouvert2.setSolde(-150.0);

        Client clientAvecDecouverts = new Client();
        clientAvecDecouverts.setId(5L);
        clientAvecDecouverts.setCourant((CptCourant) compteDecouvert1);
        clientAvecDecouverts.setEpargne((CptEpargne) compteDecouvert2);

        when(clientRepository.findById(5L)).thenReturn(Optional.of(clientAvecDecouverts));

        // When
        Double result = clientService.getMontantDecouvert(5L);

        // Then
        assertEquals(450.0, result); // 300 + 150
        verify(clientRepository).findById(5L);
    }

    @Test
    @DisplayName("GET_MONTANT_DECOUVERT - Doit retourner 0 quand aucun découvert")
    void getMontantDecouvert_ClientSansDecouvert_ShouldReturnZero() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(clientAvecComptes));

        // When
        Double result = clientService.getMontantDecouvert(1L);

        // Then
        assertEquals(0.0, result);
        verify(clientRepository).findById(1L);
    }

    @Test
    @DisplayName("GET_MONTANT_DECOUVERT - Doit ignorer les comptes positifs dans le calcul du découvert")
    void getMontantDecouvert_ClientAvecComptesPositifsEtNegatifs_ShouldReturnOnlyNegativeSum() {
        // Given
        Compte comptePositif = new CptCourant();
        comptePositif.setSolde(1000.0);
        Compte compteNegatif = new CptEpargne();
        compteNegatif.setSolde(-500.0);

        Client client = new Client();
        client.setId(6L);
        client.setCourant((CptCourant) comptePositif);
        client.setEpargne((CptEpargne) compteNegatif);

        when(clientRepository.findById(6L)).thenReturn(Optional.of(client));

        // When
        Double result = clientService.getMontantDecouvert(6L);

        // Then
        assertEquals(500.0, result); // Seulement le découvert de 500
        verify(clientRepository).findById(6L);
    }
}
