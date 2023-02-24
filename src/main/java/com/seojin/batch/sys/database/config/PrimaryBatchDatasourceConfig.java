package com.seojin.batch.sys.database.config;

import java.io.FileNotFoundException;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.seojin.batch.sys.database.annotation.EnableBatchDataSource;
import com.seojin.batch.sys.database.annotation.PrimaryBatchMapper;
import com.seojin.commons.utils.UtilsAnnotaion;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * Description : Primary Batch DatasourceConfig
 * <p>
 *
 */
@Configuration
@MapperScan(value = "com.seojin.batch", annotationClass = PrimaryBatchMapper.class, sqlSessionFactoryRef = "primaryBatchSqlSessionFactory")
@Slf4j
public class PrimaryBatchDatasourceConfig {

	
	private ApplicationContext context;

	@Autowired
	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	/**
	 * Description : dataSource 빈 구성
	 * <p>
	 * @return
	 */
	@Bean(name = "primaryBatchDatasource", destroyMethod = "close")
	@Primary
	@ConfigurationProperties(prefix = "datasource.primary")
	public DataSource primaryBatchDatasource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	/**
	 * Description : SqlSessionFactory 빈 구성
	 * <p>
	 * @param primaryBatchDatasource
	 * @param applicationContext
	 * @return
	 * @throws Exception
	 */
	@Bean(name = "primaryBatchSqlSessionFactory")
	@Primary
	public SqlSessionFactory primaryBatchSqlSessionFactory(@Autowired @Qualifier("primaryBatchDatasource") DataSource primaryBatchDatasource,
			ApplicationContext applicationContext) throws Exception {
		final EnableBatchDataSource clazz = UtilsAnnotaion.getMainclassName(context)
				.getAnnotation(EnableBatchDataSource.class);
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(primaryBatchDatasource);
		factoryBean.setConfigLocation(context.getResource(clazz.config()));
		if (StringUtils.isNotBlank(clazz.mappers())) {
		   try {
			factoryBean.setMapperLocations(context.getResources(clazz.mappers()));
		   }catch(FileNotFoundException e) {
			   log.info("Not Found Mapper Files");
		   }
		}
		return factoryBean.getObject();
	}

}
