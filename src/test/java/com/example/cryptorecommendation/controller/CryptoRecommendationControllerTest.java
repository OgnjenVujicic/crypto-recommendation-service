package com.example.cryptorecommendation.controller;

import com.example.cryptorecommendation.dto.CryptoNormalizedRangeDto;
import com.example.cryptorecommendation.dto.CryptoNormalizedRangeListDto;
import com.example.cryptorecommendation.dto.CryptoStatsDto;
import com.example.cryptorecommendation.service.CryptoRecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CryptoRecommendationController.class)
class CryptoRecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CryptoRecommendationService cryptoRecommendationService;

    @Test
    void specificCryptoStats() throws Exception {
        var cryptoStatsDto = new CryptoStatsDto(
                "BTC",
                BigDecimal.valueOf(34.32), BigDecimal.valueOf(54.43),
                BigDecimal.valueOf(3.43), BigDecimal.valueOf(60.10));

        when(cryptoRecommendationService.getSpecificCryptoStats(
                    "BTC",
                    LocalDate.of(2022,1,1),
                    LocalDate.of(2022,1,2)))
                .thenReturn(cryptoStatsDto);

        mockMvc.perform(
                        get("/api/crypto-recommend/cryptoStats/{cryptoSymbol}?dateFrom=2022-01-01&dateTo=2022-01-02",
                                "BTC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("BTC"))
                .andExpect(jsonPath("$.min").value(3.43))
                .andExpect(jsonPath("$.max").value(60.10))
                .andExpect(jsonPath("$.oldest").value(34.32))
                .andExpect(jsonPath("$.newest").value(54.43));
    }

    @Test
    void normalizedCryptosDescending() throws Exception {

        var cryptoNormalizedRangeListDto = new CryptoNormalizedRangeListDto(
                List.of(
                        new CryptoNormalizedRangeDto("BTC", BigDecimal.valueOf(55.43)),
                        new CryptoNormalizedRangeDto("ETH", BigDecimal.valueOf(11.11)))
        );

        when(cryptoRecommendationService.getNormalizedCryptosListDescending(
                    LocalDate.of(2022,1,1), LocalDate.of(2022,1,2)))
                .thenReturn(cryptoNormalizedRangeListDto);

        mockMvc.perform(
                        get("/api/crypto-recommend/normalizedPricesDescending?dateFrom=2022-01-01&dateTo=2022-01-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cryptoList[0].symbol").value("BTC"))
                .andExpect(jsonPath("$.cryptoList[0].normalizedPrice").value(55.43))
                .andExpect(jsonPath("$.cryptoList[1].symbol").value("ETH"))
                .andExpect(jsonPath("$.cryptoList[1].normalizedPrice").value(11.11));
    }

    @Test
    void highestCryptoNormalizedRangeByDay() throws Exception {

        when(cryptoRecommendationService.cryptoWithHighestNormalizedRangeByDay(LocalDate.of(2022, 1, 1)))
                .thenReturn(new CryptoNormalizedRangeDto("BTC", BigDecimal.valueOf(55.55)));

        mockMvc.perform(get("/api/crypto-recommend/highestCryptoNormalizedRange/byDay/{date}", "2022-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("BTC"))
                .andExpect(jsonPath("$.normalizedPrice").value(55.55));
    }

}
