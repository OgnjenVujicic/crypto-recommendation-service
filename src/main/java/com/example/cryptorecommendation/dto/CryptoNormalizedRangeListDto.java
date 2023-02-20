package com.example.cryptorecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CryptoNormalizedRangeListDto {
    private List<CryptoNormalizedRangeDto> cryptoList;
}
