package com.example.cryptorecommendation.service;

import com.example.cryptorecommendation.entity.CryptoPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Crypto Loader task is to load cryptocurrencies from CSV files on application startup.
 * All data will be stored in repositories so that it can be extendable in future production
 * environment and easily replaced with some in-memory or SQL DB.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CryptoLoader {

    private final CryptoRecommendationService cryptoRecommendationService;
    @Value("${crypto.directory}")
    private String cryptoDirectory;

    /**
     * Load crypto data from CSV files on ApplicationReadyEvent and store it using repositories.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void loadCryptoData() {

        log.info("Start loading of crypto data.");

        Map<String, List<CryptoPrice>> cryptoData;

        try {
            cryptoData = loadCryptoDataFromDirectory(cryptoDirectory);
        } catch (IOException e) {
            log.error("Error in loading of crypto data.", e);
            return;
        }

        cryptoData.forEach(cryptoRecommendationService::saveCrypto);

        log.info("Crypto data loaded. List of cryptos: {}.", cryptoData.keySet().stream().toList());
    }


    /**
     *
     * Load service is able to scale additional cryptos as long as they are placed in appropriate folder
     * with right naming convention ($symbolName_values.csv)
     */
    public Map<String, List<CryptoPrice>> loadCryptoDataFromDirectory(String directoryPath) throws IOException {

        var cryptoData = new HashMap<String, List<CryptoPrice>>();

        var cryptoFilesDirectory = ResourceUtils.getFile(directoryPath);

        var allCryptoFiles = FileUtils.listFiles(cryptoFilesDirectory,
                        FileFilterUtils.suffixFileFilter("_values.csv", IOCase.INSENSITIVE), null)
                .stream().filter(File::isFile).toList();

        for (var cryptoFile : allCryptoFiles) {
            var cryptoSymbol = StringUtils.removeEnd(cryptoFile.getName(), "_values.csv");
            var cryptoPrices = loadCryptoPricesFromCsvFile(cryptoFile.getAbsolutePath());
            cryptoData.put(cryptoSymbol, cryptoPrices);
        }

        return cryptoData;
    }

    public List<CryptoPrice> loadCryptoPricesFromCsvFile(String filePath) throws IOException {
        var cryptoPrices = new ArrayList<CryptoPrice>();

        var reader = Files.newBufferedReader(Paths.get(filePath));

        var rows = CSVFormat.DEFAULT.builder()
                .setHeader("timestamp", "symbol", "price")
                .setSkipHeaderRecord(true).build()
                .parse(reader);

        for (var row : rows) {
            var timestamp = Instant.ofEpochMilli(Long.parseLong(row.get("timestamp")));
            var price = Double.parseDouble(row.get("price"));

            var cryptoPrice = new CryptoPrice();
            cryptoPrice.setPrice(BigDecimal.valueOf(price));
            cryptoPrice.setDateTime(LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC));

            cryptoPrices.add(cryptoPrice);
        }

        return cryptoPrices;
    }
}
