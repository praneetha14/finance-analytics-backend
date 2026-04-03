package com.finance.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.finance.analytics")
public class FinanceAnalyticsBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(FinanceAnalyticsBackendApplication.class, args);
    }

}
