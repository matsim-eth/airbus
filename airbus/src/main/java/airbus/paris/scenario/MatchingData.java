package airbus.paris.scenario;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.matsim.core.utils.io.IOUtils;

public class MatchingData {

	public static void main(String[] args) throws IOException {

		BufferedReader reader_orig = IOUtils.getBufferedReader(args[0]);
		BufferedReader reader_disagg = IOUtils.getBufferedReader(args[1]);

		Map<String, ArrayList<Integer>> map = new HashMap<>();	
		Map<String, ArrayList<Integer>> map_disagg = new HashMap<>();	

		reader_orig.readLine();
		String s = reader_orig.readLine();
		
		while (s != null) {
			
			String[] arr = s.split(";");
			if (map.containsKey(arr[15])) {
				if (!arr[16].equals("NA")) {

				map.get(arr[15]).add(Integer.parseInt(arr[16]));
				}
			}
			else {
				if (!arr[16].equals("NA")) {
				ArrayList<Integer> newSet = new ArrayList<>();
				newSet.add(Integer.parseInt(arr[16]));
				map.put(arr[15], newSet);
				}
			}
			s = reader_orig.readLine();
		}
		int i = 0;
		int max = 0;
		
		for (String id : map.keySet()) {
			if (max < map.get(id).size())
				max = map.get(id).size();
			if (map.get(id).size() == 66) {
				//if (map.get(id).toString().equals("[14, 9]"))
				Object[] x = map.get(id).toArray();
				Arrays.sort(x);
				System.out.println(Arrays.toString(x));
			}
		}
		System.out.println(max);

		s= reader_disagg.readLine();
		s= reader_disagg.readLine();

		while (s != null) {
			
			String[] arr = s.split(";");
			if (map_disagg.containsKey(arr[0] + arr[1])) {
				if (!arr[6].equals(""))
				map_disagg.get(arr[0]+ arr[1]).add((int)Double.parseDouble(arr[6]));
			}
			else {
					
				ArrayList<Integer> newSet = new ArrayList<>();
				if (!arr[6].equals(""))
				{
				newSet.add((int)Double.parseDouble(arr[6]));
				map_disagg.put(arr[0]+ arr[1], newSet);
				}
			}
			s = reader_disagg.readLine();
		}
		max = 0;
		for (String id : map_disagg.keySet()) {
			if (max < map_disagg.get(id).size())
				max = map_disagg.get(id).size();
			if (map_disagg.get(id).size() == 69) {
				Object[] x = map_disagg.get(id).toArray();
				Arrays.sort(x);
				System.out.println(Arrays.toString(x));
			}
		}
		System.out.println(max);

		
	}
}


