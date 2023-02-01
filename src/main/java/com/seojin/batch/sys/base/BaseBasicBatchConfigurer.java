package com.seojin.batch.sys.base;

import javax.sql.DataSource;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BasicBatchConfigurer;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * Description : DB 접속 설정
 * <p>
 *
 */
@Configuration
public class BaseBasicBatchConfigurer extends BasicBatchConfigurer {

	/**
	 * DataSource
	 */
	@Autowired
	@Qualifier("primaryBatchDatasource")
	private DataSource dataSource;

	/**
	 * @param properties
	 * @param dataSource
	 * @param transactionManagerCustomizers
	 */
	protected BaseBasicBatchConfigurer(BatchProperties properties, DataSource dataSource,
			TransactionManagerCustomizers transactionManagerCustomizers) {
		super(properties, dataSource, transactionManagerCustomizers);
	}

	/**
	 * Isolation Level 변경 : can't serialize access for this transaction 오류발생으로 수정
	 */
	@Override
	protected JobRepository createJobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(dataSource);
		factory.setTransactionManager(new DataSourceTransactionManager(dataSource));
	//	factory.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
		factory.afterPropertiesSet();
		return factory.getObject();
	}
}