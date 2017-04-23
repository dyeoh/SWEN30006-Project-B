package com.unimelb.swen30006.metromadness.passengers;

import java.util.ArrayList;
import java.util.Random;

import com.unimelb.swen30006.metromadness.stations.Station;

public class Passenger {

	final public int id;
	public Station beginning;
	public Station destination;
	public float travelTime;
	public boolean reachedDestination;
	public Cargo cargo;
	
	public Passenger(int id, Random random, Station start, Station end){
		this.id = id;
		this.beginning = start;
		this.destination = end;
		this.reachedDestination = false;
		this.travelTime = 0;
		this.cargo = generateCargo(random);
	}
	
	public void update(float time){
		if(!this.reachedDestination){
			this.travelTime += time;
		}
	}
	public Cargo getCargo(){
		return cargo;
	}
	public Cargo generateCargo(Random random){
		return new Cargo(random.nextInt(51));
	}
	
}
