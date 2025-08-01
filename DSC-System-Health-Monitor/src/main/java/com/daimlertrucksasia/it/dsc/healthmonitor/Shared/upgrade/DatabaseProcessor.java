package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.upgrade;



public interface DatabaseProcessor {
    default void preProcessDatabase(org.hibernate.boot.Metadata metadata,
                                    org.hibernate.engine.spi.SessionFactoryImplementor sessionFactory,
                                    org.hibernate.service.spi.SessionFactoryServiceRegistry serviceRegistry) {}

    default void postProcessDatabase(org.hibernate.boot.Metadata metadata,
                                     org.hibernate.engine.spi.SessionFactoryImplementor sessionFactory) {}
}

