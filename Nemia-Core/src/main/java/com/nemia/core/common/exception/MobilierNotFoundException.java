package com.nemia.core.common.exception;

public class MobilierNotFoundException extends RuntimeException {

    public MobilierNotFoundException(Long id) {
        super("Mobilier introuvable : " + id);
    }
}