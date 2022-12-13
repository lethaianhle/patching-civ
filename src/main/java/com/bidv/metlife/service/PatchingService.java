package com.bidv.metlife.service;

import com.bidv.metlife.model.Response;
import com.bidv.metlife.model.STATUS;
import com.bidv.metlife.model.UpdateDateResult;
import com.bidv.metlife.model.DataModel;
import com.bidv.metlife.repository.ContractRepository;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
@Log4j2
public class PatchingService {

    @Value("${filePath}")
    private String filePath;

    @Value("${csv.date.format}")
    private String csvDateFormat;

    @Value("${csv.date.timeZone}")
    private String csvDateTimeZone;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private MongoOperations mongoOperation;

    public Response patching() {
        Response response = new Response();
        response.setStatus(STATUS.PENDING);
        log.info("Start patching data from {} to CIV...", filePath);
        long t1 = System.currentTimeMillis();
        try {
            File file = new File(filePath);
            List<DataModel> data = ReadCsv.readFromCsv(file);
            log.info("Total records in file: {}.records", data.size());
            UpdateDateResult result = updateData(data);
            response.setTotalRecordsInFile(result.getTotalRecordsInFile());
            response.setMessage(result.getMessage());
            response.setTotalRecordsUpdated(result.getTotalRecordsUpdated());
            if (result.getTotalRecordsInFile() == result.getTotalRecordsUpdated()) {
                response.setStatus(STATUS.DONE);
                response.setMessage("SUCCESS");
            }
        } catch (Exception ex) {
            log.error("Error patching data form {} to CIV: {}", filePath, ex.getMessage());
            response.setMessage("Error patching data form " + filePath + " to CIV. Error: " + ex.getMessage());
            response.setStatus(STATUS.FAIL);
        } finally {
            long t2 = System.currentTimeMillis();
            log.info("End patching data from {} to CIV in {}.milliseconds", filePath, (t2 - t1));
        }
        return response;
    }

    @Synchronized
    public UpdateDateResult updateData(List<DataModel> datas) {
        UpdateDateResult result = new UpdateDateResult();
        result.setTotalRecordsInFile(datas.size());
        long count = 0;
        long line = 1;
        for (DataModel data : datas) {
            line++;
            log.info("===>> PolicyNo: {} ___ firstIssuedDate: {} ___ line {}", data.getPolicyNo(), data.getFirstIssuedDate(), line);
            var exist = contractRepository.findContractModelByPolicyNo(data.getPolicyNo());
            if (exist == null) {
                result.setMessage("Error: No contract found with policyNo " + data.getPolicyNo() + " at line: " + line + ". Cannot update policyNo: " + data.getPolicyNo() + " with firstIssuedDate: " + data.getFirstIssuedDate());
                log.error("Cannot update policyNo: {} with firstIssuedDate: {}. Error: No contract found with policyNo at line {}", data.getPolicyNo(), data.getFirstIssuedDate(), line);
                break;
            } else {
                var firstIssuedDate = string2Date(data.getFirstIssuedDate(), line);
                if (firstIssuedDate != null) {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("policyNo").exists(true).orOperator(Criteria.where("policyNo").is(data.getPolicyNo())));
                    Update update = new Update();
                    update.set("firstIssuedDate", firstIssuedDate);
                    var res = mongoOperation.updateMulti(query, update, "contracts");
                    count++;
                } else {
                    result.setMessage("Error: Wrong input date at line " + line + ". Cannot update policyNo: " + data.getPolicyNo() + " with firstIssuedDate: " + data.getFirstIssuedDate());
                    log.error("Cannot update policyNo: {} with firstIssuedDate: {}. Error: Wrong input date at line {}", data.getPolicyNo(), data.getFirstIssuedDate(), line);
                    break;
                }
            }
        }
        log.info("Total records updated: {}.records", count);
        result.setTotalRecordsUpdated(count);
        return result;
    }

    private Date string2Date(String str, long line) {
        try {
            if (!str.trim().matches("^((19|2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$")) {
                throw new RuntimeException("Wrong date input: " + str + " at line " + line);
            }
            DateFormat dateFormat = new SimpleDateFormat(csvDateFormat);
            dateFormat.setTimeZone(TimeZone.getTimeZone(csvDateTimeZone));
            return dateFormat.parse(str);
        } catch (Exception ex) {
            log.error("Error parse date from {} in updating database. Error: {}", str, ex.getMessage());
            return null;
        }
    }

}
