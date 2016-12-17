package Voorbeeld;

import CSV.CSVUtils;
import Controller.ElevatorController;
import Model.Lift;
import Model.ManagementSystem;
import Model.User;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Simulation {
    private int mainTicker;
    private ElevatorController ec;
    private List<User> queue;
    private HashMap<User, Lift> database;
    private Controller GUIController;
    private FileWriter writer;

    private List<Integer> wachttijden;
    private int maxWachtTijd;
    private int totaleRekenTijd;
    private int totaleAfhandelTijd;
    private int aantalTimeouts;
    private int aantalLiftHoppers;

    public Simulation() {
        System.out.println("Please initiate using the correct setup");
    }

    public Simulation(ManagementSystem ms) {
        wachttijden = new ArrayList<>();
        maxWachtTijd = 0;
        totaleAfhandelTijd = 0;
        totaleRekenTijd = 0;
        aantalLiftHoppers = 0;
        aantalTimeouts = 0;

        File hulp = null;
        try {
            try {
                InputStream in = Controller.class.getClassLoader().getResourceAsStream("testLiftHopping.json");
                hulp = new File("test");
                OutputStream outputStream = new FileOutputStream(hulp);
                IOUtils.copy(in, outputStream);
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer = new FileWriter(hulp);

            CSVUtils.writeLine(writer, Arrays.asList("LiftId", "Time", "LevelId", "UserId", "OpenDoor"));
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

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
        long startTime = System.nanoTime();
        database = new HashMap<User, Lift>();
        for (Lift l : ec.getLifts()) {
            l.initiateLift();
            writeToCsv(l,null,false);
        }

        // while(!ec.getUsers().isEmpty()) {
        while (ec.getUsers().size() != 0 || database.size() != 0 || queue.size() != 0) {
            ParallelTransition thisTurnTransition = new ParallelTransition();
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

                        thisTurnTransition.getChildren().addAll(GUIController.moveUserToElevator(tempUser, tempLift));

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
                        aantalTimeouts++;
                        thisTurnTransition.getChildren().addAll(GUIController.userLeaveHall(tempUser));
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
             * system 1 tick earlier This is sort of a bug, but it does not
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

                    /*
                    if (!u.isInElevator() && l.getDirection() == 0) {
                        // + ") is not in elevator yet.");
                        if (l.getCurrentLevel() < u.getSourceId()) {
                            l.setDirection(1);
                        } else if (l.getCurrentLevel() > u.getSourceId()) {
                            l.setDirection(-1);
                        } else if (l.getCurrentLevel() == u.getSourceId()) {
                            l.setDirection(0);
                        }
                    } else {
                        // System.out.println("\t\t DEBUG - user (" + u.getId()
                        // + ") is in elevator.");
                        if (l.getCurrentLevel() < u.getDestinationId()) {
                            l.setDirection(1);
                        } else if (l.getCurrentLevel() > u.getDestinationId()) {
                            l.setDirection(-1);
                        } else if (l.getCurrentLevel() == u.getDestinationId()) {
                            l.setDirection(0);
                        }
                    }
                    */
                    if (l.getHandlingUsers().contains(u) && !u.isHandled()) {
                        if (u.isInElevator() && u.getDestinationId() == l.getCurrentLevel()) { // UITSTAPPEN
                            System.out.println(
                                    "\tSTATUS\t DEBUG - Elevator (" + l.getId() + ") is removing user (" + u.getId() + ").");
                            if (u.getOriginalDestination() == -1 || u.getOriginalDestination() == l.getCurrentLevel()) {
                                u.setHandled(true);
                            }
                            l.setUsersGettingOut(l.getUsersGettingOut() + 1);
                            l.setBoardingDelay(l.getBoardingDelay() + u.getUnboardingTime());
                        }
                    } else {
//						System.out.println(
//								"\t\t DEBUG - in elevator ("+l.getId()+"): user " + u.getId() + " | " + u.isInElevator() + " | " + l.getCurrentLevel() + "/"
//										+ u.getSourceId() + " | " + l.getCurrentLevel() + "/" + u.getDestinationId());
                        if (!u.isInElevator() && u.getSourceId() == l.getCurrentLevel()) { // INSTAPPEN
                            System.out.println(
                                    "\tSTATUS\t DEBUG - Elevator (" + l.getId() + ") is adding user (" + u.getId() + ").");
                            System.out.println(u.getTimeout() +" ... "+ u.getArrivalTime() +" ... "+ mainTicker);
                            int wachtTijd = mainTicker - (int) Math.ceil(u.getArrivalTime());
                            wachttijden.add(wachtTijd);
                            if(maxWachtTijd < wachtTijd)
                                maxWachtTijd = wachtTijd;
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
                            + l.getHandlingUsers().size() + " users.");
                    // debug:
                    for (User u : l.getHandlingUsers()) {
                        if (!u.isInElevator() && u.getSourceId() == l.getCurrentLevel()) { // instappen
                            System.out.println("\tUser (" + u.getId() + ") should be enterring.. Elevator (" + l.getId()
                                    + ") mode: " + l.getMode() + " on level " + l.getCurrentLevel());
                        } else if (u.isInElevator() && u.getDestinationId() == l.getCurrentLevel()) { // uitstappen
                            System.out.println("\tUser (" + u.getId() + ") should be leaving.. Elevator (" + l.getId()
                                    + ") mode: " + l.getMode());
                        }
                    }

                    System.out.println("\t\t DEBUG - users getting in: " + l.getUsersGettingIn()
                            + " | users getting out: " + l.getUsersGettingOut());
                    if ((l.getUsersGettingIn() + l.getUsersGettingOut()) > 0) {
                        switch (l.getMode()) {
                            case "idle":
                                l.setMode("openen");
                                l.setOperationTimer(mainTicker);

                                writeToCsv(l,null,false);
                                break;
                            case "openen":
                                if (l.getOperationTimer() + l.getOperationTimer() >= mainTicker) {
                                    l.setMode("boarding");
                                    l.setOperationTimer(mainTicker);
                                    writeToCsv(l,null,true);
                                }
                                break;
                            case "boarding":
                                double delay = 0;
                                for (User u : l.getHandlingUsers()) {
                                    if ((u.getSourceId() == l.getCurrentLevel() && !u.isInElevator())
                                            || u.getDestinationId() == l.getCurrentLevel()) {
                                        delay += u.getBoardingTime();

                                        writeToCsv(l,u,true);
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
                                        //System.out.println("\t!!\t DEBUG - user (" + u.getId() + ") - " + u.getSourceId() + ", " + u.getDestinationId() + ", " + l.getCurrentLevel());
                                        if (!u.isInElevator() && u.getSourceId() == l.getCurrentLevel()) { // instappen
                                            System.out.println("\t\t DEBUG - User (" + u.getId() + ") joined elevator");
                                            l.setCurrentUsers(l.getCurrentUsers() + 1);

                                            thisTurnTransition.getChildren().addAll(GUIController.userEnterElevator(u, l));

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
                                            //System.out.println("\t\t DEBUG - " + u.toString());


                                            thisTurnTransition.getChildren().addAll(GUIController.moveUserToLevel(u, l.getCurrentLevel()));
                                            SequentialTransition sq = new SequentialTransition();

                                            sq.getChildren().addAll(GUIController.userExitElevator(u, l));

                                            l.setCurrentUsers(l.getCurrentUsers() - 1);
                                            u.setInElevator(false);
                                            if (u.getOriginalDestination() == -1 || u.getOriginalDestination() == l.getCurrentLevel()) {
                                                u.setFinished(true);
                                                sq.getChildren().addAll(GUIController.userLeaveHall(u));
                                            } else {
                                                //reset user using the new source / old dest and put back in pool
                                                u.setArrivalTime((double) mainTicker);
                                                aantalLiftHoppers++;
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
                                            thisTurnTransition.getChildren().addAll(sq);
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
                                writeToCsv(l,null,false);
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
                // 6. follow-up from 4 -> remove handled/timed-out users
                for (User u : database.keySet())
                    if (u.isFinished()) {
                        removingUsers.add(u);
                        System.out.println("\tR\t DEBUG - removing user " + u.getId());
                    }
                for (User u : removingUsers)
                    database.remove(u);

                // 7. handle elevator movements
                for (Lift l : ec.getLifts()) {
                    //System.out.println("\t!!!\t DEBUG - " + l.toString());
                    if (l.getDirection() != 0 && l.getMovingTimer() + l.getLevelSpeed() <= mainTicker && (l.getUsersGettingIn() + l.getUsersGettingOut()) == 0) {
                        l.setNextLevel(thisTurnTransition,GUIController);
                        l.setMovingTimer(mainTicker);
                        System.out.println("\tI\t DEBUG - setting movingTimer at " + mainTicker
                                + ", next movement in atleast " + (l.getMovingTimer() + l.getLevelSpeed()));
                    }
                }
            }

            System.out.println("\t\t GUI - End of gametick adding parallelmovement");
            if(GUIController == null)
                System.out.println("k aooo");
            GUIController.sequence.getChildren().addAll(thisTurnTransition);
            mainTicker++;
        }
        System.out.println("DEBUG - queue size: " + queue.size() + " | Userlist size: " + ec.getUsers().size());

        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
        totaleRekenTijd = (int) duration;
        totaleAfhandelTijd = mainTicker;

        System.out.println("Results...");
        System.out.println("\t Totale wachttijd: " + getTotaleWachttijd() + " gameticks");
        System.out.println("\t Datasets wachttijden: " + wachttijden.toString());
        System.out.println("\t Maximale wachttijd: " + getMaxWachtTijd() + " gameticks");
        System.out.println("\t Totale rekentijd: " + getTotaleRekenTijd() + " ms");
        System.out.println("\t Totale afhandeltijd: " + getTotaleAfhandelTijd() + " gameticks");
        System.out.println("\t Aantal timeouts: " + getAantalTimeouts());
        System.out.println("\t Aantal lift hoppers: " + getAantalLiftHoppers());

        System.out.println("\n\n\n\n");

        System.out.println("\tGUI - Playing everything");
        GUIController.sequence.play();
        GUIController.showData();
    }


    private void writeToCsv(Lift tempLift, User u, boolean open) {
        String users = null;
        try {
            users = createUserString(tempLift,u);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> info = new ArrayList<>();

        info.add(tempLift.getId() + "");
        info.add(mainTicker + "");
        info.add(tempLift.getCurrentLevel() + "");
        info.add(users);
        if (open) {
            info.add("true");
        } else {
            info.add("false");
        }

        try {
            CSVUtils.writeLine(writer, info);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createUserString(Lift tempLift, User u) throws Exception {
        StringBuilder users = null;
        List<User> list = new ArrayList<>(tempLift.getHandlingUsers());
        if(u != null){
            int index = -1;
            for (int i = 0; i <list.size();i++){
                if(list.get(i).getId() == u.getId()){
                    index = i;
                }
            }
            if(index == -1)
                throw new Exception();

            list.remove(index);
        }

        for (int i = 0; i < list.size(); i++) {
            users = new StringBuilder();
            users.append(tempLift.getHandlingUsers().get(i).getId());
            if (i != tempLift.getHandlingUsers().size() - 1) {
                users.append(",");
            }
        }
        if(users!=null){
            return users.toString();
        }else{
            users = new StringBuilder();
            users.append("null");
            return users.toString();
        }
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

                GUIController.makeUserOnLevel(ec.getUsers().get(i), ec.getUsers().get(i).getSourceId());

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

    //TODO LELELELELELE
    public Lift assignElevator(User u) {
        Lift returnLift = null;
        int distance = ec.getLevels().size() + 100;

        // first check if there are no idle elevators
        // || WE DO CHECK ON CAPACITY, BUG-PREVENTION
        for (Lift l : ec.getLifts()) {
            if (l.getDirection() == 0 && l.isInRange(u.getSourceId()) && l.isInRange(u.getDestinationId())) {
                if (distance > Math.abs(u.getSourceId() - l.getCurrentLevel())
                        && l.getCurrentUsers() < l.getCapacity()) {
                    returnLift = l;
                    distance = Math.abs(u.getSourceId() - l.getCurrentLevel());
                }
            }
        }

        if (returnLift == null) {
            // check if we can assign a lift which is in use
            for (Lift l : ec.getLifts()) {
                // check if available (in use)
                if (l.getUnavailableUntil() > mainTicker
                        // check if full && is able to handle
                        && l.getCurrentUsers() < l.getCapacity() && l.isInRange(u.getSourceId())
                        && l.isInRange(u.getDestinationId())) {
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
        }

        if (returnLift == null) {
            System.out.println("\tL\t DEBUG - no suitable elevator found at the moment for user " + u.getId());
            boolean rip = true;
            for (Lift l : ec.getLifts()) {
                //System.out.println(u.getSourceId() + " & " + u.getDestinationId() + " in " + l.toString());
                //System.out.println(l.isInRange(u.getSourceId()) + " and " + l.isInRange(u.getDestinationId()));
                if (l.isInRange(u.getSourceId()) && l.isInRange(u.getDestinationId())) {
                    rip = false;
                }
            }

            if (rip) {
                //geen lift kan de gebruiker volledig helpen...
                System.out.println("\tL\t DEBUG - There exists no elevator who can support user " + u.getId());
                int afstand = Integer.MAX_VALUE;

                //kijken naar liften die idle zijn (direction == 0)
                for (Lift l : ec.getLifts()) {
                    if (l.getDirection() == 0 && l.isInRange(u.getSourceId())) {
                        for (int i = 0; i < l.getRange().size(); i++) {
                            if (l.getRange().get(i).getId() - u.getSourceId() < afstand && l.getRange().get(i).getId() - u.getSourceId() > 0) {
                                returnLift = l;
                                afstand = l.getRange().get(i).getId() - u.getSourceId();
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
                                && l.isInRange(u.getSourceId())) {
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

            if (returnLift != null) {
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

    public void setGUIController(Controller GUIController) {
        this.GUIController = GUIController;
    }

    public int getMainTicker() {
        return mainTicker;
    }

    public List<Integer> getWachttijden() {
        return wachttijden;
    }

    public int getMaxWachtTijd() {
        return maxWachtTijd;
    }

    public int getTotaleRekenTijd() {
        return totaleRekenTijd;
    }

    public int getTotaleAfhandelTijd() {
        return totaleAfhandelTijd;
    }

    public int getAantalTimeouts() {
        return aantalTimeouts;
    }

    public int getAantalLiftHoppers() {
        return aantalLiftHoppers;
    }

    public int getTotaleWachttijd() {
        int sum = 0;
        for(int i : wachttijden) {
            sum += i;
        }
        return sum;
    }
}
