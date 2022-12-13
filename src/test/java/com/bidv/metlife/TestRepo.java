package com.bidv.metlife;

import jdk.jfr.StackTrace;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SpringBootTest
public class TestRepo {

    @Test
    void test() {
        try {
            String str = "2021-12-31";
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date da = dateFormat.parse(str);
            System.out.println(dateFormat.format(da));
            System.out.println(str.matches("^((19|2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$"));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}
