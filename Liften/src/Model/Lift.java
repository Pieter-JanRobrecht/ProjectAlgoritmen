package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    "capacity",
    "currentUsers",
    "levelSpeed",
    "openingTime",
    "closingTime",
    "range",
    "startLevel",
    "currentLevel",
    "direction"
})
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
     *     The capacity
     */
    @JsonProperty("capacity")
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * 
     * @param capacity
     *     The capacity
     */
    @JsonProperty("capacity")
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * 
     * @return
     *     The levelSpeed
     */
    @JsonProperty("levelSpeed")
    public Integer getLevelSpeed() {
        return levelSpeed;
    }

    /**
     * 
     * @param levelSpeed
     *     The levelSpeed
     */
    @JsonProperty("levelSpeed")
    public void setLevelSpeed(Integer levelSpeed) {
        this.levelSpeed = levelSpeed;
    }

    /**
     * 
     * @return
     *     The openingTime
     */
    @JsonProperty("openingTime")
    public Integer getOpeningTime() {
        return openingTime;
    }

    /**
     * 
     * @param openingTime
     *     The openingTime
     */
    @JsonProperty("openingTime")
    public void setOpeningTime(Integer openingTime) {
        this.openingTime = openingTime;
    }

    /**
     * 
     * @return
     *     The closingTime
     */
    @JsonProperty("closingTime")
    public Integer getClosingTime() {
        return closingTime;
    }

    /**
     * 
     * @param closingTime
     *     The closingTime
     */
    @JsonProperty("closingTime")
    public void setClosingTime(Integer closingTime) {
        this.closingTime = closingTime;
    }

    /**
     * 
     * @return
     *     The range
     */
    @JsonProperty("range")
    public List<Range> getRange() {
        return range;
    }

    /**
     * 
     * @param range
     *     The range
     */
    @JsonProperty("range")
    public void setRange(List<Range> range) {
        this.range = range;
    }

    /**
     * 
     * @return
     *     The startLevel
     */
    @JsonProperty("startLevel")
    public Integer getStartLevel() {
        return startLevel;
    }

    /**
     * 
     * @param startLevel
     *     The startLevel
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

	@Override
	public String toString() {
		return "\n Lift [id=" + id + ", capacity=" + capacity + ", currentUsers=" + currentUsers + ", levelSpeed="
				+ levelSpeed + ", openingTime=" + openingTime + ", closingTime=" + closingTime + ", range=" + range
				+ ", startLevel=" + startLevel + ", currentLevel=" + currentLevel + ", direction=" + direction
				+ "] \n";
	}

}
