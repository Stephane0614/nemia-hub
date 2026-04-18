package com.nemia.core.flux.service;

import com.nemia.core.flux.dto.HomeSyntheseResponse;
import com.nemia.core.flux.model.Flux;
import com.nemia.core.flux.repository.FluxRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

@Service
public class HomeSyntheseService {

    private final FluxRepository fluxRepository;
    private final FluxValidationService fluxValidationService;

    public HomeSyntheseService(FluxRepository fluxRepository,
                                FluxValidationService fluxValidationService) {
        this.fluxRepository = fluxRepository;
        this.fluxValidationService = fluxValidationService;
    }

    public HomeSyntheseResponse getSynthese(String mois, Long bienId) {
        YearMonth yearMonth = parseMois(mois);
        LocalDate dateDebut = yearMonth.atDay(1);
        LocalDate dateFin = yearMonth.atEndOfMonth();

        return new HomeSyntheseResponse(
                buildPeriode(yearMonth, dateDebut, dateFin),
                buildMetriques(dateDebut, dateFin),
                buildAlertes(dateDebut, dateFin),
                buildRepartitionDepenses(dateDebut, dateFin),
                buildRecurrenceDepenses(dateDebut, dateFin),
                buildDernieresOperations(dateDebut, dateFin)
        );
    }

    private HomeSyntheseResponse.Periode buildPeriode(YearMonth yearMonth,
                                                       LocalDate dateDebut,
                                                       LocalDate dateFin) {
        String label = yearMonth.format(
                DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH));
        String labelFormate = Character.toUpperCase(label.charAt(0)) + label.substring(1);
        String moisCode = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        return new HomeSyntheseResponse.Periode(
                labelFormate,
                dateDebut.toString(),
                dateFin.toString(),
                moisCode
        );
    }

    private HomeSyntheseResponse.Metriques buildMetriques(LocalDate debut, LocalDate fin) {
        BigDecimal totalRecettes = fluxRepository.sumRecettes(debut, fin);
        BigDecimal totalDepenses = fluxRepository.sumDepenses(debut, fin);
        BigDecimal solde = totalRecettes.subtract(totalDepenses);
        long nombreOperations = fluxRepository.countOperations(debut, fin);

        return new HomeSyntheseResponse.Metriques(
                totalRecettes, totalDepenses, solde, nombreOperations);
    }

    private HomeSyntheseResponse.Alertes buildAlertes(LocalDate debut, LocalDate fin) {
        long fluxSansJustificatif = fluxRepository.countSansJustificatif(debut, fin);
        BigDecimal montantSansJustificatif = fluxRepository.sumMontantSansJustificatif(debut, fin);
        long fluxAArbitrer = fluxRepository.countAArbitrer(debut, fin);
        BigDecimal montantAArbitrer = fluxRepository.sumMontantAArbitrer(debut, fin);
        long fluxARevoir = fluxRepository.countARevoir(debut, fin);

        return new HomeSyntheseResponse.Alertes(
                fluxSansJustificatif, montantSansJustificatif,
                fluxAArbitrer, montantAArbitrer,
                fluxARevoir);
    }

    private List<HomeSyntheseResponse.RepartitionDepense> buildRepartitionDepenses(
            LocalDate debut, LocalDate fin) {

        List<Object[]> rows = fluxRepository.sumDepensesParCategorie(debut, fin);

        return rows.stream()
                .map(row -> {
                    String categorie = row[0].toString();
                    BigDecimal montant = (BigDecimal) row[1];
                    String label = formatCategorie(categorie);
                    return new HomeSyntheseResponse.RepartitionDepense(categorie, label, montant);
                })
                .toList();
    }

    private HomeSyntheseResponse.RecurrenceDepenses buildRecurrenceDepenses(
            LocalDate debut, LocalDate fin) {

        BigDecimal montantRecurrent = fluxRepository.sumDepensesRecurrentes(debut, fin);
        BigDecimal montantPonctuel = fluxRepository.sumDepensesPonctuelles(debut, fin);

        return new HomeSyntheseResponse.RecurrenceDepenses(montantRecurrent, montantPonctuel);
    }

    private List<HomeSyntheseResponse.DerniereOperation> buildDernieresOperations(
            LocalDate debut, LocalDate fin) {

        List<Flux> fluxes = fluxRepository.findDernieresOperations(
                debut, fin, PageRequest.of(0, 5));

        return fluxes.stream()
                .map(flux -> {
                    List<String> warnings = fluxValidationService.validate(
                            flux.getType(),
                            flux.getCategorie(),
                            flux.getQualificationPressentie(),
                            flux.getStatutJustificatif(),
                            flux.getOccurrence(),
                            flux.getStatutTraitement()
                    );
                    return new HomeSyntheseResponse.DerniereOperation(
                            flux.getId(),
                            flux.getDate().toString(),
                            flux.getLibelle(),
                            flux.getType().name(),
                            flux.getMontant(),
                            flux.getCategorie().name(),
                            flux.getStatutJustificatif().name(),
                            flux.getQualificationPressentie().name(),
                            warnings
                    );
                })
                .toList();
    }

    private YearMonth parseMois(String mois) {
        if (mois == null || mois.isBlank()) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(mois, DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Format de mois invalide : '" + mois + "'. Format attendu : yyyy-MM");
        }
    }

    private String formatCategorie(String categorie) {
        return categorie
                .replace("_", " ")
                .toLowerCase();
    }
}