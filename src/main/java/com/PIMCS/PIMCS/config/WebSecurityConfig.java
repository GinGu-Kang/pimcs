package com.PIMCS.PIMCS.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/","/auth/signUp").permitAll() //로그인없이 누구나 접근가능
                    .anyRequest().authenticated()       //permitAll()을 제외한 로그인해야만 볼수 있는페이지
                    .and()
                .formLogin()
                    .loginPage("/auth/login/") // 로그인이 안되었는데 다른페이지로 가주면 login 페이지로 자동 리다이렉트
                    .permitAll()
                    .and()
                .logout()
                    .permitAll();//누구나 접근할수 있다는 설정
    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)//properties에서 설정한 데이터소스를 갖고 인증해줌
                .passwordEncoder(passwordEncoder())  //passwordEncoder를 등록하고 passwordEncoder메소드를 넘겨주면 스프링에서 Bean으로 관리하고 데이터베이스에 save할때 알아서 암호화를 해줌
                .usersByUsernameQuery("select email,password, enabled " //순서를 맞춰야한다
                        + "from user "
                        + "where email = ?")  //authentication 로그인 관련 authorization 권한관련
                .authoritiesByUsernameQuery("select user_email,name,mat_management,category_management,user_management "
                        + "from role inner join user on role.user_email = user.email "
                        + "where email = ?");
    }
    @Lazy //오류발생 @Bean으로 할시 순환참조 오류 발생 https://hungrydiver.co.kr/bbs/detail/develop?id=90
    public PasswordEncoder passwordEncoder() { //패스워드 암호화 설정
        return new BCryptPasswordEncoder();
    }

//    @Bean //임시 유저기 때무에 제거
//    @Override
//    public UserDetailsService userDetailsService() {
//        UserDetails user =ㅁ
//                User.withDefaultPasswordEncoder()
//                        .username("email")
//                        .password("password")
//                        .roles("USER")
//                        .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }
}

