package com.beerstock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BeerType {
    LAGER("Lager"),
    MALZBIER("Malzbier"),
    WITBIER("Witbier"),
    ALE("Ale"),
    IPA("Ipa"),
    STOUT("Stout"),
    WEISS("Weiss"),
    PILSEN("Pilsen");

    private final String name;
}
