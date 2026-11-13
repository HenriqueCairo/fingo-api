
package com.fingo.api.config;

import com.fingo.api.security.ApiKeyAuthenticationFilter;
import com.fingo.api.security.ApiKeyService;
import com.fingo.api.security.RateLimitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fingo.api.security.IdempotencyFilter;

@Configuration
public class ApiKeyConfig {

    @Bean
    public FilterRegistrationBean<ApiKeyAuthenticationFilter>
    apiKeyFilter(ApiKeyService service) {

        var registration =
                new FilterRegistrationBean<ApiKeyAuthenticationFilter>();

        registration.setFilter(
                new ApiKeyAuthenticationFilter(service)
        );

        registration.addUrlPatterns("/*");
        registration.setOrder(10);

        return registration;
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter>
    rateLimitFilter() {

        var registration =
                new FilterRegistrationBean<RateLimitFilter>();

        registration.setFilter(new RateLimitFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(20);

        return registration;
    }

    @Bean
    public FilterRegistrationBean<IdempotencyFilter>
    idempotencyFilter() {

        var registration =
                new FilterRegistrationBean<IdempotencyFilter>();

        registration.setFilter(new IdempotencyFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(30);

        return registration;
    }
}


