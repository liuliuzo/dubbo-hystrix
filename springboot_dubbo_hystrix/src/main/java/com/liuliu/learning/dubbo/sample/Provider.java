package com.liuliu.learning.dubbo.sample;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Provider {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"META-INF/spring/provider.xml"});
        context.start();
        System.in.read();
    }
}
