import java.util.ArrayList;
import java.util.List;

import Controller.ElevatorController;
import Model.*;

public class Simulation {
	private int mainTicker;
	private ElevatorController ec;
	private int succes, failure;
	private List<User> queue;

	public Simulation() {
		System.out.println("Please initiate using the correct setup");
	}

	public Simulation(ManagementSystem ms) {
		ec = new ElevatorController(ms); // is dit copy by reference or copy by
											// value? (call..)
		mainTicker = 0;
		succes = 0;
		failure = 0;
		queue = new ArrayList<>();
	}

	/**
	 * Configurations: Set currents users = 0 Set current level = startlevel
	 * current direction is 0 --> +1 = UP | -1 = DOWN
	 * 
	 * Start = no elevators moving, assign CLOSEST elevator with NO direction to
	 * first user if no elevators with NO direction, check elevators with
	 * direction towards user.
	 * 
	 * 
	 * if no elevator found -- add user to (priority?)queue
	 * 
	 * KEEP IN MIND: REMOVE USER IF TIMEOUT HAS BEEN REACHED!
	 */
	public void startSimulationSimple() {
		// while(!ec.getUsers().isEmpty()) {
		while (ec.getUsers().size() != 0) {
			addValidUsers(mainTicker);

			for (int i = 0; i < queue.size(); i++) {
				assignElevator(queue.get(i));
			}

			mainTicker++;
		}
		System.out.println("DEBUG - queue size: " + queue.size() + " | Userlist size: " + ec.getUsers().size());
	}

	public void addValidUsers(int time) {
		// Adding valid users to the queue
		for (int i = 0; i < ec.getUsers().size(); i++) {
			System.out.println("DEBUG - " + ec.getUsers().get(i).getArrivalTime() + " < " + time + " => "
					+ (ec.getUsers().get(i).getArrivalTime() < mainTicker));
			if (ec.getUsers().get(i).getArrivalTime() < time) {
				System.out.println("DEBUG - Adding " + ec.getUsers().get(i).toString());
				queue.add(ec.getUsers().get(i));
				ec.getUsers().remove(i);
			} else {
				i = ec.getUsers().size() + 1;
			}
		}
	}

	public List<User> getValidUsers(int time) {
		List<User> returnList = new ArrayList<>();
		// Adding valid users to the returnList
		for (int i = 0; i < ec.getUsers().size(); i++) {
			System.out.println("DEBUG - " + ec.getUsers().get(i).getArrivalTime() + " < " + time + " => "
					+ (ec.getUsers().get(i).getArrivalTime() < mainTicker) + " (to returnList)");
			if (ec.getUsers().get(i).getArrivalTime() < time) {
				System.out.println("DEBUG - Adding " + ec.getUsers().get(i).toString() + " (to returnList)");
				returnList.add(ec.getUsers().get(i));
			} else {
				i = ec.getUsers().size() + 1;
			}
		}
		return returnList;
	}

	public Lift assignElevator(User u) {
		Lift returnLift = null;
		int distance = ec.getLevels().size() + 100;

		// first check if there are no idle elevators || WE DO CHECK ON
		// CAPACITY, BUG-PREVENTION
		for (Lift l : ec.getLifts()) {
			if (l.getDirection() == 0) {
				if (distance > Math.abs(u.getSourceId() - l.getCurrentLevel())
						&& l.getCurrentUsers() < l.getCapacity()) {
					returnLift = l;
					distance = Math.abs(u.getSourceId() - l.getCurrentLevel());
					
					
					
					
					
				}
			}
		}

		/**
		 * VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV UITBREIDING: Wat als lift
		 * niet weet wat de u.getDestinationId is? ... user mogelijkheid geven
		 * in te stappen op huidige niveau dan kijken of lift mogelijk is en dan
		 * terug uitstappen -> extra delay D: maar wel realistisch?
		 */

		// check if we can assign a lift which is in use || WORK IN
		// PROGRESSSSSSSS
		for (Lift l : ec.getLifts()) {
			// check if available (in use)
			if (l.getUnavailableUntil() > mainTicker
					// check if full && is able to handle
					&& l.getCurrentUsers() < l.getCapacity() && l.getRange().contains(u.getSourceId())
					&& l.getRange().contains(u.getDestinationId())) {
				if (l.getDirection() == 1 && l.getDestination() >= u.getSourceId()
						&& l.getCurrentLevel() <= u.getSourceId()) {
					// check if on path (UP)
					
					
					
					
					
				} else if (l.getDirection() == -1 && l.getDestination() <= u.getSourceId()
						&& l.getCurrentLevel() >= u.getSourceId()) {
					// check if on path (DOWN)
					
					
					
					
					
					
				}
			}
		}

		return returnLift;
	}
}
