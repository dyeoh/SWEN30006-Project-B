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
package com.unimelb.swen30006.metromadness.trains;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.unimelb.swen30006.metromadness.passengers.Passenger;
import com.unimelb.swen30006.metromadness.passengers.PassengerGenerator;
import com.unimelb.swen30006.metromadness.stations.CargoStation;
import com.unimelb.swen30006.metromadness.stations.Station;
import com.unimelb.swen30006.metromadness.tracks.Line;

public class CargoTrain extends Train {
	
	//Initialize variables for CargoTrain
	private int cargoCapacity;
	private int currentCargo;

	//Class initializer
	public CargoTrain(Line trainLine, Station start, boolean forward, String name, Color colour, int trainSize, int cargoCapacity) {
		super(trainLine, start, forward, name, colour, trainSize);
		this.setCargoCapacity(cargoCapacity);
	}

	public int getCargoCapacity() {
		return cargoCapacity;
	}

	public void setCargoCapacity(int cargoCapacity) {
		this.cargoCapacity = cargoCapacity;
	}

	public int getCurrentCargo() {
		return currentCargo;
	}

	public void setCurrentCargo(int currentCargo) {
		this.currentCargo = currentCargo;
	}

	//Embarks passenger with luggage storing it into the cargo of the train
	@Override
	public void embark(Passenger p) throws Exception {
		int cargoWeight = p.getCargo().getWeight();
		int newWeight = currentCargo + cargoWeight;
		if(cargoWeight == 0){
			throw new Exception();
		}
		//System.out.println("New Weight : " + newWeight + "CargoCap : " + cargoCapacity);
		if(this.passengers.size() > trainSize || newWeight > cargoCapacity){
			/*System.out.println("PASSENGER DID NOT GET ON");*/
			throw new Exception();
		}
		this.passengers.add(p);
		currentCargo = newWeight;
	}
	
	//Disembarks the passenger removing their luggage from the train
	@Override
	public ArrayList<Passenger> disembark(){
		ArrayList<Passenger> disembarking = new ArrayList<Passenger>();
		Iterator<Passenger> iterator = this.passengers.iterator();
		while(iterator.hasNext()){
			Passenger p = iterator.next();
			if(this.station.shouldLeave(p)){
				logger.info("Passenger "+p.id+" is disembarking at "+this.station.getName());
				currentCargo = currentCargo - p.getCargo().getWeight();
				disembarking.add(p);
				iterator.remove();
			}
		}
		return disembarking;
	}
	
	
	//Updates the state of the cargo train and passengers
	@Override
	public void update(float delta){
		// Update all passengers
		for(Passenger p: this.passengers){
			p.update(delta);
		}
		boolean hasChanged = false;
		if(previousState == null || previousState != this.state){
			previousState = this.state;
			hasChanged = true;
		}
		
		// Update the state
		switch(this.state) {
		case FROM_DEPOT:
			if(hasChanged){
				logger.info(this.name+ " is travelling from the depot: "+this.station.getName()+" Station...");
			}
			
			// We have our station initialized we just need to retrieve the next track, enter the
			// current station officially and mark as in station
			try {
				if(this.station.canEnter()){
					
					this.station.enter(this);
					this.pos = (Point2D.Float) this.station.getPosition().clone();
					this.state = State.IN_STATION;
					this.disembarked = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		case IN_STATION:
			if(hasChanged){
				logger.info(this.name+" is in "+this.station.getName()+" Station.");
			}
			
			if(this.station instanceof CargoStation){
				PassengerGenerator gen = this.station.getGenerator();
				Random random = new Random(30006);
				if(gen.generatePassenger(random) == null){
					return;
				}
				// When in station we want to disembark passengers 
				// and wait 10 seconds for incoming passengers
				if(!this.disembarked){
					this.disembark();
					this.departureTimer = this.station.getDepartureTime();
					this.disembarked = true;
				} else {
					// Count down if departure timer. 
					if(this.departureTimer>0){
						this.departureTimer -= delta;
					} else {
						// We are ready to depart, find the next track and wait until we can enter 
						try {
							boolean endOfLine = this.trainLine.endOfLine(this.station);
							if(endOfLine){
								this.setForward(!this.isForward());
							}
							this.track = this.trainLine.nextTrack(this.station, this.isForward());
							this.state = State.READY_DEPART;
							break;
						} catch (Exception e){
							// Massive error.
							return;
						}
					}
				}
			}
			else{
				// We are ready to depart, find the next track and wait until we can enter 
				try {
					boolean endOfLine = this.trainLine.endOfLine(this.station);
					if(endOfLine){
						this.setForward(!this.isForward());
					}
					this.track = this.trainLine.nextTrack(this.station, this.isForward());
					this.state = State.READY_DEPART;
					break;
				} catch (Exception e){
					// Massive error.
					return;
				}
			}
			break;
		case READY_DEPART:
			if(hasChanged){
				try{
					Station next = this.trainLine.nextStation(this.station, this.isForward());
					logger.info(this.name+ " is ready to depart for "+next.getName()+" Station!");
				}
				catch(Exception e){
					//e.printStackTrace();
				}
			}
			
			// When ready to depart, check that the track is clear and if
			// so, then occupy it if possible.
			if(this.track.canEnter(this.isForward())){
				try {
					// Find the next
					Station next = this.trainLine.nextStation(this.station, this.isForward());
					// Depart our current station
					this.station.depart(this);
					this.station = next;

				} catch (Exception e) {
//					e.printStackTrace();
				}
				this.track.enter(this);
				this.state = State.ON_ROUTE;
			}		
			break;
		case ON_ROUTE:
			if(hasChanged){
				logger.info(this.name+ " enroute to "+this.station.getName()+" Station!");
			}
			
			// Checkout if we have reached the new station
			if(this.pos.distance(this.station.getPosition()) < 10 ){
				this.state = State.WAITING_ENTRY;
			} else {
				move(delta);
			}
			break;
		case WAITING_ENTRY:
			if(hasChanged){
				logger.info(this.name+ " is awaiting entry "+this.station.getName()+" Station..!");
			}
			
			// Waiting to enter, we need to check the station has room and if so
			// then we need to enter, otherwise we just wait
			try {
				if(this.station.canEnter()){
					this.track.leave(this);
					this.pos = (Point2D.Float) this.station.getPosition().clone();
					this.station.enter(this);
					this.state = State.IN_STATION;
					this.disembarked = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

}
