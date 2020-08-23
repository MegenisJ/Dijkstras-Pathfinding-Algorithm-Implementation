
/* Put your student number here
 *c1729929
 * Optionally, if you have any comments regarding your submission, put them here. 
 * For instance, specify here if your program does not generate the proper output or does not do it in the correct manner.
 * Two new data members added to Vertex. Distance (distance from start node), and Previous (Previous node on the route to start).
 */

import java.util.*;
import java.io.*;

class Vertex {

	// Constructor: set name, chargingStation and index according to given values,
	// initilaize incidentRoads as empty array
	public Vertex(String placeName, boolean chargingStationAvailable, int idx) {
		name = placeName;
		incidentRoads = new ArrayList<Edge>();
		index = idx;
		chargingStation = chargingStationAvailable;
	}

	public String getName() {
		return name;
	}

	public boolean hasChargingStation() {
		return chargingStation;
	}

	public ArrayList<Edge> getIncidentRoads() {
		return incidentRoads;
	}
	//below are added data members
	public Integer getDistance(){
		return distance;
	}
	public void setDistance(Integer d){
		distance = d;
	}
	public void setPrevious(Vertex p){
		previous = p;
	}
	public Vertex getPrevious(){
		return previous;
	}

	// Add a road to the array incidentRoads
	public void addIncidentRoad(Edge road) {
		incidentRoads.add(road);
	}

	public int getIndex() {
		return index;
	}

	private Vertex previous;
	private Integer distance;
	private String name; // Name of the place
	private ArrayList<Edge> incidentRoads; // Incident edges
	private boolean chargingStation; // Availability of charging station
	private int index; // Index of this vertex in the vertex array of the map
}

class Edge {
	public Edge(int roadLength, Vertex firstPlace, Vertex secondPlace) {
		length = roadLength;
		incidentPlaces = new Vertex[] { firstPlace, secondPlace };
	}

	public Vertex getFirstVertex() {
		return incidentPlaces[0];
	}

	public Vertex getSecondVertex() {
		return incidentPlaces[1];
	}

	public int getLength() {
		return length;
	}

	private int length;
	private Vertex[] incidentPlaces;
}

// A class that represents a sparse matrix
public class RoadMap {

	// Default constructor
	public RoadMap() {
		places = new ArrayList<Vertex>();
		roads = new ArrayList<Edge>();
	}

	// Auxiliary function that prints out the command syntax
	public static void printCommandError() {
		System.err.println("ERROR: use one of the following commands");
		System.err.println(" - Read a map and print information: java RoadMap -i <MapFile>");
		System.err.println(
				" - Read a map and find shortest path between two vertices with charging stations: java RoadMap -s <MapFile> <StartVertexIndex> <EndVertexIndex>");
	}

	public static void main(String[] args) throws Exception {
		if (args.length == 2 && args[0].equals("-i")) {
			RoadMap map = new RoadMap();
			try {
				map.loadMap(args[1]);
			} catch (Exception e) {
				System.err.println("Error in reading map file");
				System.exit(-1);
			}

			System.out.println("Read road map from " + args[1] + ":");
			map.printMap();
		} else if (args.length == 4 && args[0].equals("-s")) {
			RoadMap map = new RoadMap();
			map.loadMap(args[1]);
			System.out.println("Read road map from " + args[1] + ":");
			map.printMap();

			int startVertexIdx = -1, endVertexIdx = -1;
			try {
				startVertexIdx = Integer.parseInt(args[2]);
				endVertexIdx = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				System.err.println("Error: start vertex and end vertex must be specified using their indices");
				System.exit(-1);
			}

			if (startVertexIdx < 0 || startVertexIdx >= map.numPlaces()) {
				System.err.println("Error: invalid index for start vertex");
				System.exit(-1);
			}

			if (endVertexIdx < 0 || endVertexIdx >= map.numPlaces()) {
				System.err.println("Error: invalid index for end vertex");
				System.exit(-1);
			}

			Vertex startVertex = map.getPlace(startVertexIdx);
			Vertex endVertex = map.getPlace(endVertexIdx);
			if (!map.isConnectedWithChargingStations(startVertex, endVertex)) {
				System.out.println();
				System.out.println("There is no path connecting " + map.getPlace(startVertexIdx).getName() + " and "
						+ map.getPlace(endVertexIdx).getName() + " with charging stations");
			} else {
				ArrayList<Vertex> path = map.shortestPathWithChargingStations(startVertex, endVertex);
				System.out.println();
				System.out.println("Shortest path with charging stations between " + startVertex.getName() + " and "
						+ endVertex.getName() + ":");
				map.printPath(path);
			}

		} else {
			printCommandError();
			System.exit(-1);
		}
	}

	// Load matrix entries from a text file
	public void loadMap(String filename) {
		File file = new File(filename);
		places.clear();
		roads.clear();

		try {
			Scanner sc = new Scanner(file);

			// Read the first line: number of vertices and number of edges
			int numVertices = sc.nextInt();
			int numEdges = sc.nextInt();

			for (int i = 0; i < numVertices; ++i) {
				// Read the vertex name and its charing station flag
				String placeName = sc.next();
				int charginStationFlag = sc.nextInt();
				boolean hasChargingStataion = (charginStationFlag == 1);
				//
				
				// Add your code here to create a new vertex using the information above and add
				// it to places
				Vertex v = new Vertex(placeName,hasChargingStataion,i);
				places.add(v);
			}

			for (int j = 0; j < numEdges; ++j) {
				// Read the edge length and the indices for its two vertices
				int vtxIndex1 = sc.nextInt();
				int vtxIndex2 = sc.nextInt();
				int length = sc.nextInt();
				Vertex vtx1 = places.get(vtxIndex1);
				Vertex vtx2 = places.get(vtxIndex2);

				Edge a = new Edge(length,vtx1,vtx2);
				roads.add(a);
				// Add your code here to create a new edge using the information above and add
				// it to roads
				// You should also set up incidentRoads for each vertex
				//This might need changing to roads.last or something CAN ALSO RENAME a
				vtx1.addIncidentRoad(a);
				vtx2.addIncidentRoad(a);
			}

			sc.close();

			// Add your code here if approparite
		} catch (Exception e) {
			e.printStackTrace();
			places.clear();
			roads.clear();
		}
	}

	// Return the shortest path between two given vertex, with charging stations on
	// each itermediate vertex.
	public ArrayList<Vertex> shortestPathWithChargingStations(Vertex startVertex, Vertex endVertex) {

		// Initialize an empty path
		ArrayList<Vertex> path = new ArrayList<Vertex>();

		// Sanity check for the case where the start vertex and the end vertex are the
		// same
		if (startVertex.getIndex() == endVertex.getIndex()) {
			path.add(startVertex);
			return path;
		}

		// Add your code here
		//Creating data structures
		//NotVisited is essentially the queue. Just making priority an attribute of the objects rather than part of the data struct.
		LinkedList<Vertex> NotVisited = new LinkedList<Vertex>();
		LinkedList<Vertex> Visited = new LinkedList<Vertex>();
		Vertex[] Previous = new Vertex[numPlaces()];
		
		
		//initialises values for distances to max and previous to null, adds all nodes to the not visited list.
		
		for(Vertex place:places){
			place.setDistance(Integer.MAX_VALUE);
			place.setPrevious(null);
			NotVisited.add(place);
		}
		startVertex.setDistance(0);
		//lowestTotal is an int to keep check of the distance of the current shortest route
		int lowestTotal = Integer.MAX_VALUE;
		
		while(NotVisited.size()!=0){	
			//current is the vertex on the not visited list with the lowest distance from start. (Therefore vertex with highest priority)
			Vertex current = null;
			//loops through all not visited to find the smallest value. 
			for(Vertex place:NotVisited){
				if (current == null){
					current = place;
				}
				else if (place.getDistance() < current.getDistance()){  	
					current = place;
				}
			}
			
		
			NotVisited.remove(current);
			Visited.add(current);
			//gets the roads coming from current vertex
			ArrayList<Edge> NewRoads = current.getIncidentRoads();
			//for each road there are two places. One will be the current node and the other will either be a new 
			//or not visited node
			for(Edge road: NewRoads){
				//finds neighbours
				Vertex first = road.getFirstVertex();
				Vertex second = road.getSecondVertex();
				int alt = current.getDistance() + road.getLength();
				//newPlace could be an already visited node.
				Vertex newPlace = null;
				if (first != current & !Visited.contains(first)){	
					newPlace = first;
				}
				if(second != current & !Visited.contains(second)){
					newPlace = second;
				}
				if (newPlace == endVertex){
					if (alt < lowestTotal){
						
						endVertex.setDistance(alt);
						endVertex.setPrevious(current);
					}
				}
				if (!Visited.contains(first)){
					if(first.hasChargingStation()==true){
						if (alt < first.getDistance()){		
							first.setDistance(alt);
							first.setPrevious(current);		
						}						
					}
				}
				if (!Visited.contains(second)){
					if(second.hasChargingStation()==true ){			
						if (alt < second.getDistance()){
							second.setDistance(alt);
							second.setPrevious(current);
							
						}		
				
					}
				}
			}	
		}

		
		Vertex u = endVertex;
		while (u != null){
			path.add(u);
			u = u.getPrevious();
		}
		Collections.reverse(path);
		return path;
	}

	// Check if two vertices are connected by a path with charging stations on each itermediate vertex.
	// Return true if such a path exists; return false otherwise.
	// The worst-case time complexity of your algorithm should be no worse than O(v + e),
	// where v and e are the number of vertices and the number of edges in the graph.
	public boolean isConnectedWithChargingStations(Vertex startVertex, Vertex endVertex) {
		// Sanity check
		if (startVertex.getIndex() == endVertex.getIndex()) {
			return true;
		}

		// Add your code here
			
		LinkedList<Vertex> NotVisited = new LinkedList<Vertex>();
		LinkedList<Vertex> Visited = new LinkedList<Vertex>();
		NotVisited.add(startVertex);
		Vertex current = null;
		while(NotVisited.size()!=0){
			current = NotVisited.poll();
			Visited.add(current);
			ArrayList<Edge> NewRoads = current.getIncidentRoads();
			for(Edge road: NewRoads){
				//finds neighbours
				Vertex first = road.getFirstVertex();
				Vertex second = road.getSecondVertex();

				if (!Visited.contains(first)){
					if (first == endVertex){
						return true;
					}
					if(first.hasChargingStation()==true){			
						NotVisited.add(first);		
					}
				}
				if (!Visited.contains(second)){
					if (second == endVertex){
						return true;
					}
					if(second.hasChargingStation()==true){		
						NotVisited.add(second);	
					}
				}
				
			}	
		}

		return false;
	}

			
	

	public void printMap() {
		System.out.println("The map contains " + this.numPlaces() + " places and " + this.numRoads() + " roads");
		System.out.println();

		System.out.println("Places:");

		for (Vertex v : places) {
			System.out.println("- name: " + v.getName() + ", charging station: " + v.hasChargingStation());
		}

		System.out.println();
		System.out.println("Roads:");

		for (Edge e : roads) {
			System.out.println("- (" + e.getFirstVertex().getName() + ", " + e.getSecondVertex().getName()
					+ "), length: " + e.getLength());
		}
	}

	public void printPath(ArrayList<Vertex> path) {
		System.out.print("(  ");

		for (Vertex v : path) {
			System.out.print(v.getName() + "  ");
		}

		System.out.println(")");
	}

	public int numPlaces() {
		return places.size();
	}

	public int numRoads() {
		return roads.size();
	}

	public Vertex getPlace(int idx) {
		return places.get(idx);
	}

	private ArrayList<Vertex> places;
	private ArrayList<Edge> roads;
}
