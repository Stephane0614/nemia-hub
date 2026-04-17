package com.nemia.core.flux.dto.referential;

import java.util.List;

public class FluxReferentialsResponse {

  private List<ReferentialItemResponse> types;
  private List<ReferentialItemResponse> categories;
  private List<ReferentialItemResponse> paymentModes;
      private List<ReferentialItemResponse> occurrences;
    private List<ReferentialItemResponse> statutJustificatifs;
    private List<ReferentialItemResponse> qualificationPressenties;
    private List<ReferentialItemResponse> statutTraitements;


  public FluxReferentialsResponse() {}

      public FluxReferentialsResponse(
            List<ReferentialItemResponse> types,
            List<ReferentialItemResponse> categories,
            List<ReferentialItemResponse> paymentModes,
            List<ReferentialItemResponse> occurrences,
            List<ReferentialItemResponse> statutJustificatifs,
            List<ReferentialItemResponse> qualificationPressenties,
            List<ReferentialItemResponse> statutTraitements
    ) {
        this.types = types;
        this.categories = categories;
        this.paymentModes = paymentModes;
        this.occurrences = occurrences;
        this.statutJustificatifs = statutJustificatifs;
        this.qualificationPressenties = qualificationPressenties;
        this.statutTraitements = statutTraitements;
    }


  public List<ReferentialItemResponse> getTypes() {
    return types;
  }

  public void setTypes(List<ReferentialItemResponse> types) {
    this.types = types;
  }

  public List<ReferentialItemResponse> getCategories() {
    return categories;
  }

  public void setCategories(List<ReferentialItemResponse> categories) {
    this.categories = categories;
  }

  public List<ReferentialItemResponse> getPaymentModes() {
    return paymentModes;
  }

  public void setPaymentModes(List<ReferentialItemResponse> paymentModes) {
    this.paymentModes = paymentModes;
  }

      public List<ReferentialItemResponse> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(List<ReferentialItemResponse> occurrences) {
        this.occurrences = occurrences;
    }

    public List<ReferentialItemResponse> getStatutJustificatifs() {
        return statutJustificatifs;
    }

    public void setStatutJustificatifs(List<ReferentialItemResponse> statutJustificatifs) {
        this.statutJustificatifs = statutJustificatifs;
    }

    public List<ReferentialItemResponse> getQualificationPressenties() {
        return qualificationPressenties;
    }

    public void setQualificationPressenties(List<ReferentialItemResponse> qualificationPressenties) {
        this.qualificationPressenties = qualificationPressenties;
    }

    public List<ReferentialItemResponse> getStatutTraitements() {
        return statutTraitements;
    }

    public void setStatutTraitements(List<ReferentialItemResponse> statutTraitements) {
        this.statutTraitements = statutTraitements;
    }

}
