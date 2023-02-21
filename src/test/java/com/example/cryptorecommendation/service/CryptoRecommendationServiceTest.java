package com.example.cryptorecommendation.service;

import com.example.cryptorecommendation.entity.Crypto;
import com.example.cryptorecommendation.entity.CryptoPrice;
import com.example.cryptorecommendation.entity.CryptoStats;
import com.example.cryptorecommendation.mapper.CryptoMapper;
import com.example.cryptorecommendation.mapper.CryptoMapperImpl;
import com.example.cryptorecommendation.repository.CryptoRepository;
import com.example.cryptorecommendation.repository.CryptoStatsRepository;
import com.example.cryptorecommendation.rest.exceptions.CryptoNotSupported;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoRecommendationServiceTest {

    @Mock
    private CryptoRepository cryptoRepository;

    @Mock
    private CryptoStatsRepository cryptoStatsRepository;

    @Spy
    private CryptoMapper cryptoMapper = new CryptoMapperImpl();

    @InjectMocks
    private CryptoRecommendationService cryptoService;

    @Test
    void saveCrypto_savesDataCorrectly() {
        var cryptoSymbol = "BTC";

        var prices = List.of(
                new CryptoPrice(
                        LocalDateTime.of(2022, 1, 1, 0, 0), BigDecimal.valueOf(11.11)),
                new CryptoPrice(
                        LocalDateTime.of(2022, 1, 3, 0, 0), BigDecimal.valueOf(33.33)),
                new CryptoPrice(
                        LocalDateTime.of(2022, 1, 2, 0, 0), BigDecimal.valueOf(22.22)));

        var sortedPrices = List.of(
                new CryptoPrice(
                        LocalDateTime.of(2022, 1, 1, 0, 0), BigDecimal.valueOf(11.11)),
                new CryptoPrice(
                        LocalDateTime.of(2022, 1, 2, 0, 0), BigDecimal.valueOf(22.22)),
                new CryptoPrice(
                        LocalDateTime.of(2022, 1, 3, 0, 0), BigDecimal.valueOf(33.33)));

        Mockito.doReturn(new Crypto(cryptoSymbol, sortedPrices)).when(cryptoRepository).findBySymbol(cryptoSymbol);
        cryptoService.saveCrypto(cryptoSymbol, prices);

        ArgumentCaptor<Crypto> argumentCaptor = ArgumentCaptor.forClass(Crypto.class);
        verify(cryptoRepository).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getSymbol()).isEqualTo(cryptoSymbol);
        assertThat(argumentCaptor.getValue().getPrices())
                .containsExactlyElementsOf(sortedPrices);
    }

    @Test
    void saveCrypto_throwsIllegalArgumentForEmptySymbol() {
        assertThatThrownBy(() -> cryptoService.saveCrypto("", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Crypto symbol cannot be empty.");
    }

    @Test
    void saveCrypto_throwsIllegalArgumentForEmptyPrices() {
        assertThatThrownBy(() -> cryptoService.saveCrypto("BTC", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Crypto price list cannot be empty.");
    }


    @Test
    void getCryptoStats_calculatesCorrectStatsAndSavesThem() {
        var crypto = new Crypto("BTC", List.of(
                new CryptoPrice(
                        LocalDateTime.of(2022, 1, 1, 0, 0), BigDecimal.valueOf(11.11)),
                new CryptoPrice(
                        LocalDateTime.of(2022, 1, 10, 0, 0), BigDecimal.valueOf(33.33)),
                new CryptoPrice(
                        LocalDateTime.of(2022, 1, 11, 0, 0), BigDecimal.valueOf(44.44)),
                new CryptoPrice(
                        LocalDateTime.of(2022, 2, 1, 0, 0), BigDecimal.valueOf(55.44)))
                );

        var cryptoStats = new CryptoStats( "BTC",
                BigDecimal.valueOf(11.11), BigDecimal.valueOf(44.44),
                BigDecimal.valueOf(11.11), BigDecimal.valueOf(44.44));

        when(cryptoRepository.findBySymbol("BTC")).thenReturn(crypto);

        assertThat(cryptoService.getCryptoStats(
                "BTC",
                LocalDateTime.of(2022, 1, 1, 0, 0),
                LocalDateTime.of(2022, 1, 12, 0, 0)))
                .isEqualTo(cryptoStats);

        verify(cryptoStatsRepository).save(cryptoStats,
                LocalDateTime.of(2022, 1, 1, 0, 0),
                LocalDateTime.of(2022, 1, 12, 0, 0));
    }

    @Test
    void getSpecificCryptoStats_returnCorrectStatsForDatesWithNoData() {
        var crypto = new Crypto("BTC", List.of(
                new CryptoPrice(
                        LocalDateTime.of(2022, 1, 1, 0, 0), BigDecimal.valueOf(11.11)),
                new CryptoPrice(
                        LocalDateTime.of(2022, 1, 10, 0, 0), BigDecimal.valueOf(33.33)))
        );

        var cryptoStats = new CryptoStats( "BTC",
                BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO);

        when(cryptoRepository.findBySymbol("BTC")).thenReturn(crypto);

        assertThat(cryptoService.getSpecificCryptoStats(
                "BTC",
                LocalDate.of(2022, 2, 1),
                LocalDate.of(2022, 2, 12)))
                .isEqualTo(cryptoMapper.mapCryptoStatsToDto(cryptoStats));

        verify(cryptoStatsRepository).save(cryptoStats,
                LocalDateTime.of(2022, 2, 1, 0, 0),
                LocalDateTime.of(2022, 2, 12, 0, 0));
    }

    @Test
    void getCryptoStats_returnsStatsFromRepository() {
        var cryptoStats = new CryptoStats( "BTC",
                BigDecimal.valueOf(11.11), BigDecimal.valueOf(44.44),
                BigDecimal.valueOf(11.11), BigDecimal.valueOf(44.44));

        when(cryptoStatsRepository.getCryptoStatsForRange("BTC", null,null))
                .thenReturn(cryptoStats);

        assertThat(cryptoService.getCryptoStats("BTC", null, null))
                .isEqualTo(cryptoStats);
    }

    @Test
    void getSpecificCryptoStats_throwsUnsupportedCrypto() {
        assertThatThrownBy(() -> cryptoService.getSpecificCryptoStats("BTC", null, null))
                .isInstanceOf(CryptoNotSupported.class)
                .hasMessageContaining("Crypto with symbol BTC is not supported currently.");
    }

    @Test
    void getNormalizedCryptosListDescending_returnsCorrectResults() {
        var cryptoSymbols = List.of("BTC", "ETH");

        var btcStats = new CryptoStats("BTC",
                BigDecimal.valueOf(10), BigDecimal.valueOf(20),
                BigDecimal.valueOf(5), BigDecimal.valueOf(30));

        var ethStats = new CryptoStats("ETH",
                BigDecimal.valueOf(20), BigDecimal.valueOf(30),
                BigDecimal.valueOf(10), BigDecimal.valueOf(50));

        when(cryptoRepository.getAllCryptoSymbols()).thenReturn(cryptoSymbols);
        when(cryptoStatsRepository.getCryptoStatsForRange("BTC", null, null))
                .thenReturn(btcStats);
        when(cryptoStatsRepository.getCryptoStatsForRange("ETH",null, null))
                .thenReturn(ethStats);

        var normalizedCryptosListDescendingDto =
                cryptoService.getNormalizedCryptosListDescending(null, null);

        var cryptoList = normalizedCryptosListDescendingDto.getCryptoList();

        assertThat(cryptoList).hasSize(2);
        assertThat(cryptoList.get(0).getSymbol()).isEqualTo("BTC");
        assertThat(cryptoList.get(1).getSymbol()).isEqualTo("ETH");

        assertThat(cryptoList.get(0).getNormalizedPrice()).isEqualTo(BigDecimal.valueOf(5));
        assertThat(cryptoList.get(1).getNormalizedPrice()).isEqualTo(BigDecimal.valueOf(4));
    }

    @Test
    void getNormalizedCryptosListDescending_returnsZeroForNoData() {
        var cryptoSymbols = List.of("BTC");
        var dateFrom = LocalDate.of(2022, 2, 1);
        var dateTo = LocalDate.of(2022, 2, 12);

        var btcStats = new CryptoStats("BTC",
                BigDecimal.ZERO,  BigDecimal.ZERO,
                BigDecimal.ZERO,  BigDecimal.ZERO);

        when(cryptoRepository.getAllCryptoSymbols()).thenReturn(cryptoSymbols);
        when(cryptoStatsRepository.getCryptoStatsForRange("BTC",
                                            LocalDateTime.of(dateFrom, LocalTime.MIN),
                                            LocalDateTime.of(dateTo, LocalTime.MIN)))
                        .thenReturn(btcStats);

        var normalizedCryptosListDescendingDto = cryptoService.getNormalizedCryptosListDescending(dateFrom, dateTo);

        var cryptoList = normalizedCryptosListDescendingDto.getCryptoList();

        assertThat(cryptoList).hasSize(1);
        assertThat(cryptoList.get(0).getSymbol()).isEqualTo("BTC");
        assertThat(cryptoList.get(0).getNormalizedPrice()).isEqualTo(BigDecimal.ZERO);
    }


    @Test
    void cryptoWithHighestNormalizedRangeByDay_returnsCorrectResults() {
        var cryptoSymbols = List.of("BTC", "ETH");
        var day = LocalDate.of(2022,1,1);
        var fromDate = LocalDateTime.of(day, LocalTime.MIN);
        var toDate = fromDate.plusDays(1);

        var btcStats = new CryptoStats("BTC",
                BigDecimal.valueOf(10), BigDecimal.valueOf(20),
                BigDecimal.valueOf(5), BigDecimal.valueOf(30));

        var ethStats = new CryptoStats("ETH",
                BigDecimal.valueOf(20), BigDecimal.valueOf(30),
                BigDecimal.valueOf(10), BigDecimal.valueOf(50));

        when(cryptoRepository.getAllCryptoSymbols()).thenReturn(cryptoSymbols);
        when(cryptoStatsRepository.getCryptoStatsForRange("BTC", fromDate, toDate))
                .thenReturn(btcStats);
        when(cryptoStatsRepository.getCryptoStatsForRange("ETH",fromDate, toDate))
                .thenReturn(ethStats);

        var cryptoWithHighestNormalizedRangeByDay = cryptoService.cryptoWithHighestNormalizedRangeByDay(day);

        assertThat(cryptoWithHighestNormalizedRangeByDay.getNormalizedPrice()).isEqualTo(BigDecimal.valueOf(5));
        assertThat(cryptoWithHighestNormalizedRangeByDay.getSymbol()).isEqualTo("BTC");
    }

}
