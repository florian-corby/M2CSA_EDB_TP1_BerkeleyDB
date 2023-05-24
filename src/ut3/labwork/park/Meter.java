package ut3.labwork.park;

import ut3.labwork.data.Ticket;
import ut3.labwork.data.TicketLog;

import ut3.labwork.data.Client;
import ut3.labwork.dbinterface.ClientDAO;
import ut3.labwork.dbinterface.DbManager;
import ut3.labwork.dbinterface.TicketDAO;
import ut3.labwork.dbinterface.TicketLogDAO;

import java.util.Date;
import java.util.Random;

/**
 * The Meter class represents a parking meter which can issue parking tickets
 * and calculate parking fees given tickets. Besides, each meter has an unique
 * ID to identify it.
 */
public class Meter implements AutoCloseable {
	/* The parking fee in cents per minute. */
	private static final int FEE_PER_MINUTE = 2;

	/* The parking meter id. */
	private final String id;

	/* The database manager used by this meter. */
	private final DbManager dbMgr;
	
	/* The ticket data access object. */
	private final ClientDAO clientDAO;

	/* The ticket data access object. */
	private final TicketDAO ticketDAO;

	/* The log message data access object. */
	private final TicketLogDAO logDAO;
	
	private final Random rand = new Random();

	/**
	 * Create a parking meter.
	 *
	 * @param id    the parking meter id
	 * @param dbMgr the database manager used by this meter
	 * @throws Exception on error
	 */
	public Meter(String id, DbManager dbMgr) throws Exception {
		this.id = id;
		this.dbMgr = dbMgr;
		clientDAO = dbMgr.createClientDAO();
		ticketDAO = dbMgr.createTicketDAO();
		logDAO = dbMgr.createTicketLogDAO();
	}

	@Override
	public void close() throws Exception {
		clientDAO.close();
		ticketDAO.close();
		logDAO.close();
	}

	/**
	 * A car parks at the given time and is issued a parking ticket.
	 *
	 * @param time the parking time
	 * @return a parking ticket
	 * @throws Exception on error
	 */
	public Ticket park(String clientId, Date time) throws Exception {
		Ticket t = new Ticket(clientId, 0L, id, time.getTime());

		dbMgr.beginTxn();
		try {
			Long ticketId = ticketDAO.saveTicket(t);
			clientDAO.saveClient(new Client(clientId, rand.nextDouble(0., 1.)));
			logDAO.saveLog(new TicketLog(
					time.getTime(), id, ticketId, TicketLog.Action.ISSUE, 0));
			dbMgr.commit();
			return new Ticket(clientId, ticketId, id, time.getTime());
		} catch (Exception e) {
			dbMgr.abort();
			throw e;
		}
	}

	/**
	 * A car departs at the given time. Given its parking ticket, computes
	 * its parking fee.
	 *
	 * @param t    the parking ticket
	 * @param time the departure time
	 * @return the parking fee in cents
	 * @throws Exception on error
	 */
	public int depart(Ticket t, Date time) throws Exception {
		dbMgr.beginTxn();

		try {
			Ticket ticket = ticketDAO.getTicket(t.getTicketId());
			Client client = clientDAO.getClient(t.getClientId());
			int fee = computeFee(ticket, time, client.getSubscriptionFactor());
			logDAO.saveLog(new TicketLog(time.getTime(),
					id, ticket.getTicketId(), TicketLog.Action.CHARGE, fee));
			ticketDAO.deleteTicket(ticket.getTicketId());
			dbMgr.commit();
			return fee;
		} catch (Exception e) {
			dbMgr.abort();
			throw e;
		}
	}

	/**
	 * Computes the parking fee given a ticket and its departure time.
	 *
	 * @param t    the parking ticket
	 * @param time the departure time
	 * @return the parking fee in cents
	 */
	private int computeFee(Ticket t, Date time, Double subscriptionFactor) {
		long departTime = time.getTime();
		long parkTime = t.getIssuingTime();

		if (parkTime < departTime) {
			return (int) ((departTime - parkTime) / 1000 / 60 * FEE_PER_MINUTE * subscriptionFactor);
		} else {
			return 0;
		}
	}
}
