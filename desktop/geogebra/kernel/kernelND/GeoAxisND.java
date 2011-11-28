package geogebra.kernel.kernelND;

import geogebra.common.kernel.Matrix.CoordMatrix;

import java.text.NumberFormat;

public interface GeoAxisND extends GeoLineND{
	
	public static final int X_AXIS = 1;
	public static final int Y_AXIS = 2;
	public static final int X_AXIS_3D = 1;
	public static final int Y_AXIS_3D = 2;
	public static final int Z_AXIS_3D = 3;
	
	
	/////////////////////////////////////////
	// METHODS FOR AXIS
	/////////////////////////////////////////	
	public String getAxisLabel();
	public String getUnitLabel();
	public int getTickStyle();
	public boolean getShowNumbers();
	

	public NumberFormat getNumberFormat();
	public double getNumbersDistance();
	public int getTickSize();
	public void updateDecorations(double distance, NumberFormat numberFormat,
			int xOffset, int yOffset, int i, int j);
	public int getNumbersXOffset();
	public int getNumbersYOffset(); 


	/////////////////////////////////////////
	// METHODS FOR GEOELEMENT
	/////////////////////////////////////////	
	public boolean isEuclidianVisible();
	public void setEuclidianVisible(boolean b);
	public void setLabelVisible(boolean b);
	
	
}
