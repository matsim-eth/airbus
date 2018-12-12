package airbus.jakarta.scenario;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitRouteStop;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;

public class CleanTransitSchedule {

	public static void main(String[] args) {

		Config config = ConfigUtils.createConfig();

		Scenario scenario = ScenarioUtils.createScenario(config);

		TransitScheduleReader tsReader = new TransitScheduleReader(scenario);
		tsReader.readFile(args[0]);
		int count = 0;
		int total = 0;
		for (TransitLine tl : scenario.getTransitSchedule().getTransitLines().values()) {

			for (TransitRoute tr : tl.getRoutes().values()) {
				double now = 0.0;
				total++;
				for (TransitRouteStop trs : tr.getStops()) {
					if (trs.getArrivalOffset() > 0.0) {
						if (trs.getArrivalOffset() < now) {
							count++;
							System.out.println(tr.getId());

							break;
						}
						now = trs.getArrivalOffset();

					}
				}
			}

		}
		System.out.println(count);
		System.out.println(total);

	}

}
