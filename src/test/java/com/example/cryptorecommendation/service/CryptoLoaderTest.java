package com.example.cryptorecommendation.service;


import com.example.cryptorecommendation.entity.CryptoPrice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class CryptoLoaderTest {

    @InjectMocks
    private CryptoLoader cryptoLoader;

    @Test
    void loadCryptoDataFromDirectory() throws IOException {

        Map<String, List<CryptoPrice>> cryptoPrices = Map.of(
                "BTC", List.of(
                        new CryptoPrice(LocalDateTime.of(2022, 1, 1, 4, 0), BigDecimal.valueOf(46813.21)),
                        new CryptoPrice(LocalDateTime.of(2022, 1, 1, 7, 0), BigDecimal.valueOf(46979.61)),
                        new CryptoPrice(LocalDateTime.of(2022, 1, 22, 11, 0), BigDecimal.valueOf(35488.54)),
                        new CryptoPrice(LocalDateTime.of(2022, 1, 31, 20, 0), BigDecimal.valueOf(38415.79))),
                "ETH", List.of(
                        new CryptoPrice(LocalDateTime.of(2022, 1, 1, 8, 0), BigDecimal.valueOf(3715.32)),
                        new CryptoPrice(LocalDateTime.of(2022, 1, 1, 10, 0), BigDecimal.valueOf(3718.67)),
                        new CryptoPrice(LocalDateTime.of(2022, 1, 19, 11, 0), BigDecimal.valueOf(3078.34)),
                        new CryptoPrice(LocalDateTime.of(2022, 1, 31, 20, 0), BigDecimal.valueOf(2672.5)))
        );


        var loadedPrices = cryptoLoader.loadCryptoDataFromDirectory("classpath:crypto_test_prices");

        assertThat(loadedPrices).containsKey("BTC").containsKey("ETH");

        assertThat(loadedPrices.get("BTC")).containsExactlyInAnyOrderElementsOf(cryptoPrices.get("BTC"));
        assertThat(loadedPrices.get("ETH")).containsExactlyInAnyOrderElementsOf(cryptoPrices.get("ETH"));
    }
}