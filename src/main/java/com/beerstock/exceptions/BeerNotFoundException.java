package com.beerstock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BeerNotFoundException extends Exception {
    public BeerNotFoundException(String beerName) {
        super(String.format("We could not find a beer with name %s in our database.", beerName));
    }

    public BeerNotFoundException(Long beerId) {
        super(String.format("We could not find a beer with id %s in our database.", beerId));
    }
}
