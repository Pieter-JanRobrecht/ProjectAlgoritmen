package Model;

import Voorbeeld.Controller;
import com.fasterxml.jackson.annotation.*;
import javafx.animation.ParallelTransition;
import javafx.scene.shape.Box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "capacity", "currentUsers", "levelSpeed", "openingTime", "closingTime", "range",
        "startLevel", "currentLevel", "direction"})
public class Lift {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("capacity")
    private Integer capacity;
    @JsonProperty("levelSpeed")
    private Integer levelSpeed;
    @JsonProperty("openingTime")
    private Integer openingTime;
    @JsonProperty("closingTime")
    private Integer closingTime;
    @JsonProperty("range")
    private List<Range> range = new ArrayList<Range>();
    @JsonProperty("startLevel")
    private Integer startLevel;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private int currentUsers;
    private int currentLevel;
    private int direction;
    private int unavailableUntil;
    private int destination;
    private int operationTimer;
    private String mode;
    private int movingTimer;

    private int usersGettingIn;
    private int usersGettingOut;
    private List<User> handlingUsers;
    private int boardingDelay;

    @JsonIgnore
    private Box box;

    /**
     * @return The id
     */
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The capacity
     */
    @JsonProperty("capacity")
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * @param capacity The capacity
     */
    @JsonProperty("capacity")
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * @return The levelSpeed
     */
    @JsonProperty("levelSpeed")
    public Integer getLevelSpeed() {
        return levelSpeed;
    }

    /**
     * @param levelSpeed The levelSpeed
     */
    @JsonProperty("levelSpeed")
    public void setLevelSpeed(Integer levelSpeed) {
        this.levelSpeed = levelSpeed;
    }

    /**
     * @return The openingTime
     */
    @JsonProperty("openingTime")
    public Integer getOpeningTime() {
        return openingTime;
    }

    /**
     * @param openingTime The openingTime
     */
    @JsonProperty("openingTime")
    public void setOpeningTime(Integer openingTime) {
        this.openingTime = openingTime;
    }

    /**
     * @return The closingTime
     */
    @JsonProperty("closingTime")
    public Integer getClosingTime() {
        return closingTime;
    }

    /**
     * @param closingTime The closingTime
     */
    @JsonProperty("closingTime")
    public void setClosingTime(Integer closingTime) {
        this.closingTime = closingTime;
    }

    /**
     * @return The range
     */
    @JsonProperty("range")
    public List<Range> getRange() {
        return range;
    }

    /**
     * @param range The range
     */
    @JsonProperty("range")
    public void setRange(List<Range> range) {
        this.range = range;
    }

    /**
     * @return The startLevel
     */
    @JsonProperty("startLevel")
    public Integer getStartLevel() {
        return startLevel;
    }

    /**
     * @param startLevel The startLevel
     */
    @JsonProperty("startLevel")
    public void setStartLevel(Integer startLevel) {
        this.startLevel = startLevel;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public int getCurrentUsers() {
        return currentUsers;
    }

    public void setCurrentUsers(int currentUsers) {
        this.currentUsers = currentUsers;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getUnavailableUntil() {
        return unavailableUntil;
    }

    public void setUnavailableUntil(int unavailableUntil) {
        this.unavailableUntil = unavailableUntil;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getOperationTimer() {
        return operationTimer;
    }

    public void setOperationTimer(int operationTimer) {
        this.operationTimer = operationTimer;
    }

    public int getMovingTimer() {
        return movingTimer;
    }

    public void setMovingTimer(int movingTimer) {
        this.movingTimer = movingTimer;
    }

    public int getUsersGettingIn() {
        return usersGettingIn;
    }

    public void setUsersGettingIn(int usersGettingIn) {
        this.usersGettingIn = usersGettingIn;
    }

    public int getUsersGettingOut() {
        return usersGettingOut;
    }

    public void setUsersGettingOut(int usersGettingOut) {
        this.usersGettingOut = usersGettingOut;
    }

    public List<User> getHandlingUsers() {
        return handlingUsers;
    }

    public void setHandlingUsers(List<User> handlingUsers) {
        this.handlingUsers = handlingUsers;
    }

    public void addHandlingUser(User u) {
        this.handlingUsers.add(u);
    }

    public void removeHandlingUser(User u) {
        this.handlingUsers.remove(u);
    }

    public int getBoardingDelay() {
        return boardingDelay;
    }

    public void setBoardingDelay(int boardingDelay) {
        this.boardingDelay = boardingDelay;
    }

    public void initiateLift() {
        currentUsers = 0;
        if (range != null)
            currentLevel = range.get(0).getId();
        direction = 0; // idle
        unavailableUntil = -1;
        destination = -1;
        operationTimer = 0;
        mode = "idle";
        movingTimer = 0;
        usersGettingIn = 0;
        usersGettingOut = 0;
        handlingUsers = new ArrayList<User>();
        System.out.println("\t.\t DEBUG - initiated " + toString());
    }

    @JsonIgnore
    public void setNextLevel(ParallelTransition transition, Controller GUIController) {
        transition.getChildren().addAll(GUIController.moveElevator(GUIController.getMs().getLifts().get(id), direction));

        if (direction == 1) {
            System.out.println("\t^\t DEBUG - Elevator (" + id + ") is ascending!");
            for (int i = 0; i < range.size(); i++) {
                if (range.get(i).getId() == currentLevel) {
                    if (i + 1 < range.size())
                        currentLevel = range.get(i + 1).getId();
                    i = range.size();
                }
            }
        } else if (direction == -1) {
            System.out.println("\tv\t DEBUG - Elevator (" + id + ") is decending!");
            for (int i = 0; i < range.size(); i++) {
                if (range.get(i).getId() == currentLevel) {
                    if (i > 0)
                        currentLevel = range.get(i - 1).getId();
                    i = range.size();
                }
            }
        } else {
            System.out.println("\t!\t DEBUG - something went wrong, setting next level whilst idling: ");
            System.out.println("\t\t\t" + toString());
        }
    }

    public boolean hasUsersOnFloor() {
        boolean found = false;
        for (User u : handlingUsers) {
            if (!u.isInElevator() && u.getSourceId() == currentLevel) {
                usersGettingIn++;
                found = true;
            } else if (u.isInElevator() && u.getDestinationId() == currentLevel) {
                usersGettingOut++;
                found = true;
            }
        }

        return found;
    }

    @Override
    public String toString() {
        return "Lift [id=" + id + ", capacity=" + capacity + ", levelSpeed=" + levelSpeed + ", openingTime="
                + openingTime + ", closingTime=" + closingTime + ", range=" + range + ", startLevel=" + startLevel
                + ", additionalProperties=" + additionalProperties + ", currentUsers=" + currentUsers
                + ", currentLevel=" + currentLevel + ", direction=" + direction + ", unavailableUntil="
                + unavailableUntil + ", destination=" + destination + ", operationTimer=" + operationTimer + ", mode="
                + mode + ", movingTimer=" + movingTimer + ", usersGettingIn=" + usersGettingIn + ", usersGettingOut="
                + usersGettingOut + ", handlingUsers=" + handlingUsers + ", boardingDelay=" + boardingDelay + "]";
    }

    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
    }
}
