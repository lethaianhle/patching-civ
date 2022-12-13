package com.bidv.metlife.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

@Getter
@Builder
@ToString
@Document(collection = "contracts")
public class ContractModel {

    @Id
    private String id;

    private String customerId;

    private String policyNo;

    private String appNo;

    private Date ackDateByOnePAS;

    private Date ackDateByCustomer;

    private String status;

    private Set<String> productCodes;

    private Date issuedDate;

    private Date firstIssuedDate;

    private boolean sentNotification;

    private Set<String> missDocTypes;

    private Set<String> hasDocTypes;

    private int automaticCount;

    private int manualCount;

    private Date lastSync;

    private Date createdDate;

    private Date modifiedDate;

}
