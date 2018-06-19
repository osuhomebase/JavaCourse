package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	// NEW IN MODULE 5
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	HashMap<Integer, Location> airports;
	
	public void setup() {
		// setting up PAppler
		size(800,600, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 750, 550);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
	
			m.setRadius(5);
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());

			
		
		}
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
						
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
						
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
						
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
					
			System.out.println(sl.getProperties());	
						
			routeList.add(sl);
		}
		map.addMarkers(routeList);
		map.addMarkers(airportList);

		// Hide all routes
		for (Marker mhide : routeList) {
			if (mhide != lastClicked) {
				mhide.setHidden(true);
			}
		}
		
	}
	
	public void draw() {
		background(0);
		map.draw();
		
	}
	// and then call that method from setUp
	
		/** Event handler that gets called automatically when the 
		 * mouse moves.
		 */
		@Override
		public void mouseMoved()
		{
			// clear the last selection
			if (lastSelected != null) {
				lastSelected.setSelected(false);
				lastSelected = null;
			
			}
			selectMarkerIfHover(airportList);
			//loop();
		}
		
		// If there is a marker selected 
		private void selectMarkerIfHover(List<Marker> markers)
		{
			// Abort if there's already a marker selected
			if (lastSelected != null) {
				return;
			}
			
			for (Marker m : markers) 
			{
				CommonMarker marker = (CommonMarker)m;
				if (marker.isInside(map,  mouseX, mouseY)) {
					lastSelected = marker;
					marker.setSelected(true);
					return;
				}
			}
		}

		private void checkAirportsForClick()
		{
			if (lastClicked != null) return;
			// Loop over the earthquake markers to see if one of them is selected
			for (Marker m : airportList) {
				AirportMarker marker = (AirportMarker)m;
				
				if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
					lastClicked = marker;
					
					// Hide all the other earthquakes and hide
					for (Marker mhide : airportList) {
						if (mhide != lastClicked) {
							mhide.setHidden(true);
						}
					}
					// Hide all the other earthquakes and hide
					for (Marker mhide : routeList) {
						// hide everything first
						mhide.setHidden(true);
						//System.out.println((String)lastClicked.getId() + " " + Integer.parseInt((String)mhide.getProperty("source")));
						// get source and destination airportIds
						int source = Integer.parseInt((String)mhide.getProperty("source"));
						int dest = Integer.parseInt((String)mhide.getProperty("destination"));
						System.out.println("airports: " + airports.get(source));
						System.out.println("lastclicked: " + lastClicked.getLocation());
						if (airports.get(source) == lastClicked.getLocation() || airports.get(dest) == lastClicked.getLocation()) {
							mhide.setHidden(false);
						}
					}
					
					return;
				}
			}
			
		}
		// loop over and unhide all markers
		private void unhideMarkers() {
			for(Marker marker : airportList) {
				marker.setHidden(false);
			}
				
			
		}
		
		@Override
		public void mouseClicked()
		{
			if (lastClicked != null) {
				unhideMarkers();
				lastClicked = null;
			}
			else if (lastClicked == null) 
			{
				checkAirportsForClick();
				
			}
		}
		

}
