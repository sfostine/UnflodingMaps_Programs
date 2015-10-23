package earthquakeMap;

import java.awt.Color;
//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * @author Samuel Fostine
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	
	public void setup() {
		size(950, 600, OPENGL);
		
		

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    // These print statements show you (1) all of the relevant properties 
	    // in the features, and (2) how to get one property and use it
	    if (earthquakes.size() > 0) {
	    	System.out.println(earthquakes.size());
	    	PointFeature f = earthquakes.get(0);
	    	System.out.println(f.getProperties());
	    	Object magObj = f.getProperty("magnitude");
	    	float mag = Float.parseFloat(magObj.toString());
	    	
	    	// PointFeatures also have a getLocation method
	    	for(int i=0; i < earthquakes.size();i++){
	    		SimplePointMarker mark = createMarker(earthquakes.get(i));
	    		mark.setRadius(10);
	    		
	    		//mark.setColor(color(0,0,255));
	    		StyleMarker(mark, Float.parseFloat(earthquakes.get(i).getProperty("magnitude").toString()));
		    	markers.add(mark);
	    	}
		    
	    }
	    
	    // Here is an example of how to use Processing's color method to generate 
	    // an int that represents the color yellow.
	    int yellow = color(255, 255, 0);
	    
	    //TODO: Add code here as appropriate
	    map.addMarkers(markers);
	    
	}
		
	// style the marker according to the magnitude of the earthquake
	private void StyleMarker(SimplePointMarker mark, Float mag) {
		int baseMag = 5;
		// if the earthquake is less than 4, radius 5 color blue
		if(mag < this.THRESHOLD_LIGHT)
		{
			mark.setRadius(baseMag);
			mark.setColor(color(0,0,255));
		}
		// if between 4 and 5, radius 8 color yellow
		else if(mag >= this.THRESHOLD_LIGHT && mag < this.THRESHOLD_MODERATE)
		{
			mark.setRadius(baseMag + 3);
			mark.setColor(color(255,255,0));
		}
		// greater than 5, radius 11, color red
		else
		{
			mark.setRadius(baseMag + 6);
			mark.setColor(color(255,0,0));
		}
	}

	// A suggested helper method that takes in an earthquake feature and 
	// returns a SimplePointMarker for that earthquake
	private SimplePointMarker createMarker(PointFeature feature)
	{
		// finish implementing and use this method, if it helps.
		return new SimplePointMarker(feature.getLocation());
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}


	// helper method to draw key in GUI
	// TODO: Implement this method to draw the key
	private void addKey() 
	{	
		//rectangle
		fill(255,255,230);
		this.rect(20, 50, 160, 250);
		// texts
		fill(50);
		this.text("Earthquake key", 50, 80);
		text("5.0 + Magnitude", 60, 125);
		text("4.0 + Magnitude", 60, 165);
		text("Below 4.0", 60, 205);
		// red ellipse
		fill(255,0,0);
		this.ellipse(40,120, 11, 11);
		// yellow ellipse
		fill(255,255,0);
		ellipse(40, 160, 8,8);
		//blue ellipse
		fill(0,0,255);
		ellipse(40,200,5,5);
		
	}
}
