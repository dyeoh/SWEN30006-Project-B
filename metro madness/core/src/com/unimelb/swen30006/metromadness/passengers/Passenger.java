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

import com.unimelb.swen30006.metromadness.stations.Station;

public class Passenger {

	final public int id;
	public Station beginning;
	public Station destination;
	public float travelTime;
	public boolean reachedDestination;
	public Cargo cargo;
	
	public Passenger(int id, int weight, Station start, Station end){
		this.id = id;
		this.beginning = start;
		this.destination = end;
		this.reachedDestination = false;
		this.travelTime = 0;
		this.cargo = new Cargo(weight);
	}
	
	public void update(float time){
		if(!this.reachedDestination){
			this.travelTime += time;
		}
	}
	public Cargo getCargo(){
		return cargo;
	}
	
}
