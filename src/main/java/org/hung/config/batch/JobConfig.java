package org.hung.config.batch;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.sql.DataSource;

import org.hung.pojo.AccountTxn;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class JobConfig {

	@Autowired
	private JobBuilderFactory jobFactory;
	
	@Autowired
	private StepBuilderFactory stepFactory;
	
	@Bean
	JdbcCursorItemReader<AccountTxn> txnReader(@Qualifier("cardDb") DataSource datasource) {
		return new JdbcCursorItemReaderBuilder<AccountTxn>()
				.name("account-txn-reader")
				.dataSource(datasource)
				.sql("select t.*, c.credit_limit from CARD_TXN t join CARD c on t.card_no = c.card_no where t.post = 'N' order by t.tx_datetime")
				.<AccountTxn>rowMapper((rs,rownum) -> {
					AccountTxn bean = new AccountTxn();
					
					bean.setTxRef(rs.getLong("tx_ref"));
					Timestamp txTs = rs.getTimestamp("tx_datetime");
					bean.setTxDatetime(txTs==null?null:txTs.toLocalDateTime());
					bean.setCardNo(rs.getString("card_no"));
					bean.setCreditLimit(rs.getBigDecimal("credit_limit"));
					bean.setCrAmt(rs.getBigDecimal("cr_amount"));
					bean.setDrAmt(rs.getBigDecimal("dr_amount"));
					bean.setPosted("Y".equalsIgnoreCase(rs.getString("post")));
					Timestamp postTs = rs.getTimestamp("post_datetime");
					bean.setPostDatetime(postTs==null?null:postTs.toLocalDateTime());
 
					return bean;
				})
				.build();				
	}
	
	ItemProcessor<AccountTxn, AccountTxn> txnProcessor() {
		return (item) -> {
			item.setPosted(true);
			item.setPostDatetime(LocalDateTime.now());
			return item;
		};
	}
	
	@Bean
	CompositeItemWriter<AccountTxn> txnWriter() {
		return new CompositeItemWriterBuilder<AccountTxn>()
				.delegates(balanceWriter(null),postTxnWriter(null))
				.build();
	}
	
	@Bean
	JdbcBatchItemWriter<AccountTxn> balanceWriter(@Qualifier("accountDb") DataSource datasource) {	
		return new JdbcBatchItemWriterBuilder<AccountTxn>()
				.dataSource(datasource)
				.assertUpdates(true)
				.sql("insert into ACCOUNT (account_no, credit_limit, balance, last_tx_ref, last_tx_datetime) values (:cardNo, :creditLimit, :netAmt, :txRef, :txDatetime)" + 
						" on duplicate key update BALANCE = BALANCE + :netAmt, LAST_TX_REF = :txRef, LAST_TX_DATETIME = :txDatetime")
				.beanMapped()
				.build();
	}	
	
	@Bean
	JdbcBatchItemWriter<AccountTxn> postTxnWriter(@Qualifier("cardDb") DataSource datasource) {	
		return new JdbcBatchItemWriterBuilder<AccountTxn>()
				.dataSource(datasource)
				.assertUpdates(true)
				.sql("update CARD_TXN set POST = 'Y', POST_DATETIME = :postDatetime where TX_REF = :txRef")
				.beanMapped()
				.build();
	}	
	
	@Bean 
	MyListener listener(@Qualifier("cardDb") DataSource datasource) {
		return new MyListener<AccountTxn,AccountTxn>(new JdbcTemplate(datasource));
	}
	
	@Bean
	Step runBalanceStep(@Qualifier("chainTxManager") PlatformTransactionManager transactionManager) {
		
		DefaultTransactionAttribute txAttribute = new DefaultTransactionAttribute();
		txAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		txAttribute.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		txAttribute.setTimeout(600);
		
		return stepFactory.get("run-balance-step")
				.transactionManager(transactionManager)
				.<AccountTxn,AccountTxn>chunk(100)
				.faultTolerant()
				//.skipPolicy(new AlwaysSkipItemSkipPolicy())
				.reader(txnReader(null))
				.processor(txnProcessor())
				.writer(txnWriter())
				.transactionAttribute(txAttribute)
				.listener(listener(null))
				.build();
	}
	
	@Bean
	Job runBalanceJob() {
		return jobFactory.get("run-balance-job")
				.start(runBalanceStep(null))
				.build();
	}
	
}
