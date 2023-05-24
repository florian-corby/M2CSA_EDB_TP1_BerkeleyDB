package ut3.labwork.dbinterface;

import ut3.labwork.data.Client;

/**
 * The client data access object wraps the logic for persisting ticketing
 * log messages into the underlying database.
 */
public interface ClientDAO extends AutoCloseable {

	String saveClient(Client client) throws Exception;

	Client getClient(String clientId) throws Exception;
}
