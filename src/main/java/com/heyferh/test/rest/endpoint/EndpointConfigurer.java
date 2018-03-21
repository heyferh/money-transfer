package com.heyferh.test.rest.endpoint;

import spark.Service;

public interface EndpointConfigurer {
    void configure(Service service);
}
