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
	private Station beginning;
	private Station destination;
	private float travelTime;
	private boolean reachedDestination;
	private Cargo cargo;
	
	public Passenger(int id, int weight, Station start, Station end){
		this.id = id;
		this.setBeginning(start);
		this.setDestination(end);
		this.reachedDestination = false;
		this.setTravelTime(0);
		this.cargo = new Cargo(weight);
	}
	
	public void update(float time){
		if(!this.reachedDestination){
			this.setTravelTime(this.getTravelTime() + time);
		}
	}
	public Cargo getCargo(){
		return cargo;
	}

	public float getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(float travelTime) {
		this.travelTime = travelTime;
	}

	public Station getDestination() {
		return destination;
	}

	public void setDestination(Station destination) {
		this.destination = destination;
	}

	public Station getBeginning() {
		return beginning;
	}

	public void setBeginning(Station beginning) {
		this.beginning = beginning;
	}
	
}
