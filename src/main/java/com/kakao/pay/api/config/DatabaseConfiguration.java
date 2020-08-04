package com.kakao.pay.api.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement(order = 2)
public class DatabaseConfiguration {

    @Bean(name = "slaveHikariConfig")
    @ConfigurationProperties(prefix = "datasource.slave")
    public HikariConfig slaveHikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = "masterHikariConfig")
    @ConfigurationProperties(prefix = "datasource.master")
    public HikariConfig masterHikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = "routingDataSource")
    public DataSource routingDataSource(@Qualifier("slaveHikariConfig") HikariConfig slaveHikariConfig, @Qualifier("masterHikariConfig") HikariConfig masterHikariConfig) {
        DataSource slaveDataSource = new HikariDataSource(slaveHikariConfig);
        DataSource masterDataSource = new HikariDataSource(masterHikariConfig);

        Map<Object, Object> resolvedDataSources = new HashMap<>();

        resolvedDataSources.put(DatabaseType.SLAVE, slaveDataSource);
        resolvedDataSources.put(DatabaseType.MASTER, masterDataSource);


        RoutingDataSource routingDataSource = new RoutingDataSource();

        routingDataSource.setTargetDataSources(resolvedDataSources);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);
        routingDataSource.afterPropertiesSet();

        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    @Bean
    @ConfigurationProperties(prefix = "mybatis")
    public SqlSessionFactoryBean sqlSessionFactoryBean(@Qualifier("routingDataSource") DataSource routingDataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();

        sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        sqlSessionFactoryBean.setDataSource(routingDataSource);

        return sqlSessionFactoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new DataSourceTransactionManager(routingDataSource);
    }
}
