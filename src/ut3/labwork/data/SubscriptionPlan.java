package ut3.labwork.data;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class SubscriptionPlan {
	/* The ticket id. */
	@PrimaryKey
	private String formulaName;

	/* The percentage (0 <= factor <= 1) of the subscription 
	 * reduction for this client */
	private Double subscriptionFactor;


	public SubscriptionPlan(String formulaName, Double red) {
		this.formulaName = formulaName;
		this.subscriptionFactor = (1.0 - red) * 100;
	}

	public String getId() {
		return formulaName;
	}
	
	public Double getRed() {
		return (1.0 - subscriptionFactor) * 100;
	}

	public Double getRedFactor() {
		return subscriptionFactor;
	}
}
