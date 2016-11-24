

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
    "levels",
    "lifts",
    "users"
})
public class ManagementSystem {

    @JsonProperty("levels")
    private List<Level> levels = new ArrayList<Level>();
    @JsonProperty("lifts")
    private List<Lift> lifts = new ArrayList<Lift>();
    @JsonProperty("users")
    private List<User> users = new ArrayList<User>();
//    @JsonIgnore
//    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The levels
     */
    @JsonProperty("levels")
    public List<Level> getLevels() {
        return levels;
    }

    /**
     * 
     * @param levels
     *     The levels
     */
    @JsonProperty("levels")
    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }

    /**
     * 
     * @return
     *     The lifts
     */
    @JsonProperty("lifts")
    public List<Lift> getLifts() {
        return lifts;
    }

    /**
     * 
     * @param lifts
     *     The lifts
     */
    @JsonProperty("lifts")
    public void setLifts(List<Lift> lifts) {
        this.lifts = lifts;
    }

    /**
     * 
     * @return
     *     The users
     */
    @JsonProperty("users")
    public List<User> getUsers() {
        return users;
    }

    /**
     * 
     * @param users
     *     The users
     */
    @JsonProperty("users")
    public void setUsers(List<User> users) {
        this.users = users;
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

	@Override
	public String toString() {
		return "ManagementSystem [levels=" + levels + ", lifts=" + lifts + ", users=" + users
				+ "] \n";
	}

    
    
}
