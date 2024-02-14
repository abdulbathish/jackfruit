package io.mosip.iiitb;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.mosip.iiitb.config.OnDemandAppConfig;
import io.mosip.iiitb.consumers.OnDemandTemplateExtractionConsumerImpl;
import io.mosip.iiitb.lib.ApiRequestService;
import io.mosip.iiitb.lib.MessageBrokerWrapper;
import io.mosip.iiitb.repository.UinHashSaltRepository;
import io.mosip.iiitb.utils.SaltUtil;
import org.aeonbits.owner.ConfigFactory;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ApiRequestService.class).in(Singleton.class);
        bind(UinHashSaltRepository.class).in(Singleton.class);
        bind(SaltUtil.class).in(Singleton.class);
        bind(OnDemandTemplateExtractionConsumerImpl.class).in(Singleton.class);
        bind(MessageBrokerWrapper.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public OnDemandAppConfig provideOnDemandConfig() {
        return ConfigFactory.create(OnDemandAppConfig.class);
    }
}
