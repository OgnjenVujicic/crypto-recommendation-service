package com.example.cryptorecommendation.repository.impl;

import com.example.cryptorecommendation.entity.Crypto;
import com.example.cryptorecommendation.repository.CryptoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Temporary in-mem repository for crypto data
 * ConcurrentHashMap is used for multi-thread safety
 */
@Repository
public class CryptoTemporaryRepository implements CryptoRepository {

    private final Map<String, Crypto> cryptoMap = new ConcurrentHashMap<>();

    @Override
    public void save(Crypto crypto) {
        cryptoMap.put(crypto.getSymbol().toUpperCase(), crypto);
    }

    @Override
    public Crypto findBySymbol(String cryptoSymbol) {
        return cryptoMap.get(cryptoSymbol.toUpperCase());
    }

    @Override
    public List<String> getAllCryptoSymbols() {
        return cryptoMap.keySet().stream().toList();
    }
}
