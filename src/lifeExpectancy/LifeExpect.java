package lifeExpectancy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;
/**
 * Visualizes life expectancy in different countries. 
 * 
 * It loads the country shapes from a GeoJSON file via a data reader, and loads the population density values from
 * another CSV file (provided by the World Bank). The data value is encoded to transparency via a simplistic linear
 * mapping.
 * @author Samuel Fostine
 */
public class LifeExpect extends PApplet{
	// the map that we are goifn to use
	UnfoldingMap map;
	// create a hashMap tp hold the values of the file
	Map <String, Float> lifeExpect;
	
	// list of Feature and marker
	List<Feature> countryFeature;
	List<Marker>countryMarker;
	
	public void setup(){
		// initial setup, size of the aaplet
		size(800, 600, OPENGL);
		// initialise the Unfolding map using Google map provider
		map = new UnfoldingMap(this, 50,50,700,700, new Google.GoogleMapProvider());
		// add event to map so that we can can zoom and move the map
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// Put the values from the file to the map structure
		lifeExpect = loadExpectFromCSV("LifeExpectancyWorldBankModule3.csv");
		
		// load a .json file with all the features for every country
		countryFeature = GeoJSONReader.loadData(this, "countries.geo.json");
		
		//create a list of marker from the features
		countryMarker = MapUtils.createSimpleMarkers(countryFeature);
		
		// add the features to the maps
		map.addMarkers(countryMarker);
		
		// color the countries according to life expectation value
		shadeCountries();
	}
	
	private void shadeCountries() {
		// for each country, we are taking formatting the marker
		for (Marker marker: countryMarker)
		{
			//if the map contains the a key with that is the id of the marker
			// set the color of this marker according to the value of the life expectancy
			if(lifeExpect.containsKey(marker.getId()))
			{
				// 40 - 90 range of the life expectancy
				// 10 - 255 range of color
				int colorLevel = (int)map(lifeExpect.get(marker.getId()), 40,90,10,255);
				marker.setColor(color(255-colorLevel, 100, colorLevel));
			}
			else
				marker.setColor(color(150,150,150));
			
		}
	}

	private Map<String, Float> loadExpectFromCSV(String filename) {
		Map <String, Float> mapLifeExpect = new HashMap<String, Float>();
		
		// loads the string file to an array
		String [] rows = loadStrings(filename);
		// for each line, add the the key and the value to the hashmap 
		String [] col;
		for(String row: rows){
			col = row.split(",");
			// if we have 6 variables in a line and the last variable is not .., out this variable in the map
			if(col.length == 6 && !col[5].equals(".."))
				mapLifeExpect.put(col[4], Float.parseFloat(col[5]));
		}
		return mapLifeExpect;
	}

	public void draw()
	{
		map.draw();
		
	}
}
