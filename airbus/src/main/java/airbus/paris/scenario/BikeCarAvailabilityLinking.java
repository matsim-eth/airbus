package airbus.paris.scenario;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.households.Household;
import org.matsim.households.Households;
import org.matsim.households.HouseholdsReaderV10;

import ch.ethz.matsim.baseline_scenario.transit.routing.DefaultEnrichedTransitRoute;
import ch.ethz.matsim.baseline_scenario.transit.routing.DefaultEnrichedTransitRouteFactory;

public class BikeCarAvailabilityLinking {

	public static void main(String[] args) {

		Config config = ConfigUtils.createConfig();
		
		Scenario scenario = ScenarioUtils.createScenario(config);
		scenario.getPopulation().getFactory().getRouteFactories().setRouteFactory(DefaultEnrichedTransitRoute.class,
                new DefaultEnrichedTransitRouteFactory());
		PopulationReader popReader = new PopulationReader(scenario);
		popReader.readFile(args[0]);
		
		HouseholdsReaderV10 housReader = new HouseholdsReaderV10(scenario.getHouseholds());
		housReader.readFile(args[1]);
		Households households = scenario.getHouseholds();
	
		for (Household household : households.getHouseholds().values()) {
				
			for (Id<Person> perId : household.getMemberIds()) {
					Person person = scenario.getPopulation().getPersons().get(perId);
					person.getAttributes().putAttribute("carAvailability", household.getAttributes().getAttribute("carAvailability"));
					person.getAttributes().putAttribute("bikeAvailability", household.getAttributes().getAttribute("bikeAvailability"));
					
			}
		}
		
		PopulationWriter popWriter = new PopulationWriter(scenario.getPopulation());
		popWriter.write(args[2]);		
		
	}

}
