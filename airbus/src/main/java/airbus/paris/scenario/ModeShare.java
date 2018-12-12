package airbus.paris.scenario;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;

import ch.ethz.matsim.baseline_scenario.transit.routing.DefaultEnrichedTransitRoute;
import ch.ethz.matsim.baseline_scenario.transit.routing.DefaultEnrichedTransitRouteFactory;

public class ModeShare {

	public static void main(String[] args) {
Config config = ConfigUtils.createConfig();
		
		Scenario scenario = ScenarioUtils.createScenario(config);
		scenario.getPopulation().getFactory().getRouteFactories().setRouteFactory(DefaultEnrichedTransitRoute.class,
                new DefaultEnrichedTransitRouteFactory());
		PopulationReader popReader = new PopulationReader(scenario);
		popReader.readFile(args[0]);
		int car = 0;
		int walk = 0;
		int pt = 0;
		int bike = 0;
		boolean ptLeg = false;
		for (Person person : scenario.getPopulation().getPersons().values()) {
			Plan plan = person.getSelectedPlan();
			
			for (PlanElement pe : plan.getPlanElements()) {
				
				if (pe instanceof Leg) {
					if (((Leg) pe).getMode().equals("car"))
						car++;
					else if (((Leg) pe).getMode().equals("walk")) {
						walk++;
						
					}
					else if (((Leg) pe).getMode().equals("bike")) {
						bike++;
					}
					else if ( ( ((Leg) pe).getMode().equals("transit_walk") || ((Leg) pe).getMode().equals("access_walk") ) && !ptLeg) {
						pt++;
						ptLeg = true;
					}
				}
				else {
					if (pe instanceof Activity) {
						if (!((Activity) pe).getType().equals("pt interaction"))
							ptLeg = false;

					}
				}
			}
			
			
		}
		int total = walk + bike + car + pt;
		System.out.println("walk: " + (double)walk/total * 100);
		System.out.println("bike: " + (double)bike/total * 100);
		System.out.println("car: " + (double)car/total * 100);
		System.out.println("pt: " + (double)pt/total * 100);
		System.out.println(total);

	}

}
