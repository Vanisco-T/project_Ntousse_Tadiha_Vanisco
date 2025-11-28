package org.formation.projet_ntousse_tadiha_vanisco.services;

import org.formation.projet_ntousse_tadiha_vanisco.entities.Compte;
import org.formation.projet_ntousse_tadiha_vanisco.entities.CptCourant;
import org.formation.projet_ntousse_tadiha_vanisco.entities.CptEpargne;
import org.formation.projet_ntousse_tadiha_vanisco.repositories.CompteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires - VirementService")
class VirementServiceTest {

    @Mock
    private CompteRepository compteRepository;

    @InjectMocks
    private VirementService virementService;

    private Compte compteSource;
    private Compte compteDestination;
    private Compte compteEpargneSource;
    private Compte compteEpargneDestination;

    @BeforeEach
    void setUp() {
        // Configuration du compte source (courant)
        compteSource = new CptCourant();
        compteSource.setId(1L);
        compteSource.setNumero("CC001");
        compteSource.setSolde(2000.0);

        // Configuration du compte destination (courant)
        compteDestination = new CptCourant();
        compteDestination.setId(2L);
        compteDestination.setNumero("CC002");
        compteDestination.setSolde(1000.0);

        // Configuration du compte épargne source
        compteEpargneSource = new CptEpargne();
        compteEpargneSource.setId(3L);
        compteEpargneSource.setNumero("CE001");
        compteEpargneSource.setSolde(5000.0);

        // Configuration du compte épargne destination
        compteEpargneDestination = new CptEpargne();
        compteEpargneDestination.setId(4L);
        compteEpargneDestination.setNumero("CE002");
        compteEpargneDestination.setSolde(3000.0);
    }

    @Test
    @DisplayName("EFFECTUER_VIREMENT - Doit effectuer un virement entre comptes courants avec succès")
    void effectuerVirement_ComptesCourants_ValidMontant_Success() {
        // Given
        when(compteRepository.findByNumero("CC001")).thenReturn(compteSource);
        when(compteRepository.findByNumero("CC002")).thenReturn(compteDestination);
        when(compteRepository.save(any(Compte.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        virementService.effectuerVirement("CC001", "CC002", 500.0);

        // Then
        assertEquals(1500.0, compteSource.getSolde(), "Le compte source doit être débité de 500€");
        assertEquals(1500.0, compteDestination.getSolde(), "Le compte destination doit être crédité de 500€");

        verify(compteRepository).findByNumero("CC001");
        verify(compteRepository).findByNumero("CC002");
        verify(compteRepository, times(2)).save(any(Compte.class));
    }

    @Test
    @DisplayName("EFFECTUER_VIREMENT - Doit effectuer un virement entre comptes épargne avec succès")
    void effectuerVirement_ComptesEpargne_ValidMontant_Success() {
        // Given
        when(compteRepository.findByNumero("CE001")).thenReturn(compteEpargneSource);
        when(compteRepository.findByNumero("CE002")).thenReturn(compteEpargneDestination);
        when(compteRepository.save(any(Compte.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        virementService.effectuerVirement("CE001", "CE002", 1000.0);

        // Then
        assertEquals(4000.0, compteEpargneSource.getSolde(), "Le compte épargne source doit être débité de 1000€");
        assertEquals(4000.0, compteEpargneDestination.getSolde(), "Le compte épargne destination doit être crédité de 1000€");

        verify(compteRepository).findByNumero("CE001");
        verify(compteRepository).findByNumero("CE002");
        verify(compteRepository, times(2)).save(any(Compte.class));
    }

    @Test
    @DisplayName("EFFECTUER_VIREMENT - Doit effectuer un virement mixte (courant vers épargne) avec succès")
    void effectuerVirement_CourantVersEpargne_ValidMontant_Success() {
        // Given
        when(compteRepository.findByNumero("CC001")).thenReturn(compteSource);
        when(compteRepository.findByNumero("CE001")).thenReturn(compteEpargneSource);
        when(compteRepository.save(any(Compte.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        virementService.effectuerVirement("CC001", "CE001", 750.0);

        // Then
        assertEquals(1250.0, compteSource.getSolde(), "Le compte courant doit être débité de 750€");
        assertEquals(5750.0, compteEpargneSource.getSolde(), "Le compte épargne doit être crédité de 750€");

        verify(compteRepository).findByNumero("CC001");
        verify(compteRepository).findByNumero("CE001");
        verify(compteRepository, times(2)).save(any(Compte.class));
    }

    @Test
    @DisplayName("EFFECTUER_VIREMENT - Doit autoriser un virement avec découvert dans la limite")
    void effectuerVirement_CompteCourant_DecouvertAutorise_Success() {
        // Given
        Compte compteSourceAvecDecouvert = new CptCourant();
        compteSourceAvecDecouvert.setId(5L);
        compteSourceAvecDecouvert.setNumero("CC005");
        compteSourceAvecDecouvert.setSolde(500.0); // Découvert max = 1000€

        when(compteRepository.findByNumero("CC005")).thenReturn(compteSourceAvecDecouvert);
        when(compteRepository.findByNumero("CC002")).thenReturn(compteDestination);
        when(compteRepository.save(any(Compte.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When - Débiter 1000€ (solde 500€ + découvert 500€)
        virementService.effectuerVirement("CC005", "CC002", 1000.0);

        // Then
        assertEquals(-500.0, compteSourceAvecDecouvert.getSolde(), "Le compte source doit être à découvert de 500€");
        assertEquals(2000.0, compteDestination.getSolde(), "Le compte destination doit être crédité de 1000€");

        verify(compteRepository).findByNumero("CC005");
        verify(compteRepository).findByNumero("CC002");
        verify(compteRepository, times(2)).save(any(Compte.class));
    }

    @Test
    @DisplayName("EFFECTUER_VIREMENT - Doit échouer quand le compte source n'existe pas")
    void effectuerVirement_CompteSourceInexistant_ShouldThrowException() {
        // Given
        when(compteRepository.findByNumero("INEXISTANT")).thenReturn(null);
        when(compteRepository.findByNumero("CC002")).thenReturn(compteDestination);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> virementService.effectuerVirement("INEXISTANT", "CC002", 100.0)
        );

        assertEquals("Compte source ou destination non trouvé", exception.getMessage());
        verify(compteRepository).findByNumero("INEXISTANT");
        verify(compteRepository).findByNumero("CC002");
        verify(compteRepository, never()).save(any(Compte.class));
    }

    @Test
    @DisplayName("EFFECTUER_VIREMENT - Doit échouer quand le compte destination n'existe pas")
    void effectuerVirement_CompteDestinationInexistant_ShouldThrowException() {
        // Given
        when(compteRepository.findByNumero("CC001")).thenReturn(compteSource);
        when(compteRepository.findByNumero("INEXISTANT")).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> virementService.effectuerVirement("CC001", "INEXISTANT", 100.0)
        );

        assertEquals("Compte source ou destination non trouvé", exception.getMessage());
        verify(compteRepository).findByNumero("CC001");
        verify(compteRepository).findByNumero("INEXISTANT");
        verify(compteRepository, never()).save(any(Compte.class));
    }

    @Test
    @DisplayName("EFFECTUER_VIREMENT - Doit échouer quand le montant est négatif")
    void effectuerVirement_MontantNegatif_ShouldThrowException() {
        // Given
        when(compteRepository.findByNumero("CC001")).thenReturn(compteSource);
        when(compteRepository.findByNumero("CC002")).thenReturn(compteDestination);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> virementService.effectuerVirement("CC001", "CC002", -100.0)
        );

        assertEquals("Montant du virement doit être positif", exception.getMessage());
        verify(compteRepository).findByNumero("CC001");
        verify(compteRepository).findByNumero("CC002");
        verify(compteRepository, never()).save(any(Compte.class));
    }

    @Test
    @DisplayName("EFFECTUER_VIREMENT - Doit échouer quand le montant est zéro")
    void effectuerVirement_MontantZero_ShouldThrowException() {
        // Given
        when(compteRepository.findByNumero("CC001")).thenReturn(compteSource);
        when(compteRepository.findByNumero("CC002")).thenReturn(compteDestination);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> virementService.effectuerVirement("CC001", "CC002", 0.0)
        );

        assertEquals("Montant du virement doit être positif", exception.getMessage());
        verify(compteRepository).findByNumero("CC001");
        verify(compteRepository).findByNumero("CC002");
        verify(compteRepository, never()).save(any(Compte.class));
    }

    @Test
    @DisplayName("EFFECTUER_VIREMENT - Doit échouer quand les fonds sont insuffisants sur compte courant")
    void effectuerVirement_CompteCourant_FondsInsuffisants_ShouldThrowException() {
        // Given
        Compte compteSourceFaible = new CptCourant();
        compteSourceFaible.setId(6L);
        compteSourceFaible.setNumero("CC006");
        compteSourceFaible.setSolde(100.0); // Découvert max = 1000€

        when(compteRepository.findByNumero("CC006")).thenReturn(compteSourceFaible);
        when(compteRepository.findByNumero("CC002")).thenReturn(compteDestination);

        // When & Then - Tenter de débiter 1500€ (limite = 100 + 1000 = 1100€)
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> virementService.effectuerVirement("CC006", "CC002", 1500.0)
        );

        assertEquals("Fonds insuffisants pour effectuer le virement", exception.getMessage());
        verify(compteRepository).findByNumero("CC006");
        verify(compteRepository).findByNumero("CC002");
        verify(compteRepository, never()).save(any(Compte.class));
    }

    @Test
    @DisplayName("EFFECTUER_VIREMENT - Doit échouer quand les fonds sont insuffisants sur compte épargne")
    void effectuerVirement_CompteEpargne_FondsInsuffisants_ShouldThrowException() {
        // Given
        Compte compteEpargneFaible = new CptEpargne();
        compteEpargneFaible.setId(7L);
        compteEpargneFaible.setNumero("CE007");
        compteEpargneFaible.setSolde(500.0); // Aucun découvert autorisé

        when(compteRepository.findByNumero("CE007")).thenReturn(compteEpargneFaible);
        when(compteRepository.findByNumero("CC002")).thenReturn(compteDestination);

        // When & Then - Tenter de débiter 600€ (solde = 500€)
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> virementService.effectuerVirement("CE007", "CC002", 600.0)
        );

        assertEquals("Fonds insuffisants pour effectuer le virement", exception.getMessage());
        verify(compteRepository).findByNumero("CE007");
        verify(compteRepository).findByNumero("CC002");
        verify(compteRepository, never()).save(any(Compte.class));
    }

    @Test
    @DisplayName("EFFECTUER_VIREMENT - Doit échouer quand le découvert maximum est dépassé")
    void effectuerVirement_CompteCourant_DecouvertMaxDepasse_ShouldThrowException() {
        // Given
        Compte compteSourceLimite = new CptCourant();
        compteSourceLimite.setId(8L);
        compteSourceLimite.setNumero("CC008");
        compteSourceLimite.setSolde(100.0); // Découvert max = 1000€

        when(compteRepository.findByNumero("CC008")).thenReturn(compteSourceLimite);
        when(compteRepository.findByNumero("CC002")).thenReturn(compteDestination);

        // When & Then - Tenter de débiter 1200€ (limite = 100 + 1000 = 1100€)
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> virementService.effectuerVirement("CC008", "CC002", 1200.0)
        );

        assertEquals("Fonds insuffisants pour effectuer le virement", exception.getMessage());
        verify(compteRepository).findByNumero("CC008");
        verify(compteRepository).findByNumero("CC002");
        verify(compteRepository, never()).save(any(Compte.class));
    }

    @Test
    @DisplayName("EFFECTUER_VIREMENT - Doit sauvegarder les deux comptes après virement réussi")
    void effectuerVirement_Success_ShouldSaveBothComptes() {
        // Given
        when(compteRepository.findByNumero("CC001")).thenReturn(compteSource);
        when(compteRepository.findByNumero("CC002")).thenReturn(compteDestination);
        when(compteRepository.save(any(Compte.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        virementService.effectuerVirement("CC001", "CC002", 300.0);

        // Then
        verify(compteRepository).save(compteSource);
        verify(compteRepository).save(compteDestination);
        verify(compteRepository, times(2)).save(any(Compte.class));

        // Vérifier que les soldes sont mis à jour
        assertEquals(1700.0, compteSource.getSolde());
        assertEquals(1300.0, compteDestination.getSolde());
    }

    @Test
    @DisplayName("EFFECTUER_VIREMENT - Doit maintenir l'intégrité transactionnelle en cas d'erreur")
    void effectuerVirement_ShouldNotSaveIfAnyErrorOccurs() {
        // Given - Compte source valide, compte destination inexistant
        when(compteRepository.findByNumero("CC001")).thenReturn(compteSource);
        when(compteRepository.findByNumero("INEXISTANT")).thenReturn(null);

        // When & Then
        assertThrows(
                RuntimeException.class,
                () -> virementService.effectuerVirement("CC001", "INEXISTANT", 100.0)
        );

        // Vérifier qu'aucun compte n'a été sauvegardé
        verify(compteRepository, never()).save(any(Compte.class));
        // Vérifier que le solde source n'a pas changé
        assertEquals(2000.0, compteSource.getSolde(), "Le solde source ne doit pas changer en cas d'erreur");
    }
}
