package com.example.cryptorecommendation.controller;


import com.example.cryptorecommendation.dto.CryptoNormalizedRangeDto;
import com.example.cryptorecommendation.dto.CryptoNormalizedRangeListDto;
import com.example.cryptorecommendation.dto.CryptoStatsDto;
import com.example.cryptorecommendation.service.CryptoRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller - Crypto Recommendation
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/crypto-recommend")
public class CryptoRecommendationController {

    private final CryptoRecommendationService cryptoRecommendationService;

    @Operation(summary = "Get Stats (oldest/newest/min/max values) for specific Crypto.",
               description = """
                    Optionally provide dateFrom and dateTo request parameters in ISO date format: yyyy-MM-dd.
                    If provided, endpoint will return stats of Crypto for that specific date range.
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CryptoStatsDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Crypto Not Found/Supported",
                    content = @Content) })
    @GetMapping("cryptoStats/{crypto}")
    public ResponseEntity<CryptoStatsDto> specificCryptoStats(
            @PathVariable String crypto,
            @RequestParam(name = "dateFrom", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
            @RequestParam(name = "dateTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo){
        return ResponseEntity.ok(cryptoRecommendationService.getSpecificCryptoStats(crypto,dateFrom,dateTo));
    }

    @Operation(summary = "Get list of all Cryptos sorted by Normalized Range Descending.",
               description = """
                    Optionally provide dateFrom and dateTo request parameters in ISO date format: yyyy-MM-dd.
                    If provided, endpoint will return list of all Cryptos sorted by Normalized Range Descending
                    for that specific date range.
                    """)
    @GetMapping("normalizedPricesDescending")
    public ResponseEntity<CryptoNormalizedRangeListDto> normalizedCryptosDescending(
            @RequestParam(name = "dateFrom", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
            @RequestParam(name = "dateTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo
            ){
        return ResponseEntity.ok(
                cryptoRecommendationService.getNormalizedCryptosListDescending(dateFrom,dateTo)
        );
    }

    @Operation(summary = "Get Crypto with highest Normalized Range for specific day.")
    @GetMapping("highestCryptoNormalizedRange/byDay/{date}")
    public ResponseEntity<CryptoNormalizedRangeDto> highestCryptoNormalizedRangeByDay(
            @Parameter(description = "ISO date format: yyyy-MM-dd")
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        return ResponseEntity.ok(cryptoRecommendationService.cryptoWithHighestNormalizedRangeByDay(date));
    }

}
