package com.seojin.batch;

import com.seojin.batch.sys.database.annotation.EnableBatchDataSource;
import com.seojin.commons.annotations.EnableSeojinRestApplication;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Description : Batch API Service
 * <p>
 *
 * <pre>
 * Note
 * ※ Batch Job
 * - @Configuration 어노테이션을 선언하고, 모든 JOB, STEP은 Bean으로 등록한다.
 * - BaseAbstractJob을 상속받아 Job config를 생성한다.  
 * </pre>
 *
 */
@SpringBootApplication
@EnableBatchProcessing
@EnableSeojinRestApplication
@EnableBatchDataSource(mappers = "classpath:mappers/*/primary/**/*.xml")
public class SeojinBatchApplication extends SpringBootServletInitializer {

	/**
	 * Description : Batch API Service Main
	 * <p>
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("spring.config.additional-location",
				"classpath:/config/default/,classpath:/config/test/,classpath:/config/devel/,classpath:/config/prod/");

		SpringApplication.run(SeojinBatchApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		System.setProperty("spring.config.additional-location",
				"classpath:/config/default/,classpath:/config/test/,classpath:/config/devel/,classpath:/config/prod/");

		return application.sources(SeojinBatchApplication.class);
	}
}
