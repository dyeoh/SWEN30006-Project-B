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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.unimelb.swen30006.metromadness.passengers.Passenger;
import com.unimelb.swen30006.metromadness.stations.Station;
import com.unimelb.swen30006.metromadness.tracks.Line;
import com.unimelb.swen30006.metromadness.tracks.Track;

public class Train {
	
	// Logger
	protected static Logger logger = LogManager.getLogger();
	// The state that a train can be in
	public enum State {
		IN_STATION, READY_DEPART, ON_ROUTE, WAITING_ENTRY, FROM_DEPOT
	}

	// Constants
	public static final int MAX_TRIPS=4;
	public static final Color FORWARD_COLOUR = Color.ORANGE;
	public static final Color BACKWARD_COLOUR = Color.VIOLET;
	public static final float TRAIN_WIDTH=4;
	public static final float TRAIN_LENGTH = 6;
	public static final float TRAIN_SPEED=50f;
	public static final int BIG_SIZE = 80;
	public static final int SMALL_SIZE = 10;
	
	// The train's name
	
	protected String name;

	// The line that this is traveling on
	protected Line trainLine;

	// Passenger Information
	protected ArrayList<Passenger> passengers;
	protected float departureTimer;
	
	// Station and track and position information
	protected Station station; 
	protected Track track;
	protected Point2D.Float pos;

	// Direction and direction
	protected boolean forward;
	protected State state;

	// State variables
	private int numTrips;
	protected boolean disembarked;
	
	
	protected State previousState = null;
	
	//
	
	private Color displayColour;
	protected int trainSize;

	
	public Train(Line trainLine, Station start, boolean forward, String name, Color colour, int trainSize){
		this.trainLine = trainLine;
		this.station = start;
		this.state = State.FROM_DEPOT;
		this.setForward(forward);
		this.passengers = new ArrayList<Passenger>();
		this.name = name;
		this.displayColour = colour;
		this.setTrainSize(trainSize);
	}
	
	//Updates the state of the train and all the passengers
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
			
			if(passengers.isEmpty()){
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
					//e.printStackTrace();
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

	//Moves the train
	public void move(float delta){
		// Work out where we're going
		float angle = angleAlongLine(this.pos.x,this.pos.y,this.station.getPosition().x,this.station.getPosition().y);
		float newX = this.pos.x + (float)( Math.cos(angle) * delta * TRAIN_SPEED);
		float newY = this.pos.y + (float)( Math.sin(angle) * delta * TRAIN_SPEED);
		this.pos.setLocation(newX, newY);
	}

	//Embraks the passengers only if they are not carrying any cargo
	public void embark(Passenger p) throws Exception {
		if(this.passengers.size() > trainSize || p.getCargo().getWeight() != 0){
			throw new Exception();
		}
		this.passengers.add(p);
	}

	//Disembarks the passengers if they have reached their destination
	public ArrayList<Passenger> disembark(){
		ArrayList<Passenger> disembarking = new ArrayList<Passenger>();
		Iterator<Passenger> iterator = this.passengers.iterator();
		while(iterator.hasNext()){
			Passenger p = iterator.next();
			if(this.station.shouldLeave(p)){
				logger.info("Passenger "+p.id+" is disembarking at "+this.station.getName());
				disembarking.add(p);
				iterator.remove();
			}
		}
		return disembarking;
	}

	@Override
	public String toString() {
		return "Train [line=" + this.trainLine.getName() +", departureTimer=" + departureTimer + ", pos=" + pos + ", forward=" + isForward() + ", state=" + state
				+ ", numTrips=" + numTrips + ", disembarked=" + disembarked + "]";
	}

	public boolean inStation(){
		return (this.state == State.IN_STATION || this.state == State.READY_DEPART);
	}
	
	public float angleAlongLine(float x1, float y1, float x2, float y2){	
		return (float) Math.atan2((y2-y1),(x2-x1));
	}

	//Draws the train on the map
	public void render(ShapeRenderer renderer){
		if(!this.inStation()){
			Color col = this.isForward() ? FORWARD_COLOUR : BACKWARD_COLOUR;
			if(trainSize > 10){
				float percentage = this.passengers.size()/10f;
				renderer.setColor(col.cpy().lerp(displayColour, percentage));
				renderer.circle(this.pos.x, this.pos.y, TRAIN_WIDTH*(1+percentage));
			}
			else{
				float percentage = this.passengers.size()/20f;
				renderer.setColor(col.cpy().lerp(displayColour, percentage));
				renderer.circle(this.pos.x, this.pos.y, TRAIN_WIDTH*(1+percentage));
			}
		}
	}

	public boolean isForward() {
		return forward;
	}

	public void setForward(boolean forward) {
		this.forward = forward;
	}

	public int getTrainSize() {
		return trainSize;
	}

	public void setTrainSize(int trainSize) {
		this.trainSize = trainSize;
	}
	
}