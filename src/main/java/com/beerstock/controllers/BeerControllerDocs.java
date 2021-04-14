package com.beerstock.controllers;

import com.beerstock.dtos.BeerDTO;
import com.beerstock.dtos.QuantityDTO;
import com.beerstock.exceptions.BeerAlreadyRegisteredException;
import com.beerstock.exceptions.BeerNegativeQuantityException;
import com.beerstock.exceptions.BeerNotFoundException;
import com.beerstock.exceptions.BeerStockExceededException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Api("Manages beer stock")
public interface BeerControllerDocs {

    @ApiOperation(value = "Beer creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success beer creation"),
            @ApiResponse(code = 400, message = "Missing required fields or wrong field range value.")
    })
    BeerDTO create(BeerDTO beerDTO) throws BeerAlreadyRegisteredException;

    @ApiOperation(value = "Returns beer found by a given name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success beer found in the system"),
            @ApiResponse(code = 404, message = "Beer with given name not found.")
    })
    BeerDTO findByName(@PathVariable String name) throws BeerNotFoundException;

    @ApiOperation(value = "Returns a list of all beers registered in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all beers registered in the system"),
    })
    List<BeerDTO> listAll();

    @ApiOperation(value = "Delete a beer found by a given valid Id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success beer deleted in the system"),
            @ApiResponse(code = 404, message = "Beer with given id not found.")
    })
    void deleteById(@PathVariable Long id) throws BeerNotFoundException;

    @ApiOperation(value = "Increment a beer quantity by the given amount")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success beer quantity incremented"),
            @ApiResponse(code = 404, message = "Beer with given id not found."),
            @ApiResponse(code = 400, message = "Quantity to increment would exceed beer maximum amount")
    })
    BeerDTO increment(@PathVariable Long id, QuantityDTO quantityDTO) throws BeerNotFoundException, BeerStockExceededException;

    @ApiOperation(value = "Decrement a beer quantity by the given amount")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success beer quantity decremented"),
            @ApiResponse(code = 404, message = "Beer with given id not found."),
            @ApiResponse(code = 400, message = "Quantity to decrement would make beer quantity negative")
    })
    BeerDTO decrement(@PathVariable Long id, QuantityDTO quantityDTO) throws BeerNotFoundException, BeerNegativeQuantityException;
}
