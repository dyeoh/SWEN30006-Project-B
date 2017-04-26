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
	public Station s;
	// The line they are travelling on
	public ArrayList<Line> lines;
	
	// The max volume
	public float maxVolume;
	
	public PassengerGenerator(Station s, ArrayList<Line> lines, float max){
		this.s = s;
		this.lines = lines;
		this.maxVolume = max;
	}
	
	public Passenger[] generatePassengers(){
		int count = random.nextInt(4)+1;
		Passenger[] passengers = new Passenger[count];
		for(int i=0; i<count; i++){
			passengers[i] = generatePassenger(random);
		}
		return passengers;
	}
	
	public Passenger generatePassenger(Random random){
		// Pick a random station from the line
		Line l = this.lines.get(random.nextInt(this.lines.size()));
		int current_station = l.stations.indexOf(this.s);
		boolean forward = random.nextBoolean();
		
		// If we are the end of the line then set our direction forward or backward
		if(current_station == 0){
			//System.out.println("forward");
			forward = true;
		} else if (current_station == l.stations.size()-1){
			//System.out.println("backward");
			forward = false;
		}
		
		// Find the station
		int index = 0;
		
		//Flag the shiz
		int flag = 0;
		if(this.s instanceof CargoStation){
			flag = 1;
		}
		
		if (forward){
			if (flag == 0){
				index = random.nextInt(l.stations.size()-1-current_station) + current_station + 1;
				Station s = l.stations.get(index);
				return new Passenger(idGen++, 0, this.s, s);
			}
			else{
				while(current_station <= l.stations.size()-1){
			
				//System.out.println("current: " + current_station + " max: " + (l.stations.size()-1));
				if(l.stations.get(current_station) instanceof CargoStation 
						&& l.stations.get(current_station) != this.s){
					Station s = l.stations.get(current_station);
					return new Passenger(idGen++, random.nextInt(51), this.s, s);
				}
				current_station ++;
				}
			}
		}
		else {
			if (flag == 0 ){
				index = current_station - 1 - random.nextInt(current_station);
				Station s = l.stations.get(index);
				return new Passenger(idGen++, 0, this.s, s);
			}
			else{
				while(current_station >= 0){
					if(l.stations.get(current_station) instanceof CargoStation
							&& l.stations.get(current_station) != this.s){
						Station s = l.stations.get(current_station);
						return new Passenger(idGen++, random.nextInt(51), this.s, s);
					}
					current_station --;
				}
			}
		}
		return null;
	}
	
}
