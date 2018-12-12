package airbus.paris.scenario;

import java.util.HashSet;
import java.util.Set;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.NetworkReaderMatsimV2;
import org.matsim.core.scenario.ScenarioUtils;

public class AddPassengerModeToNetwork {

	public static void main(String[] args) {

		Config config = ConfigUtils.createConfig();
		
		Scenario scenario = ScenarioUtils.createScenario(config);
		
		NetworkReaderMatsimV2 netReader = new NetworkReaderMatsimV2(scenario.getNetwork());
		
		netReader.readFile(args[0]);
		
		for (Link link : scenario.getNetwork().getLinks().values()) {
			
			if (link.getAllowedModes().contains("car")) {
				Set<String> newEntry = new HashSet<>();
			
				for (String s : link.getAllowedModes()) 
					newEntry.add(s);
				newEntry.add("car_passenger");
				link.setAllowedModes(newEntry);
			}
		}
		
		
		NetworkWriter netWriter = new NetworkWriter(scenario.getNetwork());
		
		netWriter.writeV2(args[1]);
		
	}

}
