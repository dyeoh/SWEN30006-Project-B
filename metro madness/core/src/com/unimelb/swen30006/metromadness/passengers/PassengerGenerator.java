/*
 * SWEN30006 Software Modelling and Design
 * 2017 Semester 1
 * 
 * Project B - Metro Madness
 * 
 * GROUP 73
 * Darren Yeoh Cheang Leng - 715863
 * Ziqian Qiao -
 * Marco Vermaak -
 *
 */
package com.unimelb.swen30006.metromadness.passengers;

import java.util.ArrayList;
import java.util.Random;

import com.unimelb.swen30006.metromadness.stations.CargoStation;
import com.unimelb.swen30006.metromadness.stations.Station;
import com.unimelb.swen30006.metromadness.tracks.Line;

public class PassengerGenerator {
	
	// Random number generator
	static final private Random random = new Random(30006);
	
	// Passenger id generator
	static private int idGen = 1;
	
	
	// The station that passengers are getting on
	private Station s;
	// The line they are travelling on
	private ArrayList<Line> lines;
	
	// The max volume
	private float maxVolume;
	
	public PassengerGenerator(Station s, ArrayList<Line> lines, float max){
		this.s = s;
		this.lines = lines;
		this.setMaxVolume(max);
	}
	
	//Generates a random amount of passengers
	public Passenger[] generatePassengers(){
		int count = random.nextInt(4)+1;
		Passenger[] passengers = new Passenger[count];
		for(int i=0; i<count; i++){
			passengers[i] = generatePassenger(random);
		}
		return passengers;
	}
	
	//Generates a random passenger based on the station, returns null if fails
	public Passenger generatePassenger(Random random){
		// Pick a random station from the line
		Line l = this.lines.get(random.nextInt(this.lines.size()));
		int current_station = l.getStations().indexOf(this.s);
		
		//Sets direction of the passenger at random
		boolean forward = random.nextBoolean();
		
		// If we are the end of the line then set our direction forward or backward
		if(current_station == 0){
			//System.out.println("forward");
			forward = true;
		} else if (current_station == l.getStations().size()-1){
			//System.out.println("backward");
			forward = false;
		}
		
		// Find the end station
		int index = 0;
		
		//Flag to check if it is a CargoStation
		int flag = 0;
		if(this.s instanceof CargoStation){
			flag = 1;
		}
		
		if (forward){
			//Get random station in the forward direction if active station
			if (flag == 0){
				index = random.nextInt(l.getStations().size()-1-current_station) + current_station + 1;
				Station s = l.getStations().get(index);
				return new Passenger(idGen++, 0, this.s, s);
			}
			//Get next cargo station in the forward direction
			else{
				while(current_station <= l.getStations().size()-1){
				//System.out.println("current: " + current_station + " max: " + (l.stations.size()-1));
				if(l.getStations().get(current_station) instanceof CargoStation 
						&& l.getStations().get(current_station) != this.s){
					Station s = l.getStations().get(current_station);
					return new Passenger(idGen++, random.nextInt(51), this.s, s);
				}
				current_station ++;
				}
			}
		}
		else {
			if (flag == 0 ){
				//Get random station in the backward direction
				index = current_station - 1 - random.nextInt(current_station);
				Station s = l.getStations().get(index);
				return new Passenger(idGen++, 0, this.s, s);
			}
			else{
				//Get next cargo station in the backward direction
				while(current_station >= 0){
					if(l.getStations().get(current_station) instanceof CargoStation
							&& l.getStations().get(current_station) != this.s){
						Station s = l.getStations().get(current_station);
						return new Passenger(idGen++, random.nextInt(51), this.s, s);
					}
					current_station --;
				}
			}
		}
		//Passenger failed to be generated
		return null;
	}

	public float getMaxVolume() {
		return maxVolume;
	}

	public void setMaxVolume(float maxVolume) {
		this.maxVolume = maxVolume;
	}
	
}
