package com.beerstock.controllers;

import com.beerstock.builders.BeerDTOBuilder;
import com.beerstock.builders.QuantityDTOBuilder;
import com.beerstock.dtos.BeerDTO;
import com.beerstock.dtos.QuantityDTO;
import com.beerstock.exceptions.BeerNegativeQuantityException;
import com.beerstock.exceptions.BeerNotFoundException;
import com.beerstock.exceptions.BeerStockExceededException;
import com.beerstock.services.BeerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;
import java.util.List;

import static com.beerstock.utils.JsonConverter.asJsonString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BeerControllerTest {
    private static final String BEER_API_URL = "/api/v1/beers";
    private static final String BEER_API_SUB_PATH_INCREMENT = "/increment";
    private static final String BEER_API_SUB_PATH_DECREMENT = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private BeerService beerService;

    @InjectMocks
    private BeerController beerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(beerController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPostIsCalledWithABeerThenABeerIsCreated() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        when(beerService.create(beerDTO)).thenReturn(beerDTO);
        mockMvc.perform(post(BEER_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())))
                .andExpect(jsonPath("$.max", is(beerDTO.getMax())));
    }

    @Test
    void whenPostIsCalledWithoutRequiredFieldThenAErrorIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setBrand(null);

        mockMvc.perform(post(BEER_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetIsCalledWithBeerNameThenABeerIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        when(beerService.findByName(beerDTO.getName())).thenReturn(beerDTO);
        mockMvc.perform(get(BEER_API_URL + "/" + beerDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())))
                .andExpect(jsonPath("$.max", is(beerDTO.getMax())));
    }

    @Test
    void whenGetIsCalledWithInvalidBeerNameThenAErrorIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        when(beerService.findByName(beerDTO.getName())).thenThrow(BeerNotFoundException.class);
        mockMvc.perform(get(BEER_API_URL + "/" + beerDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetIsCalledThenAListOfBeersIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        when(beerService.listAll()).thenReturn(List.of(beerDTO));
        mockMvc.perform(get(BEER_API_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(beerDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$[0].type", is(beerDTO.getType().toString())))
                .andExpect(jsonPath("$[0].quantity", is(beerDTO.getQuantity())))
                .andExpect(jsonPath("$[0].max", is(beerDTO.getMax())));
    }

    @Test
    void whenGetIsCalledThenAEmptyListIsReturned() throws Exception {
        when(beerService.listAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(BEER_API_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    void whenDeleteIsCalledWithBeerIdThenABeerIsDeleted() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        doNothing().when(beerService).deleteById(beerDTO.getId());
        mockMvc.perform(delete(BEER_API_URL + "/" + beerDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDeleteIsCalledWithInvalidBeerIdThenAErrorIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        doThrow(BeerNotFoundException.class).when(beerService).deleteById(beerDTO.getId());
        mockMvc.perform(delete(BEER_API_URL + "/" + beerDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPatchIsCalledToIncrementThenABeerQuantityIsIncrement() throws Exception {
        QuantityDTO quantityDTO = QuantityDTOBuilder.builder().build().toQuantityDTO();

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());

        when(beerService.increment(beerDTO.getId(), quantityDTO.getQuantity())).thenReturn(beerDTO);

        mockMvc.perform(patch(BEER_API_URL + "/" + beerDTO.getId() + BEER_API_SUB_PATH_INCREMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())))
                .andExpect(jsonPath("$.max", is(beerDTO.getMax())));
    }

    @Test
    void whenPatchIsCalledToIncrementWithAnInvalidBeerIdThenAErrorIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        QuantityDTO quantityDTO = QuantityDTOBuilder.builder().build().toQuantityDTO();

        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());

        when(beerService.increment(beerDTO.getId(), quantityDTO.getQuantity())).thenThrow(BeerNotFoundException.class);

        mockMvc.perform(patch(BEER_API_URL + "/" + beerDTO.getId() + BEER_API_SUB_PATH_INCREMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPatchIsCalledToIncrementWithAQuantityThatWouldExceedMaxThenAErrorIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        QuantityDTO quantityDTO = QuantityDTOBuilder.builder().quantity(beerDTO.getMax()).build().toQuantityDTO();

        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());

        when(beerService.increment(beerDTO.getId(), quantityDTO.getQuantity())).thenThrow(BeerStockExceededException.class);

        mockMvc.perform(patch(BEER_API_URL + "/" + beerDTO.getId() + BEER_API_SUB_PATH_INCREMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPatchIsCalledToDecrementThenABeerQuantityIsDecrement() throws Exception {
        QuantityDTO quantityDTO = QuantityDTOBuilder.builder().build().toQuantityDTO();

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setQuantity(beerDTO.getQuantity() - quantityDTO.getQuantity());

        when(beerService.decrement(beerDTO.getId(), quantityDTO.getQuantity())).thenReturn(beerDTO);

        mockMvc.perform(patch(BEER_API_URL + "/" + beerDTO.getId() + BEER_API_SUB_PATH_DECREMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())))
                .andExpect(jsonPath("$.max", is(beerDTO.getMax())));
    }

    @Test
    void whenPatchIsCalledToDecrementWithAnInvalidBeerIdThenAErrorIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        QuantityDTO quantityDTO = QuantityDTOBuilder.builder().build().toQuantityDTO();

        beerDTO.setQuantity(beerDTO.getQuantity() - quantityDTO.getQuantity());

        when(beerService.decrement(beerDTO.getId(), quantityDTO.getQuantity())).thenThrow(BeerNotFoundException.class);

        mockMvc.perform(patch(BEER_API_URL + "/" + beerDTO.getId() + BEER_API_SUB_PATH_DECREMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPatchIsCalledToDecrementWithAQuantityThatWouldMakeQuantityNegativeThenAErrorIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        QuantityDTO quantityDTO = QuantityDTOBuilder.builder().quantity(beerDTO.getMax()).build().toQuantityDTO();

        beerDTO.setQuantity(beerDTO.getQuantity() - quantityDTO.getQuantity());

        when(beerService.decrement(beerDTO.getId(), quantityDTO.getQuantity())).thenThrow(BeerNegativeQuantityException.class);

        mockMvc.perform(patch(BEER_API_URL + "/" + beerDTO.getId() + BEER_API_SUB_PATH_DECREMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isBadRequest());
    }
}