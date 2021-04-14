package com.beerstock.builders;

import com.beerstock.dtos.QuantityDTO;
import lombok.Builder;

@Builder
public class QuantityDTOBuilder {
    @Builder.Default
    private int quantity = 10;

    public QuantityDTO toQuantityDTO() {
        return new QuantityDTO(quantity);
    }
}
