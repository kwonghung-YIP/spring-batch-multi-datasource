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
	@ConfigurationProperties("spring-batch-db.datasource")
	DataSource springBatchDb() {
		DataSourceBuilder builder = DataSourceBuilder.create();
		builder.type(HikariDataSource.class);
		return builder.build();
	}
	
	@Bean
	@ConfigurationProperties("card-db.datasource")
	DataSource cardDb() {
		DataSourceBuilder builder = DataSourceBuilder.create();
		builder.type(HikariDataSource.class);
		return builder.build();
	}
	
	@Bean
	PlatformTransactionManager cardDbTxManager() {
		return new DataSourceTransactionManager(cardDb());
	}
	
	@Bean
	DataSourceInitializer cardDbInit(@Value("${card-db.init-script}") Resource initScript) {
		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(cardDb());
		initializer.setEnabled(true);
		
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(initScript);
		populator.setSeparator("//");
		initializer.setDatabasePopulator(populator);
		
		ResourceDatabasePopulator cleaner = new ResourceDatabasePopulator();
		initializer.setDatabaseCleaner(cleaner);
		
		return initializer;
	}
	
	@Bean
	@ConfigurationProperties("account-db.datasource")
	DataSource accountDb() {
		DataSourceBuilder builder = DataSourceBuilder.create();
		builder.type(HikariDataSource.class);
		return builder.build();
	}

	@Bean
	DataSourceInitializer accountDbInit(@Value("${account-db.init-script}") Resource initScript) {
		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(accountDb());
		initializer.setEnabled(true);
		
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(initScript);
		initializer.setDatabasePopulator(populator);
		
		ResourceDatabasePopulator cleaner = new ResourceDatabasePopulator();
		initializer.setDatabaseCleaner(cleaner);
		
		return initializer;
	}
	
	@Bean
	PlatformTransactionManager accountDbTxManager() {
		return new DataSourceTransactionManager(accountDb());
		//return new JtaTransactionManager();
	}
	
	@Bean
	PlatformTransactionManager chainTxManager() {
		ChainedTransactionManager txManager = new ChainedTransactionManager(cardDbTxManager(),accountDbTxManager());
		return txManager;
	}
}
