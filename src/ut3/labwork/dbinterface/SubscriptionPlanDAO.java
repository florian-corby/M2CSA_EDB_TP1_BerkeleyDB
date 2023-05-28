package ut3.labwork.dbinterface;

import ut3.labwork.data.Client;
import ut3.labwork.data.SubscriptionPlan;

/**
 * The client data access object wraps the logic for persisting ticketing
 * log messages into the underlying database.
 */
public interface SubscriptionPlanDAO extends AutoCloseable {

	String saveFormula(SubscriptionPlan plan) throws Exception;

	Client getFormula(String planId) throws Exception;
}
