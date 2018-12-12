package airbus.paris.scenario;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.IOUtils;

public class CoordinateTransform {

	public static void main(String[] args) throws IOException {

		BufferedReader reader = IOUtils.getBufferedReader(args[0]);
		BufferedWriter writer = IOUtils.getBufferedWriter(args[1]);
		CoordinateTransformation coordTrans = TransformationFactory.getCoordinateTransformation("EPSG:27561", "WGS84");

		writer.write(reader.readLine() + ";x_orig_wgs84;y_orig_wgs84;x_dest_wgs84;y_dest_wgs84");
		writer.newLine();
		String s = reader.readLine();
		while (s != null) {
			
			String[] arr = s.split(";");
			Coord coord_orig = CoordUtils.createCoord(Double.parseDouble(arr[0]),
					Double.parseDouble(arr[1]));
			Coord coord_orig_tr = coordTrans.transform(coord_orig);
			
			Coord coord_dest = CoordUtils.createCoord(Double.parseDouble(arr[2]),
					Double.parseDouble(arr[3]));
			Coord coord_dest_tr = coordTrans.transform(coord_dest);			
			
			writer.write(s + ";" + coord_orig_tr.getX() + ";" + coord_orig_tr.getY() + ";" + 
					coord_dest_tr.getX() + ";" + coord_dest_tr.getY());
			writer.newLine();
			
			s = reader.readLine();
		}
		
		writer.flush();
		writer.close();
	}

}
