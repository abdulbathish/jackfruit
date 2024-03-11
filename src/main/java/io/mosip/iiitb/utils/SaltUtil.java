package io.mosip.iiitb.utils;

import com.google.inject.Inject;
import io.mosip.iiitb.entity.UinHashSaltEntity;
import io.mosip.iiitb.repository.UinHashSaltRepository;
import org.slf4j.Logger;

import java.math.BigInteger;

public class SaltUtil {
    private final UinHashSaltRepository uinHashSaltRepository;
    private final Logger logger;
    @Inject
    public SaltUtil(
            UinHashSaltRepository uinHashSaltRepository,
            Logger logger
    ) {
        this.uinHashSaltRepository = uinHashSaltRepository;
        this.logger = logger;
    }

    public String getSaltForVid(String vid) {
        BigInteger idRepoModulo = new BigInteger("1000");
        BigInteger id = new BigInteger(vid);
        int modulo = id.mod(idRepoModulo).intValue();
        String salt = getSaltFromDB(modulo);
        return salt;
    }

    private String getSaltFromDB(int id) {
        UinHashSaltRepository uhsr = this.uinHashSaltRepository;
        UinHashSaltEntity saltEntity = uhsr.findById(id);
        if (saltEntity == null)
            logger.debug("salt entity not found");
        else
            logger.debug(String.format(
                    "salt = %s", saltEntity.getSalt()
            ));

        return saltEntity != null ? saltEntity.getSalt() : null;
    }

}
