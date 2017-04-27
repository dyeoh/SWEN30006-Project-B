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
package com.unimelb.swen30006.metromadness;

import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.unimelb.swen30006.metromadness.stations.Station;
import com.unimelb.swen30006.metromadness.tracks.Line;
import com.unimelb.swen30006.metromadness.trains.Train;

public class Simulation {

	private ArrayList<Station> stations;
	private ArrayList<Line> lines;
	private ArrayList<Train> trains;
	
	public Simulation(){
		
		// Create a list of lines
		this.lines = new ArrayList<Line>();
				
		// Create a list of stations
		this.stations = new ArrayList<Station>();
		
		// Create a list of trains
		this.trains = new ArrayList<Train>();
	}
	
	
	// Update all the trains in the simulation
	public void update(){
		// Update all the trains
		for(Train t: this.trains){
			t.update(Gdx.graphics.getDeltaTime());
		}
	}
	
	public void render(ShapeRenderer renderer){
		for(Line l: this.lines){
			l.render(renderer);
		}

		for(Train t: this.trains){
			t.render(renderer);
		}
		for(Station s: this.stations){
			s.render(renderer);
		}
	}
	
	public ArrayList<Station> getStations(){
  		return this.stations;
  	}
	  	
  	public ArrayList<Line> getLines(){
  		return this.lines;
  	}
	  	
  	public ArrayList<Train> getTrains(){
  		return this.trains;
  	}
	  	
  	public void setStations(Collection<Station> stations){
  		this.stations.addAll(stations);
  	}
	  	
  	public void setLines(Collection<Line> lines){
  		this.lines.addAll(lines);
  	}
	  	
  	public void setTrains(Collection<Train> trains){
  		this.trains.addAll(trains);
  	}
}
