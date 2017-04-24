package com.unimelb.swen30006.metromadness.trains;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.unimelb.swen30006.metromadness.passengers.Passenger;
import com.unimelb.swen30006.metromadness.stations.Station;
import com.unimelb.swen30006.metromadness.tracks.Line;

public class CargoTrain extends Train {
	
	private int cargoCapacity;
	private int currentCargo;

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

	@Override
	public void embark(Passenger p) throws Exception {
		int cargoWeight = p.getCargo().getWeight();
		int newWeight = currentCargo + cargoWeight;
		/*System.out.println("New Weight : " + newWeight + "CargoCap : " + cargoCapacity);*/
		if(this.passengers.size() > trainSize || newWeight > cargoCapacity){
			/*System.out.println("PASSENGER DID NOT GET ON");*/
			throw new Exception();
		}
		this.passengers.add(p);
		currentCargo = newWeight;
	}
	
	@Override
	public ArrayList<Passenger> disembark(){
		ArrayList<Passenger> disembarking = new ArrayList<Passenger>();
		Iterator<Passenger> iterator = this.passengers.iterator();
		while(iterator.hasNext()){
			Passenger p = iterator.next();
			if(this.station.shouldLeave(p)){
				logger.info("Passenger "+p.id+" is disembarking at "+this.station.name);
				currentCargo = currentCargo - p.getCargo().getWeight();
				disembarking.add(p);
				iterator.remove();
			}
		}
		return disembarking;
	}
	
}
