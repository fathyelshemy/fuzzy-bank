package com.fuzzy.bank.filters;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

@Component
@Slf4j
public class RequestLogFilter implements Filter  {

    private final String regex;

    public RequestLogFilter() {
        this.regex = Stream
                .of(".*/v2/api-docs", ".*/swagger-resources", ".*/swagger-ui.html", ".*/webjars")
                .reduce((str1, str2) -> String.format("%s|%s", str1, str2))
                .map(str -> String.format("(%s).*", str))
                .orElse("");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // don't need to implement init method

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String path = ((HttpServletRequest) request).getServletPath();

        if (!path.matches(this.regex)) {
            request = new CustomHttpServletRequestWrapper((HttpServletRequest) request);
            response = new CustomHttpServletResponseWrapper((HttpServletResponse) response);

            if (((HttpServletRequest) request).getHeader("host") != null) {
                final String host = ((HttpServletRequest) request).getHeader("host");
                MDC.put("host", host);
            }

            /* request info */
            final String method = ((HttpServletRequest) request).getMethod();
            final String url = ((HttpServletRequest) request).getRequestURL().toString();
            log.info("REST, request, {}, {}", method, url);

            /* request headers */
            final List<String> requestHeaders = new LinkedList<>();
            final Enumeration<String> headerNames = ((HttpServletRequest) request).getHeaderNames();
            while (headerNames.hasMoreElements()) {
                final String headerName = headerNames.nextElement();
                if (!headerName.equals("authorization")) {
                    final String headerValue = ((HttpServletRequest) request).getHeader(headerName);
                    requestHeaders.add(String.format("%s: %s", headerName, headerValue));
                }
            }
            log.info("REST, headers, {}", requestHeaders.toString());

            /* request data */
            final String requestData = ((CustomHttpServletRequestWrapper) request).getBody().replaceAll("\n", "");
            final StringBuilder stringBuilder = new StringBuilder();
            if (request.getParameterMap().size() > 0) {
                stringBuilder.append("{");
                request.getParameterMap().forEach((key, value) -> stringBuilder.append(key).append(": ").append(value[0]).append(", "));
                stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
                stringBuilder.append("}");
            }
            log.info("REST, data, {}, {}", requestData, stringBuilder.toString());

            /* response */
            try {
                chain.doFilter(request, response);
                response.flushBuffer();
            }finally {
                final String removingWhitespacesRegex = "\n";

                final Integer status = ((CustomHttpServletResponseWrapper) response).getStatus();
                final String contentType = response.getContentType();
                String responseData = ((CustomHttpServletResponseWrapper) response).getBody().replaceAll(removingWhitespacesRegex, "");
                if (status < 400)
                    log.info("REST, response, {}, {}, {}", status, contentType, responseData);
                else
                    log.error("REST, response, {}, {}, {}", status, contentType, responseData);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    // don't need to implement destroy method
    }
}
