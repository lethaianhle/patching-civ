package com.bidv.metlife.service;

import com.bidv.metlife.model.DataModel;
import com.opencsv.CSVReader;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ReadCsv {

    public static List<DataModel> readFromCsv(File file) {
        List<DataModel> response = new ArrayList<>();
        int line = 1;
        try (Reader reader = new FileReader(file); CSVReader csvReader = new CSVReader(reader)) {
            List<String[]> lines = csvReader.readAll();
            lines.remove(0);
            for (String[] arr : lines) {
                line++;
                try {
                    DataModel data = new DataModel();
                    data.setPolicyNo(arr[0]);
                    data.setFirstIssuedDate(arr[1]);
                    log.info(data.toString());
                    response.add(data);
                } catch (Exception ex) {
                    log.error(file.getName() + " parse csv error at line " + line);
                    log.error(ex);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return response;
    }

}
