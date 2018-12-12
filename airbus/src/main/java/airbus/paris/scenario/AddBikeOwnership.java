package airbus.paris.scenario;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;

import ch.ethz.matsim.baseline_scenario.transit.routing.DefaultEnrichedTransitRoute;
import ch.ethz.matsim.baseline_scenario.transit.routing.DefaultEnrichedTransitRouteFactory;

public class AddBikeOwnership {

	public static void main(String[] args) {

		Config config = ConfigUtils.createConfig();
		
		Scenario scenario = ScenarioUtils.createScenario(config);
		scenario.getPopulation().getFactory().getRouteFactories().setRouteFactory(DefaultEnrichedTransitRoute.class,
                new DefaultEnrichedTransitRouteFactory());
		PopulationReader popReader = new PopulationReader(scenario);
		popReader.readFile(args[0]);
		
		for (Person person : scenario.getPopulation().getPersons().values()) {
			if (MatsimRandom.getRandom().nextDouble() < 0.70)
				person.getAttributes().putAttribute("bikeOwnerhip", "no");
			else
				person.getAttributes().putAttribute("bikeOwnerhip", "yes");			
		}
		
		PopulationWriter popWriter = new PopulationWriter(scenario.getPopulation());
		popWriter.write(args[1]);		
		
	}

}
