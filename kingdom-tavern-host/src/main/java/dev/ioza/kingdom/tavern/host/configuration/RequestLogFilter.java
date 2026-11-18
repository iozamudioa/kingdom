package dev.ioza.kingdom.tavern.host.configuration;

import java.io.IOException;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestLogFilter extends OncePerRequestFilter {

    private static final Logger log = Logger.getLogger(RequestLogFilter.class.getName());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        log.info(request.getMethod() + " " + request.getRequestURI());

        filterChain.doFilter(request, response);
    }
}
