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
     * <p>
     * Start = no elevators moving, assign CLOSEST elevator with NO direction to
     * first user
     * <p>
     * if no elevators with NO direction found, check elevators with direction
     * towards user.
     * <p>
     * If first initial user (first request) target was reached, elevator
     * becomes idle!
     * <p>
     * So if user on floor 2 requests, then other user floor 1.. elevator will
     * stop on floor 1 and 2 if another user requests floor 3 whilst the lift is
     * handling floor 1 and 2 (with 2 being initial) this other elevatoruser
     * will deemed as "no elevator found" because it is not on path towards
     * floor 2
     * <p>
     * if no elevator found -- add user to (priority?)queue
     * <p>
     * KEEP IN MIND: REMOVE USER IF TIMEOUT HAS BEEN REACHED!
     *
     * @throws Exception
     */
    public void startSimulationSimple() throws Exception {
        database = new HashMap<User, Lift>();
        for (Lift l : ec.getLifts()) {
            l.initiateLift();
        }

        // while(!ec.getUsers().isEmpty()) {
        while (ec.getUsers().size() != 0 || database.size() != 0 || queue.size() != 0) {
            ArrayList<User> removingUsers = new ArrayList<>();

            System.out.println("\nGametick (" + mainTicker + ") \t queue size: " + queue.size() + " \t");

            // 1. add all users waiting to a queue
            addValidUsers(mainTicker);

            // 2. database opvullen met welke lift naar welke user gaat :)
            List<User> nextQueue = new ArrayList<>();
            for (int i = 0; i < queue.size(); i++) {
                User tempUser = queue.get(i);
                if (!tempUser.isFinished()) {
                    Lift tempLift = assignElevator(tempUser);
                    if (tempLift != null) {
                        System.out.println("\tE\t DEBUG - Elevator found, " + tempUser.getId() + " assigned elevator "
                                + tempLift.getId());
                        database.put(tempUser, tempLift);

                        if (tempLift.getDirection() == 0 && tempLift.getDestination() == -1) {
                            if (tempUser.getSourceId() > tempLift.getCurrentLevel()) {
                                tempLift.setMovingTimer(mainTicker);
                                tempLift.setDirection(1);
                                tempLift.setDestination(tempUser.getSourceId());
                            } else if (tempUser.getSourceId() < tempLift.getCurrentLevel()) {
                                tempLift.setMovingTimer(mainTicker);
                                tempLift.setDirection(-1);
                                tempLift.setDestination(tempUser.getSourceId());
                            }
                        }

                    } else if (((tempUser.getTimeout() + tempUser.getArrivalTime()) < mainTicker) && !tempUser.isInElevator()) {
                        System.out.println("\tR\t DEBUG - removing user " + tempUser.getId() + " due to timeout: " + (tempUser.getTimeout() + tempUser.getArrivalTime()) + " < " + mainTicker);
                    } else {
                        System.out.println("\tE\t DEBUG - Elevator not found");
                        nextQueue.add(tempUser);
                    }
                }
            }


            /*
            if (tempUser.isFinished() || ((tempUser.getTimeout() + tempUser.getArrivalTime()) < mainTicker) && !tempUser.isInElevator()) {
                if (tempUser.getTimeout() + tempUser.getArrivalTime() > mainTicker) {
                    System.out.println("\tR\t DEBUG - removing user " + tempUser.getId() + " due to timeout: " + (tempUser.getTimeout() + tempUser.getArrivalTime()) + " < " + mainTicker);
                    removingUsers.add(tempUser);
                } else {
                    removingUsers.add(tempUser);
                    System.out.println("\tR\t DEBUG - removing user " + tempUser.getId());
                }
            }
            for (User u : removingUsers)
                queue.remove(u);
            */

            // 3. update old queue
            queue = nextQueue;

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
             * system 1 tick earlier.
             * This is sort of a bug, but it does not
             * interfere with the timing of the rest of the system. As the
             * person boarding will handle the closing of the elevator within
             * appropriate timing.
             *
             */

            if (!database.isEmpty()) {
                System.out.println("\tSTART\t DEBUG - current database size: " + database.size() + " | "
                        + database.keySet().size());

                // 4. set appropriate variables
                for (User u : database.keySet()) {
                    Lift l = database.get(u);

                    if (l.getHandlingUsers().contains(u) && !u.isHandled()) {
                        if (u.isInElevator() && u.getDestinationId() == l.getCurrentLevel()) { // UITSTAPPEN
                            System.out.println(
                                    "\tSTATUS\t DEBUG - Elevator (" + l.getId() + ") is removing user (" + u.getId() + ").");
                            if(u.getOriginalDestination() == -1 ) {
                                u.setHandled(true);
                            }
                            l.setUsersGettingOut(l.getUsersGettingOut() + 1);
                            l.setBoardingDelay(l.getBoardingDelay() + u.getUnboardingTime());
                        }
                    } else {
                        if (!u.isInElevator() && u.getSourceId() == l.getCurrentLevel()) { // INSTAPPEN
                            System.out.println(
                                    "\tSTATUS\t DEBUG - Elevator (" + l.getId() + ") is adding user (" + u.getId() + ").");
                            l.addHandlingUser(u);
                            l.setUsersGettingIn(l.getUsersGettingIn() + 1);
                            l.setBoardingDelay(l.getBoardingDelay() + u.getBoardingTime());
                        }
                    }

                    if (u.isFinished() || ((u.getTimeout() + u.getArrivalTime()) < mainTicker) && !u.isInElevator()) {
                        if (u.getTimeout() + u.getArrivalTime() > mainTicker) {
                            System.out.println("\tR\t DEBUG - removing user " + u.getId() + " due to timeout: " + (u.getTimeout() + u.getArrivalTime()) + " < " + mainTicker);
                            removingUsers.add(u);
                        } else {
                            removingUsers.add(u);
                            System.out.println("\tR\t DEBUG - removing user " + u.getId());
                        }
                    }
                }
                for (User u : removingUsers)
                    queue.remove(u);

                // 5. handle elevator handlings
                for (Lift l : ec.getLifts()) {
                    System.out.println();
                    System.out.println("\t\t DEBUG - Elevator (" + l.getId() + ") contains "
                            + l.getHandlingUsers().size() + " users, from " + l.getCurrentLevel()
                            + " is heading to: " + l.getDestination() + " direction(" + l.getDirection() + ")");
                    // debug:
                    for (User u : l.getHandlingUsers()) {
                        if (!u.isInElevator() && u.getSourceId() == l.getCurrentLevel()) { // instappen
                            System.out.println("\tUser (" + u.getId() + ") should be enterring.. Elevator (" + l.getId()
                                    + ") mode: " + l.getMode());
                        } else if (u.isInElevator() && u.getDestinationId() == l.getCurrentLevel()) { // uitstappen
                            System.out.println("\tUser (" + u.getId() + ") should be leaving.. Elevator (" + l.getId()
                                    + ") mode: " + l.getMode());
                        }
                    }

                    System.out.println("\t\t DEBUG - users getting in:" + l.getUsersGettingIn()
                            + " | users getting out: " + l.getUsersGettingOut());
                    if ((l.getUsersGettingIn() + l.getUsersGettingOut()) > 0) {
                        switch (l.getMode()) {
                            case "idle":
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
                                double delay = 0;
                                for (User u : l.getHandlingUsers()) {
                                    if ((u.getSourceId() == l.getCurrentLevel() && !u.isInElevator())
                                            || u.getDestinationId() == l.getCurrentLevel()) {
                                        delay += u.getBoardingTime();
                                    }
                                }

                                if (l.getOperationTimer() + delay >= mainTicker) {
                                    l.setMode("closing");
                                    l.setOperationTimer(mainTicker);
                                }
                                break;
                            case "closing":
                                if (l.getOperationTimer() + l.getClosingTime() >= mainTicker) {
                                    l.setMode("idle");
                                    l.setMovingTimer(mainTicker);
                                    l.setBoardingDelay(0);

                                    removingUsers = new ArrayList<>();

                                    for (User u : l.getHandlingUsers()) {
                                        if (!u.isInElevator() && u.getSourceId() == l.getCurrentLevel()) { // instappen
                                            System.out.println("\t\t DEBUG - User (" + u.getId() + ") joined elevator");
                                            l.setCurrentUsers(l.getCurrentUsers() + 1);
                                            u.setInElevator(true);
                                            if (l.getUsersGettingIn() == 0)
                                                throw new Exception();
                                            l.setUsersGettingIn(l.getUsersGettingIn() - 1);

                                            System.out.println("\t!!!\t DEBUG - Amount of users in elevator: " + l.getCurrentUsers());
                                            if (l.getCurrentUsers() == 1) {
                                                l.setDestination(u.getDestinationId());
                                                if (l.getCurrentLevel() < u.getDestinationId()) {
                                                    l.setDirection(1);
                                                } else {
                                                    l.setDirection(-1);
                                                }
                                            }

                                        } else if (u.getDestinationId() == l.getCurrentLevel()) { // uitstappen
                                            System.out.println("\t\t DEBUG - User (" + u.getId() + ") left elevator");
                                            l.setCurrentUsers(l.getCurrentUsers() - 1);

                                            u.setInElevator(false);
                                            if(u.getOriginalDestination() == -1) {
                                                u.setFinished(true);
                                            } else {
                                                //reset user using the new source / old dest and put back in pool
                                                u.setArrivalTime((double) mainTicker);
                                                u.setSourceId(u.getDestinationId());
                                                u.setDestinationId(u.getOriginalDestination());
                                                queue.add(u);
                                            }
                                            removingUsers.add(u);
                                            if (l.getUsersGettingOut() == 0)
                                                throw new Exception();
                                            l.setUsersGettingOut(l.getUsersGettingOut() - 1);

                                            if (l.getCurrentUsers() == 0) {
                                                l.setDestination(-1);
                                                l.setDirection(0);
                                            }
                                        }
                                    }

                                    removingUsers.forEach(l::removeHandlingUser);

                                    if (l.getHandlingUsers().size() == 1) {
                                        l.setDestination(l.getHandlingUsers().get(0).getDestinationId());
                                        if (l.getCurrentLevel() > l.getHandlingUsers().get(0).getDestinationId()) {
                                            l.setDirection(-1);
                                        } else {
                                            l.setDirection(1);
                                        }
                                    }
                                }
                                break;
                            default:
                                System.out
                                        .println("\t!\t DEBUG - (" + l.getMode() + ").. this mode is not an elevator mode");
                                break;
                        }
                    }
                }
                System.out.println();
                removingUsers = new ArrayList<>();
                // 6. follow-up from 5 -> remove handled/timed-out users
                for (User u : database.keySet())
                    if (u.isFinished()) {
                        removingUsers.add(u);
                        System.out.println("\tR\t DEBUG - removing user " + u.getId());
                    }
                for (User u : removingUsers)
                    database.remove(u);

                // 7. handle elevator movements
                for (Lift l : ec.getLifts()) {
                    if (l.getDirection() != 0 && l.getMovingTimer() + l.getLevelSpeed() <= mainTicker && (l.getUsersGettingIn() + l.getUsersGettingOut()) == 0) {
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
            if (ec.getUsers().get(i).getArrivalTime() < time) {
                System.out.println("\t\t DEBUG - Adding " + ec.getUsers().get(i).toString() + " (to returnList)");
                returnList.add(ec.getUsers().get(i));
            } else {
                i = ec.getUsers().size() + 1;
            }
        }
        return returnList;
    }

    //TODO LELELELELELE
    public Lift assignElevator(User u) {
        Lift returnLift = null;
        int distance = ec.getLevels().size() + 100;
        // first check if there are no idle elevators || WE DO CHECK ON CAPACITY, BUG-PREVENTION
        for (Lift l : ec.getLifts()) {
            if (l.getDirection() == 0 && l.getRange().contains(u.getSourceId())) {
                if (distance > Math.abs(u.getSourceId() - l.getCurrentLevel())
                        && l.getCurrentUsers() < l.getCapacity()) {
                    returnLift = l;
                    distance = Math.abs(u.getSourceId() - l.getCurrentLevel());
                }
            }
        }

        // check if we can assign a lift which is in use
        for (Lift l : ec.getLifts()) {
            // check if available (in use)
            if (l.getUnavailableUntil() > mainTicker
                    // check if full && is able to handle
                    && l.getCurrentUsers() < l.getCapacity() && l.getRange().contains(u.getSourceId())
                    && l.getRange().contains(u.getDestinationId())) {
                if (l.getDirection() == 1 && l.getDestination() >= u.getSourceId()
                        && l.getCurrentLevel() <= u.getSourceId()
                        && u.isUp()) {
                    // ^^check if on path (UP)
                    /**
                     * Nog iets doen hier?
                     */
                    if (distance > Math.abs(u.getSourceId() - l.getCurrentLevel())) {
                        returnLift = l;
                        distance = Math.abs(u.getSourceId() - l.getCurrentLevel());
                    }
                } else if (l.getDirection() == -1 && l.getDestination() <= u.getSourceId()
                        && l.getCurrentLevel() >= u.getSourceId()
                        && !u.isUp()) {
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

        if (returnLift == null) {
            System.out.println("\tL\t DEBUG - no suitable elevator found at the moment");
            boolean rip = true;
            for (Lift l : ec.getLifts()) {
                if (l.getRange().contains(u.getSourceId()) && l.getRange().contains(u.getDestinationId())) {
                    rip = false;
                }
            }

            if (rip) {
                //geen lift kan de gebruiker volledig helpen...
                System.out.println("\tL\t DEBUG - There exists no elevator who can support this user");
                int afstand = Integer.MAX_VALUE;

                //kijken naar liften die idle zijn (direction == 0)
                for (Lift l : ec.getLifts()) {
                    if (l.getDirection() == 0 && l.getRange().contains(u.getSourceId())) {
                        for (int i = 0; i < l.getRange().size(); i++) {
                            if (Math.abs(l.getRange().get(i).getId() - u.getSourceId()) < afstand) {
                                returnLift = l;
                                afstand = Math.abs(l.getRange().get(i).getId() - u.getSourceId());
                            }
                        }
                    }
                }


                //not found? kijken naar liften die in use zijn maar mss wel bruikbaar zijn
                if (returnLift == null) {
                    for (Lift l : ec.getLifts()) {
                        // check if available (in use)
                        if (l.getUnavailableUntil() > mainTicker
                                // check if full && is able to handle
                                && l.getCurrentUsers() < l.getCapacity()
                                && l.getRange().contains(u.getSourceId())) {
                            if (l.getDirection() == 1 && l.getDestination() >= u.getSourceId()
                                    && l.getCurrentLevel() <= u.getSourceId()
                                    && u.isUp()) {
                                for (int i = 0; i < l.getRange().size(); i++) {
                                    if (Math.abs(l.getRange().get(i).getId() - u.getDestinationId()) < afstand) {
                                        returnLift = l;
                                        afstand = Math.abs(l.getRange().get(i).getId() - u.getDestinationId());
                                    }
                                }



                            } else if (l.getDirection() == -1 && l.getDestination() <= u.getSourceId()
                                    && l.getCurrentLevel() >= u.getSourceId()
                                    && !u.isUp()) {
                                for (int i = 0; i < l.getRange().size(); i++) {
                                    if (Math.abs(l.getRange().get(i).getId() - u.getDestinationId()) < afstand) {
                                        returnLift = l;
                                        afstand = Math.abs(l.getRange().get(i).getId() - u.getDestinationId());
                                    }
                                }


                            }
                        }
                    }
                }
            }

            if(returnLift != null) {
                u.setOriginalDestination(u.getDestinationId());
                u.setOriginalSource(u.getSourceId());

                int afstand = Integer.MAX_VALUE;
                int newDestination = -1;
                for (int i = 0; i < returnLift.getRange().size(); i++) {
                    if (Math.abs(returnLift.getRange().get(i).getId() - u.getDestinationId()) < afstand) {
                        newDestination = returnLift.getRange().get(i).getId();
                        afstand = Math.abs(returnLift.getRange().get(i).getId() - u.getDestinationId());
                    }
                }

                u.setDestinationId(newDestination);
            }
        }

        return returnLift;
    }
}
