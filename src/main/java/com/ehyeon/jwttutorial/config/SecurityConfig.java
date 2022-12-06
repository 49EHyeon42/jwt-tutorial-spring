package com.ehyeon.jwttutorial.config;

import com.ehyeon.jwttutorial.jwt.JwtAccessDeniedHandler;
import com.ehyeon.jwttutorial.jwt.JwtAuthenticationEntryPoint;
import com.ehyeon.jwttutorial.jwt.JwtSecurityConfig;
import com.ehyeon.jwttutorial.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//@EnableWebSecurity // 기본적인 웹 보안 활성화
@Configuration // EnableWebSecurity 에서 Configuration 로 변경
@EnableGlobalMethodSecurity(prePostEnabled = true) // @PreAuthorize 어노테이션을 메서드 단위로 추가히기 위해서 사용
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /*
     * 추가적인 설정 두 가지 방법
     * 1. WebSecurityConfigurer 를 implements
     * 2. WebSecurityConfigurerAdapter 를 extends -> 강의에서 사용, 더 이상 사용되지 않음
     * */

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // 의존성 주입
    public SecurityConfig(TokenProvider tokenProvider,
        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
        JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        // h2 요청과 favicon 요청 무시
        web.ignoring().antMatchers("/h2-console/**", "/favicon.ico");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*
         * 1. csrf 설정 disable
         * 2. exception 을 핸들링할 때 jwtAuthenticationEntryPoint, jwtAccessDeniedHandler 추가
         * 3. h2-console 설정 추가
         * 4. 세션을 사용하지 않기 때문에 STATELESS 설정
         * 5. hello, authenticate, signup 열림
         * 6. JwtSecurityConfig 적용
         * */

        http.csrf().disable().exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler).and().headers().frameOptions().sameOrigin()
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests().antMatchers("/api/hello").permitAll()
            .antMatchers("/api/authenticate").permitAll().antMatchers("/api/signup").permitAll()
            .anyRequest().authenticated().and().apply(new JwtSecurityConfig(tokenProvider));
    }
}
