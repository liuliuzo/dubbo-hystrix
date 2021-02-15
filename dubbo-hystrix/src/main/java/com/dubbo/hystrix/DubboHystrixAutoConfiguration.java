package com.dubbo.hystrix;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({AbstractHystrixConfigBuilder.class, DefaultHystrixConfigBuilder.class, HystrixConfigBuilderFactory.class})
public class DubboHystrixAutoConfiguration {

    @Autowired
    private ApplicationContext context;

    @Bean
    @ConditionalOnBean(name = "hystrixConfigBuilderFactory")
    public AbstractHystrixConfigBuilder hystrixConfigBuilder() {
        HystrixConfigBuilderFactory factory = (HystrixConfigBuilderFactory) context.getBean("hystrixConfigBuilderFactory");
        return factory.getHystrixConfigBuilder();
    }

    @Bean
    @ConditionalOnMissingBean(name = "hystrixConfigBuilder")
    public AbstractHystrixConfigBuilder defaultHystrixConfigBuilder() {
        return new DefaultHystrixConfigBuilder();
    }

    @Bean
    public HystrixMetricsStreamServlet hystrixMetricsStreamServlet() {
        return new HystrixMetricsStreamServlet();
    }

    @Bean
    public ServletRegistrationBean registration(HystrixMetricsStreamServlet servlet) {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.setServlet(servlet);
        registrationBean.setEnabled(true);//是否启用该registrationBean
        registrationBean.addUrlMappings("/hystrix.stream");
        return registrationBean;
    }
}
