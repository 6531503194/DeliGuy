package com.deliguy.restaurent_service.config;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayRequestFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String gatewayHeader = httpRequest.getHeader("X-GATEWAY-INTERNAL");

        if (!"true".equals(gatewayHeader)) {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }
}
