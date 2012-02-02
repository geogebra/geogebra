package geogebra.common.kernel.kernelND;

import geogebra.common.util.NumberFormatAdapter;

public interface GeoAxisND extends GeoLineND{
	
	public static final int X_AXIS = 0;
	public static final int Y_AXIS = 1;
	public static final int Z_AXIS = 2;
	public static final int X_AXIS_3D = X_AXIS;
	public static final int Y_AXIS_3D = Y_AXIS;
	public static final int Z_AXIS_3D = Z_AXIS;
	
	
	/////////////////////////////////////////
	// METHODS FOR AXIS
	/////////////////////////////////////////	
	public String getAxisLabel();
	public String getUnitLabel();
	public int getTickStyle();
	public boolean getShowNumbers();
	

	public NumberFormatAdapter getNumberFormat();
	public double getNumbersDistance();
	public int getTickSize();
	public void updateDecorations(double distance, NumberFormatAdapter numberFormat,
			int xOffset, int yOffset, int i, int j);
	public int getNumbersXOffset();
	public int getNumbersYOffset(); 

	public int getType();

	/////////////////////////////////////////
	// METHODS FOR GEOELEMENT
	/////////////////////////////////////////	
	public boolean isEuclidianVisible();
	public void setEuclidianVisible(boolean b);
	public void setLabelVisible(boolean b);
	
	
}
