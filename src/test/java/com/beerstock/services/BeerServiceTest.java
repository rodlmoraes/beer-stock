package com.beerstock.services;

import com.beerstock.builders.BeerDTOBuilder;
import com.beerstock.dtos.BeerDTO;
import com.beerstock.entities.Beer;
import com.beerstock.exceptions.BeerAlreadyRegisteredException;
import com.beerstock.exceptions.BeerNegativeQuantityException;
import com.beerstock.exceptions.BeerNotFoundException;
import com.beerstock.exceptions.BeerStockExceededException;
import com.beerstock.mappers.BeerMapper;
import com.beerstock.repositories.BeerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
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
    void whenCreateIsCalledWithBeerThenItShouldBeCreated() throws BeerAlreadyRegisteredException {
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedSavedBeer = beerMapper.toModel(expectedBeerDTO);

        when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
        when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);

        BeerDTO createdBeerDTO = beerService.create(expectedBeerDTO);
        assertThat(createdBeerDTO, equalTo((expectedBeerDTO)));
    }

    @Test
    void whenCreateIsCalledWithAlreadyRegisteredBeerThenAnExceptionShouldBeThrown() {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer duplicatedBeer = beerMapper.toModel(beerDTO);

        when(beerRepository.findByName(beerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));

        assertThrows(BeerAlreadyRegisteredException.class, () ->  beerService.create(beerDTO));
    }

    @Test
    void whenFindByNameIsCalledWithBeerNameThenItShouldReturnTheBeer() throws BeerNotFoundException {
        BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);

        when(beerRepository.findByName(expectedFoundBeerDTO.getName())).thenReturn(Optional.of(expectedFoundBeer));

        BeerDTO foundBeerDTO = beerService.findByName(expectedFoundBeer.getName());
        assertThat(foundBeerDTO, equalTo((expectedFoundBeerDTO)));
    }

    @Test
    void whenFindByNameIsCalledWithNotRegisteredBeerNameThenAnExceptionShouldBeThrown() {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        when(beerRepository.findByName(beerDTO.getName())).thenReturn(Optional.empty());

        assertThrows(BeerNotFoundException.class, () -> beerService.findByName(beerDTO.getName()));
    }

    @Test
    void whenListAllIsCalledThenItShouldReturnAListOfBeers() {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer beer = beerMapper.toModel(beerDTO);

        when(beerRepository.findAll()).thenReturn(List.of(beer));

        List<BeerDTO> beers = beerService.listAll();
        assertThat(beers, contains(beerDTO));
    }

    @Test
    void whenListAllIsCalledThenItShouldReturnAEmptyList() {
        when(beerRepository.findAll()).thenReturn(Collections.emptyList());

        List<BeerDTO> beers = beerService.listAll();
        assertThat(beers, empty());
    }

    @Test
    void whenDeleteByIdIsCalledWithBeerIdThenItShouldDeleteTheBeer() throws BeerNotFoundException {
        BeerDTO expectedDeletedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedDeletedBeer = beerMapper.toModel(expectedDeletedBeerDTO);

        when(beerRepository.findById(expectedDeletedBeerDTO.getId())).thenReturn(Optional.of(expectedDeletedBeer));
        doNothing().when(beerRepository).delete(expectedDeletedBeer);

        beerService.deleteById(expectedDeletedBeerDTO.getId());
        verify(beerRepository, times(1)).findById(expectedDeletedBeerDTO.getId());
        verify(beerRepository, times(1)).delete(expectedDeletedBeer);
    }

    @Test
    void whenDeleteByIdIsCalledWithNotRegisteredBeerIdThenAnExceptionShouldBeThrown() {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        when(beerRepository.findById(beerDTO.getId())).thenReturn(Optional.empty());

        assertThrows(BeerNotFoundException.class, () -> beerService.deleteById(beerDTO.getId()));
    }

    @Test
    void whenIncrementIsCalledThenBeerQuantityIsIncremented() throws BeerNotFoundException, BeerStockExceededException {
        BeerDTO beerToIncrementDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer beerToIncrement = beerMapper.toModel(beerToIncrementDTO);

        when(beerRepository.findById(beerToIncrementDTO.getId())).thenReturn(Optional.of(beerToIncrement));
        when(beerRepository.save(beerToIncrement)).thenReturn(beerToIncrement);
        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = beerToIncrementDTO.getQuantity() + quantityToIncrement;

        BeerDTO incrementedBeerDTO = beerService.increment(beerToIncrementDTO.getId(), quantityToIncrement);

        assertThat(expectedQuantityAfterIncrement, equalTo(incrementedBeerDTO.getQuantity()));
        assertThat(incrementedBeerDTO.getQuantity(), lessThan(beerToIncrement.getMax()));
    }

    @Test
    void whenIncrementIsCalledWithNotRegisteredBeerIdThenAnExceptionShouldBeThrown() {
        BeerDTO beerToIncrementDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        int quantityToIncrement = 10;

        when(beerRepository.findById(beerToIncrementDTO.getId())).thenReturn(Optional.empty());

        assertThrows(BeerNotFoundException.class, () -> beerService.increment(beerToIncrementDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementIsCalledWithQuantityThatWouldExceedMaxThenAnExceptionShouldBeThrown() {
        BeerDTO beerToIncrementDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer beerToIncrement = beerMapper.toModel(beerToIncrementDTO);

        when(beerRepository.findById(beerToIncrementDTO.getId())).thenReturn(Optional.of(beerToIncrement));
        int quantityToIncrement = beerToIncrementDTO.getMax();

        assertThrows(BeerStockExceededException.class, () -> beerService.increment(beerToIncrementDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenDecrementIsCalledThenBeerQuantityIsDecremented() throws BeerNotFoundException, BeerNegativeQuantityException {
        BeerDTO beerToDecrementDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer beerToDecrement = beerMapper.toModel(beerToDecrementDTO);

        when(beerRepository.findById(beerToDecrementDTO.getId())).thenReturn(Optional.of(beerToDecrement));
        when(beerRepository.save(beerToDecrement)).thenReturn(beerToDecrement);
        int quantityToDecrement = 5;
        int expectedQuantityAfterDecrement = beerToDecrementDTO.getQuantity() - quantityToDecrement;

        BeerDTO decrementedBeerDTO = beerService.decrement(beerToDecrementDTO.getId(), quantityToDecrement);

        assertThat(expectedQuantityAfterDecrement, equalTo(decrementedBeerDTO.getQuantity()));
        assertThat(decrementedBeerDTO.getQuantity(), greaterThan(0));
    }

    @Test
    void whenDecrementIsCalledWithNotRegisteredBeerIdThenAnExceptionShouldBeThrown() {
        BeerDTO beerToDecrementDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        int quantityToDecrement = 5;

        when(beerRepository.findById(beerToDecrementDTO.getId())).thenReturn(Optional.empty());

        assertThrows(BeerNotFoundException.class, () -> beerService.decrement(beerToDecrementDTO.getId(), quantityToDecrement));
    }

    @Test
    void whenDecrementIsCalledWithQuantityThatWouldMakeQuantityNegativeThenAnExceptionShouldBeThrown() {
        BeerDTO beerToDecrementDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer beerToDecrement = beerMapper.toModel(beerToDecrementDTO);

        when(beerRepository.findById(beerToDecrementDTO.getId())).thenReturn(Optional.of(beerToDecrement));
        int quantityToDecrement = beerToDecrementDTO.getMax();

        assertThrows(BeerNegativeQuantityException.class, () -> beerService.decrement(beerToDecrementDTO.getId(), quantityToDecrement));
    }
}