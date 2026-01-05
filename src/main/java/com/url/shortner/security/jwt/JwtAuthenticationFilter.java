package com.url.shortner.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.url.shortner.dto.common.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String uri = request.getRequestURI();
            if (uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs")) {
                filterChain.doFilter(request, response);
                return;
            }
            String jwt = jwtUtils.getJwtFromRequest(request);
            if(jwt != null && jwtUtils.validateToken(jwt)) {
                String userName = jwtUtils.getUserNameFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                if(userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token has expired");
            return;

        } catch (RuntimeException ex) {
            // Your exception is wrapped in RuntimeException
            if (ex.getCause() instanceof MalformedJwtException ||
                    ex.getCause() instanceof io.jsonwebtoken.MalformedJwtException) {
                log.error("Malformed JWT token (wrapped): {}", ex.getMessage());
                sendErrorResponse(response, HttpStatus.FORBIDDEN, "Invalid token format");
                return;
            }
            throw ex; // Re-throw if it's a different RuntimeException

        } catch (Exception ex) {
            log.error("Exception while filtering token: {}", ex.getMessage(), ex);
            sendErrorResponse(response, HttpStatus.FORBIDDEN, "Authentication failed");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(new ApiResponse(false, null, message)));
    }
}
