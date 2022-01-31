package com.PIMCS.PIMCS.config;

import com.PIMCS.PIMCS.service.UserAuthService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

/*
    스프링 세큐리티
    로그인 및 권한
    2022.01.22

 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserAuthService userAuthService;



    @Autowired
    private DataSource dataSource;
    @Override
    public void configure(WebSecurity web) { // 4
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http

                .authorizeRequests()
                    .antMatchers( "/auth/signUp","/").permitAll() //permitAll이 있을시 로그인없이도 접근가능
                    .antMatchers("/hello","/home","auth/update").hasRole("User")
                    .antMatchers("/management/company/worker").hasRole("UserManagement")
                    .antMatchers("/mat/create","/mat/update","/mat/delete").hasRole("MatMangement")
                    .antMatchers("/product/create","/product/update","/product/delete").hasRole("MatMangement")
                    .anyRequest().authenticated() //나머지 요청들은 권한의 종류에 상관 없이 권한이 있어야 접근 가능(로그인해야함)
                .and()
                .formLogin()
                    .loginPage("/auth/login")
                    .defaultSuccessUrl("/hello")
                    .usernameParameter("email")
                    .permitAll()
//                    .failureUrl("/auth/login")
                .and()
                    .logout()
                    .logoutUrl("/auth/logout")
                    .logoutSuccessUrl("/auth/login")
                    .deleteCookies("JSESSIONID","remember-me")
                    .permitAll();


        http.exceptionHandling().accessDeniedPage("/noneRole");
    }

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth)
//            throws Exception {
//        auth.jdbcAuthentication()
//                .dataSource(dataSource)//properties에서 설정한 데이터소스를 갖고 인증해줌
//                .passwordEncoder(passwordEncoder())  //passwordEncoder를 등록하고 passwordEncoder메소드를 넘겨주면 스프링에서 Bean으로 관리하고 데이터베이스에 save할때 알아서 암호화를 해줌
//                .usersByUsernameQuery("select email,password,enabled " //순서를 맞춰야한다
//                        + "from user "
//                        + "where email = ?")  //authentication 로그인 관련 authorization 권한관련
//                .authoritiesByUsernameQuery("select user_email,name,mat_management,category_management,user_management "
//                        + "from role inner join user on role.user_email = user.email "
//                        + "where email = ?");
//    }
//    @Lazy //오류발생 @Bean으로 할시 순환참조 오류 발생 https://hungrydiver.co.kr/bbs/detail/develop?id=90
//    public PasswordEncoder passwordEncoder() { //패스워드 암호화 설정
//        return new BCryptPasswordEncoder();
//    }

//    @Bean //임시 유저기 때무에 제거
//    @Override
//    public UserDetailsService userDetailsService() {
//        UserDetails user =
//                User.withDefaultPasswordEncoder()
//                        .username("root")
//                        .password("11")
//                        .roles("User","admin")
//                        .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userAuthService)
                .passwordEncoder(passwordEncoder());
    }
}

