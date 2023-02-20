package com.example.cryptorecommendation.repository.impl;

import com.example.cryptorecommendation.entity.CryptoStats;
import com.example.cryptorecommendation.repository.CryptoStatsRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Temporary in-mem repository for crypto stats data
 * ConcurrentHashMap is used for multi-thread safety
 */
@Repository
public class CryptoStatsTemporaryRepository implements CryptoStatsRepository {

    private final Map<String, CryptoStats> cryptoStatsMap = new ConcurrentHashMap<>();

    @Override
    public void save(CryptoStats cryptoStats, LocalDateTime fromDate, LocalDateTime toDate) {
        var key = getKeyForCryptoStats(cryptoStats.getSymbol().toUpperCase(), fromDate, toDate);
        cryptoStatsMap.put(key, cryptoStats);
    }

    @Override
    public CryptoStats getCryptoStatsForRange(String cryptoSymbol, LocalDateTime fromDate, LocalDateTime toDate) {
        var key = getKeyForCryptoStats(cryptoSymbol.toUpperCase(), fromDate, toDate);
        return cryptoStatsMap.get(key);
    }

    private String getKeyForCryptoStats(String cryptoSymbol, LocalDateTime fromDate, LocalDateTime toDate) {
        if(fromDate == null && toDate == null) {
            return "%s-allTime".formatted(cryptoSymbol);
        }
        return "%s-%s-%s".formatted(cryptoSymbol, fromDate, toDate);
    }
}
