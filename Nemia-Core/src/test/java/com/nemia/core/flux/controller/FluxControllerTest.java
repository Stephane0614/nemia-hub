package com.nemia.core.flux.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nemia.core.flux.dto.FluxResponse;
import com.nemia.core.flux.model.FluxCategory;
import com.nemia.core.flux.model.FluxType;
import com.nemia.core.flux.model.Occurrence;
import com.nemia.core.flux.model.PaymentMode;
import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.flux.model.StatutJustificatif;
import com.nemia.core.flux.model.StatutTraitement;
import com.nemia.core.flux.service.FluxService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;



@WebMvcTest(FluxController.class)
class FluxControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private FluxService fluxService;

  @Test
  void shouldCreateFlux() throws Exception {
    FluxResponse response = buildFluxResponse(
      1L,
      LocalDate.of(2026, 4, 15),
      FluxType.RECETTE,
      "Loyer avril",
      new BigDecimal("850.00"),
      FluxCategory.LOYER,
      PaymentMode.VIREMENT,
      "Payé le 5"
    );

    when(fluxService.create(any())).thenReturn(response);

    String requestBody = """
      {
        "date": "2026-04-15",
        "type": "RECETTE",
        "libelle": "Loyer avril",
        "montant": 850.00,
        "categorie": "LOYER",
        "modePaiement": "VIREMENT",
        "commentaire": "Payé le 5"
      }
      """;

    mockMvc
      .perform(post("/api/flux").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.date").value("2026-04-15"))
      .andExpect(jsonPath("$.type").value("RECETTE"))
      .andExpect(jsonPath("$.libelle").value("Loyer avril"))
      .andExpect(jsonPath("$.montant").value(850.00))
      .andExpect(jsonPath("$.categorie").value("LOYER"))
      .andExpect(jsonPath("$.modePaiement").value("VIREMENT"))
      .andExpect(jsonPath("$.commentaire").value("Payé le 5"));

    verify(fluxService).create(any());
  }

  @Test
  void shouldReturnAllFlux() throws Exception {
    FluxResponse first = buildFluxResponse(
      1L,
      LocalDate.of(2026, 4, 15),
      FluxType.RECETTE,
      "Loyer avril",
      new BigDecimal("850.00"),
      FluxCategory.LOYER,
      PaymentMode.VIREMENT,
      "Payé le 5"
    );

    FluxResponse second = buildFluxResponse(
      2L,
      LocalDate.of(2026, 4, 16),
      FluxType.DEPENSE,
      "Internet",
      new BigDecimal("29.99"),
      FluxCategory.INTERNET,
      PaymentMode.PRELEVEMENT,
      null
    );

    when(fluxService.findAll()).thenReturn(List.of(first, second));

    mockMvc
      .perform(get("/api/flux"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].id").value(1))
      .andExpect(jsonPath("$[0].libelle").value("Loyer avril"))
      .andExpect(jsonPath("$[1].id").value(2))
      .andExpect(jsonPath("$[1].libelle").value("Internet"));

    verify(fluxService).findAll();
  }

  @Test
  void shouldReturnFluxById() throws Exception {
    Long id = 1L;

    FluxResponse response = buildFluxResponse(
      id,
      LocalDate.of(2026, 4, 15),
      FluxType.RECETTE,
      "Loyer avril",
      new BigDecimal("850.00"),
      FluxCategory.LOYER,
      PaymentMode.VIREMENT,
      "Payé le 5"
    );

    when(fluxService.findById(id)).thenReturn(response);

    mockMvc
      .perform(get("/api/flux/{id}", id))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.date").value("2026-04-15"))
      .andExpect(jsonPath("$.type").value("RECETTE"))
      .andExpect(jsonPath("$.libelle").value("Loyer avril"))
      .andExpect(jsonPath("$.montant").value(850.00))
      .andExpect(jsonPath("$.categorie").value("LOYER"))
      .andExpect(jsonPath("$.modePaiement").value("VIREMENT"))
      .andExpect(jsonPath("$.commentaire").value("Payé le 5"));

    verify(fluxService).findById(id);
  }

  @Test
  void shouldUpdateFlux() throws Exception {
    Long id = 1L;

    FluxResponse response = buildFluxResponse(
      id,
      LocalDate.of(2026, 4, 15),
      FluxType.DEPENSE,
      "Internet avril",
      new BigDecimal("29.99"),
      FluxCategory.INTERNET,
      PaymentMode.PRELEVEMENT,
      null
    );

    when(fluxService.update(eq(id), any())).thenReturn(response);

    String requestBody = """
      {
        "date": "2026-04-15",
        "type": "DEPENSE",
        "libelle": "Internet avril",
        "montant": 29.99,
        "categorie": "INTERNET",
        "modePaiement": "PRELEVEMENT",
        "commentaire": null
      }
      """;

    mockMvc
      .perform(
        put("/api/flux/{id}", id).contentType(MediaType.APPLICATION_JSON).content(requestBody)
      )
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.type").value("DEPENSE"))
      .andExpect(jsonPath("$.libelle").value("Internet avril"))
      .andExpect(jsonPath("$.montant").value(29.99))
      .andExpect(jsonPath("$.categorie").value("INTERNET"))
      .andExpect(jsonPath("$.modePaiement").value("PRELEVEMENT"));

    verify(fluxService).update(eq(id), any());
  }

  @Test
  void shouldDeleteFlux() throws Exception {
    Long id = 1L;

    doNothing().when(fluxService).delete(id);

    mockMvc.perform(delete("/api/flux/{id}", id)).andDo(print()).andExpect(status().isNoContent());

    verify(fluxService).delete(id);
  }

  private FluxResponse buildFluxResponse(
    Long id,
    LocalDate date,
    FluxType type,
    String libelle,
    BigDecimal montant,
    FluxCategory categorie,
    PaymentMode modePaiement,
    String commentaire
  ) {
    FluxResponse response = new FluxResponse();
    response.setId(id);
    response.setDate(date);
    response.setType(type);
    response.setLibelle(libelle);
    response.setMontant(montant);
    response.setCategorie(categorie);
    response.setModePaiement(modePaiement);
    response.setCommentaire(commentaire);
    response.setCreatedAt(LocalDateTime.of(2026, 4, 15, 10, 0));
    response.setUpdatedAt(LocalDateTime.of(2026, 4, 15, 10, 0));
    return response;
  }

    @Test
  void shouldReturnWarningsWhenOccurrencePonctuelOnRecurrentCategory() throws Exception {
    FluxResponse response = buildFluxResponse(
      1L,
      LocalDate.of(2026, 4, 15),
      FluxType.DEPENSE,
      "Charges copro",
      new BigDecimal("200.00"),
      FluxCategory.CHARGES_COPRO,
      PaymentMode.VIREMENT,
      null
    );
    response.setOccurrence(Occurrence.PONCTUEL);
    response.setWarnings(List.of("Occurrence PONCTUEL suspecte pour une catégorie structurellement récurrente : Charges copropriété"));

    when(fluxService.create(any())).thenReturn(response);

    String requestBody = """
      {
        "date": "2026-04-15",
        "type": "DEPENSE",
        "libelle": "Charges copro",
        "montant": 200.00,
        "categorie": "CHARGES_COPRO",
        "modePaiement": "VIREMENT",
        "occurrence": "PONCTUEL"
      }
      """;

    mockMvc
      .perform(post("/api/flux").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.warnings").isArray())
      .andExpect(jsonPath("$.warnings.length()").value(1));

    verify(fluxService).create(any());
  }
  @Test
  void shouldRejectRecetteWithDepenseCategory() throws Exception {
    when(fluxService.create(any())).thenThrow(
      new IllegalArgumentException("Incohérence bloquante : une RECETTE ne peut pas avoir la catégorie Électricité")
    );

    String requestBody = """
      {
        "date": "2026-04-15",
        "type": "RECETTE",
        "libelle": "Test invalide",
        "montant": 200.00,
        "categorie": "ELECTRICITE",
        "modePaiement": "VIREMENT"
      }
      """;

    mockMvc
      .perform(post("/api/flux").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.message").value("Incohérence bloquante : une RECETTE ne peut pas avoir la catégorie Électricité"));

    verify(fluxService).create(any());
  }
  @Test
void shouldReturnContextualFieldsInResponse() throws Exception {
  FluxResponse response = buildFluxResponse(
    1L,
    LocalDate.of(2026, 4, 15),
    FluxType.DEPENSE,
    "Internet avril",
    new BigDecimal("29.99"),
    FluxCategory.INTERNET,
    PaymentMode.PRELEVEMENT,
    null
  );
  response.setBienId(10L);
  response.setExerciceId(2L);
  response.setOccurrence(Occurrence.RECURRENT);
  response.setStatutJustificatif(StatutJustificatif.FOURNI);
  response.setQualificationPressentie(QualificationPressentie.CHARGE_COURANTE);
  response.setStatutTraitement(StatutTraitement.A_REVOIR);
  response.setWarnings(List.of());

  when(fluxService.create(any())).thenReturn(response);

  String requestBody = """
    {
      "date": "2026-04-15",
      "type": "DEPENSE",
      "libelle": "Internet avril",
      "montant": 29.99,
      "categorie": "INTERNET",
      "modePaiement": "PRELEVEMENT",
      "bienId": 10,
      "exerciceId": 2,
      "occurrence": "RECURRENT",
      "statutJustificatif": "FOURNI",
      "qualificationPressentie": "CHARGE_COURANTE",
      "statutTraitement": "BRUT"
    }
    """;

  mockMvc
    .perform(post("/api/flux").contentType(MediaType.APPLICATION_JSON).content(requestBody))
    .andDo(print())
    .andExpect(status().isCreated())
    .andExpect(jsonPath("$.bienId").value(10))
    .andExpect(jsonPath("$.exerciceId").value(2))
    .andExpect(jsonPath("$.occurrence").value("RECURRENT"))
    .andExpect(jsonPath("$.statutJustificatif").value("FOURNI"))
    .andExpect(jsonPath("$.qualificationPressentie").value("CHARGE_COURANTE"))
    .andExpect(jsonPath("$.statutTraitement").value("A_REVOIR"))
    .andExpect(jsonPath("$.warnings").isArray());

  verify(fluxService).create(any());
}
}
