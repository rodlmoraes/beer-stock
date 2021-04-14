package com.beerstock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeerNegativeQuantityException extends Exception {
    public BeerNegativeQuantityException(Long id, int quantityToDecrement) {
        super(String.format("Beer with id %s cannot be decremented by %s, it cannot have a negative quantity.", id, quantityToDecrement));
    }
}
