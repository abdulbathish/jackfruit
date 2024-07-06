package io.mosip.iiitb.utils;

import com.google.inject.Inject;
import io.mosip.iiitb.entity.UinHashSaltEntity;
import io.mosip.iiitb.repository.UinHashSaltRepository;
import io.mosip.iiitb.utils.HMACUtil2;
import io.mosip.kernel.core.util.HMACUtils2;
import org.slf4j.Logger;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

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
        String salt;
        try {
            int maxLen = 3;
            int modulo = calculateModulo(vid, maxLen);
            salt = getSaltFromDB(modulo);
        } catch (NoSuchAlgorithmException e) {
            logger.debug("Failed to calculate modulo due to missing algorithm: ", e);
            return null;
        }
        return salt;
    }

    private int calculateModulo(String input, int maxLength) throws NoSuchAlgorithmException {
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
            logger.debug("salt entity not found");
        else
            logger.debug(String.format(
                    "salt = %s", saltEntity.getSalt()
            ));

        return saltEntity != null ? saltEntity.getSalt() : null;
    }

}
