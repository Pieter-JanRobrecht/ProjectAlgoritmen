package Controller;

import java.util.ArrayList;
import java.util.List;

import Model.*;

public class ElevatorController {
	private List<Level> levels = new ArrayList<Level>();
	private List<Lift> lifts = new ArrayList<Lift>();
	private List<User> users = new ArrayList<User>();
	
	public ElevatorController(List<Level> levels, List<Lift> lifts, List<User> users) {
		this.levels = levels;
		this.lifts = lifts;
		this.users = users;
	}
	
	public ElevatorController(ManagementSystem ms) {
		this.levels = ms.getLevels();
		this.lifts = ms.getLifts();
		this.users = ms.getUsers();
	}
	
	public List<Level> getLevels() {
		return levels;
	}
	
	public void setLevels(List<Level> levels) {
		this.levels = levels;
	}
	
	public List<Lift> getLifts() {
		return lifts;
	}
	
	public void setLifts(List<Lift> lifts) {
		this.lifts = lifts;
	}
	
	public List<User> getUsers() {
		return users;
	}
	
	public void setUsers(List<User> users) {
		this.users = users;
	}
}
