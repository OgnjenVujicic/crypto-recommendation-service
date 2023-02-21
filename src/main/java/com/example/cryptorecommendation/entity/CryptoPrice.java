package com.example.cryptorecommendation.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CryptoPrice {
    LocalDateTime dateTime;
    BigDecimal price;
}
