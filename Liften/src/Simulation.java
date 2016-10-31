import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Controller.ElevatorController;
import Model.*;

public class Simulation {
	private int mainTicker;
	private ElevatorController ec;
	private List<User> queue;
	private HashMap<User, Lift> database;
	private List<User> removeFromDatabase;

	public Simulation() {
		System.out.println("Please initiate using the correct setup");
	}

	public Simulation(ManagementSystem ms) {
		ec = new ElevatorController(ms); // is dit copy by reference or copy by
											// value? (call..)
		mainTicker = 0;
		queue = new ArrayList<>();
	}

	/**
	 * Configurations: Set currents users = 0 Set current level = startlevel
	 * current direction is 0 --> +1 = UP | -1 = DOWN
	 * 
	 * Start = no elevators moving, assign CLOSEST elevator with NO direction to
	 * first user
	 * 
	 * if no elevators with NO direction found, check elevators with direction
	 * towards user.
	 * 
	 * If first initial user (first request) target was reached, elevator
	 * becomes idle!
	 * 
	 * So if user on floor 2 requests, then other user floor 1.. elevator will
	 * stop on floor 1 and 2 if another user requests floor 3 whilst the lift is
	 * handling floor 1 and 2 (with 2 being initial) this other elevatoruser
	 * will deemed as "no elevator found" because it is not on path towards
	 * floor 2
	 * 
	 * if no elevator found -- add user to (priority?)queue
	 * 
	 * KEEP IN MIND: REMOVE USER IF TIMEOUT HAS BEEN REACHED!
	 */
	public void startSimulationSimple() {
		database = new HashMap<User, Lift>();
		for (Lift l : ec.getLifts()) {
			l.initiateLift();
		}

		// while(!ec.getUsers().isEmpty()) {
		while (ec.getUsers().size() != 0) {
			System.out.println("Gametick (" + mainTicker + ") \t queue size: " + queue.size() + " \t");
			// resetting variables
			removeFromDatabase = new ArrayList<>();

			// 1. add all users waiting to a queue
			addValidUsers(mainTicker);

			// 2. database opvullen met welke lift naar welke user gaat :)
			List<User> nextQueue = new ArrayList<>();
			for (int i = 0; i < queue.size(); i++) {
				User tempUser = queue.get(i);
				Lift tempLift = assignElevator(tempUser);
				if (tempLift != null) {
					System.out.println("DEBUG assignElevatorSource - Elevator found, " + tempUser.getId() + " assigned elevator " + tempLift.getId());
					database.put(tempUser, tempLift);
					if(tempLift.getDirection() == 0) {
						if(tempUser.getSourceId() > tempLift.getCurrentLevel()) {
							tempLift.setDirection(1);
							tempLift.setDestination(tempUser.getDestinationId());
						}
						else if(tempUser.getSourceId() < tempLift.getCurrentLevel()) {
							tempLift.setDirection(-1);
							tempLift.setDestination(tempUser.getDestinationId());
						}
					}
				} else {
					System.out.println("DEBUG assignElevatorSource - Elevator not found");
					nextQueue.add(tempUser);
				}
			}

			// 3. update old queue
			queue = nextQueue;

			// 4. handle elevator
			/**
			 * Pseudocode can be found on following image:
			 * https://gyazo.com/c5ee38012c9f45206e329d354d4a6240
			 */
			if (!database.isEmpty()) {
				System.out.println("DEBUG database - current database size: " + database.size());
				for (User u : database.keySet()) {
					Lift l = database.get(u);
					// if on same level, aka user has to step in
					if (u.getSourceId() == l.getCurrentLevel()) {
						System.out.println("User should be enterring.. Elevator ("+l.getId()+") mode: " + l.getMode());
						switch (l.getMode()) {
						case "idle":
							u.setInElevator(true);
							l.setMode("openen");
							l.setOperationTimer(mainTicker);
							break;
						case "openen":
							if (l.getOperationTimer() + l.getOperationTimer() >= mainTicker) {
								l.setMode("boarding");
								l.setOperationTimer(mainTicker);
							}
							break;
						case "boarding":
							if (l.getOperationTimer() + u.getBoardingTime() >= mainTicker) {
								l.setMode("closing");
								l.setOperationTimer(mainTicker);
							}
							break;
						case "closing":
							if (l.getOperationTimer() + l.getClosingTime() >= mainTicker) {
								l.setMode("idle");
								l.setMovingTimer(mainTicker);
								if (l.getCurrentUsers() < l.getCapacity()) {
									//System.out.println("DEBUG switchIN - joined elevator " + u.toString());
									l.setCurrentUsers(l.getCurrentLevel() + 1);
								} else
									System.out.println(
											"WARNING - something went wrong.. Capacity reached of " + l.toString());
							}
							break;
						default:
							System.out.println("DEBUG switchIN - (" + l.getMode() + ") ripperoni in pepperoni ;-;");
							break;
						}

						// if on same level, aka user has to step out
					} else if (u.getDestinationId() == l.getCurrentLevel()) {
						System.out.println("User should be leaving.. Elevator ("+l.getId()+") mode: " + l.getMode());
						switch (l.getMode()) {
						case "idle":
							l.setMode("openen");
							l.setOperationTimer(mainTicker);
							break;
						case "openen":
							if (l.getOperationTimer() + l.getOperationTimer() >= mainTicker) {
								l.setMode("unboarding");
								l.setOperationTimer(mainTicker);
							}
							break;
						case "unboarding":
							if (l.getOperationTimer() + u.getUnboardingTime() >= mainTicker) {
								l.setMode("closing");
								l.setOperationTimer(mainTicker);
							}
							break;
						case "closing":
							if (l.getOperationTimer() + l.getClosingTime() >= mainTicker) {
								l.setMode("idle");
								l.setMovingTimer(mainTicker);
								if (l.getCurrentUsers() > 0) {
									System.out.println("DEBUG switchOUT - handled " + u.toString());
									l.setCurrentUsers(l.getCurrentLevel() - 1);
									removeFromDatabase.add(u);
								} else
									System.out.println(
											"WARNING - something went seriously wrong.. Negative amount of users in "
													+ l.toString());
							}
							break;
						default:
							System.out.println("DEBUG switchOUT- (" + l.getMode() + ") ripperoni in pepperoni ;-;");
							break;
						}
					} else if (u.isInElevator() == false && u.getArrivalTime() + u.getTimeout() < mainTicker) {
						// arguable to do <= instead of <.. personal preference?
						removeFromDatabase.add(u);
						System.out.println("DEBUG - " + (u.getArrivalTime() + u.getTimeout()) + "/" + mainTicker
								+ " - LEAVING " + u.toString());
					} else if (l.getDirection() == 0) {
						// do nothing?
						// System.out.println("DEBUG - Elevator " + l.getId() +
						// " is idling...");
					} else {
						if (l.getMovingTimer() + l.getLevelSpeed() >= mainTicker) {
							l.setNextLevel();
							l.setMovingTimer(mainTicker);
						}
					}
				}

				// 5. follow-up from 4 -> remove handled/timed-out users
				for (User u : removeFromDatabase)
					database.remove(u);
			}

			mainTicker++;
		}
		System.out.println("DEBUG - queue size: " + queue.size() + " | Userlist size: " + ec.getUsers().size());
	}

	public void addValidUsers(int time) {
		// Adding valid users to the queue
		for (int i = 0; i < ec.getUsers().size(); i++) {
//			System.out.println("DEBUG addValidUsers - " + ec.getUsers().get(i).getArrivalTime() + " < " + time + " => "
//					+ (ec.getUsers().get(i).getArrivalTime() < mainTicker));
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
//			System.out.println("DEBUG getValidUsers - " + ec.getUsers().get(i).getArrivalTime() + " < " + time + " => "
//					+ (ec.getUsers().get(i).getArrivalTime() < mainTicker) + " (to returnList)");
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

		// first check if there are no idle elevators
		// || WE DO CHECK ON CAPACITY, BUG-PREVENTION
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
		 * UITBREIDING: Wat als lift niet weet wat de u.getDestinationId is? ...
		 * user mogelijkheid geven in te stappen op huidige niveau dan kijken of
		 * lift mogelijk is en dan terug uitstappen -> extra delay D: maar wel
		 * realistisch?
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
					// ^^check if on path (UP)
					/**
					 * Nog iets doen hier?
					 */
					if (distance > Math.abs(u.getSourceId() - l.getCurrentLevel())) {
						returnLift = l;
						distance = Math.abs(u.getSourceId() - l.getCurrentLevel());
					}
				} else if (l.getDirection() == -1 && l.getDestination() <= u.getSourceId()
						&& l.getCurrentLevel() >= u.getSourceId()) {
					// ^^check if on path (DOWN)
					/**
					 * Nog iets doen hier?
					 */
					if (distance > Math.abs(u.getSourceId() - l.getCurrentLevel())) {
						returnLift = l;
						distance = Math.abs(u.getSourceId() - l.getCurrentLevel());
					}
				}
			}
		}

		return returnLift;
	}
}
