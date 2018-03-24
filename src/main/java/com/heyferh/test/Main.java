package com.heyferh.test;

import com.heyferh.test.rest.RestContext;

public class Main {
    public static void main(String[] args) {

        AppComponent appComponent = DaggerAppComponent.create();

        RestContext restContext = new RestContext();
        restContext.addEndpoint(appComponent.accountEndpoint());
        restContext.addEndpoint(appComponent.transactionEndpoint());
        restContext.init();
    }
}
