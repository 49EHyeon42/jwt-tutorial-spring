package com.ehyeon.jwttutorial.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@EnableWebSecurity // 기본적인 웹 보안 활성화
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /*
     * 추가적인 설정 두 가지 방법
     * 1. WebSecurityConfigurer 를 implements
     * 2. WebSecurityConfigurerAdapter 를 extends -> 강의에서 사용, 더 이상 사용되지 않음
     * */

    @Override
    public void configure(WebSecurity web) {
        // h2 요청과 favicon 요청 무시
        web.ignoring()
            .antMatchers("/h2-console/**", "/favicon.ico");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests() // 접근 제한 설정
            .antMatchers("/api/hello").permitAll() // 인증 없이 접근 허용
            .anyRequest().authenticated();
    }
}
