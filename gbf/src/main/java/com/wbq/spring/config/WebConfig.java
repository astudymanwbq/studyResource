package com.wbq.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author 吴璧钦
 * @date 2018/6/22 13:14
 * @Description
 **/
@Configuration
@ComponentScan(value = "com.*", includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,
        value = Controller.class)})
@EnableWebMvc
@EnableAsync
public class WebConfig extends AsyncConfigurerSupport {

    /**
     * 初始化视图解析器
     *
     * @return
     */
    @Bean(name = "internalResourceViewResolver")
    public ViewResolver initViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/pages/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    /**
     * 初始化RequestMappingHandlerAdapter 加载http的json转换器
     *
     * @return
     */
    @Bean(name = "requestMappingHandlerAdapter")
    public HandlerAdapter initRequestMappingHandlerAdapter() {
        //创建RequestMappingHandlerAdapter适配器
        RequestMappingHandlerAdapter rmha = new RequestMappingHandlerAdapter();
        //http json转换器
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter =
                new MappingJackson2HttpMessageConverter();
        MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(mediaType);
        //加入转换器支持的类型
        jackson2HttpMessageConverter.setSupportedMediaTypes(mediaTypes);
        //往适配器中加入json转换器
        rmha.getMessageConverters().add(jackson2HttpMessageConverter);
        return rmha;
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(200);
        taskExecutor.initialize();
        return taskExecutor;
    }
}
