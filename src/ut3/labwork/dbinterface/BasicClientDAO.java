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

/**
 * BasicTicketLogDAO is a TicketLogDAO implementation using the base API.
 */
public class BasicClientDAO implements ClientDAO {
	/* The file name of the client log database. */
	static final String DB_NAME = "client.db";

	/* The database manager from which this DAO is created. */
	private final BasicDbManager dbMgr;

	/* The client database handle. */
	private final Database clientDb;

	/* The binding for value database entries. */
	private final ClientBinding valueBinding;
	
	
	/**
	 * Create a data access object for ticket logs.
	 *
	 * @param dbManager the database manager creating this object
	 * @throws Exception on error
	 */
	public BasicClientDAO(BasicDbManager dbManager) throws Exception {
		dbMgr = dbManager;

		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(true);
		clientDb = dbMgr.env.openDatabase(null, DB_NAME, dbConfig);
		
		valueBinding = new ClientBinding();
	}

	@Override
	public void close() throws Exception {
		clientDb.close();
	}

	/**
	 * Helper class to convert between clients and byte arrays.
	 */
	private static class ClientBinding extends TupleBinding<Client> {
		@Override
		public Client entryToObject(TupleInput in) {
			return new Client(in.readString(), in.readString());
		}

		@Override
		public void objectToEntry(Client client, TupleOutput out) {
			out.writeString(client.getId());
			out.writeString(client.getFormulaId());
		}
	}

	@Override
	public String saveClient(Client client) throws Exception {
		DatabaseEntry key = new DatabaseEntry(client.getId().getBytes("UTF-8"));
		DatabaseEntry value = new DatabaseEntry();

		valueBinding.objectToEntry(
				new Client(client.getId(), client.getFormulaId()), value);
		
		clientDb.putNoOverwrite(dbMgr.getCurrentTxn(), key, value);

		return client.getId();	
	}

	@Override
	public Client getClient(String clientId) throws Exception {
		DatabaseEntry key = new DatabaseEntry(clientId.getBytes("UTF-8"));
		DatabaseEntry value = new DatabaseEntry();

		OperationStatus s = clientDb.get(
				dbMgr.getCurrentTxn(), key, value, LockMode.READ_COMMITTED);

		if (s == OperationStatus.SUCCESS) {
			return valueBinding.entryToObject(value);
		} else {
			return null;
		}
	}
}
