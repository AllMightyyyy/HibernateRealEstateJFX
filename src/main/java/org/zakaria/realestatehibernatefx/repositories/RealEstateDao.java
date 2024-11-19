package org.zakaria.realestatehibernatefx.repositories;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zakaria.realestatehibernatefx.model.RealEstate;
import org.zakaria.realestatehibernatefx.utility.HibernateUtil;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for RealEstate entities.
 */
public class RealEstateDao {

    private static final Logger logger = LoggerFactory.getLogger(RealEstateDao.class);

    /**
     * Saves a new RealEstate entity to the database.
     *
     * @param realEstate the RealEstate entity to save
     * @return true if successful, false otherwise
     */
    public boolean saveRealEstate(RealEstate realEstate) {
        System.out.println("SAVING REAL ESTATE : "  + realEstate.toString());
        return performTransaction(session -> session.persist(realEstate));
    }

    /**
     * Updates an existing RealEstate entity in the database.
     *
     * @param realEstate the RealEstate entity to update
     * @return true if successful, false otherwise
     */
    public boolean updateRealEstate(RealEstate realEstate) {
        return performTransaction(session -> session.merge(realEstate));
    }

    /**
     * Deletes a RealEstate entity from the database.
     *
     * @param realEstate the RealEstate entity to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteRealEstate(RealEstate realEstate) {
        return performTransaction(session -> session.remove(realEstate));
    }

    /**
     * Retrieves a RealEstate entity by its ID.
     *
     * @param id the ID of the RealEstate entity
     * @return the RealEstate entity if found, null otherwise
     */
    public RealEstate getRealEstateById(int id) {
        try (Session currentSession = HibernateUtil.getSession()) {
            return currentSession.get(RealEstate.class, id);
        } catch (Exception e) {
            logException(e);
            return null;
        }
    }

    /**
     * Deletes a RealEstate entity by its ID.
     *
     * @param id the ID of the RealEstate entity to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteRealEstateById(int id) {
        RealEstate searchedRealEstate = getRealEstateById(id);
        if (searchedRealEstate == null) {
            return false;
        }
        return deleteRealEstate(searchedRealEstate);
    }

    /**
     * Retrieves all RealEstate entities from the database.
     *
     * @return a list of RealEstate entities
     */
    public List<RealEstate> getAllRealEstates() {
        try (Session currentSession = HibernateUtil.getSession()) {
            return currentSession.createQuery("from RealEstate", RealEstate.class).list();
        } catch (Exception e) {
            logException(e);
            return null;
        }
    }

    /**
     * Retrieves a RealEstate entity by its name.
     *
     * @param name the name of the RealEstate entity
     * @return an Optional containing the RealEstate entity if found, empty otherwise
     */
    public Optional<RealEstate> getRealEstateByName(String name) {
        try (Session currentSession = HibernateUtil.getSession()) {
            return currentSession.createQuery("FROM RealEstate i WHERE i.realEstateName = :name", RealEstate.class)
                    .setParameter("name", name)
                    .uniqueResultOptional();
        } catch (Exception e) {
            logException(e);
            return Optional.empty();
        }
    }

    /**
     * Performs a transaction with the provided action.
     *
     * @param action the action to perform within the transaction
     * @return true if the transaction was successful, false otherwise
     */
    private boolean performTransaction(TransactionConsumer action) {
        Transaction tx = null;
        try (Session currentSession = HibernateUtil.getSession()) {
            tx = currentSession.beginTransaction();
            action.accept(currentSession);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logException(e);
            return false;
        }
    }

    /**
     * Logs exceptions using SLF4J.
     *
     * @param e the exception to log
     */
    private void logException(Exception e) {
        logger.error("An exception occurred: ", e);
    }

    /**
     * Functional interface for transaction actions.
     */
    @FunctionalInterface
    private interface TransactionConsumer {
        void accept(Session session) throws Exception;
    }
}
