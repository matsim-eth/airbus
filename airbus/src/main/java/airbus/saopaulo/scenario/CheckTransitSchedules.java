package airbus.saopaulo.scenario;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

public class CheckTransitSchedules {

	public static void main(String[] args) {

		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
		
		TransitScheduleReader transitReader = new TransitScheduleReader(scenario);
		
		transitReader.readFile(args[0]);
		
		Config config2 = ConfigUtils.createConfig();
		Scenario scenario2 = ScenarioUtils.createScenario(config2);
		
		TransitScheduleReader transitReader2 = new TransitScheduleReader(scenario2);
		
		transitReader2.readFile(args[1]);
		
		for (TransitLine tl : scenario.getTransitSchedule().getTransitLines().values()) {
			if (scenario2.getTransitSchedule().getTransitLines().containsKey(tl.getId()))
				System.out.println("problem1");
		}
		
		for (TransitStopFacility ts: scenario.getTransitSchedule().getFacilities().values()) {
			if (scenario2.getTransitSchedule().getFacilities().containsKey(ts.getId()))
				System.out.println(ts.getId());

		}
		
	}

}
