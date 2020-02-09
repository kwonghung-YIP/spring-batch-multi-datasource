package org.hung.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DatabaseConfig {

	@Bean
	@Primary
	@ConfigurationProperties("spring-batch.datasource")
	DataSource batchDatasource() {
		DataSourceBuilder builder = DataSourceBuilder.create();
		builder.type(HikariDataSource.class);
		return builder.build();
	}
	
	@Bean
	@ConfigurationProperties("db1.datasource")
	DataSource db1() {
		DataSourceBuilder builder = DataSourceBuilder.create();
		builder.type(HikariDataSource.class);
		return builder.build();
	}
	
	@Bean
	PlatformTransactionManager db1TxManager() {
		return new DataSourceTransactionManager(db1());
	}
	
	@Bean
	DataSourceInitializer db1Init(@Value("${db1.init-script}") Resource initScript) {
		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(db1());
		initializer.setEnabled(true);
		
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(initScript);
		initializer.setDatabasePopulator(populator);
		
		ResourceDatabasePopulator cleaner = new ResourceDatabasePopulator();
		initializer.setDatabaseCleaner(cleaner);
		
		return initializer;
	}
	
	@Bean
	@ConfigurationProperties("db2.datasource")
	DataSource db2() {
		DataSourceBuilder builder = DataSourceBuilder.create();
		builder.type(HikariDataSource.class);
		return builder.build();
	}

	@Bean
	DataSourceInitializer db2Init(@Value("${db2.init-script}") Resource initScript) {
		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(db2());
		initializer.setEnabled(true);
		
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(initScript);
		initializer.setDatabasePopulator(populator);
		
		ResourceDatabasePopulator cleaner = new ResourceDatabasePopulator();
		initializer.setDatabaseCleaner(cleaner);
		
		return initializer;
	}
	
	@Bean
	PlatformTransactionManager db2TxManager() {
		return new DataSourceTransactionManager(db2());
		//return new JtaTransactionManager();
	}
	
	@Bean
	PlatformTransactionManager chainTxManager() {
		ChainedTransactionManager txManager = new ChainedTransactionManager(db1TxManager(),db2TxManager());
		return txManager;
	}
}
