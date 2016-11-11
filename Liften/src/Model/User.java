
package Model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
    private Integer boardingTime;
    @JsonProperty("unboardingTime")
    private Integer unboardingTime;
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
    
    public User() {
    	inElevator = false;
    	finished = false;
    	handled = false;
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
    public Integer getBoardingTime() {
        return boardingTime;
    }

    /**
     * 
     * @param boardingTime
     *     The boardingTime
     */
    @JsonProperty("boardingTime")
    public void setBoardingTime(Integer boardingTime) {
        this.boardingTime = boardingTime;
    }

    /**
     * 
     * @return
     *     The unboardingTime
     */
    @JsonProperty("unboardingTime")
    public Integer getUnboardingTime() {
        return unboardingTime;
    }

    /**
     * 
     * @param unboardingTime
     *     The unboardingTime
     */
    @JsonProperty("unboardingTime")
    public void setUnboardingTime(Integer unboardingTime) {
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

	@Override
	public String toString() {
		return "User [id=" + id + ", arrivalTime=" + arrivalTime + ", boardingTime=" + boardingTime
				+ ", unboardingTime=" + unboardingTime + ", timeout=" + timeout + ", sourceId=" + sourceId
				+ ", destinationId=" + destinationId + ", additionalProperties=" + additionalProperties
				+ ", inElevator=" + inElevator + ", finished=" + finished + ", handled=" + handled + "]";
	}
}
