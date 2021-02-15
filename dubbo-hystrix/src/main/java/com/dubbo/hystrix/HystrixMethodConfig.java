package com.dubbo.hystrix;

import lombok.Data;

@Data
public class HystrixMethodConfig {
    private String groupKey;

    private String commandKey;

    private String threadKey;

    private String fallbackClass;

}
