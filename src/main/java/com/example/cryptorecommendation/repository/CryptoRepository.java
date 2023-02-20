package com.example.cryptorecommendation.repository;

import com.example.cryptorecommendation.entity.Crypto;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Here is used interface for crypto repository.
 * This will make possible to have multiple implementations of persistence layer
 * and in future be able to easily replace existing impl with production suitable implementations(redis,sql,nosql...)
 */
@Repository
public interface CryptoRepository {

    void save(Crypto crypto);

    Crypto findBySymbol(String cryptoSymbol);

    List<String> getAllCryptoSymbols();
}
