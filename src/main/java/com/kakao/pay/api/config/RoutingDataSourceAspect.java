package com.kakao.pay.api.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
@Order(1)
@Component
public class RoutingDataSourceAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingDataSourceAspect.class);
    private static final String IS = "is";
    private static final String SELECT = "select";

    @Pointcut("execution(* com..mapper..*(..))")
    public void executeQuery() {

    }

    @Before("executeQuery()")
    public void switchDataSource(JoinPoint joinPoint) {
        if (TransactionSynchronizationManager.isSynchronizationActive() && !TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            DataSourceContextHolder.set(DatabaseType.MASTER);

            LOGGER.info("Transaction DataSource => [{}] || Method => [{}]", DataSourceContextHolder.get(), joinPoint.getSignature());
        } else if (this.isPrefixStartIs(joinPoint.getSignature().getName()) || this.isPrefixStartSelect(joinPoint.getSignature().getName())) {
            DataSourceContextHolder.set(DatabaseType.SLAVE);

            LOGGER.info("Switch DataSource => [{}] || Method => [{}]", DataSourceContextHolder.get(), joinPoint.getSignature());
        }
    }

    @After("executeQuery()")
    public void restoreDataSource(JoinPoint joinPoint) {
        DataSourceContextHolder.clear();

        LOGGER.info("Restore DataSource => [{}] || Method => [{}]", DataSourceContextHolder.get(), joinPoint.getSignature());
    }

    private boolean isPrefixStartIs(String methodName) {
        return methodName.startsWith(IS);
    }

    private boolean isPrefixStartSelect(String methodName) {
        return methodName.startsWith(SELECT);
    }
}
