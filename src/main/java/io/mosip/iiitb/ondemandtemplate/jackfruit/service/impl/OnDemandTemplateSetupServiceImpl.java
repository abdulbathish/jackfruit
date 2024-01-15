package io.mosip.iiitb.ondemandtemplate.jackfruit.service.impl;

import io.mosip.iiitb.ondemandtemplate.jackfruit.service.OnDemandTemplateSetupService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
// Why is this import not working ?
import io.mosip.credential.request.generator.entity.CredentialEntity;

@RefreshScope
@Component
public class OnDemandTemplateSetupServiceImpl implements OnDemandTemplateSetupService {
    public String dummyMethod(String vid) {
        return "";
    }
}
