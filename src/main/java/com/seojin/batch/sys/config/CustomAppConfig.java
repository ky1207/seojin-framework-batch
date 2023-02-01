package com.seojin.batch.sys.config;

import com.seojin.commons.config.AppConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Description : YML에 설정된 data 선언
 * <p>
 *
 * <pre>
 * Note
 * -  상수 선언은 Const 사용
 * </pre>
 */
@Configuration
@Data
@EqualsAndHashCode(callSuper = false)
public class CustomAppConfig extends AppConfig {
    
    @Value("${security.access.ip}")
    private String accessIp;
}
