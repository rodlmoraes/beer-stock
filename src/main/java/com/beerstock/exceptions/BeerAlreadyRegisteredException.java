package com.beerstock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeerAlreadyRegisteredException extends Exception {
    public BeerAlreadyRegisteredException(String beerName) {
        super(String.format("Beer with name %s has already been registered in our database.", beerName));
    }
}
