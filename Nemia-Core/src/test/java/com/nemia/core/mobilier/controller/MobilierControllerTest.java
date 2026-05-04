package com.nemia.core.mobilier.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nemia.core.common.exception.MobilierNotFoundException;
import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.mobilier.dto.MobilierResponse;
import com.nemia.core.mobilier.model.CategorieMobilier;
import com.nemia.core.mobilier.model.EtatUsage;
import com.nemia.core.mobilier.model.StatutMobilier;
import com.nemia.core.mobilier.service.MobilierService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class MobilierControllerTest {

  @Mock
  private MobilierService mobilierService;

  @InjectMocks
  private MobilierController mobilierController;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(mobilierController)
      .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
      .build();
  }

  // ── Helpers ──

  private MobilierResponse buildResponse(Long id) {
    MobilierResponse r = new MobilierResponse();
    r.setId(id);
    r.setDesignation("Canapé convertible");
    r.setBienId(1L);
    r.setDateAcquisition(LocalDate.of(2026, 3, 15));
    r.setMontant(new BigDecimal("850.00"));
    r.setQuantite(1);
    r.setCategorieMobilier(CategorieMobilier.MEUBLE);
    r.setEtatUsage(EtatUsage.NEUF);
    r.setQualificationPressentie(QualificationPressentie.IMMOBILISATION);
    r.setStatutMobilier(StatutMobilier.BRUT);
    r.setCommentaire("Salon principal");
    r.setCreatedAt(LocalDateTime.of(2026, 4, 1, 10, 0));
    r.setUpdatedAt(LocalDateTime.of(2026, 4, 1, 10, 0));
    return r;
  }

  private String requestBodyComplet() {
    return """
    {
      "designation": "Canapé convertible",
      "bienId": 1,
      "dateAcquisition": "2026-03-15",
      "montant": 850.00,
      "quantite": 1,
      "categorieMobilier": "MEUBLE",
      "etatUsage": "NEUF",
      "qualificationPressentie": "IMMOBILISATION",
      "statutMobilier": "BRUT",
      "commentaire": "Salon principal"
    }
    """;
  }

  private String requestBodyMinimal() {
    return """
    {
      "designation": "Table de nuit",
      "bienId": 1,
      "montant": 120.00,
      "categorieMobilier": "MEUBLE",
      "qualificationPressentie": "CHARGE_COURANTE",
      "statutMobilier": "BRUT"
    }
    """;
  }

  // ── CRUD ──

  @Test
  void shouldCreateMobilierComplet() throws Exception {
    when(mobilierService.create(any())).thenReturn(buildResponse(1L));

    mockMvc
      .perform(post("/api/mobilier").contentType(MediaType.APPLICATION_JSON).content(requestBodyComplet()))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.designation").value("Canapé convertible"))
      .andExpect(jsonPath("$.bienId").value(1))
      .andExpect(jsonPath("$.montant").value(850.00))
      .andExpect(jsonPath("$.categorieMobilier").value("MEUBLE"))
      .andExpect(jsonPath("$.qualificationPressentie").value("IMMOBILISATION"))
      .andExpect(jsonPath("$.statutMobilier").value("BRUT"));

    verify(mobilierService).create(any());
  }

  @Test
  void shouldCreateMobilierSansChampsOptionnels() throws Exception {
    MobilierResponse r = buildResponse(2L);
    r.setDateAcquisition(null);
    r.setEtatUsage(null);
    r.setCommentaire(null);
    r.setJustificatifId(null);
    when(mobilierService.create(any())).thenReturn(r);

    mockMvc
      .perform(post("/api/mobilier").contentType(MediaType.APPLICATION_JSON).content(requestBodyMinimal()))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(2));

    verify(mobilierService).create(any());
  }

  @Test
  void shouldReturnAllMobilier() throws Exception {
    when(mobilierService.findAll(null)).thenReturn(List.of(buildResponse(1L), buildResponse(2L)));

    mockMvc
      .perform(get("/api/mobilier"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2));

    verify(mobilierService).findAll(null);
  }

  @Test
  void shouldReturnMobilierFilteredByBienId() throws Exception {
    when(mobilierService.findAll(1L)).thenReturn(List.of(buildResponse(1L)));

    mockMvc
      .perform(get("/api/mobilier").param("bienId", "1"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1));

    verify(mobilierService).findAll(1L);
  }

  @Test
  void shouldReturnEmptyListWhenNoBienId() throws Exception {
    when(mobilierService.findAll(99L)).thenReturn(List.of());

    mockMvc
      .perform(get("/api/mobilier").param("bienId", "99"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void shouldReturnMobilierById() throws Exception {
    when(mobilierService.findById(1L)).thenReturn(buildResponse(1L));

    mockMvc
      .perform(get("/api/mobilier/{id}", 1L))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.designation").value("Canapé convertible"));

    verify(mobilierService).findById(1L);
  }

  @Test
  void shouldReturn404WhenMobilierNotFound() throws Exception {
    when(mobilierService.findById(99L)).thenThrow(new MobilierNotFoundException(99L));

    mockMvc
      .perform(get("/api/mobilier/{id}", 99L))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.message").value("Mobilier introuvable : 99"));

    verify(mobilierService).findById(99L);
  }

  @Test
  void shouldUpdateMobilier() throws Exception {
    MobilierResponse updated = buildResponse(1L);
    updated.setDesignation("Lit double");
    updated.setMontant(new BigDecimal("1200.00"));
    when(mobilierService.update(eq(1L), any())).thenReturn(updated);

    String body = """
      {
        "designation": "Lit double",
        "bienId": 1,
        "montant": 1200.00,
        "categorieMobilier": "LITERIE",
        "qualificationPressentie": "IMMOBILISATION",
        "statutMobilier": "QUALIFIE"
      }
      """;

    mockMvc
      .perform(put("/api/mobilier/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.designation").value("Lit double"))
      .andExpect(jsonPath("$.montant").value(1200.00));

    verify(mobilierService).update(eq(1L), any());
  }

  @Test
  void shouldReturn404WhenUpdatingNonExistentMobilier() throws Exception {
    when(mobilierService.update(eq(99L), any())).thenThrow(new MobilierNotFoundException(99L));

    mockMvc
      .perform(put("/api/mobilier/{id}", 99L).contentType(MediaType.APPLICATION_JSON).content(requestBodyComplet()))
      .andDo(print())
      .andExpect(status().isNotFound());
  }

  @Test
  void shouldDeleteMobilier() throws Exception {
    doNothing().when(mobilierService).delete(1L);

    mockMvc.perform(delete("/api/mobilier/{id}", 1L)).andDo(print()).andExpect(status().isNoContent());

    verify(mobilierService).delete(1L);
  }

  @Test
  void shouldReturn404WhenDeletingNonExistentMobilier() throws Exception {
    doThrow(new MobilierNotFoundException(99L)).when(mobilierService).delete(99L);

    mockMvc.perform(delete("/api/mobilier/{id}", 99L)).andDo(print()).andExpect(status().isNotFound());
  }

  // ── Validations ──

  @Test
  void shouldReturn400WhenDesignationIsBlank() throws Exception {
    String body = """
      {
        "designation": "",
        "bienId": 1,
        "montant": 500.00,
        "categorieMobilier": "MEUBLE",
        "qualificationPressentie": "IMMOBILISATION",
        "statutMobilier": "BRUT"
      }
      """;

    mockMvc
      .perform(post("/api/mobilier").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.designation").exists());

    verifyNoInteractions(mobilierService);
  }

  @Test
  void shouldReturn400WhenBienIdIsNull() throws Exception {
    String body = """
      {
        "designation": "Armoire",
        "montant": 500.00,
        "categorieMobilier": "MEUBLE",
        "qualificationPressentie": "IMMOBILISATION",
        "statutMobilier": "BRUT"
      }
      """;

    mockMvc
      .perform(post("/api/mobilier").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.bienId").exists());

    verifyNoInteractions(mobilierService);
  }

  @Test
  void shouldReturn400WhenMontantIsNegative() throws Exception {
    String body = """
      {
        "designation": "Armoire",
        "bienId": 1,
        "montant": -100.00,
        "categorieMobilier": "MEUBLE",
        "qualificationPressentie": "IMMOBILISATION",
        "statutMobilier": "BRUT"
      }
      """;

    mockMvc
      .perform(post("/api/mobilier").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.montant").exists());

    verifyNoInteractions(mobilierService);
  }

  @Test
  void shouldReturn400WhenQuantiteIsNegative() throws Exception {
    String body = """
      {
        "designation": "Chaise",
        "bienId": 1,
        "montant": 80.00,
        "quantite": -2,
        "categorieMobilier": "MEUBLE",
        "qualificationPressentie": "CHARGE_COURANTE",
        "statutMobilier": "BRUT"
      }
      """;

    mockMvc
      .perform(post("/api/mobilier").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.quantite").exists());

    verifyNoInteractions(mobilierService);
  }

  @Test
  void shouldReturn400WhenCategorieIsNull() throws Exception {
    String body = """
      {
        "designation": "Armoire",
        "bienId": 1,
        "montant": 500.00,
        "qualificationPressentie": "IMMOBILISATION",
        "statutMobilier": "BRUT"
      }
      """;

    mockMvc
      .perform(post("/api/mobilier").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.categorieMobilier").exists());

    verifyNoInteractions(mobilierService);
  }

  @Test
  void shouldReturn400WhenQualificationIsNull() throws Exception {
    String body = """
      {
        "designation": "Armoire",
        "bienId": 1,
        "montant": 500.00,
        "categorieMobilier": "MEUBLE",
        "statutMobilier": "BRUT"
      }
      """;

    mockMvc
      .perform(post("/api/mobilier").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.qualificationPressentie").exists());

    verifyNoInteractions(mobilierService);
  }

  @Test
  void shouldReturn400WhenStatutIsNull() throws Exception {
    String body = """
      {
        "designation": "Armoire",
        "bienId": 1,
        "montant": 500.00,
        "categorieMobilier": "MEUBLE",
        "qualificationPressentie": "IMMOBILISATION"
      }
      """;

    mockMvc
      .perform(post("/api/mobilier").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.statutMobilier").exists());

    verifyNoInteractions(mobilierService);
  }
}
