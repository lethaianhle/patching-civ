package com.bidv.metlife.model;

import lombok.Data;

@Data
public class Response {

    private long totalRecordsInFile;

    private long totalRecordsUpdated;

    private STATUS status;

    private String message;

}
