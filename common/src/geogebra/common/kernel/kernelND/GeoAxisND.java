package geogebra.common.kernel.kernelND;

import geogebra.common.util.NumberFormatAdapter;

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
	
	/** @return format for numbers*/
	public NumberFormatAdapter getNumberFormat();
	/** @return axis distance between numbered ticks*/
	public double getNumbersDistance();
	/** @return tick size in pixels*/
	public int getTickSize();
	/**
	 * @param distance tick distance
	 * @param numberFormat number format
	 * @param xOffset x-offset for numbers
	 * @param yOffset y-offset for numbers
	 * @param labelXoffset x-offset for labels
	 * @param labelYoffset y-offset for labels
	 */
	public void updateDecorations(double distance, NumberFormatAdapter numberFormat,
			int xOffset, int yOffset, int labelXoffset, int labelYoffset);
	/**
	 * @return x-offset of numbers (for 3D)
	 */
	public int getNumbersXOffset();
	/**
	 * @return y-offset of numbers (for 3D)
	 */
	public int getNumbersYOffset(); 
	/** @return axis id*/
	public int getType();

	/////////////////////////////////////////
	// METHODS FOR GEOELEMENT
	/////////////////////////////////////////	
	
	
	
}
