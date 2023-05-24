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
	private Long clientId;

	/* The percentage (0 <= factor <= 1) of the subscription 
	 * reduction for this client */
	private Double subscriptionFactor;


	public Client(Long clientId, Double subscriptionFactor) {
		this.clientId = clientId;
		this.subscriptionFactor = subscriptionFactor;
	}

	public long getId() {
		return clientId;
	}

	public Double getSubscriptionFactor() {
		return subscriptionFactor;
	}
}
