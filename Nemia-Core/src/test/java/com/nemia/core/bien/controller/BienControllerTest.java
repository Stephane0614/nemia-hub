package com.nemia.core.bien.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nemia.core.bien.dto.BienResponse;
import com.nemia.core.bien.model.RegimeVise;
import com.nemia.core.bien.model.StatutActiviteBien;
import com.nemia.core.bien.model.TypeLocation;
import com.nemia.core.common.exception.BienAlreadyExistsException;
import com.nemia.core.common.exception.BienNotFoundException;
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
class BienControllerTest {

  @Mock
  private com.nemia.core.bien.service.BienService bienService;

  @InjectMocks
  private BienController bienController;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders
      .standaloneSetup(bienController)
      .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
      .build();
  }

  @Test
  void shouldCreateBien() throws Exception {
    BienResponse response = buildBienResponse(1L, "Studio Bordeaux", "12 rue des Capucins, 33000 Bordeaux",
      StatutActiviteBien.ACTIF, TypeLocation.LMNP_LONGUE_DUREE, RegimeVise.REEL);

    when(bienService.create(any())).thenReturn(response);

    String requestBody = """
      {
        "nomUsuel": "Studio Bordeaux",
        "adresseSimplifiee": "12 rue des Capucins, 33000 Bordeaux",
        "statutActivite": "ACTIF",
        "typeLocation": "LMNP_LONGUE_DUREE",
        "dateMiseEnLocation": "2023-09-01",
        "regimeVise": "REEL",
        "commentaire": "Premier bien"
      }
      """;

    mockMvc.perform(post("/api/biens").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.nomUsuel").value("Studio Bordeaux"))
      .andExpect(jsonPath("$.statutActivite").value("ACTIF"));

    verify(bienService).create(any());
  }

  @Test
  void shouldCreateBienWithOptionalFieldsAbsent() throws Exception {
    BienResponse response = buildBienResponse(2L, "Studio Paris", "45 rue de la Roquette, 75011 Paris",
      StatutActiviteBien.EN_PREPARATION, null, null);

    when(bienService.create(any())).thenReturn(response);

    String requestBody = """
      {
        "nomUsuel": "Studio Paris",
        "adresseSimplifiee": "45 rue de la Roquette, 75011 Paris",
        "statutActivite": "EN_PREPARATION"
      }
      """;

    mockMvc.perform(post("/api/biens").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(2))
      .andExpect(jsonPath("$.nomUsuel").value("Studio Paris"));

    verify(bienService).create(any());
  }

  @Test
  void shouldReturnAllBiens() throws Exception {
    BienResponse first = buildBienResponse(1L, "Studio Bordeaux", "12 rue des Capucins, 33000 Bordeaux",
      StatutActiviteBien.ACTIF, TypeLocation.LMNP_LONGUE_DUREE, RegimeVise.REEL);
    BienResponse second = buildBienResponse(2L, "T2 Lyon", "8 rue Marietton, 69009 Lyon",
      StatutActiviteBien.ACTIF, TypeLocation.LMNP_LONGUE_DUREE, RegimeVise.REEL);

    when(bienService.findAll()).thenReturn(List.of(first, second));

    mockMvc.perform(get("/api/biens"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].nomUsuel").value("Studio Bordeaux"))
      .andExpect(jsonPath("$[1].nomUsuel").value("T2 Lyon"));

    verify(bienService).findAll();
  }

  @Test
  void shouldReturnBienById() throws Exception {
    Long id = 1L;
    BienResponse response = buildBienResponse(id, "Studio Bordeaux", "12 rue des Capucins, 33000 Bordeaux",
      StatutActiviteBien.ACTIF, TypeLocation.LMNP_LONGUE_DUREE, RegimeVise.REEL);

    when(bienService.findById(id)).thenReturn(response);

    mockMvc.perform(get("/api/biens/{id}", id))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.nomUsuel").value("Studio Bordeaux"));

    verify(bienService).findById(id);
  }

  @Test
  void shouldReturn404WhenBienNotFound() throws Exception {
    Long id = 99L;
    when(bienService.findById(id)).thenThrow(new BienNotFoundException(id));

    mockMvc.perform(get("/api/biens/{id}", id))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.message").value("Bien introuvable : 99"));

    verify(bienService).findById(id);
  }

  @Test
  void shouldUpdateBien() throws Exception {
    Long id = 1L;
    BienResponse response = buildBienResponse(id, "Studio Bordeaux MAJ", "12 rue des Capucins, 33000 Bordeaux",
      StatutActiviteBien.ACTIF, TypeLocation.LMNP_LONGUE_DUREE, RegimeVise.REEL);

    when(bienService.update(eq(id), any())).thenReturn(response);

    String requestBody = """
      {
        "nomUsuel": "Studio Bordeaux MAJ",
        "adresseSimplifiee": "12 rue des Capucins, 33000 Bordeaux",
        "statutActivite": "ACTIF",
        "typeLocation": "LMNP_LONGUE_DUREE",
        "regimeVise": "REEL"
      }
      """;

    mockMvc.perform(put("/api/biens/{id}", id).contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.nomUsuel").value("Studio Bordeaux MAJ"));

    verify(bienService).update(eq(id), any());
  }

  @Test
  void shouldReturn404WhenUpdatingNonExistentBien() throws Exception {
    Long id = 99L;
    when(bienService.update(eq(id), any())).thenThrow(new BienNotFoundException(id));

    String requestBody = """
      {
        "nomUsuel": "Studio inexistant",
        "adresseSimplifiee": "Adresse quelconque",
        "statutActivite": "ACTIF"
      }
      """;

    mockMvc.perform(put("/api/biens/{id}", id).contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isNotFound());

    verify(bienService).update(eq(id), any());
  }

  @Test
  void shouldDeleteBien() throws Exception {
    Long id = 1L;
    doNothing().when(bienService).delete(id);

    mockMvc.perform(delete("/api/biens/{id}", id))
      .andDo(print())
      .andExpect(status().isNoContent());

    verify(bienService).delete(id);
  }

  @Test
  void shouldReturn404WhenDeletingNonExistentBien() throws Exception {
    Long id = 99L;
    doThrow(new BienNotFoundException(id)).when(bienService).delete(id);

    mockMvc.perform(delete("/api/biens/{id}", id))
      .andDo(print())
      .andExpect(status().isNotFound());

    verify(bienService).delete(id);
  }

  private BienResponse buildBienResponse(Long id, String nomUsuel, String adresseSimplifiee,
    StatutActiviteBien statutActivite, TypeLocation typeLocation, RegimeVise regimeVise) {
    BienResponse response = new BienResponse();
    response.setId(id);
    response.setNomUsuel(nomUsuel);
    response.setAdresseSimplifiee(adresseSimplifiee);
    response.setStatutActivite(statutActivite);
    response.setTypeLocation(typeLocation);
    response.setDateMiseEnLocation(LocalDate.of(2023, 9, 1));
    response.setRegimeVise(regimeVise);
    response.setCommentaire(null);
    response.setCreatedAt(LocalDateTime.of(2026, 4, 18, 10, 0));
    response.setUpdatedAt(LocalDateTime.of(2026, 4, 18, 10, 0));
    return response;
  }

  @Test
void shouldReturn409WhenCreatingDuplicateBien() throws Exception {
  when(bienService.create(any())).thenThrow(
    new BienAlreadyExistsException("Studio Bordeaux", "12 rue des Capucins, 33000 Bordeaux")
  );

  String requestBody = """
    {
      "nomUsuel": "Studio Bordeaux",
      "adresseSimplifiee": "12 rue des Capucins, 33000 Bordeaux",
      "statutActivite": "ACTIF"
    }
    """;

  mockMvc.perform(post("/api/biens").contentType(MediaType.APPLICATION_JSON).content(requestBody))
    .andDo(print())
    .andExpect(status().isConflict())
    .andExpect(jsonPath("$.message").value("Un bien existe déjà avec ce nom et cette adresse : Studio Bordeaux — 12 rue des Capucins, 33000 Bordeaux"));

  verify(bienService).create(any());
}

@Test
void shouldReturn409WhenUpdatingToExistingCombination() throws Exception {
  Long id = 1L;
  when(bienService.update(eq(id), any())).thenThrow(
    new BienAlreadyExistsException("Studio Bordeaux", "12 rue des Capucins, 33000 Bordeaux")
  );

  String requestBody = """
    {
      "nomUsuel": "Studio Bordeaux",
      "adresseSimplifiee": "12 rue des Capucins, 33000 Bordeaux",
      "statutActivite": "ACTIF"
    }
    """;

  mockMvc.perform(put("/api/biens/{id}", id).contentType(MediaType.APPLICATION_JSON).content(requestBody))
    .andDo(print())
    .andExpect(status().isConflict());

  verify(bienService).update(eq(id), any());
}

@Test
void shouldReturn200WhenUpdatingBienWithSameNomAndAdresse() throws Exception {
  Long id = 1L;
  BienResponse response = buildBienResponse(id, "Studio Bordeaux", "12 rue des Capucins, 33000 Bordeaux",
    StatutActiviteBien.ACTIF, TypeLocation.LMNP_LONGUE_DUREE, RegimeVise.REEL);

  when(bienService.update(eq(id), any())).thenReturn(response);

  String requestBody = """
    {
      "nomUsuel": "Studio Bordeaux",
      "adresseSimplifiee": "12 rue des Capucins, 33000 Bordeaux",
      "statutActivite": "ACTIF"
    }
    """;

  mockMvc.perform(put("/api/biens/{id}", id).contentType(MediaType.APPLICATION_JSON).content(requestBody))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.nomUsuel").value("Studio Bordeaux"));

  verify(bienService).update(eq(id), any());
}
}