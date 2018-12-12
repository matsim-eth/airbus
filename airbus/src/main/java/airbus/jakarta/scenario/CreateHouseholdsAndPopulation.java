package airbus.jakarta.scenario;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.households.Household;
import org.matsim.households.HouseholdsFactoryImpl;
import org.matsim.households.HouseholdsWriterV10;

public class CreateHouseholdsAndPopulation {

	public static void main(String[] args) throws IOException {

		double prct = Double.parseDouble(args[4]);
		int[] startTimeCount = new int[48];
		int totalCount = 0;
		int[][] durationCount = new int[48][30];
		int[] counts = new int[48];
		Config config = ConfigUtils.createConfig();

		Scenario scenarioSP = ScenarioUtils.createScenario(config);

		PopulationReader readerPop = new PopulationReader(scenarioSP);
		readerPop.readFile(args[0]);

		for (Person person : scenarioSP.getPopulation().getPersons().values()) {

			Plan plan = person.getSelectedPlan();

			for (PlanElement pe : plan.getPlanElements()) {
				if (pe instanceof Activity) {
					if (((Activity) pe).getType().equals("work")) {

						double duration = ((Activity) pe).getEndTime() - ((Activity) pe).getStartTime();
						if ((int) Math.floor(((Activity) pe).getStartTime()) < 0 || ((Activity) pe).getEndTime() < 0)
							System.out.println("bla");
						else {

							if (duration > 0 && duration < 16 * 3600.0 && ((Activity) pe).getStartTime() < 24 * 3600) {
								startTimeCount[(int) Math.floor(((Activity) pe).getStartTime() / 1800.0)]++;
								durationCount[(int) Math.floor(((Activity) pe).getStartTime() / 1800.0)][(int) Math
										.ceil(duration / 3600.0)]++;
								counts[(int) Math.floor(((Activity) pe).getStartTime() / 1800.0)]++;
								totalCount++;
							}
						}
					}
				}
			}

		}

		List<Double> cdf = new LinkedList<>();
		double last = 0.0;
		for (int i : startTimeCount) {
			cdf.add((double) i / (double) totalCount + last);
			last += (double) i / (double) totalCount;
		}

		List<List<Double>> cdfDurations = new LinkedList<>();

		for (int i = 0; i < 48; i++) {
			last = 0.0;
			List newEntry = new LinkedList<>();
			for (int j : durationCount[i]) {
				if (counts[i] == 0) {
					newEntry.add(last);
					last += 0.0;
				} else {
					newEntry.add((double) j / (double) counts[i] + last);
					last += (double) j / (double) counts[i];
				}
			}
			cdfDurations.add(newEntry);

		}

		HouseholdsFactoryImpl housholdsFactory = new HouseholdsFactoryImpl();

		BufferedReader reader2 = IOUtils.getBufferedReader(args[1]);

		reader2.readLine();

		String s = reader2.readLine();
		int i = 1;
		Scenario scenario = ScenarioUtils.createScenario(config);

		PopulationFactory popFactory = scenario.getPopulation().getFactory();
		Coord currentHome = null;

		while (s != null) {
			String[] arr = s.split(",");
			if (scenario.getHouseholds().getHouseholds().containsKey(Id.create(arr[10], Household.class))) {
				Household household = scenario.getHouseholds().getHouseholds().get(Id.create(arr[10], Household.class));
				String id = Integer.toString(i++);
				household.getMemberIds().add(Id.createPersonId(id));

				Person person = scenario.getPopulation().getFactory().createPerson(Id.createPersonId(id));

				Plan plan = scenario.getPopulation().getFactory().createPlan();
				// vary work location for the person +-500m
				double varyDistance = (MatsimRandom.getRandom().nextDouble() - 0.5) * 750.0;

				Coord coordHome = currentHome;
				Coord coordWork = CoordUtils.createCoord(Double.parseDouble(arr[9]) + varyDistance,
						Double.parseDouble(arr[8]) + varyDistance);
				double crowfly = CoordUtils.calcEuclideanDistance(coordHome, coordWork);
				double time = crowfly / 40.0;
				Activity homeActivity = popFactory.createActivityFromCoord("home", coordHome);

				double selector = MatsimRandom.getRandom().nextDouble();
				int index = 0;

				while (index < cdf.size()) {
					if (selector < cdf.get(index)) {
						break;
					}

					index++;
				}

				// vary this +-15min uniformly
				double varyTime = (MatsimRandom.getRandom().nextDouble() - 0.5) * 30.0 * 60.0;
				double currentTime = index * 1800.0 - time + varyTime > 0 ? index * 1800.0 - time + varyTime : 0;

				int index2 = 0;
				selector = MatsimRandom.getRandom().nextDouble();

				while (index2 < cdfDurations.get(index).size()) {
					if (selector < cdfDurations.get(index).get(index2)) {
						break;
					}

					index2++;
				}

				homeActivity.setEndTime(currentTime);
				Leg workLeg = popFactory.createLeg("walk");
				currentTime += time;
				String act;
				if (arr[15].equals("w"))
					act = "work";
				else
					act = "education";
				Activity workActivity = popFactory.createActivityFromCoord(act, coordWork);
				workActivity.setStartTime(currentTime);
				workActivity.setEndTime(currentTime + index2 * 3600.0);
				Leg homeLeg = popFactory.createLeg("walk");
				Activity homeActivity2 = popFactory.createActivityFromCoord("home", coordHome);
				homeActivity2.setStartTime(currentTime + index2 * 3600.0 + time);

				plan.addActivity(homeActivity);
				plan.addLeg(workLeg);
				plan.addActivity(workActivity);
				plan.addLeg(homeLeg);
				plan.addActivity(homeActivity2);

				person.addPlan(plan);
				person.setSelectedPlan(plan);

				person.getAttributes().putAttribute("sex", arr[2]);
				person.getAttributes().putAttribute("age", Integer.parseInt(arr[1]));
				person.getAttributes().putAttribute("carAvailability",
						household.getAttributes().getAttribute("carAvailability"));
				person.getAttributes().putAttribute("motorcycleAvailability",
						household.getAttributes().getAttribute("motorcycleAvailability"));

				scenario.getPopulation().addPerson(person);
			} else {

				if (prct > MatsimRandom.getRandom().nextDouble()) {

					Household household = housholdsFactory.createHousehold(Id.create(arr[10], Household.class));
					String id = Integer.toString(i++);
					household.getMemberIds().add(Id.createPersonId(id));
					scenario.getHouseholds().getHouseholds().put(Id.create(arr[10], Household.class), household);

					Person person = scenario.getPopulation().getFactory().createPerson(Id.createPersonId(id));

					Plan plan = scenario.getPopulation().getFactory().createPlan();

					double varyDistance = (MatsimRandom.getRandom().nextDouble() - 0.5) * 750.0;

					Coord coordHome = CoordUtils.createCoord(Double.parseDouble(arr[7]) + varyDistance,
							Double.parseDouble(arr[6]) + varyDistance);
					currentHome = coordHome;
					Coord coordWork = CoordUtils.createCoord(Double.parseDouble(arr[9]) + varyDistance,
							Double.parseDouble(arr[8]) + varyDistance);
					double crowfly = CoordUtils.calcEuclideanDistance(coordHome, coordWork);
					double time = crowfly / 8.0;
					Activity homeActivity = popFactory.createActivityFromCoord("home", coordHome);

					double selector = MatsimRandom.getRandom().nextDouble();
					int index = 0;

					while (index < cdf.size()) {
						if (selector < cdf.get(index)) {
							break;
						}

						index++;
					}

					// TODO: vary this +-15min uniformly
					double currentTime = index * 1800.0 - time > 0 ? index * 1800.0 - time : 0;

					int index2 = 0;
					selector = MatsimRandom.getRandom().nextDouble();

					while (index2 < cdfDurations.get(index).size()) {
						if (selector < cdfDurations.get(index).get(index2)) {
							break;
						}

						index2++;
					}

					homeActivity.setEndTime(currentTime);
					Leg workLeg = popFactory.createLeg("walk");
					currentTime += time;
					String act;
					if (arr[15].equals("w"))
						act = "work";
					else
						act = "education";
					Activity workActivity = popFactory.createActivityFromCoord(act, coordWork);
					workActivity.setStartTime(currentTime);
					workActivity.setEndTime(currentTime + index2 * 3600.0);
					Leg homeLeg = popFactory.createLeg("walk");
					Activity homeActivity2 = popFactory.createActivityFromCoord("home", coordHome);
					homeActivity2.setStartTime(currentTime + index2 * 3600.0 + time);

					plan.addActivity(homeActivity);
					plan.addLeg(workLeg);
					plan.addActivity(workActivity);
					plan.addLeg(homeLeg);
					plan.addActivity(homeActivity2);

					person.addPlan(plan);
					person.setSelectedPlan(plan);

					// TODO: add attributes for the persons/households
					if (arr[11].equals("NA") || Integer.parseInt(arr[11]) == 0) {
						household.getAttributes().putAttribute("carAvailability", "never");
						person.getAttributes().putAttribute("carAvailability", "never");

					} else {
						household.getAttributes().putAttribute("carAvailability", "some");
						person.getAttributes().putAttribute("carAvailability", "some");
					}

					if (arr[12].equals("NA") || Integer.parseInt(arr[12]) == 0) {
						household.getAttributes().putAttribute("motorcycleAvailability", "never");
						person.getAttributes().putAttribute("motorcycleAvailability", "never");

					} else {
						household.getAttributes().putAttribute("motorcycleAvailability", "some");
						person.getAttributes().putAttribute("motorcycleAvailability", "some");
					}

					person.getAttributes().putAttribute("sex", arr[2]);
					person.getAttributes().putAttribute("age", Integer.parseInt(arr[1]));

					scenario.getPopulation().addPerson(person);
				}
			}

			s = reader2.readLine();
		}

		PopulationWriter popWriter = new PopulationWriter(scenario.getPopulation());
		popWriter.write(args[2]);
		HouseholdsWriterV10 housWriter = new HouseholdsWriterV10(scenario.getHouseholds());
		housWriter.writeFile(args[3]);

	}

}
