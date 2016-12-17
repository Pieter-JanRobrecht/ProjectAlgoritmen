package DataGenereren;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import Model.Level;
import Model.Lift;
import Model.ManagementSystem;
import Model.Range;
import Model.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;


import java.io.FileWriter;  
import java.io.IOException;  
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import Model.Level;
import Model.Lift;
import Model.ManagementSystem;
import Model.Range;
import Model.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;  

public class GenDataController {
	

	@FXML
	private Slider levelsSlider;
	@FXML
	private Slider levelsSlider1;
	@FXML
	private Slider liftSlider1;
	@FXML
	private Slider liftSlider2;
	@FXML
	private Slider userSlider;
	@FXML
	private Slider userSlider1;
	@FXML
	private Slider userSlider2;
	@FXML
	private Slider userSlider3;
	@FXML
	private Slider variableSlider;
	@FXML
	private Slider variableSlider1;
	@FXML
	private Slider variableSlider2;
	@FXML
	private Slider variableSlider3;
	@FXML
	private Slider variableSlider4;
	@FXML
	private Slider variableSlider5;
	@FXML
	private Slider variableSlider6;
	@FXML
	private TextField textField;
	@FXML
	protected TextField outputTextAreaLevels;
	@FXML
	protected TextField outputTextAreaLevels1;
	@FXML
	protected TextField outputTextAreaLift1;
	@FXML
	protected TextField outputTextAreaLift2;
	@FXML
	protected TextField outputTextAreaUser;
	@FXML
	protected TextField outputTextAreaUser1;
	@FXML
	protected TextField outputTextAreaUser2;
	@FXML
	protected TextField outputTextAreaUser3;
	
	@FXML
	protected CheckBox offSetCheckbox;
	
	@FXML
	protected Label statusLabel;
	
	@FXML protected ComboBox<String> spreidingCombo;
	
	
	
	
	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public GenDataController() {
	}
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		levelsSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				
				outputTextAreaLevels.setText(""+ newValue.intValue() );
			}
		});
		levelsSlider1.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				
				outputTextAreaLevels1.setText(""+newValue.intValue() ) ;
			}
		});
		liftSlider1.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				
				outputTextAreaLift1.setText(""+ newValue.intValue() );
			}
		});
		liftSlider2.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				
				outputTextAreaLift2.setText(""+ newValue.intValue() );
			}
		});
		userSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				
				outputTextAreaUser.setText(""+ newValue.intValue() );
			}
		});
		userSlider1.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				
				outputTextAreaUser1.setText(""+ newValue.intValue() );
			}
		});
		userSlider2.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				
				outputTextAreaUser2.setText(""+ newValue.intValue() );
			}
		});
		userSlider3.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				
				outputTextAreaUser3.setText(""+ newValue.intValue() );
			}
		});
	

	}
	
	public void generateJson() {
		double offsetPercentage=(offSetCheckbox.isSelected())?variableSlider.getValue()/100:0;
		
		Random random= new Random();
		double  arrivalTime=0;

		int liftCapacity=(int) variableSlider3.getValue();
		double levelSpeed=variableSlider6.getValue();
		double openingTime = variableSlider4.getValue();
		double closingTime=variableSlider5.getValue();;
		//TODO
		String spreiding=spreidingCombo.getValue();
	
		double boardingTime=variableSlider1.getValue();
		double unboardingTime=variableSlider2.getValue();
		int timeout=100;

		boolean [] levelsReached;
	//	double chanceOfStopOnFloor=0;//1-levelsSlider1.getValue()/100; //0= stops on every floor 0.5= stops on roughly half of the floors
		double chanceOfStopOnFloor=1-levelsSlider1.getValue()/100; //0= stops on every floor 0.5= stops on roughly half of the floors


		int amountOfLevels=(int)levelsSlider.getValue();
		int amountOfLifts= (int)liftSlider1.getValue();
		int amountOfUsers= (int) userSlider.getValue();
		int tempoOfArrival=(int) userSlider3.getValue();

		double patiencePercentage=userSlider2.getValue();
		double oldAgePercentage=userSlider1.getValue();
		double brokenElevators=liftSlider2.getValue();



		//INSTELLEN VERDIEPEN


		levelsReached= new boolean [amountOfLevels];
		ArrayList <Level> levels= new ArrayList<Level>();
		for (int i=0; i< amountOfLevels;i++){
			Level lvl= new Level();
			lvl.setId(i);
			levels.add(lvl);}

		//INSTELLEN LIFTEN
	
		ArrayList <Lift> lifts= new ArrayList<Lift>();
		for (int i=0; i< amountOfLifts;i++){
			Lift lft= new Lift();
			List<Range> range = new ArrayList<Range>();
			lft.setId(i);
			lft.setCapacity(liftCapacity);
			lft.setLevelSpeed(levelSpeed);
			if (random.nextDouble()>brokenElevators/100) {lft.setOpeningTime(openingTime*(1-offsetPercentage+2*offsetPercentage*random.nextDouble())); lft.setClosingTime(closingTime*(1-offsetPercentage+2*offsetPercentage*random.nextDouble()));} else {lft.setOpeningTime((1-offsetPercentage+2*offsetPercentage*random.nextDouble())*openingTime*5); lft.setClosingTime((1-offsetPercentage+2*offsetPercentage*random.nextDouble())*closingTime*5);}
			
			
			
			for(int j=0; j< amountOfLevels; j++){
				if((random.nextDouble()) > chanceOfStopOnFloor) {Range rng= new Range(); rng.setId(j); levelsReached[j]=true;range.add(rng);
				
				}
			
				
			}
			lft.setRange(range);
			lft.setStartLevel(range.get(random.nextInt(range.size())).getId() );
			lifts.add(lft);
			//OP HET EINDE CONTROLEREN OF ALLE VERDIEPINGEN KUNNEN BEREIKT WORDEN, ZONIET RANDOM TOEVOEGEN
			if (i== amountOfLifts-1) {
				for (int k=0; k< levelsReached.length; k++) {
					Range tempRange= new Range(); 
					if (!levelsReached[k]) {
						tempRange.setId(k); 
						int o=random.nextInt(amountOfLifts);
						lifts.get(o).getRange().add(tempRange);
						Collections.sort(lifts.get(0).getRange(), new Comparator<Range>(){
						    public int compare(Range s1, Range s2) {
						        return s1.getId()-s2.getId();
						    }
						});
					} }
			
			}}

		//INSTELLEN USERS

	
		ArrayList <User> users= new ArrayList<User>();
		for (int i=0; i< amountOfUsers;i++){
			User usr= new User();
			usr.setId(i);
			double temp;
			switch(spreiding){
			case "'s Ochtends": if(arrivalTime<300) temp=arrivalTime+ random.nextInt(tempoOfArrival/2); else temp=arrivalTime+ random.nextInt(tempoOfArrival); usr.setArrivalTime(temp); arrivalTime= temp;break;
			case "'s Middags": if(arrivalTime>300 && arrivalTime<600) temp=arrivalTime+ random.nextInt(tempoOfArrival/2); else temp=arrivalTime+ random.nextInt(tempoOfArrival);usr.setArrivalTime(temp);arrivalTime=temp; break;
			case "'s Avonds": if(arrivalTime>600) temp=arrivalTime+ random.nextInt(tempoOfArrival/2); else temp=arrivalTime+ random.nextInt(tempoOfArrival);usr.setArrivalTime(temp);arrivalTime= temp; break;
			case "Gelijktijdig verdeeld over de dag": temp=arrivalTime+ random.nextInt(tempoOfArrival);usr.setArrivalTime(temp);arrivalTime=temp; break;
			default: temp=arrivalTime+ random.nextInt(tempoOfArrival);usr.setArrivalTime(temp);arrivalTime= temp;
			}
			
			
			
			double tempBoardingTime= boardingTime* (1-offsetPercentage+2*offsetPercentage*random.nextDouble());
			double tempUnboardingTime= unboardingTime* (1-offsetPercentage+2*offsetPercentage*random.nextDouble());
			if(random.nextDouble()> oldAgePercentage/100) {usr.setUnboardingTime(tempUnboardingTime);usr.setBoardingTime(tempBoardingTime);} else {usr.setBoardingTime(2*tempBoardingTime); usr.setUnboardingTime(2*tempUnboardingTime);}
			
			//if(random.nextDouble()<patiencePercentage/100) usr.setTimeout(timeout); else 
			usr.setTimeout((int) ((timeout*patiencePercentage/100)* (1-offsetPercentage+2*offsetPercentage*random.nextDouble())));
			usr.setSourceId(random.nextInt(amountOfLevels));
			int temp2= usr.getSourceId();
			while(temp2==usr.getSourceId()) {temp2= random.nextInt(amountOfLevels);}
			usr.setDestinationId(temp2);
			
			users.add(usr);
		}


		  


		  
		  ManagementSystem msObj=new ManagementSystem();  
		  msObj.setLevels(levels);  
		  msObj.setLifts(lifts); 
		  msObj.setUsers(users);
		
		    
		
		  Gson gson = new GsonBuilder().setPrettyPrinting().setExclusionStrategies(new AnnotationExclusionStrategy()).create();
		    
		  // convert java object to JSON format,  
		  // and returned as JSON formatted string  
		  String json = gson.toJson(msObj);   
		  
		    
		  try {  
		   //write converted json data to a file named "test.json"  
		   FileWriter writer = new FileWriter("data/test.json");  
		   writer.write(json);  
		   writer.close();  
		    
		  } catch (IOException e) {  
		   e.printStackTrace();  
		  }  
		    
		  System.out.println(json);  
		  statusLabel.setText("Zie test.json in /data");
		    
		     }
	
	
	}
	


	


	
