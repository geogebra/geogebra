package geogebra.common.kernel.discrete.signalprocessor.voronoi.eventqueue;

import geogebra.common.kernel.discrete.signalprocessor.voronoi.statusstructure.VLinkedNode;

public class VCircleEvent extends VEvent {

	/* ***************************************************** */
	// Variables

	// X/Y Coordinate for bottom of circle
	private int x;
	private int y;

	// Note: y corresponds to the lowest most point of the circle
	// whereas center_y is the center of the circle.
	private int center_y;

	public VLinkedNode leafnode;

	/* ***************************************************** */
	// Constructors

	public VCircleEvent() {
		super();
	}

	public VCircleEvent(int _x, int _y) {
		this.x = _x;
		this.y = _y;
	}

	/* ***************************************************** */
	// Abstract Methods

	@Override
	public double getX() {
		return this.x;
	}

	public void setX(int _x) {
		this.x = _x;
	}

	@Override
	public double getY() {
		return this.y;
	}

	public void setY(int _y) {
		this.y = _y;
	}

	public int getCenterY() {
		return this.center_y;
	}

	public void setCenterY(int _center_y) {
		this.center_y = _center_y;
	}

	public boolean isSiteEvent() {
		return false;
	}

	public boolean isCircleEvent() {
		return true;
	}

	/* ***************************************************** */
	// To String Method

	public String toString() {
		return "VCircleEvent (" + x + "," + y + ")";
	}

	/* ***************************************************** */
}
