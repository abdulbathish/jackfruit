package io.mosip.iiitb.repository;

import com.google.inject.Inject;
import io.mosip.iiitb.config.OnDemandAppConfig;
import io.mosip.iiitb.entity.UinHashSaltEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class UinHashSaltRepository {
    private final EntityManagerFactory emf;
    private final EntityManager em;

    @Inject
    public UinHashSaltRepository(
            OnDemandAppConfig config
    ) {

        System.out.printf("On demand app config = %s", config.dbName());
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", "jdbc:postgresql://qa3.mosip.net:30090/mosip_idrepo");
        properties.put("jakarta.persistence.jdbc.user", "postgres");
        properties.put("jakarta.persistence.jdbc.password", "mosip123");

        emf = Persistence.createEntityManagerFactory(
                "myPU",
                properties
        );
        em = emf.createEntityManager();
    }

    public UinHashSaltEntity findById(int id) {
        return em.find(UinHashSaltEntity.class, id);
    }

    public void close() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
