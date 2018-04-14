package com.heyferh.test;

import com.heyferh.test.rest.RestContext;
import com.heyferh.test.rest.endpoint.AccountEndpoint;
import com.heyferh.test.rest.endpoint.TransactionEndpoint;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class, MetricsConfig.class);
        RestContext restContext = new RestContext();
        restContext.addEndpoint(ctx.getBean(AccountEndpoint.class));
        restContext.addEndpoint(ctx.getBean(TransactionEndpoint.class));
        restContext.init();
    }
}
