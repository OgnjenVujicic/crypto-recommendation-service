package com.example.cryptorecommendation.rest.exceptions;

public class CryptoNotSupported extends RuntimeException {
    public CryptoNotSupported(String cryptoSymbol) {
        super("Crypto with symbol %s is not supported currently.".formatted(cryptoSymbol));
    }
}
