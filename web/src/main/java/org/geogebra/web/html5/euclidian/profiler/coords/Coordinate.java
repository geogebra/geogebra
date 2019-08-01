package org.geogebra.web.html5.euclidian.profiler.coords;

/**
 * POJO to be used for autonomously drawing onto the canvas.
 */
public class Coordinate {

	private long time;
	private double x;
	private double y;
	private boolean isTouchEnd;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public boolean isTouchEnd() {
		return isTouchEnd;
	}

	public void setTouchEnd(boolean touchEnd) {
		isTouchEnd = touchEnd;
	}
}
