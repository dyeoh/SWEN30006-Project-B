package com.unimelb.swen30006.metromadness.trains;

import com.badlogic.gdx.graphics.Color;
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
	
}
