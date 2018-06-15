package org.geogebra.common.euclidian.draw;

public interface DrawWidget {
	public void setWidth(int newWidth);

	public void setHeight(int newHeight);

	public int getLeft();

	public int getTop();

	public void setAbsoluteScreenLoc(int x, int y);

	public double getOriginalRatio();

	public int getWidth();

	public int getHeight();

	public void resetRatio();

	public void update();
}
