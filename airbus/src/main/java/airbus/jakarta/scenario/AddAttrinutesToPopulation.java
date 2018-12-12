package airbus.jakarta.scenario;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.population.io.PopulationWriter;
import org.matsim.core.scenario.ScenarioUtils;

import ch.ethz.matsim.baseline_scenario.transit.routing.DefaultEnrichedTransitRoute;
import ch.ethz.matsim.baseline_scenario.transit.routing.DefaultEnrichedTransitRouteFactory;

public class AddAttrinutesToPopulation {

	public static void main(String[] args) {

		Config config = ConfigUtils.createConfig();

		Scenario scenario = ScenarioUtils.createScenario(config);
		scenario.getPopulation().getFactory().getRouteFactories().setRouteFactory(DefaultEnrichedTransitRoute.class,
                new DefaultEnrichedTransitRouteFactory());
		PopulationReader popReader = new PopulationReader(scenario);
		popReader.readFile(args[0]);
		
		//for (Person person : scenario.getPopulation().getPersons().values()) {
			
		//}
		
		
		PopulationWriter popWriter = new PopulationWriter(scenario.getPopulation());
		popWriter.writeV6(args[1]);
		
	}

}
