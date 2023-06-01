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
		this.subscriptionFactor = (1.0 - red);
	}

	public String getId() {
		return formulaName;
	}
	
	// Gives the value 0.8 for 80% of reduction, etc.
	public Double getRed() {
		return (1.0 - subscriptionFactor) * 100;
	}

	// Gives the factor by which we should multiply in
	// order to get our 80% of reduction, etc.
	public Double getRedFactor() {
		return subscriptionFactor;
	}
}
