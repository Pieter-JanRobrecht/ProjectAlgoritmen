
package Model;

import DataGenereren.Exclude;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.SerializedName;
import javafx.scene.shape.Sphere;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "arrivalTime",
        "boardingTime",
        "unboardingTime",
        "timeout",
        "source-id",
        "destination-id"
})
public class User {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("arrivalTime")
    private Double arrivalTime;
    @JsonProperty("boardingTime")
    private Double boardingTime;
    @JsonProperty("unboardingTime")
    private Double unboardingTime;
    @JsonProperty("timeout")
    private Integer timeout;
    // @JsonProperty("source-id")
    @SerializedName("source-id")
    private Integer sourceId;
    //@JsonProperty("destination-id")
    @SerializedName("destination-id")
    private Integer destinationId;
    //    @JsonIgnore
//    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    @Exclude
    private boolean inElevator;
    @Exclude
    private boolean finished;
    @Exclude
    private boolean handled;

    @JsonIgnore
    private Sphere sphere;
    @Exclude
    private int originalSource = -1;
    @Exclude
    private int originalDestination = -1;

    @Exclude
    private boolean liftHopper;

    @Exclude
    private boolean up;

    public User() {
        inElevator = false;
        finished = false;
        handled = false;
    }

    public void initialize() {
        originalDestination = destinationId;
        originalSource = sourceId;
        liftHopper = false;
        if (destinationId > sourceId) {
            up = true;
        } else {
            up = false;
        }
    }

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
     * @return The arrivalTime
     */
    @JsonProperty("arrivalTime")
    public Double getArrivalTime() {
        return arrivalTime;
    }

    /**
     * @param arrivalTime The arrivalTime
     */
    @JsonProperty("arrivalTime")
    public void setArrivalTime(Double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     * @return The boardingTime
     */
    @JsonProperty("boardingTime")
    public Double getBoardingTime() {
        return boardingTime;
    }


    @JsonProperty("boardingTime")
    public void setBoardingTime(Double boardingTime) {
        this.boardingTime = boardingTime;
    }

    /**
     * @return The unboardingTime
     */
    @JsonProperty("unboardingTime")
    public Double getUnboardingTime() {
        return unboardingTime;
    }

    /**
     * @param unboardingTime The unboardingTime
     */
    @JsonProperty("unboardingTime")
    public void setUnboardingTime(Double unboardingTime) {
        this.unboardingTime = unboardingTime;
    }

    /**
     * @return The timeout
     */
    @JsonProperty("timeout")
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * @param timeout The timeout
     */
    @JsonProperty("timeout")
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * @return The sourceId
     */
    @JsonProperty("source-id")
    public Integer getSourceId() {
        return sourceId;
    }

    /**
     * @param sourceId The source-id
     */
    @JsonProperty("source-id")
    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * @return The destinationId
     */
    @JsonProperty("destination-id")
    public Integer getDestinationId() {
        return destinationId;
    }

    /**
     * @param destinationId The destination-id
     */
    @JsonProperty("destination-id")
    public void setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
    }

//    @JsonAnyGetter
//    public Map<String, Object> getAdditionalProperties() {
//        return this.additionalProperties;
//    }
//
//    @JsonAnySetter
//    public void setAdditionalProperty(String name, Object value) {
//        this.additionalProperties.put(name, value);
//    }

    public boolean isInElevator() {
        return inElevator;
    }

    public void setInElevator(boolean inElevator) {
        this.inElevator = inElevator;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public int getOriginalSource() {
        return originalSource;
    }

    public void setOriginalSource(int originalSource) {
        this.originalSource = originalSource;
    }

    public int getOriginalDestination() {
        return originalDestination;
    }

    public void setOriginalDestination(int originalDestination) {
        this.originalDestination = originalDestination;
    }

    public boolean isLiftHopper() {
        return liftHopper;
    }

    public void setLiftHopper(boolean liftHopper) {
        this.liftHopper = liftHopper;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", arrivalTime=" + arrivalTime +
                ", boardingTime=" + boardingTime +
                ", unboardingTime=" + unboardingTime +
                ", timeout=" + timeout +
                ", sourceId=" + sourceId +
                ", destinationId=" + destinationId +
                ", inElevator=" + inElevator +
                ", finished=" + finished +
                ", handled=" + handled +
                ", originalSource=" + originalSource +
                ", originalDestination=" + originalDestination +
                ", liftHopper=" + liftHopper +
                '}';
    }

    public Sphere getSphere() {
        return sphere;
    }

    public void setSphere(Sphere sphere) {
        this.sphere = sphere;
    }
}
