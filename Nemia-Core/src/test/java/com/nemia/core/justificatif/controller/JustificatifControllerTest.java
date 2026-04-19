package com.nemia.core.justificatif.controller;

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

import com.nemia.core.common.exception.JustificatifNotFoundException;
import com.nemia.core.justificatif.dto.JustificatifResponse;
import com.nemia.core.justificatif.model.StatutDocumentaire;
import com.nemia.core.justificatif.model.TypePiece;
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
class JustificatifControllerTest {

  @Mock
  private com.nemia.core.justificatif.service.JustificatifService justificatifService;

  @InjectMocks
  private JustificatifController justificatifController;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders
      .standaloneSetup(justificatifController)
      .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
      .build();
  }

  @Test
  void shouldCreateJustificatif() throws Exception {
    JustificatifResponse response = buildJustificatifResponse(
      1L, TypePiece.FACTURE, StatutDocumentaire.FOURNI, "FAC-2026-001", "EDF", null);

    when(justificatifService.create(any())).thenReturn(response);

    String requestBody = """
      {
        "typePiece": "FACTURE",
        "statutDocumentaire": "FOURNI",
        "datePiece": "2026-04-01",
        "referencePiece": "FAC-2026-001",
        "emetteur": "EDF",
        "commentaire": "Facture electricite avril"
      }
      """;

    mockMvc.perform(post("/api/justificatifs").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.typePiece").value("FACTURE"))
      .andExpect(jsonPath("$.statutDocumentaire").value("FOURNI"))
      .andExpect(jsonPath("$.referencePiece").value("FAC-2026-001"));

    verify(justificatifService).create(any());
  }

  @Test
  void shouldCreateJustificatifWithOptionalFieldsAbsent() throws Exception {
    JustificatifResponse response = buildJustificatifResponse(
      2L, TypePiece.TICKET, StatutDocumentaire.A_FOURNIR, null, null, null);

    when(justificatifService.create(any())).thenReturn(response);

    String requestBody = """
      {
        "typePiece": "TICKET",
        "statutDocumentaire": "A_FOURNIR"
      }
      """;

    mockMvc.perform(post("/api/justificatifs").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(2))
      .andExpect(jsonPath("$.typePiece").value("TICKET"));

    verify(justificatifService).create(any());
  }

  @Test
  void shouldCreateJustificatifWithFichierAssocie() throws Exception {
    JustificatifResponse response = buildJustificatifResponse(
      3L, TypePiece.FACTURE, StatutDocumentaire.FOURNI, null, null, "path/to/file.pdf");

    when(justificatifService.create(any())).thenReturn(response);

    String requestBody = """
      {
        "typePiece": "FACTURE",
        "statutDocumentaire": "FOURNI",
        "fichierAssocie": "path/to/file.pdf"
      }
      """;

    mockMvc.perform(post("/api/justificatifs").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.fichierAssocie").value("path/to/file.pdf"));

    verify(justificatifService).create(any());
  }

  @Test
  void shouldReturnAllJustificatifs() throws Exception {
    JustificatifResponse first = buildJustificatifResponse(
      1L, TypePiece.FACTURE, StatutDocumentaire.FOURNI, "FAC-001", "EDF", null);
    JustificatifResponse second = buildJustificatifResponse(
      2L, TypePiece.TICKET, StatutDocumentaire.A_FOURNIR, null, null, null);

    when(justificatifService.findAll()).thenReturn(List.of(first, second));

    mockMvc.perform(get("/api/justificatifs"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].typePiece").value("FACTURE"))
      .andExpect(jsonPath("$[1].typePiece").value("TICKET"));

    verify(justificatifService).findAll();
  }

  @Test
  void shouldReturnJustificatifById() throws Exception {
    Long id = 1L;
    JustificatifResponse response = buildJustificatifResponse(
      id, TypePiece.FACTURE, StatutDocumentaire.FOURNI, "FAC-001", "EDF", null);

    when(justificatifService.findById(id)).thenReturn(response);

    mockMvc.perform(get("/api/justificatifs/{id}", id))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.typePiece").value("FACTURE"));

    verify(justificatifService).findById(id);
  }

  @Test
  void shouldReturn404WhenJustificatifNotFound() throws Exception {
    Long id = 99L;
    when(justificatifService.findById(id)).thenThrow(new JustificatifNotFoundException(id));

    mockMvc.perform(get("/api/justificatifs/{id}", id))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.message").value("Justificatif introuvable : 99"));

    verify(justificatifService).findById(id);
  }

  @Test
  void shouldUpdateJustificatif() throws Exception {
    Long id = 1L;
    JustificatifResponse response = buildJustificatifResponse(
      id, TypePiece.FACTURE, StatutDocumentaire.A_VERIFIER, "FAC-001-MAJ", "EDF", null);

    when(justificatifService.update(eq(id), any())).thenReturn(response);

    String requestBody = """
      {
        "typePiece": "FACTURE",
        "statutDocumentaire": "A_VERIFIER",
        "referencePiece": "FAC-001-MAJ",
        "emetteur": "EDF"
      }
      """;

    mockMvc.perform(put("/api/justificatifs/{id}", id).contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.statutDocumentaire").value("A_VERIFIER"))
      .andExpect(jsonPath("$.referencePiece").value("FAC-001-MAJ"));

    verify(justificatifService).update(eq(id), any());
  }

  @Test
  void shouldReturn404WhenUpdatingNonExistentJustificatif() throws Exception {
    Long id = 99L;
    when(justificatifService.update(eq(id), any())).thenThrow(new JustificatifNotFoundException(id));

    String requestBody = """
      {
        "typePiece": "FACTURE",
        "statutDocumentaire": "FOURNI"
      }
      """;

    mockMvc.perform(put("/api/justificatifs/{id}", id).contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isNotFound());

    verify(justificatifService).update(eq(id), any());
  }

  @Test
  void shouldDeleteJustificatif() throws Exception {
    Long id = 1L;
    doNothing().when(justificatifService).delete(id);

    mockMvc.perform(delete("/api/justificatifs/{id}", id))
      .andDo(print())
      .andExpect(status().isNoContent());

    verify(justificatifService).delete(id);
  }

  @Test
  void shouldReturn404WhenDeletingNonExistentJustificatif() throws Exception {
    Long id = 99L;
    doThrow(new JustificatifNotFoundException(id)).when(justificatifService).delete(id);

    mockMvc.perform(delete("/api/justificatifs/{id}", id))
      .andDo(print())
      .andExpect(status().isNotFound());

    verify(justificatifService).delete(id);
  }

  private JustificatifResponse buildJustificatifResponse(
    Long id, TypePiece typePiece, StatutDocumentaire statutDocumentaire,
    String referencePiece, String emetteur, String fichierAssocie) {
    JustificatifResponse response = new JustificatifResponse();
    response.setId(id);
    response.setTypePiece(typePiece);
    response.setStatutDocumentaire(statutDocumentaire);
    response.setDatePiece(LocalDate.of(2026, 4, 1));
    response.setReferencePiece(referencePiece);
    response.setEmetteur(emetteur);
    response.setCommentaire(null);
    response.setFichierAssocie(fichierAssocie);
    response.setCreatedAt(LocalDateTime.of(2026, 4, 19, 10, 0));
    response.setUpdatedAt(LocalDateTime.of(2026, 4, 19, 10, 0));
    return response;
  }
}