package airbus.paris.scenario;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws ApiException, InterruptedException, IOException {
		
		Path path = Paths.get(args[1]);
		BufferedWriter writer = Files.newBufferedWriter(path);
		
		
		GeoApiContext context = new GeoApiContext.Builder().apiKey(args[0]).build();
		LatLng location = new LatLng(48.855574, 2.351093);
		PlaceType type = PlaceType.GROCERY_OR_SUPERMARKET;
		PlacesSearchResponse response = PlacesApi.nearbySearchQuery(context, location).radius(1000).type(type).await();
		PlacesSearchResult[] res = response.results;

		for (PlacesSearchResult r : res) {
			PlaceDetails details = PlacesApi.placeDetails(context, r.placeId).await();
			
			writer.write(details.name + ";" + details.formattedAddress + ";" + details.openingHours + "\n");
			
		}
		
		writer.flush();
		writer.close();
	}

}
