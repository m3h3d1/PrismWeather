package com.mehedi.prismweather.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String requestId = UUID.randomUUID().toString();

        // Add the request ID as a header (optional)
        httpResponse.addHeader("X-Request-Id", requestId);

        // Attach the request ID to the request attributes
        httpRequest.setAttribute("requestId", requestId);

        chain.doFilter(servletRequest, servletResponse);
    }
}
