package airbus.paris.scenario;

import java.io.BufferedWriter;
import java.io.IOException;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PersonUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;

import ch.ethz.matsim.baseline_scenario.transit.routing.DefaultEnrichedTransitRoute;
import ch.ethz.matsim.baseline_scenario.transit.routing.DefaultEnrichedTransitRouteFactory;

public class PopulationToCSV {

	public static void main(String[] args) throws IOException {

		Config config = ConfigUtils.createConfig();

		Scenario scenario = ScenarioUtils.createScenario(config);
		scenario.getPopulation().getFactory().getRouteFactories().setRouteFactory(DefaultEnrichedTransitRoute.class,
                new DefaultEnrichedTransitRouteFactory());
		PopulationReader popReader = new PopulationReader(scenario);
		popReader.readFile(args[0]);
		
		BufferedWriter writer = IOUtils.getBufferedWriter(args[1]);
		
		
		for (Person person : scenario.getPopulation().getPersons().values()) {
			
			writer.write(person.getId().toString() + "," + PersonUtils.getAge(person)  + "," + PersonUtils.getSex(person) +
			 "," + person.getAttributes().getAttribute("carAvailability") + "," + PersonUtils.getLicense(person));
			writer.newLine();
		}
		
		writer.flush();
		writer.close();
	}

}
