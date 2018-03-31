package com.heyferh.test.rest;

import com.heyferh.test.rest.endpoint.EndpointConfigurer;
import spark.Service;

import static com.heyferh.test.util.EntityManagerHelper.closeEntityManagerFactory;

public class RestContext {

    private final Service service;

    public RestContext() {
        this.service = Service.ignite();
    }

    public void addEndpoint(EndpointConfigurer endpoint) {
        endpoint.configure(service);
    }

    public void init() {
        service.after((request, response) -> response.type("application/json"));
    }

    public void stop() {
        closeEntityManagerFactory();
        this.service.stop();
    }
}
