package com.kakao.pay.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {
    private final Logger LOGGER = LoggerFactory.getLogger(RoutingDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        LOGGER.info("Current DataSource => [{}]", DataSourceContextHolder.get());

        return DataSourceContextHolder.get();
    }
}
