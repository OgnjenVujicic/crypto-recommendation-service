package com.example.cryptorecommendation.mapper;

import com.example.cryptorecommendation.dto.CryptoStatsDto;
import com.example.cryptorecommendation.entity.CryptoStats;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface CryptoMapper {

    CryptoStatsDto mapCryptoStatsToDto(CryptoStats cryptoStats);
}
