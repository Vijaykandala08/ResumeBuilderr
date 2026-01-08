package com.spring.resumebuilder.security;

import com.spring.resumebuilder.model.User;
import com.spring.resumebuilder.respository.Userrespository;
import com.spring.resumebuilder.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final Userrespository  userrespository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null ;
        String userId = null;
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
            try {
                userId = jwtUtils.getUserIdFromToken(token);
            } catch(Exception e) {
                log.info("Token is not valid/available");
            }
        }
        //
        if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if(jwtUtils.validateToken(token) && !jwtUtils.isTokenExpired(token)){
                    User user = userrespository.findById(userId)
                            .orElseThrow(()-> new UsernameNotFoundException("User not found"));
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user,null,new ArrayList<>());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }catch (Exception e) {
                log.error("Exception occurred while validating the token");

            }
        }
        filterChain.doFilter(request, response);

    }
}
