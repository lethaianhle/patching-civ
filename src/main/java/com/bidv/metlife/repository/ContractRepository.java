package com.bidv.metlife.repository;

import com.bidv.metlife.model.ContractModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContractRepository extends MongoRepository<ContractModel, String> {

    ContractModel findContractModelByPolicyNo(String policyNo);

}
