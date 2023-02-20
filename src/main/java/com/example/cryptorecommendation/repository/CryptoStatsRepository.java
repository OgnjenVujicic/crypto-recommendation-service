package com.example.cryptorecommendation.repository;

import com.example.cryptorecommendation.entity.CryptoStats;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Crypto Stats interface repository.
 *
 */
@Repository
public interface CryptoStatsRepository {
    void save(CryptoStats cryptoStats, LocalDateTime fromDate, LocalDateTime toDate);

    CryptoStats getCryptoStatsForRange(String cryptoSymbol, LocalDateTime fromDate, LocalDateTime toDate);
}
