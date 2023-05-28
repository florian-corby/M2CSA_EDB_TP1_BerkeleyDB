package ut3.labwork.data;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * The Java definition of what is a client and the data
 * it contains as a consequence.
 */
@Entity
public class Client {
	/* The ticket id. */
	@PrimaryKey
	private String clientId;

	/* The percentage (0 <= factor <= 1) of the subscription 
	 * reduction for this client */
	private String formulaId;


	public Client(String clientId, String formulaId) {
		this.clientId = clientId;
		this.formulaId = formulaId;
	}

	public String getId() {
		return clientId;
	}
	
	public String getFormulaId() {
		return formulaId;
	}
}
