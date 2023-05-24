package ut3.labwork.dbinterface;

import java.io.UnsupportedEncodingException;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Sequence;
import com.sleepycat.je.SequenceConfig;
import com.sleepycat.sample.dbinterface.BasicDbManager;

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

	/* The binding for key database entries. */
	private final TupleBinding<Long> keyBinding;

	/* The binding for value database entries. */
	private final ClientBinding valueBinding;
	
	/* The client id sequence handle. */
	private final Sequence idSeq;
	
	/* The key of the ticket id sequence. */
	static final DatabaseEntry ID_SEQ_KEY;

	static {
		DatabaseEntry idSeqKey;
		try {
			idSeqKey = new DatabaseEntry("ClientIdSeq".getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			idSeqKey = new DatabaseEntry("ClientIdSeq".getBytes());
		}
		ID_SEQ_KEY = idSeqKey;
	}

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
//		dbConfig.setType(DatabaseType.BTREE);
		clientDb = dbMgr.env.openDatabase(null, DB_NAME, dbConfig);
		
		SequenceConfig seqConfig = new SequenceConfig();
		seqConfig.setAutoCommitNoSync(true);
		seqConfig.setCacheSize(100);
		idSeq = clientDb.openSequence(null, ID_SEQ_KEY, seqConfig);

		keyBinding = TupleBinding.getPrimitiveBinding(Long.class);
		valueBinding = new ClientBinding();
	}

	@Override
	public void close() throws Exception {
		clientDb.close();
	}

	/**
	 * Helper class to convert between ticket logs and byte arrays.
	 */
	private static class ClientBinding extends TupleBinding<Client> {
		@Override
		public Client entryToObject(TupleInput in) {
			return new Client(in.readLong(), in.readDouble());
		}

		@Override
		public void objectToEntry(Client client, TupleOutput out) {
			out.writeLong(client.getId());
			out.writeDouble(client.getSubscriptionFactor());
		}
	}

	@Override
	public Long saveClient(Client client) throws Exception {
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry value = new DatabaseEntry();

		Long clientId = idSeq.get(null, 1);

		keyBinding.objectToEntry(clientId, key);
		valueBinding.objectToEntry(
				new Client(clientId, client.getSubscriptionFactor()), value);

		clientDb.putNoOverwrite(dbMgr.getCurrentTxn(), key, value);

		return clientId;	
	}

	@Override
	public Client getClient(Long clientId) throws Exception {
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry value = new DatabaseEntry();

		keyBinding.objectToEntry(clientId, key);

		OperationStatus s = clientDb.get(
				dbMgr.getCurrentTxn(), key, value, LockMode.READ_COMMITTED);

		if (s == OperationStatus.SUCCESS) {
			return valueBinding.entryToObject(value);
		} else {
			return null;
		}
	}
}
