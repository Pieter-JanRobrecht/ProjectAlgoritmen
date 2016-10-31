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
		while (ec.getUsers().size() != 0 || database.size() != 0) {
			System.out.println("\nGametick (" + mainTicker + ") \t queue size: " + queue.size() + " \t");
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
					System.out.println("\tE\t DEBUG - Elevator found, " + tempUser.getId() + " assigned elevator "
							+ tempLift.getId());
					database.put(tempUser, tempLift);
					if (tempLift.getDirection() == 0) {
						if (tempUser.getSourceId() > tempLift.getCurrentLevel()) {
							tempLift.setDirection(1);
							tempLift.setDestination(tempUser.getDestinationId());
						} else if (tempUser.getSourceId() < tempLift.getCurrentLevel()) {
							tempLift.setDirection(-1);
							tempLift.setDestination(tempUser.getDestinationId());
						}
					}
				} else {
					System.out.println("\tE\t DEBUG - Elevator not found");
					nextQueue.add(tempUser);
				}
			}

			// 3. update old queue
			queue = nextQueue;

			// 4. handle elevator
			/**
			 * Pseudocode can be found on following image:
			 * https://gyazo.com/c5ee38012c9f45206e329d354d4a6240
			 * 
			 * Remark (BUG): If multiple users in elevator, we assume they can
			 * simultaneously leave the elevator WITH THE SPEED OF THE FIRST
			 * PERSON(!)
			 * 
			 * Remark (BUG): https://gyazo.com/111fb95e7779c98fda7fbc5a6b0006bf
			 * if one person boards, other unboards.. see screenshot boarding
			 * first.. in gametick 13 te mode was set from boardingIN to
			 * closingIN, the person unboarding receives closingIN as mode
			 * instead of real mode boardingIN which causes him to be out of the
			 * system 1 tick earlier This is sort of a bug, but it does not
			 * interfere with the timing of the rest of the system. As the
			 * person boarding will handle the closing of the elevator within
			 * appropriate timing.
			 *
			 */

			if (!database.isEmpty()) {
				System.out.println(
						"\t\t DEBUG - current database size: " + database.size() + " | " + database.keySet().size());
				for (User u : database.keySet()) {
					Lift l = database.get(u);

					if (!u.isInElevator()) {
						if (l.getCurrentLevel() < u.getSourceId()) {
							l.setDirection(1);
						} else if (l.getCurrentLevel() > u.getSourceId()) {
							l.setDirection(-1);
						} else if (l.getCurrentLevel() == u.getSourceId()) {
							l.setDirection(0);
						}
					} else {
						if (l.getCurrentLevel() < u.getDestinationId()) {
							l.setDirection(1);
						} else if (l.getCurrentLevel() > u.getDestinationId()) {
							l.setDirection(-1);
						} else if (l.getCurrentLevel() == u.getDestinationId()) {
							l.setDirection(0);
						}
					}

					// if on same level, aka user has to step in
					System.out.println("\t\t DEBUG - current elevator ("+l.getId()+") floor: " + l.getCurrentLevel()
							+ " | start user floor: " + u.getSourceId() + " | end user floor: " + u.getDestinationId());
					if (!u.isInElevator() && u.getSourceId() == l.getCurrentLevel()) {
						l.setDirection(0);
						System.out.println("\tUser (" + u.getId() + ") should be enterring.. Elevator (" + l.getId()
								+ ") mode: " + l.getMode());
						switch (l.getMode()) {
						case "idle":
							l.setMode("openenIN");
							l.setOperationTimer(mainTicker);
							break;
						case "openenIN":
							if (l.getOperationTimer() + l.getOperationTimer() >= mainTicker) {
								l.setMode("boardingIN");
								l.setOperationTimer(mainTicker);
							}
							break;
						case "boardingIN":
							// GLITCH: als meerdere personen instappen gaan ze
							// ten eerste allemaal tegelijk instappen, ipv
							// sequentieel te wachten. Bovendien zal ook
							// iedereen instappen aan de snelheid van de
							// snelste, dus... fout :/
							if (l.getOperationTimer() + u.getBoardingTime() >= mainTicker) {
								l.setMode("closingIN");
								l.setOperationTimer(mainTicker);
							}
							break;
						case "closingIN":
							if (l.getOperationTimer() + l.getClosingTime() >= mainTicker) {
								l.setMode("idle");
								l.setMovingTimer(mainTicker);
								if (l.getCurrentUsers() < l.getCapacity()) {
									System.out.println("\t\t DEBUG - User (" + u.getId() + ") joined elevator");
									l.setCurrentUsers(l.getCurrentLevel() + 1);
									u.setInElevator(true);
								} else
									System.out.println(
											"WARNING - something went wrong.. Capacity reached of " + l.toString());
							}
							break;
						case "openenOUT":
							// doNothing
							break;
						case "unboardingOUT":
							// doNothing
							// GLITCH??? D: We gaan zeggen dat de persoon kan
							// instappen met zelfde snelheid dat de andere
							// persoon kan uitstappen
							break;
						case "closingOUT":
							// equal to closingIN tbf
							if (l.getCurrentUsers() < l.getCapacity()) {
								System.out.println("\t\t DEBUG - User (" + u.getId() + ") joined elevator");
								l.setCurrentUsers(l.getCurrentLevel() + 1);
								u.setInElevator(true);
							} else
								System.out.println(
										"WARNING - something went wrong.. Capacity reached of " + l.toString());
							break;
						default:
							System.out
									.println("\t!\t DEBUG - (" + l.getMode() + ").. this mode is not an elevator mode");
							break;
						}

						// if on same level, aka user has to step out
					} else if (u.isInElevator() && u.getDestinationId() == l.getCurrentLevel()) {
						l.setDirection(0);
						System.out.println("\tUser (" + u.getId() + ") should be leaving.. Elevator (" + l.getId()
								+ ") mode: " + l.getMode());
						switch (l.getMode()) {
						case "idle":
							l.setMode("openenOUT");
							l.setOperationTimer(mainTicker);
							break;
						case "openenOUT":
							if (l.getOperationTimer() + l.getOperationTimer() >= mainTicker) {
								l.setMode("unboardingOUT");
								l.setOperationTimer(mainTicker);
							}
							break;
						case "unboardingOUT":
							if (l.getOperationTimer() + u.getUnboardingTime() >= mainTicker) {
								l.setMode("closingOUT");
								l.setOperationTimer(mainTicker);
							}
							break;
						case "closingOUT":
							if (l.getOperationTimer() + l.getClosingTime() >= mainTicker) {
								l.setMode("idle");
								l.setMovingTimer(mainTicker);
								if (l.getCurrentUsers() > 0) {
									System.out.println("\t\t DEBUG - handled " + u.toString());
									l.setCurrentUsers(l.getCurrentLevel() - 1);
									removeFromDatabase.add(u);
								} else
									System.out.println(
											"WARNING - something went seriously wrong.. Negative amount of users in "
													+ l.toString());
							}
							break;
						case "openenIN":
							// doNothing
							break;
						case "boardingIN":
							// doNothing
							// GLITCH??? D: We gaan zeggen dat de persoon kan
							// uitstappen met zelfde snelheid dat de andere
							// persoon kan instappen
							break;
						case "closingIN":
							// equal to closingOUT tbf
							if (l.getCurrentUsers() > 0) {
								System.out.println("\t\t DEBUG - handled " + u.toString());
								l.setCurrentUsers(l.getCurrentLevel() - 1);
								removeFromDatabase.add(u);
							} else
								System.out.println(
										"WARNING - something went seriously wrong.. Negative amount of users in "
												+ l.toString());
							break;
						default:
							System.out
									.println("\t!\t DEBUG - (" + l.getMode() + ").. this mode is not an elevator mode");
							break;
						}
					} else if (u.isInElevator() == false && u.getArrivalTime() + u.getTimeout() < mainTicker) {
						// arguable to do <= instead of <.. personal preference?
						removeFromDatabase.add(u);
						System.out.println("\t\t DEBUG userTimeout- " + (u.getArrivalTime() + u.getTimeout()) + "/"
								+ mainTicker + " - LEAVING " + u.toString());
					} else {
						System.out.println(
								"\t\t DEBUG - No interaction for this user (" + u.getId() + ") this gametick!");
					}
				}

				// 5. follow-up from 4 -> remove handled/timed-out users
				for (User u : removeFromDatabase)
					database.remove(u);

				// 6. handle elevator movements
				for (Lift l : ec.getLifts()) {
					if (l.getDirection() != 0 && l.getMovingTimer() + l.getLevelSpeed() <= mainTicker) {
						l.setNextLevel();
						l.setMovingTimer(mainTicker);
						System.out.println("\tI\t DEBUG - setting movingTimer at " + mainTicker
								+ ", next movement in atleast " + (l.getMovingTimer() + l.getLevelSpeed()));
					}
				}
			}

			mainTicker++;
		}
		System.out.println("DEBUG - queue size: " + queue.size() + " | Userlist size: " + ec.getUsers().size());
	}

	public void addValidUsers(int time) {
		// Adding valid users to the queue
		for (int i = 0; i < ec.getUsers().size(); i++) {
			// System.out.println("\t\t DEBUG addValidUsers - " +
			// ec.getUsers().get(i).getArrivalTime() + " < " + time + " => "
			// + (ec.getUsers().get(i).getArrivalTime() < mainTicker));
			if (ec.getUsers().get(i).getArrivalTime() < time) {
				System.out.println("\t\t DEBUG - Adding " + ec.getUsers().get(i).toString());
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
			// System.out.println("\t\t DEBUG getValidUsers - " +
			// ec.getUsers().get(i).getArrivalTime() + " < " + time + " => "
			// + (ec.getUsers().get(i).getArrivalTime() < mainTicker) + " (to
			// returnList)");
			if (ec.getUsers().get(i).getArrivalTime() < time) {
				System.out.println("\t\t DEBUG - Adding " + ec.getUsers().get(i).toString() + " (to returnList)");
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
