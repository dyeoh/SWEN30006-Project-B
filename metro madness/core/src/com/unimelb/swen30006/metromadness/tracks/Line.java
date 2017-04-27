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
package com.unimelb.swen30006.metromadness.tracks;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.unimelb.swen30006.metromadness.stations.Station;

public class Line {
	
	// The colour of this line
	private Color lineColour;
	private Color trackColour;
	
	// The name of this line
	private String name;
	// The stations on this line
	private ArrayList<Station> stations;
	// The tracks on this line between stations
	private ArrayList<Track> tracks;
		
	// Create a line
	public Line(Color stationColour, Color lineColour, String name){
		// Set the line colour
		this.setLineColour(stationColour);
		this.trackColour = lineColour;
		this.setName(name);
		
		// Create the data structures
		this.setStations(new ArrayList<Station>());
		this.tracks = new ArrayList<Track>();
	}
	
	
	public void addStation(Station s, Boolean two_way){
		// We need to build the track if this is adding to existing stations
		if(this.getStations().size() > 0){
			// Get the last station
			Station last = this.getStations().get(this.getStations().size()-1);
			
			// Generate a new track
			Track t;
			if(two_way){
				t = new DualTrack(last.getPosition(), s.getPosition(), this.trackColour);
			} else {
				t = new Track(last.getPosition(), s.getPosition(), this.trackColour);
			}
			this.tracks.add(t);
		}
		
		// Add the station
		s.registerLine(this);
		this.getStations().add(s);
	}
	
	@Override
	public String toString() {
		return "Line [lineColour=" + getLineColour() + ", trackColour=" + trackColour + ", name=" + getName() + "]";
	}


	public boolean endOfLine(Station s) throws Exception{
		if(this.getStations().contains(s)){
			int index = this.getStations().indexOf(s);
			return (index==0 || index==this.getStations().size()-1);
		} else {
			throw new Exception();
		}
	}

	
	
	public Track nextTrack(Station currentStation, boolean forward) throws Exception {
		if(this.getStations().contains(currentStation)){
			// Determine the track index
			int curIndex = this.getStations().lastIndexOf(currentStation);
			// Increment to retrieve
			if(!forward){ curIndex -=1;}
			
			// Check index is within range
			if((curIndex < 0) || (curIndex > this.tracks.size()-1)){
				throw new Exception();
			} else {
				return this.tracks.get(curIndex);
			}
			
		} else {
			throw new Exception();
		}
	}
	
	public Station nextStation(Station s, boolean forward) throws Exception{
		if(this.getStations().contains(s)){
			int curIndex = this.getStations().lastIndexOf(s);
			if(forward){ curIndex+=1;}else{ curIndex -=1;}
			
			// Check index is within range
			if((curIndex < 0) || (curIndex > this.getStations().size()-1)){
				throw new Exception();
			} else {
				return this.getStations().get(curIndex);
			}
		} else {
			throw new Exception();
		}
	}
	
	public void render(ShapeRenderer renderer){
		// Set the color to our line
		renderer.setColor(trackColour);
	
		// Draw all the track sections
		for(Track t: this.tracks){
			t.render(renderer);
		}	
	}


	public ArrayList<Station> getStations() {
		return stations;
	}


	public void setStations(ArrayList<Station> stations) {
		this.stations = stations;
	}


	public Color getLineColour() {
		return lineColour;
	}


	public void setLineColour(Color lineColour) {
		this.lineColour = lineColour;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
}
