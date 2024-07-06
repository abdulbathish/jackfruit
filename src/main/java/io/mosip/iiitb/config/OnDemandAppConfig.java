package io.mosip.iiitb.config;

import org.aeonbits.owner.Config;


@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "${runtimeOndemandAppConfigPropertiesPath}",
        "system:properties",
        "system:env",
        "classpath:ondemand-default.properties"
})
public interface OnDemandAppConfig extends Config {
    @Key("mosip.iiitb.ondemand.db.jdbc.url")
    String dbUrl();


    @Key("mosip.iiitb.ondemand.db.jdbc.user")
    String dbUsername();

    @Key("mosip.iiitb.ondemand.db.jdbc.password")
    String dbPassword();

    @Key("mosip.iiitb.ondemand.db.name")
    String dbName();

    @Config.Key("mosip.iiitb.ondemand.kafka.groupId")
    String kafkaGroupId();

    @Config.Key("mosip.iiitb.ondemand.kafka.brokers")
    String kafkaBrokers();

    @Config.Key("mosip.iiitb.ondemand.kafka.topic")
    String kafkaTopic();


    @Config.Key("mosip.iiitb.ondemand.kafka.port")
    String kafkaPort();

    @Config.Key("mosip.iiitb.ondemand.kafka.hostname")
    String kafkaHostname();

    @Key("mosip.iiitb.ondemand.regproc.appId")
    String regprocAppId();
    @Key("mosip.iiitb.ondemand.regproc.clientId")
    String regprocClientId();

    @Key("mosip.iiitb.ondemand.regproc.clientPass")
    String regprocClientPass();

    @Key("mosip.iiitb.ondemand.regproc.partnerCode")
    String regprocPartnerCode();

    @Key("mosip.iiitb.ondemand.issueCreds.appId")
    String issueCredsAppId();
    @Key("mosip.iiitb.ondemand.issueCreds.clientId")
    String issueCredsClientId();

    @Key("mosip.iiitb.ondemand.issueCreds.clientPass")
    String issueCredsClientPass();

    @Key("mosip.iiitb.ondemand.issueCreds.partnerCode")
    String issueCredsPartnerCode();

    @Key("mosip.iiitb.ondemand.mosip.server.url")
    String mosipServerUrl();
    @Key("mosip.iiitb.ondemand.auth.endpoint")
    String authEndpoint();
    @Key("mosip.iiitb.ondemand.credential.request.endpoint")
    String credentialRequestEndpoint();
    @Key("mosip.iiitb.ondemand.credential.issuance.endpoint")
    String credentialIssuanceEndpoint();
    @Key("mosip.iiitb.ondemand.keymanager.tokenid.endpoint")
    String keyManagerTokenIdEndpoint();
    @Key("mosip.iiitb.ondemand.credential-request-generator.user")
    String credentialRequestGeneratorUser();

    @Key("mosip.iiitb.ondemand.http-requester.timeout-in-secs")
    Integer httpRequestTimeoutInSecs();

    @Key("mosip.iiitb.ondemand.auth.credentialType")
    String authCredentialType();

    @Key("mosip.iiitb.ondemand.auth.recipient")
    String authRecipient();
    @Key("mosip.iiitb.saltRepoModulo")
    Integer saltRepoModulo();

    @Key("mosip.iiitb.utils.saltutil.len")
    Integer saltUtilLen();

    @Key("mosip.iiitb.ondemand.credentialrequest.additionaldata.expiryTimestamp")
    String expiryTimestamp();

    @Key("mosip.iiitb.ondemand.persistance-unit.name")
    String dbPersistanceUnitName();

    @Key("mosip.iiitb.ondemand.message-private-key-pathname")
    String privateKeyFileLocation();
}

// sensitive information should always be passed in ENV
//
