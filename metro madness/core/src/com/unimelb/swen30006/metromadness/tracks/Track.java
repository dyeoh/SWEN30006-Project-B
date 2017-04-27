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

import java.awt.geom.Point2D;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.unimelb.swen30006.metromadness.trains.Train;

public class Track {
	protected static final float DRAW_RADIUS=10f;
	protected static final int LINE_WIDTH=6;
	protected Point2D.Float startPos;
	protected Point2D.Float endPos;
	protected Color trackColour;
	protected boolean occupied;
	
	public Track(Point2D.Float start, Point2D.Float end, Color trackCol){
		this.startPos = start;
		this.endPos = end;
		this.trackColour = trackCol;
		this.occupied = false;
	}
	
	public void render(ShapeRenderer renderer){
		renderer.rectLine(startPos.x, startPos.y, endPos.x, endPos.y, LINE_WIDTH);
	}
	
	public boolean canEnter(boolean forward){
		return !this.occupied;
	}
	
	public void enter(Train t){
		this.occupied = true;
	}
	
	@Override
	public String toString() {
		return "Track [startPos=" + startPos + ", endPos=" + endPos + ", trackColour=" + trackColour + ", occupied="
				+ occupied + "]";
	}

	public void leave(Train t){
		this.occupied = false;
	}

	public static float getDrawRadius() {
		return DRAW_RADIUS;
	}
}
