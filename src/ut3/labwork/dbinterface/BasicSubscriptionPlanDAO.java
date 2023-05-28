package ut3.labwork.dbinterface;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import ut3.labwork.data.Client;
import ut3.labwork.data.SubscriptionPlan;

/**
 * BasicTicketLogDAO is a TicketLogDAO implementation using the base API.
 */
public class BasicSubscriptionPlanDAO implements SubscriptionPlanDAO {
	/* The file name of the client log database. */
	static final String DB_NAME = "plan.db";

	/* The database manager from which this DAO is created. */
	private final BasicDbManager dbMgr;

	/* The client database handle. */
	private final Database planDb;

	/* The binding for value database entries. */
	private final ClientBinding valueBinding;
	
	
	/**
	 * Create a data access object for subscription plans.
	 *
	 * @param dbManager the database manager creating this object
	 * @throws Exception on error
	 */
	public BasicSubscriptionPlanDAO(BasicDbManager dbManager) throws Exception {
		dbMgr = dbManager;

		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(true);
		planDb = dbMgr.env.openDatabase(null, DB_NAME, dbConfig);

		valueBinding = new ClientBinding();
	}

	@Override
	public void close() throws Exception {
		planDb.close();
	}

	/**
	 * Helper class to convert between clients and byte arrays.
	 */
	private static class ClientBinding extends TupleBinding<Client> {
		@Override
		public Client entryToObject(TupleInput in) {
			return new Client(in.readString(), in.readDouble());
		}

		@Override
		public void objectToEntry(Client client, TupleOutput out) {
			out.writeString(client.getId());
			out.writeDouble(client.getSubscriptionFactor());
		}
	}

	@Override
	public String saveFormula(SubscriptionPlan plan) throws Exception {
		DatabaseEntry key = new DatabaseEntry(plan.getId().getBytes("UTF-8"));
		DatabaseEntry value = new DatabaseEntry();

		valueBinding.objectToEntry(
				new Client(plan.getId(), plan.getRedFactor()), value);
		
		planDb.putNoOverwrite(dbMgr.getCurrentTxn(), key, value);

		return plan.getId();	
	}

	@Override
	public Client getFormula(String planId) throws Exception {
		DatabaseEntry key = new DatabaseEntry(planId.getBytes("UTF-8"));
		DatabaseEntry value = new DatabaseEntry();

		OperationStatus s = planDb.get(
				dbMgr.getCurrentTxn(), key, value, LockMode.READ_COMMITTED);

		if (s == OperationStatus.SUCCESS) {
			return valueBinding.entryToObject(value);
		} else {
			return null;
		}
	}
}
