package org.zakaria.realestatehibernatefx.utility;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.zakaria.realestatehibernatefx.model.RealEstate;

/**
 * Utility class for managing Hibernate SessionFactory and Sessions.
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            configuration.addAnnotatedClass(RealEstate.class);
            sessionFactory = configuration.buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed: " + e);
        }
    }

    /**
     * Provides a new Hibernate Session.
     *
     * @return a new Session
     */
    public static Session getSession(){
        return sessionFactory.openSession();
    }

    /**
     * Shuts down the SessionFactory, releasing all resources.
     */
    public static void shutdown(){
        if (sessionFactory != null){
            sessionFactory.close();
        }
    }
}
