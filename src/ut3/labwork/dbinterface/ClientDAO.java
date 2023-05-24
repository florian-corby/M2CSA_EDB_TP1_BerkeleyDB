package ut3.labwork.dbinterface;

import ut3.labwork.data.Client;

/**
 * The client data access object wraps the logic for persisting ticketing
 * log messages into the underlying database.
 */
public interface ClientDAO extends AutoCloseable {

	Long saveClient(Client client) throws Exception;

	Client getClient(Long clientId) throws Exception;
}
