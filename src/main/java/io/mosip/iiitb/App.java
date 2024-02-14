package io.mosip.iiitb;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.mosip.iiitb.lib.MessageBrokerWrapper;

public class App {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());
        MessageBrokerWrapper mbroker = injector.getInstance(MessageBrokerWrapper.class);
        mbroker.start();
    }
}