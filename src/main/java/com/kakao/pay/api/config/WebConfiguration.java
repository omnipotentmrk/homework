package com.kakao.pay.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.pay.api.interceptor.AuthInterceptor;
import com.kakao.pay.api.interceptor.TokenInterceptor;
import com.kakao.pay.api.resolver.UserArgumentResolver;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@ComponentScan("com.kakao.pay.api")
@MapperScan("com.kakao.pay.api.mapper")
public class WebConfiguration extends WebMvcConfigurationSupport {
    private final AuthInterceptor authInterceptor;
    private final TokenInterceptor tokenInterceptor;
    private final ObjectMapper customObjectMapper;
    private final UserArgumentResolver userArgumentResolver;

    public WebConfiguration(AuthInterceptor authInterceptor, TokenInterceptor tokenInterceptor, ObjectMapper customObjectMapper, UserArgumentResolver userArgumentResolver) {
        this.authInterceptor = authInterceptor;
        this.tokenInterceptor = tokenInterceptor;
        this.customObjectMapper = customObjectMapper;
        this.userArgumentResolver = userArgumentResolver;
    }

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);

        super.addArgumentResolvers(argumentResolvers);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(customObjectMapper));
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor);
        registry.addInterceptor(tokenInterceptor).excludePathPatterns("/spread-money/event");

        super.addInterceptors(registry);
    }
}
