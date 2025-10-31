/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.service.predictive.lot47;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.sisllc.instaiml.util.ResourceAccessUtils;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSVDataParser {

    private static final String SRC_MAIN_RESOURCE_DOCS_LOT47 = "src/main/resources";
    private static final String DOCS_LOT47 = "/lottdocs/mil4701012020to10252025.csv";

    public static List<LottoDraw> getLott47Data() {
        List<LottoDraw> draws = new ArrayList<>();
        try {
            File file = ResourceAccessUtils.getResourceFile(DOCS_LOT47);
            if (file != null) {
                try (CSVReader reader = new CSVReader(new FileReader(file))) {
                    List<String[]> r = reader.readAll();
                    for (String[] ln : r) {
                        if (ln.length > 1 && !ln[1].contains("Winning")) {
                            String line = ln[1];
                            LottoDraw draw = parseDrawLine(line);
                            draws.add(draw);
                        }
                    }
                }
            }
        } catch (CsvException | IOException ex) {
            log.error("Error getLott47Data for file {}", DOCS_LOT47);
        }
        
        log.info("File {} processed with {} records extracted", DOCS_LOT47, draws.size());

        return draws;
    }

    private static LottoDraw parseDrawLine(String line) {
        try {
            String[] numberArray = line.split(",");

            if (numberArray.length == 6) {
                int[] numbers = new int[6];
                for (int i = 0; i < 6; i++) {
                    numbers[i] = Integer.parseInt(numberArray[i].trim());
                }
                Arrays.sort(numbers); // Sort for consistency
                return new LottoDraw(numbers);
            }
        } catch (NumberFormatException e) {
            log.error("Error parsing line: {}", line);
        }
        return null;
    }

}
