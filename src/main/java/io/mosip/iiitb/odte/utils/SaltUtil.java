package io.mosip.iiitb.odte.utils;

import com.google.inject.Inject;
import io.mosip.iiitb.odte.entity.UinHashSaltEntity;
import io.mosip.iiitb.odte.config.OnDemandAppConfig;
import io.mosip.iiitb.odte.repository.UinHashSaltRepository;
import io.mosip.kernel.core.util.HMACUtils2;
import org.slf4j.Logger;


import java.security.NoSuchAlgorithmException;

public class SaltUtil {
    private final UinHashSaltRepository uinHashSaltRepository;
    private final OnDemandAppConfig config;
    private final Logger logger;

    @Inject
    public SaltUtil(
            UinHashSaltRepository uinHashSaltRepository,
            OnDemandAppConfig config,
            Logger logger
    ) {
        this.uinHashSaltRepository = uinHashSaltRepository;
        this.logger = logger;
        this.config = config;
    }


    public String getSaltForVid(String vid) {
        try {
            int modulo = calculateModulo(vid);
            String salt = getSaltFromDB(modulo);
            return salt;
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to calculate modulo due to missing algorithm: ", e);
            return null;
        }
    }

    public int calculateModulo(String input) throws NoSuchAlgorithmException {
        Integer maxLength = config.saltUtilLen();
        String hash = HMACUtils2.digestAsPlainText(input.getBytes());
        int hexToDecimal = convertSubstringToInt(hash, maxLength, 16);
        String decimalStr = String.valueOf(hexToDecimal);
        return convertSubstringToInt(decimalStr, maxLength, 10);
    }

    private int convertSubstringToInt(String input, int maxLength, int radix) {
        String substring = extractSubstring(input, maxLength);
        return Integer.parseInt(substring, radix);
    }

    private String extractSubstring(String input, int maxLength) {
        int length = input.length();
        return length > maxLength ? input.substring(length - maxLength) : input;
    }

    private String getSaltFromDB(int id) {
        UinHashSaltRepository uhsr = this.uinHashSaltRepository;
        UinHashSaltEntity saltEntity = uhsr.findById(id);
        if (saltEntity == null)
            logger.error("salt entity not found");
        else
            logger.error(String.format(
                    "salt = %s", saltEntity.getSalt()
            ));

        return saltEntity != null ? saltEntity.getSalt() : null;
    }

}
