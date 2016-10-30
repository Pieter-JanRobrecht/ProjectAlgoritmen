import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import Model.ManagementSystem;

public class Main {

	public static void main(String[] args) {
		File file = null;
		try {
			file = new File(Main.class.getClassLoader().getResource("initialInstance.json").toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
    	ManagementSystem ms = null;

			try {
			ms = (ManagementSystem) objectMapper.readValue(file, ManagementSystem.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if (ms != null) {
        	System.out.println(ms.getLevels().toString());
        	System.out.println("\n");
        	System.out.println(ms.getLifts().toString());
        	System.out.println("\n");
        	System.out.println("Aantal users: " + ms.getUsers().size());
        }
	}

}
