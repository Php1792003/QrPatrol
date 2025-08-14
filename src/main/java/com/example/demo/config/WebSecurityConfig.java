    package com.example.demo.config;

    import com.example.demo.security.CustomUserDetailsService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
    import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
    import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

    import javax.sql.DataSource;

    @Configuration
    @EnableWebSecurity
    public class WebSecurityConfig {

        @Autowired
        private DataSource dataSource;

        @Bean
        public UserDetailsService userDetailsService() {
            return new CustomUserDetailsService();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
            DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
            authProvider.setUserDetailsService(userDetailsService());
            authProvider.setPasswordEncoder(passwordEncoder());
            return authProvider;
        }

        @Bean
        public PersistentTokenRepository persistentTokenRepository() {
            JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
            tokenRepository.setDataSource(dataSource);
            return tokenRepository;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
            http
                    .authenticationProvider(daoAuthenticationProvider)
                    .csrf(csrf -> csrf
                            .ignoringRequestMatchers("/api/**","/contact","/pricing", "/about", "/violation-search")
                    )
                    .authorizeHttpRequests(authz -> authz
                            .requestMatchers("/admin/users/**").hasAuthority("ROLE_ADMIN")
                            .requestMatchers("/admin/qrcodes/**").hasAuthority("ROLE_ADMIN")
                            .requestMatchers("/admin/violations/**").hasAnyAuthority("ROLE_ADMIN", "VIEWER")
                            .requestMatchers("/admin/logs/**").hasAnyAuthority("ROLE_ADMIN", "VIEWER")
                            .requestMatchers("/viewer/**").hasAnyAuthority("ROLE_ADMIN", "VIEWER")
                            .requestMatchers("/scanner/**", "/api/scanner/**").hasAuthority("SCANNER")

                            .requestMatchers("/profile/**", "/user-avatars/**", "/dashboard").authenticated()

                            .requestMatchers(
                                    "/", "/violation-search", "/manifest.json", "/sw.js", "/login",
                                    "/css/**", "/js/**", "/img/**", "/vendor/**", "/favicon.ico", "/error",
                                    "/contact", "/pricing", "/about", "/violation-images/**"
                            ).permitAll()

                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form
                            .loginPage("/login")
                            .loginProcessingUrl("/login")
                            .defaultSuccessUrl("/dashboard", true)
                            .failureUrl("/login?error=true")
                            .permitAll()
                    )
                    .logout(logout -> logout
                            .logoutUrl("/logout")
                            .logoutSuccessUrl("/login?logout")
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID", "remember-me")
                            .permitAll()
                    )
                    .rememberMe(rememberMe -> rememberMe
                            .tokenRepository(persistentTokenRepository())
                            .userDetailsService(userDetailsService())
                            .tokenValiditySeconds(86400 * 30) // 30 ngày
                    );

            return http.build();
        }
    }