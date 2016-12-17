
package Model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
    @JsonProperty("source-id")
    private Integer sourceId;
    @JsonProperty("destination-id")
    private Integer destinationId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private boolean inElevator;
    private boolean finished;
    private boolean handled;

    @JsonIgnore
    private Sphere sphere;
    private int originalSource = -1;
    private int originalDestination = -1;
    
    private boolean liftHopper;

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
        if(destinationId > sourceId) {
            up = true;
        } else {
            up = false;
        }
    }
    /**
     * 
     * @return
     *     The id
     */
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The arrivalTime
     */
    @JsonProperty("arrivalTime")
    public Double getArrivalTime() {
        return arrivalTime;
    }

    /**
     * 
     * @param arrivalTime
     *     The arrivalTime
     */
    @JsonProperty("arrivalTime")
    public void setArrivalTime(Double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     * 
     * @return
     *     The boardingTime
     */
    @JsonProperty("boardingTime")
    public Double getBoardingTime() {
        return boardingTime;
    }

    /**
     * 
     * @param boardingTime
     *     The boardingTime
     */
    @JsonProperty("boardingTime")
    public void setBoardingTime(Double boardingTime) {
        this.boardingTime = boardingTime;
    }

    /**
     * 
     * @return
     *     The unboardingTime
     */
    @JsonProperty("unboardingTime")
    public Double getUnboardingTime() {
        return unboardingTime;
    }

    /**
     * 
     * @param unboardingTime
     *     The unboardingTime
     */
    @JsonProperty("unboardingTime")
    public void setUnboardingTime(Double unboardingTime) {
        this.unboardingTime = unboardingTime;
    }

    /**
     * 
     * @return
     *     The timeout
     */
    @JsonProperty("timeout")
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * 
     * @param timeout
     *     The timeout
     */
    @JsonProperty("timeout")
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * 
     * @return
     *     The sourceId
     */
    @JsonProperty("source-id")
    public Integer getSourceId() {
        return sourceId;
    }

    /**
     * 
     * @param sourceId
     *     The source-id
     */
    @JsonProperty("source-id")
    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * 
     * @return
     *     The destinationId
     */
    @JsonProperty("destination-id")
    public Integer getDestinationId() {
        return destinationId;
    }

    /**
     * 
     * @param destinationId
     *     The destination-id
     */
    @JsonProperty("destination-id")
    public void setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

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
                ", additionalProperties=" + additionalProperties +
                ", inElevator=" + inElevator +
                ", finished=" + finished +
                ", handled=" + handled +
                ", originalSource=" + originalSource +
                ", originalDestination=" + originalDestination +
                ", liftHopper=" + liftHopper +
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
