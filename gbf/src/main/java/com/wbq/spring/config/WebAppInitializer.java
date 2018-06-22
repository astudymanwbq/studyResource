package com.wbq.spring.config;


import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

/**
 * @author 吴璧钦
 * @date 2018/6/22 11:39
 * @Description
 **/
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{RootConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    /**
     * 上传文件配置
     * @param registration
     */
    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        String filepath="G:\\wubiqin\\project\\upload";
        Long singleMax=(long)(5*Math.pow(2,20));//5mb
        Long totalMax=(long)(10*Math.pow(2,20));//10mb
        registration.setMultipartConfig(new MultipartConfigElement(filepath,singleMax,totalMax,0));
    }
}
