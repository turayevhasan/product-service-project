package uz.pdp.conf.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

    public final String[] WHITE_LIST = {
            "/",
            "/home",
            "/auth/login",
            "/auth/register",
            "/auth/register/confirm/account/*"
    };

    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationHandler customAuthenticationHandler;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    public SecurityConfiguration(CustomUserDetailsService userDetailsService, CustomAuthenticationHandler customAuthenticationHandler, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.customAuthenticationHandler = customAuthenticationHandler;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.userDetailsService(userDetailsService);

        http.csrf().disable();
        http.authorizeRequests()
                .requestMatchers(WHITE_LIST).permitAll() // open Urls
                .anyRequest()
                .authenticated();

        http.formLogin()
                .loginPage("/auth/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .failureHandler(customAuthenticationHandler)
                .successHandler(customAuthenticationSuccessHandler);

        http.logout()
                .logoutUrl("/auth/logout")
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", HttpMethod.POST.name()))
                .logoutSuccessUrl("/auth/login");

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
