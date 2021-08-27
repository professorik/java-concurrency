package bsa.java.concurrency.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author professorik
 * @created 28/06/2021 - 17:12
 * @project concurrency
 */

@Component
@Slf4j
public class Logger implements Filter{
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        log.info("Request: [{} {}]", httpRequest.getMethod(), httpRequest.getRequestURI());
        chain.doFilter(request, response);
        if (httpResponse.getStatus() == 500) {
            log.info("Response: [Server error]");
        }else if (httpResponse.getContentType() == null) {
            log.info("Response: [{}]", httpResponse.getStatus());
        }else {
            log.info("Response: [{} {}]", httpResponse.getStatus(), httpResponse.getContentType());
        }
    }
}
