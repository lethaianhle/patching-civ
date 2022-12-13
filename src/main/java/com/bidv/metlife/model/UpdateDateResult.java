package com.bidv.metlife.model;

import lombok.Data;

@Data
public class UpdateDateResult {

    private long totalRecordsUpdated;

    private long totalRecordsInFile;

    private String message;

    private String status;

    private long errorLine;

}
