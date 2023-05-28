package ut3.labwork;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ut3.labwork.data.Client;
import ut3.labwork.data.SubscriptionPlan;
import ut3.labwork.data.Ticket;
import ut3.labwork.dbinterface.ClientDAO;
import ut3.labwork.dbinterface.DbManager;
import ut3.labwork.dbinterface.SubscriptionPlanDAO;
import ut3.labwork.park.Meter;
import ut3.labwork.park.Reporting;

/**
 * The parking demo application.
 * <p/>
 * This demo application simulates a parking lot with one parking meter:
 * 1. A parking meter is created.
 * 2. A few cars arrive at the parking lot and the parking meter issues a
 * parking ticket for each car.
 * 3. Some of the parked cars leave the parking lot and the parking meter
 * calculates the parking fee for each car.
 * 4. At the end of the day, the parking lot manager runs a few analysis
 * about what happened in the day.
 */
public class Ut3ParkingDemo {
	/* The home directory for the database environment. */
	private static final String ENV_HOME = "parking_demo";

	public static void main(String[] args) throws Exception {
		// Create a helper factory object
		DbManagerFactory factory =
				new DbManagerFactory(ENV_HOME, DbManagerFactory.API.BASE);

		// Setup the database: create database files/tables, indexes, etc.
		try (DbManager mgr = factory.createManager()) {
			mgr.setupDb();
		}

		// Create a parking meter and run the simulated events
		System.out.println("======= UT3 Simulation starts =======");
		Map<String, Ticket> ticketMap = new HashMap<>();

		try (DbManager mgr = factory.createManager();
				Meter meter = new Meter("meter", mgr)) {

			// I fill up my subscribed customers table
			DemoData.getSimClients(mgr);
			DemoData.getSimPlans(mgr);
			
			// I proceed with the simulation which will use the known subscribed client DB
			for (DemoData.SimEvent e : DemoData.getEventsSortedByTime()) {
				if (e.isPark) {
					Ticket t = meter.park(new Client(e.carName, e.clientFormula), new Date(e.time));
					ticketMap.put(e.carName, t);
				} else {
					Ticket t = ticketMap.get(e.carName);
					meter.depart(t, new Date(e.time));
				}
			}
		}

		System.out.println("======= UT3 Simulation ends =======");

		// Generate a few reports at the end of the simulation
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date from = fmt.parse("2014-12-20T00:00:00");
		Date until = fmt.parse("2014-12-21T00:00:00");
		try (DbManager mgr = factory.createManager();
				Reporting rpt = new Reporting(mgr)) {
			int carsParked = rpt.totalCarsParkedDuring(
					from.getTime(), until.getTime(), "meter");
			int feesCollected = rpt.totalFeesCollectedDuring(
					from.getTime(), until.getTime(), "meter");
			System.out.println("Total number of cars entered on 2014-12-20: " +
					carsParked);
			System.out.println("Total fees collected on 2014-12-20: " +
					feesCollected / 100 + " dollar(s) " +
					feesCollected % 100 + " cent(s).");
		}

	}
}

/**
 * DemoData class holds the parking data for the demo application.
 */
class DemoData {
	/*
	 * The raw parking data, in the following form:
	 * {<car name>, <park time>, <depart time>}
	 */
	private static String[][] data = {
			{"AQ-170-UZ", "Gold", "2014-12-20T01:12:00", "2014-12-20T21:10:15"},
			{"LS-182-TL", "Silver", "2014-12-20T02:03:25", "2014-12-20T03:12:50"},
			{"FC-184-PO", "Bronze", "2014-12-20T03:05:12", null},
			{"MS-168-LB", "NoPlan", "2014-12-19T21:02:14", "2014-12-20T00:02:10"},
			{"AM-165-NA", "Personel", "2014-12-20T22:05:37", "2014-12-21T02:05:32"}
	};
	
	/*
	 * The existing subscription plans and their associations with the clients. 
	 * Each client can only have one subscription at a time here.
	 */
	private static String[][] subscriptionPlans = {
			{"NoPlan", "0.0"},
			{"Bronze", "0.2"},
			{"Silver", "0.5"},
			{"Gold", "0.8"},
			{"Personel", "1.0"}
	};
	
	public static void  getSimPlans(DbManager mgr) throws Exception {
		mgr.beginTxn();
		SubscriptionPlanDAO subscriptionPlanDAO = mgr.createSubscriptionPlanDAO();
		try {
			for(String[] row : subscriptionPlans) {
				SubscriptionPlan p = new SubscriptionPlan(row[0], Double.parseDouble(row[1]));
				subscriptionPlanDAO.saveFormula(p);
			}
			mgr.commit();
		} catch (Exception e) {
			mgr.abort();
			throw e;
		}
		subscriptionPlanDAO.close();
	}

	public static void getSimClients(DbManager mgr) throws Exception {
		mgr.beginTxn();
		ClientDAO clientDAO = mgr.createClientDAO();
		try {
			for(String[] row : data) {
				Client c = new Client(row[0], row[1]);
				clientDAO.saveClient(c);
			}
			mgr.commit();
		} catch (Exception e) {
			mgr.abort();
			throw e;
		}
		clientDAO.close();
	}

	/* Convert the raw data to SimEvents and sort them by time. */
	public static List<SimEvent> getEventsSortedByTime() throws Exception {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		List<SimEvent> events = new ArrayList<>(data.length * 2);

		for (String[] row : data) {
			events.add(new SimEvent(
					row[0], row[1], fmt.parse(row[2]).getTime(), true));
			if (row[3] != null) {
				events.add(new SimEvent(
						row[0], row[1], fmt.parse(row[3]).getTime(), false));
			}
		}

		Collections.sort(events);

		return events;
	}

	/* The structure for simulation events. */
	public static class SimEvent implements Comparable<SimEvent> {
		/* The car name. */
		String carName;
		/* The subscriber reduction */
		String clientFormula;
		/* The time of the event. */
		Long time;
		/* The event: true for park; false for depart. */
		boolean isPark;

		public SimEvent(String carName, String clientFormula, Long time, boolean isPark) {
			this.carName = carName;
			this.clientFormula = clientFormula;
			this.time = time;
			this.isPark = isPark;
		}

		@Override
		public int compareTo(SimEvent o) {
			return time.compareTo(o.time);
		}
		
		public String getDateTime() {
			Date date = new Date(time);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    return format.format(date);
		}
	}
}
