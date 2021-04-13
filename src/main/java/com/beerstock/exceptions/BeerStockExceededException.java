package com.beerstock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeerStockExceededException extends Exception {
    public BeerStockExceededException(Long id, int quantityToIncrement) {
        super(String.format("Beer with id %s cannot be incremented by %s, it exceeds the stock limit.", id, quantityToIncrement));
    }
}
