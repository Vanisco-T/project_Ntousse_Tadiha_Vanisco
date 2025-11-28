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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires - CompteService")
class CompteServiceTest {

    @Mock
    private CompteRepository compteRepository;

    @InjectMocks
    private CompteService compteService;

    private Compte compteCourant;
    private Compte compteEpargne;

    @BeforeEach
    void setUp() {
        // Configuration d'un compte courant de test
        compteCourant = new CptCourant();
        compteCourant.setId(1L);
        compteCourant.setNumero("CC001");
        compteCourant.setSolde(1000.0);

        // Configuration d'un compte épargne de test
        compteEpargne = new CptEpargne();
        compteEpargne.setId(2L);
        compteEpargne.setNumero("CE001");
        compteEpargne.setSolde(2000.0);
    }

    @Test
    @DisplayName("CRÉDITER - Doit créditer un compte courant avec succès")
    void crediter_CompteCourantExistant_MontantPositif_Success() {
        // Given
        when(compteRepository.findByNumero("CC001")).thenReturn(compteCourant);
        when(compteRepository.save(any(Compte.class))).thenReturn(compteCourant);

        // When
        Compte result = compteService.crediter("CC001", 500.0);

        // Then
        assertNotNull(result);
        assertEquals(1500.0, result.getSolde());
        verify(compteRepository).findByNumero("CC001");
        verify(compteRepository).save(compteCourant);
    }

    @Test
    @DisplayName("CRÉDITER - Doit créditer un compte épargne avec succès")
    void crediter_CompteEpargneExistant_MontantPositif_Success() {
        // Given
        when(compteRepository.findByNumero("CE001")).thenReturn(compteEpargne);
        when(compteRepository.save(any(Compte.class))).thenReturn(compteEpargne);

        // When
        Compte result = compteService.crediter("CE001", 300.0);

        // Then
        assertNotNull(result);
        assertEquals(2300.0, result.getSolde());
        verify(compteRepository).findByNumero("CE001");
        verify(compteRepository).save(compteEpargne);
    }

    @Test
    @DisplayName("CRÉDITER - Doit échouer quand le compte n'existe pas")
    void crediter_CompteInexistant_ShouldThrowException() {
        // Given
        when(compteRepository.findByNumero("INEXISTANT")).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> compteService.crediter("INEXISTANT", 100.0));

        assertEquals("Compte non trouvé ou montant invalide", exception.getMessage());
        verify(compteRepository).findByNumero("INEXISTANT");
        verify(compteRepository, never()).save(any(Compte.class));
    }

    @Test
    @DisplayName("CRÉDITER - Doit échouer quand le montant est négatif")
    void crediter_MontantNegatif_ShouldThrowException() {
        // Given
        when(compteRepository.findByNumero("CC001")).thenReturn(compteCourant);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> compteService.crediter("CC001", -100.0));

        assertEquals("Compte non trouvé ou montant invalide", exception.getMessage());
        verify(compteRepository).findByNumero("CC001");
        verify(compteRepository, never()).save(any(Compte.class));
    }

    @Test
    @DisplayName("CRÉDITER - Doit échouer quand le montant est zéro")
    void crediter_MontantZero_ShouldThrowException() {
        // Given
        when(compteRepository.findByNumero("CC001")).thenReturn(compteCourant);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> compteService.crediter("CC001", 0.0));

        assertEquals("Compte non trouvé ou montant invalide", exception.getMessage());
        verify(compteRepository).findByNumero("CC001");
        verify(compteRepository, never()).save(any(Compte.class));
    }

    @Test
    @DisplayName("DÉBITER - Doit débiter un compte courant avec succès")
    void debiter_CompteCourantExistant_SoldeSuffisant_Success() {
        // Given
        when(compteRepository.findByNumero("CC001")).thenReturn(compteCourant);
        when(compteRepository.save(any(Compte.class))).thenReturn(compteCourant);

        // When
        Compte result = compteService.debiter("CC001", 500.0);

        // Then
        assertNotNull(result);
        assertEquals(500.0, result.getSolde());
        verify(compteRepository).findByNumero("CC001");
        verify(compteRepository).save(compteCourant);
    }

    @Test
    @DisplayName("DÉBITER - Doit débiter un compte épargne avec succès")
    void debiter_CompteEpargneExistant_SoldeSuffisant_Success() {
        // Given
        when(compteRepository.findByNumero("CE001")).thenReturn(compteEpargne);
        when(compteRepository.save(any(Compte.class))).thenReturn(compteEpargne);

        // When
        Compte result = compteService.debiter("CE001", 1000.0);

        // Then
        assertNotNull(result);
        assertEquals(1000.0, result.getSolde());
        verify(compteRepository).findByNumero("CE001");
        verify(compteRepository).save(compteEpargne);
    }

    @Test
    @DisplayName("DÉBITER - Doit échouer quand le solde est insuffisant sur compte épargne")
    void debiter_CompteEpargne_SoldeInsuffisant_ShouldThrowException() {
        // Given
        when(compteRepository.findByNumero("CE001")).thenReturn(compteEpargne);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> compteService.debiter("CE001", 2500.0));

        assertEquals("Opération impossible", exception.getMessage());
        verify(compteRepository).findByNumero("CE001");
        verify(compteRepository, never()).save(any(Compte.class));
    }

    @Test
    @DisplayName("DÉBITER - Doit échouer quand le découvert est dépassé sur compte courant")
    void debiter_CompteCourant_DecouvertDepasse_ShouldThrowException() {
        // Given
        when(compteRepository.findByNumero("CC001")).thenReturn(compteCourant);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> compteService.debiter("CC001", 2500.0)); // 1000 + 1000 découvert = 2000 max

        assertEquals("Opération impossible", exception.getMessage());
        verify(compteRepository).findByNumero("CC001");
        verify(compteRepository, never()).save(any(Compte.class));
    }

    @Test
    @DisplayName("DÉBITER - Doit autoriser le découvert dans la limite sur compte courant")
    void debiter_CompteCourant_DecouvertAutorise_Success() {
        // Given
        when(compteRepository.findByNumero("CC001")).thenReturn(compteCourant);
        when(compteRepository.save(any(Compte.class))).thenReturn(compteCourant);

        // When - Débiter 1500€ sur un compte avec 1000€ (découvert de 500€ autorisé)
        Compte result = compteService.debiter("CC001", 1500.0);

        // Then
        assertNotNull(result);
        assertEquals(-500.0, result.getSolde());
        verify(compteRepository).findByNumero("CC001");
        verify(compteRepository).save(compteCourant);
    }

    @Test
    @DisplayName("DÉBITER - Doit échouer quand le compte n'existe pas")
    void debiter_CompteInexistant_ShouldThrowException() {
        // Given
        when(compteRepository.findByNumero("INEXISTANT")).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> compteService.debiter("INEXISTANT", 100.0));

        assertEquals("Opération impossible", exception.getMessage());
        verify(compteRepository).findByNumero("INEXISTANT");
        verify(compteRepository, never()).save(any(Compte.class));
    }

    @Test
    @DisplayName("DÉBITER - Doit échouer quand le montant est négatif")
    void debiter_MontantNegatif_ShouldThrowException() {
        // Given
        when(compteRepository.findByNumero("CC001")).thenReturn(compteCourant);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> compteService.debiter("CC001", -100.0));

        assertEquals("Opération impossible", exception.getMessage());
        verify(compteRepository).findByNumero("CC001");
        verify(compteRepository, never()).save(any(Compte.class));
    }

    @Test
    @DisplayName("FIND_BY_NUMERO - Doit retourner un compte existant")
    void findByNumero_CompteExistant_ShouldReturnCompte() {
        // Given
        when(compteRepository.findByNumero("CC001")).thenReturn(compteCourant);

        // When
        Compte result = compteService.findByNumero("CC001");

        // Then
        assertNotNull(result);
        assertEquals("CC001", result.getNumero());
        assertEquals(1000.0, result.getSolde());
        verify(compteRepository).findByNumero("CC001");
    }

    @Test
    @DisplayName("FIND_BY_NUMERO - Doit retourner null pour un compte inexistant")
    void findByNumero_CompteInexistant_ShouldReturnNull() {
        // Given
        when(compteRepository.findByNumero("INEXISTANT")).thenReturn(null);

        // When
        Compte result = compteService.findByNumero("INEXISTANT");

        // Then
        assertNull(result);
        verify(compteRepository).findByNumero("INEXISTANT");
    }
}
