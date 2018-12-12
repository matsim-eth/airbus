package airbus.analysis;

import java.io.BufferedWriter;
import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.NetworkReaderMatsimV2;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.IOUtils;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

public class SpeedAnalysis {

	public static void main(String[] args) throws ApiException, InterruptedException, IOException {
		GeoApiContext context = new GeoApiContext.Builder().apiKey(args[4]).build();

		String year = args[0].split("/")[0];
		String month = args[0].split("/")[1];
		String day = args[0].split("/")[2];

		Config config = ConfigUtils.createConfig();

		Scenario scenario = ScenarioUtils.createScenario(config);

		PopulationReader popReader = new PopulationReader(scenario);
		popReader.readFile(args[1]);

		NetworkReaderMatsimV2 networkReader = new NetworkReaderMatsimV2(scenario.getNetwork());
		networkReader.readFile(args[2]);

		Network network = scenario.getNetwork();
		BufferedWriter writer = IOUtils.getBufferedWriter(args[3]);
		writer.write("id;traveltimesim;distancesim;traveltimegoogle;distancegoogle;speedsim;speedgoogle");
		writer.newLine();
		CoordinateTransformation coordTrans = TransformationFactory.getCoordinateTransformation("EPSG:2154", "WGS84");
		int i = 200;
		for (Person person : scenario.getPopulation().getPersons().values()) {
			Plan plan = person.getSelectedPlan();

			for (PlanElement pe : plan.getPlanElements()) {
				if (pe instanceof Leg) {

					if (((Leg) pe).getMode().equals("car")) {
						i--;
						Coord startCoord = network.getLinks().get(((Leg) pe).getRoute().getStartLinkId()).getCoord();
						Coord startCoord_wgs = coordTrans.transform(startCoord);
						Coord endCoord = network.getLinks().get(((Leg) pe).getRoute().getEndLinkId()).getCoord();
						Coord endCoord_wgs = coordTrans.transform(endCoord);

						int h = (int) (((Leg) pe).getDepartureTime() / 3600.0);
						if (h > 23)
							continue;
						int min = (int) ((((Leg) pe).getDepartureTime() - h * 3600) / 60);
						DateTime time = new DateTime(Integer.parseInt(year), Integer.parseInt(month),
								Integer.parseInt(day), h, min, DateTimeZone.getDefault());
						DirectionsRoute[] route = (DirectionsApi
								.getDirections(context, startCoord_wgs.getY() + " " + startCoord_wgs.getX(),
										endCoord_wgs.getY() + " " + endCoord_wgs.getX())
								.mode(TravelMode.DRIVING).departureTime(time).alternatives(false).await()).routes;

						if (route == null || route.length == 0) {
							writer.write(i + "-99;-99;-99;-99;-99;-99;-99");
							writer.newLine();
						} else if (route.length > 0) {
							double distance = 0.0;
							double travelTime = 0.0;
							for (DirectionsLeg l : route[0].legs) {

								travelTime += l.duration.inSeconds;
								distance += l.distance.inMeters;
							}
							writer.write(i + ";" + h + ":" + min + ";" + startCoord_wgs.getY() + ";"
									+ startCoord_wgs.getX() + ";" + endCoord_wgs.getY() + ";" + endCoord_wgs.getX()
									+ ";" + ((Leg) pe).getRoute().getTravelTime() + ";"
									+ ((Leg) pe).getRoute().getDistance() + ";" + travelTime + ";" + distance + ";"
									+ ((Leg) pe).getRoute().getDistance() / ((Leg) pe).getRoute().getTravelTime() + ";"
									+ distance / travelTime);
							writer.newLine();
						}
						if (i == 0)
							break;
					}
				}
				if (i == 0)
					break;
			}

		}
		writer.flush();
		writer.close();
	}
}
