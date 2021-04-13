package com.beerstock.controllers;

import com.beerstock.dtos.BeerDTO;
import com.beerstock.exceptions.BeerAlreadyRegisteredException;
import com.beerstock.exceptions.BeerNotFoundException;
import com.beerstock.services.BeerService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/beers")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerController {
    private final BeerService beerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeerDTO create(@RequestBody @Valid BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
        return beerService.create(beerDTO);
    }

    @GetMapping("/{name}")
    public BeerDTO findByName(@PathVariable String name) throws BeerNotFoundException {
        return beerService.findByName(name);
    }

    @GetMapping
    public List<BeerDTO> listAll() {
        return beerService.listAll();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) throws BeerNotFoundException {
        beerService.deleteById(id);
    }
}