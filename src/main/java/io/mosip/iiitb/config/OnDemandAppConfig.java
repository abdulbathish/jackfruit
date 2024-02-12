package io.mosip.iiitb.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
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



}

// sensitive information should always be passed in ENV
//
