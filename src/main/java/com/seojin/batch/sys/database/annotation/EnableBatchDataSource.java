package com.seojin.batch.sys.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.seojin.batch.sys.database.config.PrimaryBatchDatasourceConfig;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({ PrimaryBatchDatasourceConfig.class })
public @interface EnableBatchDataSource {
	String config() default "classpath:mybatis-config.xml";

	String mappers() default "";
}
