package com.seojin.batch.sys.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SecurityConfig
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private CustomAppConfig config;

    @Autowired
    public void setConfig(CustomAppConfig config) {
        this.config = config;
    }

    /**
     * SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { // NOPMD
        http.authorizeRequests().antMatchers("/api/**").access(hasRoleAndIpAddress());
        http.authorizeRequests().antMatchers("/actuator/**").permitAll();
        http.csrf().disable();

        return http.build();
    }

    private String hasRoleAndIpAddress() {
        String roleAndIps = getRole() + " and (" + getIpAddress() + ")";
        log.debug("hasRoleAndIpAddress::{}", roleAndIps);

        return roleAndIps;
    }

    private String getRole() {
        return "isAnonymous()";
    }

    private String getIpAddress() {
        String ips = config.getAccessIp();
        if (ips == null) {
            ips = "127.0.0.1,0:0:0:0:0:0:0:1";
        }
        List<String> accessIpList = Arrays.asList(ips.split("\\s*,\\s*"));

        return accessIpList.stream().collect(Collectors.joining("') or hasIpAddress('", "hasIpAddress('", "')"));
    }
}
