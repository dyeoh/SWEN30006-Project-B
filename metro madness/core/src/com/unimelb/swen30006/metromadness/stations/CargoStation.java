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
package com.unimelb.swen30006.metromadness.stations;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.unimelb.swen30006.metromadness.passengers.Passenger;
import com.unimelb.swen30006.metromadness.passengers.PassengerGenerator;
import com.unimelb.swen30006.metromadness.routers.PassengerRouter;
import com.unimelb.swen30006.metromadness.tracks.Line;
import com.unimelb.swen30006.metromadness.trains.CargoTrain;
import com.unimelb.swen30006.metromadness.trains.Train;

public class CargoStation extends Station {
	// Logger
	private static Logger logger = LogManager.getLogger();
	
	private ArrayList<Passenger> waiting;
	private float maxVolume;
	
	public CargoStation(float x, float y, PassengerRouter router, String name, float maxPax) {
		super(x, y, router, name);
		this.waiting = new ArrayList<Passenger>();
		this.g = new PassengerGenerator(this, this.lines, maxPax);
		this.maxVolume = maxPax;
	}
	
	//Handles the entering of a train into a cargo station
	@Override
	public void enter(Train t) throws Exception {
		//Checks if train can enter
		if(trains.size() >= PLATFORMS){
			throw new Exception();
		} else {
			// Add the train
			this.trains.add(t);
			
			// Add the waiting passengers
			Iterator<Passenger> pIter = this.waiting.iterator();
			while(pIter.hasNext()){
				Passenger p = pIter.next();
				try {
					logger.info("Passenger "+p.id+" carrying "+p.getCargo().getWeight() +" kg cargo embarking at "+this.getName()+" heading to "+p.getDestination().getName());
					t.embark(p);
					pIter.remove();
				} catch (Exception e){
					// Do nothing, already waiting
					break;
				}
			}
			
			//Do not add new passengers if there are too many already
			if (this.waiting.size() > maxVolume){
				return;
			}
			// Add the new passenger
			Passenger[] ps = this.g.generatePassengers();
			for(Passenger p: ps){
				if(!(t instanceof CargoTrain)){
					return;
				}
				else{
					try {
						logger.info("Passenger "+p.id+" carrying "+p.getCargo().getWeight() +" kg embarking at "+this.getName()+" heading to "+p.getDestination().getName());
						t.embark(p);
					}
					catch(Exception e){
						if(p != null){
							this.waiting.add(p);
						}
					}
				}
			}
		}
	}

	//Renders the cargo station
	public void render(ShapeRenderer renderer){
		float radius = RADIUS;
		for(int i=0; (i<this.lines.size() && i<MAX_LINES); i++){
			Line l = this.lines.get(i);
			renderer.setColor(l.getLineColour());
			renderer.circle(this.getPosition().x, this.getPosition().y, radius, NUM_CIRCLE_STATMENTS);
			radius = radius - 1;
		}
		
		// Calculate the percentage
		float t = this.trains.size()/(float)PLATFORMS;
		Color c = Color.WHITE.cpy().lerp(Color.BLUE, t);
		if(this.waiting.size() > 0){
			c = Color.GREEN;
		}
		
		renderer.setColor(c);
		renderer.circle(this.getPosition().x, this.getPosition().y, radius, NUM_CIRCLE_STATMENTS);
	}
}
