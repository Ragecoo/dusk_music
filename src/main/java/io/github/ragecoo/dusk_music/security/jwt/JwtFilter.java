package io.github.ragecoo.dusk_music.security.jwt;


import io.github.ragecoo.dusk_music.dto.userdto.AuthUser;
import io.github.ragecoo.dusk_music.model.enums.Role;
import io.github.ragecoo.dusk_music.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


/** Класс отвечающий за перехватывание http запросов и подтверждение авторизации пользователя  */
@Slf4j
@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    private CustomUserDetailsService service;


    /** Метод отвечает за фильтрацию, сохранение данных пользователя в контексте безопасности и дальнейше передачи запроса в цепочку фильтров
     * @param request Принимает HTTP запрос
     * @param response Принимает HTTP ответ
     * @param filterChain Принимает цепочку фильтров
     * @see #getTokenFromRequest(HttpServletRequest)
     * @see JwtService#validateJwtToken(String) */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        
        log.debug("=== JWT Filter processing request ===");
        log.debug("Request path: {} {}", method, requestPath);
        
        String token= getTokenFromRequest(request);
        
        if(token != null) {
            log.debug("Token found in request (length: {})", token.length());
            boolean isValid = jwtService.validateJwtToken(token);
            log.debug("Token validation result: {}", isValid);
            
            if(isValid){
                log.debug("Token is valid, setting authentication context");
                setCustomUserDetailsToSecurityContextHolder(request,token);
            } else {
                log.warn("Token is invalid for request: {} {}", method, requestPath);
            }
        } else {
            log.debug("No token found in request: {} {}", method, requestPath);
        }

        filterChain.doFilter(request,response);
    }

    /** Метод отвечающий за создание объекта аутентификации и достает из токена информацию о пользователе
     * @param request Принимает HTTP запрос
     * @param token Принимает токен
     * @see JwtService#validateJwtToken(String)
     * @see JwtService#getUsernameFromToken(String)
     * @see JwtService#getUserIdFromToken(String)
     * @see JwtService#getRoleFromToken(String)
     */
    private void setCustomUserDetailsToSecurityContextHolder(HttpServletRequest request,String token) {
        log.debug("Setting user details from token...");
        
        Long userId= jwtService.getUserIdFromToken(token);
        String username= jwtService.getUsernameFromToken(token);
        Role role= jwtService.getRoleFromToken(token);
        
        log.debug("Extracted from token - userId: {}, username: {}, role: {}", userId, username, role);

        var authorities= List.of(new SimpleGrantedAuthority("ROLE_"+role.name()));
        var principal= new AuthUser(userId,username,authorities);

        var auth= new UsernamePasswordAuthenticationToken(principal,null,authorities);
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(auth);
        log.debug("Authentication context set successfully");
    }

    /** Метод отвечающий за получения токена из HTTP запроса
     * @param request Принимает HttpServletRequest request
     * @return Возвращает строку с токеном из запроса*/
    private String getTokenFromRequest(HttpServletRequest request){
        String bearerToken= request.getHeader(HttpHeaders.AUTHORIZATION);
        log.debug("Authorization header: {}", bearerToken != null ? (bearerToken.length() > 20 ? bearerToken.substring(0, 20) + "..." : bearerToken) : "null");
        
        if(bearerToken!=null && bearerToken.startsWith("Bearer ")){
            String token = bearerToken.substring(7);
            log.debug("Extracted token (length: {})", token.length());
            return token;
        }
        log.debug("No valid Bearer token found");
        return null;
    }
}
