package com.example.cryptorecommendation.service;

import com.example.cryptorecommendation.dto.CryptoNormalizedRangeDto;
import com.example.cryptorecommendation.dto.CryptoNormalizedRangeListDto;
import com.example.cryptorecommendation.dto.CryptoStatsDto;
import com.example.cryptorecommendation.entity.Crypto;
import com.example.cryptorecommendation.entity.CryptoPrice;
import com.example.cryptorecommendation.entity.CryptoStats;
import com.example.cryptorecommendation.mapper.CryptoMapper;
import com.example.cryptorecommendation.repository.CryptoRepository;
import com.example.cryptorecommendation.repository.CryptoStatsRepository;
import com.example.cryptorecommendation.rest.exceptions.CryptoNotSupported;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * Service for processing crypto data and returning statistics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoRecommendationService {

    private final CryptoRepository cryptoRepository;
    private final CryptoStatsRepository cryptoStatsRepository;
    private final CryptoMapper cryptoMapper;

    /**
     * Method to store crypto data in repository
     * Also the method is storing calculated stats of crypto all-time values
     *
     * @param symbol crypto symbol
     * @param prices list of crypto prices with timestamps
     */
    public void saveCrypto(String symbol, List<CryptoPrice> prices){
        checkCryptoDataNotEmpty(symbol, prices);

        prices = prices.stream().sorted(Comparator.comparing(CryptoPrice::getDateTime)).toList();
        cryptoRepository.save(new Crypto(symbol, prices));

        getCryptoStats(symbol, null, null);
    }

    /**
     * Based on crypto symbol and dates from and to the method will return stats for provided Crypto.
     * The stats for specific dates are stored in memory and not calculated again for better performance.
     */
    public CryptoStats getCryptoStats(String cryptoSymbol, LocalDateTime dateFrom, LocalDateTime dateTo){
        var cryptoStats = cryptoStatsRepository.getCryptoStatsForRange(cryptoSymbol, dateFrom, dateTo);
        if(cryptoStats == null) {
            cryptoStats = calculateCryptoStats(cryptoSymbol, dateFrom, dateTo);
            cryptoStatsRepository.save(cryptoStats, dateFrom, dateTo);
        }

        return cryptoStats;
    }

    /**
     * Method for calculating Crypto Stats for specified date range.
     * If the dates are null, the method will calculate stats considering all ever stored prices.
     */
    private CryptoStats calculateCryptoStats(String cryptoSymbol, LocalDateTime dateFrom, LocalDateTime dateTo) {
        var prices = cryptoRepository.findBySymbol(cryptoSymbol).getPrices();

        if(dateFrom != null && dateTo != null) {
            prices = prices.stream().filter(e -> e.getDateTime().isAfter(dateFrom.minusSeconds(1)) &&
                                        e.getDateTime().isBefore(dateTo)).toList();
        }

        if(!prices.isEmpty()) {
            CryptoStats cryptoStats = new CryptoStats();
            cryptoStats.setSymbol(cryptoSymbol);
            cryptoStats.setMax(calculateMaxPrice(prices));
            cryptoStats.setMin(calculateMinPrice(prices));
            cryptoStats.setOldest(prices.get(0).getPrice());
            cryptoStats.setNewest(prices.get(prices.size() -1).getPrice());
            return cryptoStats;
        }
        return new CryptoStats(cryptoSymbol, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private BigDecimal calculateMaxPrice(List<CryptoPrice> prices) {
        return prices.stream().map(CryptoPrice::getPrice).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateMinPrice(List<CryptoPrice> prices) {
        return prices.stream().map(CryptoPrice::getPrice).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
    }

    private void checkCryptoDataNotEmpty(String symbol, List<CryptoPrice> prices) {
        if (StringUtils.isBlank(symbol)) {
            throw new IllegalArgumentException("Crypto symbol cannot be empty.");
        }

        if (prices == null || prices.isEmpty()) {
            throw new IllegalArgumentException("Crypto price list cannot be empty.");
        }
    }

    /**
     * Returns Crypto Stats for a given date range.
     */
    public CryptoStatsDto getSpecificCryptoStats(String cryptoSymbol, LocalDate dateFrom, LocalDate dateTo){
        checkIfCryptoIsSupported(cryptoSymbol);
        var dateTimeFrom = dateFrom != null ? LocalDateTime.of(dateFrom, LocalTime.MIN) : null;
        var dateTimeTo = dateTo != null ? LocalDateTime.of(dateTo, LocalTime.MIN) : null;

        var cryptoStats = getCryptoStats(cryptoSymbol, dateTimeFrom, dateTimeTo);
        return cryptoMapper.mapCryptoStatsToDto(cryptoStats);
    }

    /**
     * If Crypto is not detected in our in-memory database, return CryptoNotSupported exception.
     */
    private void checkIfCryptoIsSupported(String cryptoSymbol) {
        Crypto crypto = cryptoRepository.findBySymbol(cryptoSymbol);
        if(crypto == null) {
            log.error("Crypto {} not supported", cryptoSymbol);
            throw new CryptoNotSupported(cryptoSymbol);
        }
    }

    /**
     * Based on date range provided in arguments, the method will calculate normalized prices for that range for
     * each crypto in DB and return it in descending sorted list.
     */
    public CryptoNormalizedRangeListDto getNormalizedCryptosListDescending(LocalDate dateFrom, LocalDate dateTo) {
        var cryptoSymbols = cryptoRepository.getAllCryptoSymbols();

        var normalizedCryptosList = new ArrayList<CryptoNormalizedRangeDto>();
        for(String cryptoSymbol : cryptoSymbols) {
            var dateTimeFrom = dateFrom != null ? LocalDateTime.of(dateFrom, LocalTime.MIN) : null;
            var dateTimeTo = dateTo != null ? LocalDateTime.of(dateTo, LocalTime.MIN) : null;

            var cryptoNormalizedRange = calculateNormalizedCryptoPriceForDateRange(cryptoSymbol,dateTimeFrom,dateTimeTo);
            normalizedCryptosList.add(cryptoNormalizedRange);
        }

        normalizedCryptosList.sort(Comparator.comparing(CryptoNormalizedRangeDto::getNormalizedPrice).reversed());
        return new CryptoNormalizedRangeListDto(normalizedCryptosList);
    }

    /**
     * Method for calculating normalized price for Crypto provided and for provided date range.
     */
    private CryptoNormalizedRangeDto calculateNormalizedCryptoPriceForDateRange(String cryptoSymbol,
                                                                                LocalDateTime fromDate,
                                                                                LocalDateTime toDate) {
        var cryptoStats = getCryptoStats(cryptoSymbol, fromDate, toDate);
        var normalizedRange = new CryptoNormalizedRangeDto();
        normalizedRange.setSymbol(cryptoSymbol);

        if(!BigDecimal.ZERO.equals(cryptoStats.getMin())){
            var normalizedPrice = (cryptoStats.getMax().subtract(cryptoStats.getMin()))
                    .divide(cryptoStats.getMin(), RoundingMode.HALF_EVEN);
            normalizedRange.setNormalizedPrice(normalizedPrice);
            return normalizedRange;
        }

        normalizedRange.setNormalizedPrice(BigDecimal.ZERO);
        return normalizedRange;
    }

    /**
     * Return Crypto with highest normalized range for the specified day.
     */
    public CryptoNormalizedRangeDto cryptoWithHighestNormalizedRangeByDay(LocalDate date) {
        var fromDate = LocalDateTime.of(date, LocalTime.MIN);
        var toDate = fromDate.plusDays(1);

        var highestNormalizedCryptoValue = BigDecimal.ZERO;
        var highestNormalizedCryptoSymbol = "";

        for(var crypto : cryptoRepository.getAllCryptoSymbols()){
            var normalizedCrypto = calculateNormalizedCryptoPriceForDateRange(crypto, fromDate, toDate);
            if(normalizedCrypto.getNormalizedPrice().compareTo(highestNormalizedCryptoValue) > 0) {
                highestNormalizedCryptoValue = normalizedCrypto.getNormalizedPrice();
                highestNormalizedCryptoSymbol = normalizedCrypto.getSymbol();
            }
        }

        return new CryptoNormalizedRangeDto(highestNormalizedCryptoSymbol, highestNormalizedCryptoValue);
    }
}
