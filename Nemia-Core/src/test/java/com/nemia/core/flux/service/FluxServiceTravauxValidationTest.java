package com.nemia.core.flux.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nemia.core.flux.dto.CreateFluxRequest;
import com.nemia.core.flux.model.FluxCategory;
import com.nemia.core.flux.model.FluxType;
import com.nemia.core.flux.model.PaymentMode;
import com.nemia.core.flux.repository.FluxRepository;
import com.nemia.core.justificatif.repository.JustificatifRepository;
import com.nemia.core.travaux.repository.TravauxRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FluxServiceTravauxValidationTest {

  @Mock
  private FluxRepository fluxRepository;

  @Mock
  private FluxValidationService fluxValidationService;

  @Mock
  private JustificatifRepository justificatifRepository;

  @Mock
  private TravauxRepository travauxRepository;

  @InjectMocks
  private FluxService fluxService;

  private CreateFluxRequest buildRequest(Long travauxId) {
    CreateFluxRequest request = new CreateFluxRequest();
    request.setDate(LocalDate.of(2026, 4, 1));
    request.setType(FluxType.DEPENSE);
    request.setLibelle("Facture travaux");
    request.setMontant(new BigDecimal("1000.00"));
    request.setCategorie(FluxCategory.TRAVAUX);
    request.setModePaiement(PaymentMode.VIREMENT);
    request.setTravauxId(travauxId);
    return request;
  }

  @Test
  void shouldThrowWhenTravauxIdIsInvalid() {
    when(travauxRepository.existsById(99L)).thenReturn(false);

    assertThatThrownBy(() -> fluxService.create(buildRequest(99L)))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Travaux introuvable");
  }

  @Test
  void shouldNotThrowWhenTravauxIdIsNull() {
    when(fluxRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(fluxValidationService.computeWarnings(any(), any(), any(), any(), any(), any())).thenReturn(List.of());

    fluxService.create(buildRequest(null));
  }

  @Test
  void shouldNotThrowWhenTravauxIdIsValid() {
    when(travauxRepository.existsById(1L)).thenReturn(true);
    when(fluxRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(fluxValidationService.computeWarnings(any(), any(), any(), any(), any(), any())).thenReturn(List.of());

    fluxService.create(buildRequest(1L));
  }
}
