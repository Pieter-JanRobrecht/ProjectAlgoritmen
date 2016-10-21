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
    @JsonProperty("currentUsers")
    private Integer currentUsers;
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
    @JsonProperty("currentLevel")
    private Integer currentLevel;
    @JsonProperty("direction")
    private Boolean direction;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
     *     The currentUsers
     */
    @JsonProperty("currentUsers")
    public Integer getCurrentUsers() {
        return currentUsers;
    }

    /**
     * 
     * @param currentUsers
     *     The currentUsers
     */
    @JsonProperty("currentUsers")
    public void setCurrentUsers(Integer currentUsers) {
        this.currentUsers = currentUsers;
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

    /**
     * 
     * @return
     *     The currentLevel
     */
    @JsonProperty("currentLevel")
    public Integer getCurrentLevel() {
        return currentLevel;
    }

    /**
     * 
     * @param currentLevel
     *     The currentLevel
     */
    @JsonProperty("currentLevel")
    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }

    /**
     * 
     * @return
     *     The direction
     */
    @JsonProperty("direction")
    public Boolean getDirection() {
        return direction;
    }

    /**
     * 
     * @param direction
     *     The direction
     */
    @JsonProperty("direction")
    public void setDirection(Boolean direction) {
        this.direction = direction;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

	@Override
	public String toString() {
		return "\n Lift [id=" + id + ", capacity=" + capacity + ", currentUsers=" + currentUsers + ", levelSpeed="
				+ levelSpeed + ", openingTime=" + openingTime + ", closingTime=" + closingTime + ", range=" + range
				+ ", startLevel=" + startLevel + ", currentLevel=" + currentLevel + ", direction=" + direction
				+ "] \n";
	}

}
