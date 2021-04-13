package com.beerstock.services;

import com.beerstock.builders.BeerDTOBuilder;
import com.beerstock.dtos.BeerDTO;
import com.beerstock.entities.Beer;
import com.beerstock.exceptions.BeerAlreadyRegisteredException;
import com.beerstock.exceptions.BeerNotFoundException;
import com.beerstock.mappers.BeerMapper;
import com.beerstock.repositories.BeerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeerServiceTest {
    @Mock
    private BeerRepository beerRepository;

    private final BeerMapper beerMapper = BeerMapper.INSTANCE;

    @InjectMocks
    private BeerService beerService;

    @Test
    void whenBeerIsGivenThenItShouldBeCreated() throws BeerAlreadyRegisteredException {
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedSavedBeer = beerMapper.toModel(expectedBeerDTO);

        when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
        when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);

        BeerDTO createdBeerDTO = beerService.create(expectedBeerDTO);
        assertThat(createdBeerDTO, equalTo((expectedBeerDTO)));
    }

    @Test
    void whenAlreadyRegisteredBeerIsGivenThenAnExceptionShouldBeThrown() {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer duplicatedBeer = beerMapper.toModel(beerDTO);

        when(beerRepository.findByName(beerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));

        assertThrows(BeerAlreadyRegisteredException.class, () ->  beerService.create(beerDTO));
    }

    @Test
    void whenBeerNameIsGivenThenItShouldReturnTheBeer() throws BeerNotFoundException {
        BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);

        when(beerRepository.findByName(expectedFoundBeer.getName())).thenReturn(Optional.of(expectedFoundBeer));

        BeerDTO foundBeerDTO = beerService.findByName(expectedFoundBeer.getName());
        assertThat(foundBeerDTO, equalTo((expectedFoundBeerDTO)));
    }

    @Test
    void whenNotRegisteredBeerNameIsGivenThenAnExceptionShouldBeThrown() {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        when(beerRepository.findByName(beerDTO.getName())).thenReturn(Optional.empty());

        assertThrows(BeerNotFoundException.class, () -> beerService.findByName(beerDTO.getName()));
    }
}