package org.geogebra.common.util.shape;

public class Rectangle {

	private final XYPoint location;
	private final Size size;

	public Rectangle() {
		this(0, 0, 0, 0);
	}

	public Rectangle(double minX, double maxX, double minY, double maxY) {
		this(new XYPoint(minX, minY), new Size(maxX - minX, maxY - minY));
	}

	public Rectangle(XYPoint location, Size size) {
		this.location = location;
		this.size = size;
	}

	public double getWidth() {
		return size.getWidth();
	}

	public double getHeight() {
		return size.getHeight();
	}

	public double getMinX() {
		return location.getX();
	}

	public void setMinX(double minX) {
		double maxX = getMaxX();
		location.setX(minX);
		size.setWidth(maxX - minX);
	}

	public double getMaxX() {
		return location.getX() + size.getWidth();
	}

	public void setMaxX(double maxX) {
		size.setWidth(maxX - getMinX());
	}

	public double getMinY() {
		return location.getY();
	}

	public void setMinY(double minY) {
		double maxY = getMaxY();
		location.setY(minY);
		size.setHeight(maxY - minY);
	}

	public double getMaxY() {
		return location.getY() + size.getHeight();
	}

	public void setMaxY(double maxY) {
		size.setHeight(maxY - getMinY());
	}

	public void moveVertically(double delta) {
		location.setY(location.getY() + delta);
	}
}
