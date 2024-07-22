package io.mosip.iiitb.odte;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.mosip.iiitb.odte.config.OnDemandAppConfig;
import io.mosip.iiitb.odte.lib.MessageBrokerWrapper;

public class App {
    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new AppModule());
        OnDemandAppConfig config = injector.getInstance(OnDemandAppConfig.class);
        MessageBrokerWrapper broker = injector.getInstance(MessageBrokerWrapper.class);
        broker.start();
    }
}