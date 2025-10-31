package io.github.ragecoo.dusk_music.config;


import io.github.ragecoo.dusk_music.security.CustomUserDetailsService;
import io.github.ragecoo.dusk_music.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityConfiguration {

    private final JwtFilter jwtFilter;

    /** Bean который возвращает кастомный UserDetailsService
     * @param impl Принимает наш CustomUSerDetailsService
     * @return Возвращает CustomUserDetailsService*/
    @Bean
    public UserDetailsService userDetailsService(CustomUserDetailsService impl) {

        return impl;
    }


    /** CORS конфигурация
     * @return CorsConfigurationSource */
    @Primary
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration cfg= new CorsConfiguration();
        //dev
        cfg.setAllowedOrigins(List.of("*"));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization","Content-type"));
        //dev
        cfg.setAllowCredentials(false);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source= new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",cfg);

        return source;

    }



    /** Создает bean необходимый механизму безопасности для аутентификации  */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception{
        return cfg.getAuthenticationManager();
    }




    /** Bean реализующий хэширование паролей */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();

    }
}
