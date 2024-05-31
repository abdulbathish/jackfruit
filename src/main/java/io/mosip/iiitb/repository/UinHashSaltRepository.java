package io.mosip.iiitb.repository;

import com.google.inject.Inject;
import io.mosip.iiitb.config.OnDemandAppConfig;
import io.mosip.iiitb.entity.UinHashSaltEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UinHashSaltRepository {
    private final Logger logger;
    private final EntityManagerFactory emf;
    private final EntityManager em;
    private final UinHashSaltEntity[] dbValuesCache;

    @Inject
    public UinHashSaltRepository(
            OnDemandAppConfig config,
            Logger loggerArg
    ) {
        logger = loggerArg;
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", config.dbUrl());
        properties.put("jakarta.persistence.jdbc.user", config.dbUsername());
        properties.put("jakarta.persistence.jdbc.password", config.dbPassword());

        emf = Persistence.createEntityManagerFactory(
                config.dbPersistanceUnitName(),
                properties
        );
        em = emf.createEntityManager();
        dbValuesCache = new UinHashSaltEntity[1000];
        initCache();
    }

    public UinHashSaltEntity findById(int id) {
        if (id > -1 && id < 1000) {
            return dbValuesCache[id];
        }
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

    private void initCache() {
        logger.debug("Initializing Cache");
        TypedQuery<UinHashSaltEntity> query = em.createQuery(
                "SELECT u FROM UinHashSaltEntity u",
                UinHashSaltEntity.class
        );
        List<UinHashSaltEntity> results = query.getResultList();
        int cached_count = 0;
        for (UinHashSaltEntity record: results) {
            int id = record.getId();
            if (id < 1000) {
                ++cached_count;
                dbValuesCache[id] = record;
            }
        }
        logger.debug(
                "Caching completed, Cached {} entries",
                cached_count
        );
    }
}
