package com.azoopindia.it.asi.traffic.manager.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DuplicateRecordFoundException extends DataIntegrityViolationException{
    public DuplicateRecordFoundException(String message) {
        super(message);
    }
}
