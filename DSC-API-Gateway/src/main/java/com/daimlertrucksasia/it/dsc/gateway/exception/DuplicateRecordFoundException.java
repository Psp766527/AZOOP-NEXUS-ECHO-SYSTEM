package com.daimlertrucksasia.it.dsc.gateway.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DuplicateRecordFoundException extends DataIntegrityViolationException{
    public DuplicateRecordFoundException(String message) {
        super(message);
    }
}
