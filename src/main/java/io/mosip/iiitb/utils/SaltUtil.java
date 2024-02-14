package io.mosip.iiitb.utils;

import com.google.inject.Inject;
import io.mosip.iiitb.entity.UinHashSaltEntity;
import io.mosip.iiitb.repository.UinHashSaltRepository;

import java.math.BigInteger;

public class SaltUtil {
    private final UinHashSaltRepository uinHashSaltRepository;
    @Inject
    public SaltUtil(UinHashSaltRepository uinHashSaltRepository) {
        this.uinHashSaltRepository = uinHashSaltRepository;
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
            System.out.println("salt entity not found");
        else
            System.out.printf("salt = %s", saltEntity.getSalt());

        return saltEntity != null ? saltEntity.getSalt() : null;
    }

}
