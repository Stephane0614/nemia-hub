package com.nemia.core.common.exception;

public class BienAlreadyExistsException extends RuntimeException {

  public BienAlreadyExistsException(String nomUsuel, String adresseSimplifiee) {
    super("Un bien existe déjà avec ce nom et cette adresse : " + nomUsuel + " — " + adresseSimplifiee);
  }
}
