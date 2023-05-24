package ut3.labwork.dbinterface;

import ut3.labwork.data.Ticket;

/**
 * The ticket data access object wraps the logic for persisting parking
 * tickets into the underlying databases.
 */
public interface TicketDAO extends AutoCloseable {
	/**
	 * Save the given ticket to the underlying database. A new ticket id is
	 * generated automatically for the given ticket and the given value of
	 * its id field is ignored.
	 *
	 * @param t the parking ticket to save
	 * @return the ticket id generated for the saved ticket
	 * @throws Exception on error
	 */
	Long saveTicket(Ticket t) throws Exception;

	/**
	 * Retrieve a saved ticket based on its id. Returns null if the ticket's
	 * id does not exist.
	 *
	 * @param ticketId the ticket id
	 * @return a parking ticket
	 */
	Ticket getTicket(long ticketId) throws Exception;

	/**
	 * Delete a saved ticket based on its id.
	 *
	 * @param ticketId the ticket id
	 */
	void deleteTicket(long ticketId) throws Exception;
}