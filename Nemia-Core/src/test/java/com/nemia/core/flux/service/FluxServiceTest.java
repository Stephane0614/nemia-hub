package com.nemia.core.flux.service;

import com.nemia.core.common.exception.FluxNotFoundException;
import com.nemia.core.flux.dto.CreateFluxRequest;
import com.nemia.core.flux.dto.FluxResponse;
import com.nemia.core.flux.dto.UpdateFluxRequest;
import com.nemia.core.flux.model.Flux;
import com.nemia.core.flux.model.FluxCategory;
import com.nemia.core.flux.model.FluxType;
import com.nemia.core.flux.model.PaymentMode;
import com.nemia.core.flux.repository.FluxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

@ExtendWith(MockitoExtension.class)
class FluxServiceTest {

    @Mock
    private FluxRepository fluxRepository;

    @InjectMocks
    private FluxService fluxService;

    @Test
    void shouldNormalizeCreateRequestBeforeSaving() {
        CreateFluxRequest request = new CreateFluxRequest();
        request.setDate(LocalDate.of(2026, 4, 15));
        request.setType(FluxType.RECETTE);
        request.setLibelle("  Loyer avril  ");
        request.setMontant(new BigDecimal("850.00"));
        request.setCategorie(FluxCategory.LOYER);
        request.setModePaiement(PaymentMode.VIREMENT);
        request.setCommentaire("   ");

        when(fluxRepository.save(any(Flux.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FluxResponse response = fluxService.create(request);

        ArgumentCaptor<Flux> fluxCaptor = ArgumentCaptor.forClass(Flux.class);
        verify(fluxRepository).save(fluxCaptor.capture());

        Flux savedFlux = fluxCaptor.getValue();

        assertEquals("Loyer avril", savedFlux.getLibelle());
        assertNull(savedFlux.getCommentaire());

        assertEquals("Loyer avril", response.getLibelle());
        assertNull(response.getCommentaire());
    }

    @Test
    void shouldThrowWhenFluxIsNotFound() {
        Long id = 999L;

        when(fluxRepository.findById(id)).thenReturn(java.util.Optional.empty());

        FluxNotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(
                FluxNotFoundException.class,
                () -> fluxService.findById(id));

        assertEquals("Flux introuvable avec l'id : 999", exception.getMessage());
    }

    @Test
    void shouldThrowWhenUpdatingUnknownFlux() {
        Long id = 999L;

        UpdateFluxRequest request = new UpdateFluxRequest();
        request.setDate(LocalDate.of(2026, 4, 15));
        request.setType(FluxType.DEPENSE);
        request.setLibelle("Internet");
        request.setMontant(new BigDecimal("29.99"));
        request.setCategorie(FluxCategory.INTERNET);
        request.setModePaiement(PaymentMode.PRELEVEMENT);
        request.setCommentaire("Abonnement fibre");

        when(fluxRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(FluxNotFoundException.class, () -> fluxService.update(id, request));

        verify(fluxRepository, never()).save(any(Flux.class));
    }

}