package org.geogebra.common.kernel.kernelND;


/**
 * Axis in euclidian view (or euclidian view 3D)
 *
 */
public interface GeoAxisND extends GeoLineND{
	/** xAxis id (for XML)*/
	public static final int X_AXIS = 0;
	/** yAxis id (for XML)*/
	public static final int Y_AXIS = 1;
	/** zAxis id (for XML)*/
	public static final int Z_AXIS = 2;
	/** xAxis3D id (for XML)*/
	public static final int X_AXIS_3D = X_AXIS;
	/** yAxis3D id (for XML)*/
	public static final int Y_AXIS_3D = Y_AXIS;
	/** zAxis3D id (for XML)*/
	public static final int Z_AXIS_3D = Z_AXIS;
	
	
	/////////////////////////////////////////
	// METHODS FOR AXIS
	/////////////////////////////////////////	
	/** @return axis unit*/
	public String getUnitLabel();
	/** @return tick style*/
	public int getTickStyle();
	/** @return whether numbers should be displayed*/
	public boolean getShowNumbers();
	
	/** @return tick size in pixels*/
	public int getTickSize();
	

	
	/** @return axis id*/
	public int getType();

	/////////////////////////////////////////
	// METHODS FOR GEOELEMENT
	/////////////////////////////////////////	
	
	
	
}
