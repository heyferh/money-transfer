package com.heyferh.test;

import com.heyferh.test.rest.endpoint.AccountEndpoint;
import com.heyferh.test.rest.endpoint.TransactionEndpoint;
import dagger.Component;

@Component(modules = {AppComponentModule.class})
public interface AppComponent {

    AccountEndpoint accountEndpoint();

    TransactionEndpoint transactionEndpoint();
}
