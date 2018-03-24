package com.heyferh.test;

import com.heyferh.test.rest.endpoint.AccountEndpoint;
import com.heyferh.test.rest.endpoint.TransactionEndpoint;
import dagger.Component;

import javax.inject.Singleton;

@Component(modules = {AppComponentModule.class})
@Singleton
public interface AppComponent {

    AccountEndpoint accountEndpoint();

    TransactionEndpoint transactionEndpoint();
}
