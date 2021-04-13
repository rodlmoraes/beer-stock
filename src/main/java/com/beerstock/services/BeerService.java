package com.beerstock.services;

import com.beerstock.dtos.BeerDTO;
import com.beerstock.entities.Beer;
import com.beerstock.exceptions.BeerAlreadyRegisteredException;
import com.beerstock.exceptions.BeerNotFoundException;
import com.beerstock.mappers.BeerMapper;
import com.beerstock.repositories.BeerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerService {
    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper = BeerMapper.INSTANCE;

    public BeerDTO create(BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(beerDTO.getName());
        Beer beer = beerMapper.toModel(beerDTO);
        Beer savedBeer = beerRepository.save(beer);
        return beerMapper.toDTO(savedBeer);
    }

    public BeerDTO findByName(String name) throws BeerNotFoundException {
        return beerRepository.findByName(name)
                .map(beerMapper::toDTO)
                .orElseThrow(() -> new BeerNotFoundException(name));
    }

    public List<BeerDTO> listAll() {
        return beerRepository.findAll().stream().map(beerMapper::toDTO).collect(Collectors.toList());
    }

    public void deleteById(Long id) throws BeerNotFoundException {
        Beer beer = verifyIfExists(id);
        beerRepository.delete(beer);
    }

    private void verifyIfIsAlreadyRegistered(String name) throws BeerAlreadyRegisteredException {
        Optional<Beer> beer = beerRepository.findByName(name);
        if (beer.isPresent()) throw new BeerAlreadyRegisteredException(name);
    }

    private Beer verifyIfExists(Long id) throws BeerNotFoundException {
        return beerRepository.findById(id).orElseThrow(() -> new BeerNotFoundException(id));
    }
}
