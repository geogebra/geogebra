package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra3D.euclidian3D.EuclidianView3D;

import java.awt.Color;
import java.text.NumberFormat;

public class GeoAxis3D extends GeoLine3D {
	
	public static final int X_AXIS_3D = 1;
	public static final int Y_AXIS_3D = 2;
	public static final int Z_AXIS_3D = 3;
	private String axisLabel;
	
	// for numbers and ticks
	private NumberFormat numberFormat;
	private double numbersDistance;
	private int numbersXOffset, numbersYOffset;
	private int ticksize = 5; //TODO


	public GeoAxis3D(Construction cons) {
		super(cons);
	}
	
	
	public GeoAxis3D(Construction c, int type){
		this(c);
		
		switch (type) {
		case X_AXIS_3D:
			setCoord(EuclidianView3D.o,EuclidianView3D.vx);
			label = "xAxis3D";
			setAxisLabel("x");
			setObjColor(Color.RED);
			break;

		case Y_AXIS_3D:
			setCoord(EuclidianView3D.o,EuclidianView3D.vy);
			label = "yAxis3D";
			setAxisLabel("y");
			//setObjColor(Color.GREEN);
			setObjColor(new Color(0,0.5f,0));
			break;
			
		case Z_AXIS_3D:
			setCoord(EuclidianView3D.o,EuclidianView3D.vz);
			label = "zAxis3D";
			setAxisLabel("z");
			setObjColor(Color.BLUE);
			break;
		}
		
		setFixed(true);
		setLabelVisible(false);
		
	}
	
	
	

	protected boolean isAvailableAtConstructionStep(int step) {
		// this method is overwritten
		// in order to make the axes available
		// in empty constructions too (for step == -1)
		return true;
	}




	public boolean isDefined() {
		return true;
	}

	public int getGeoClassType() {
		
		return GEO_CLASS_AXIS3D;
	}
	
	
	public String toValueString() {
		return label;
	}
	
	
	/** return label of the axis (e.g. x, y, z ...)
	 * @return label of the axis
	 */
	public String getAxisLabel(){
		return axisLabel;
	}

	/** set the label of the axis (e.g. x, y, z ...)
	 * @param label label of the axis
	 */
	public void setAxisLabel(String label){
		axisLabel = label;
	}

	
	/**
	 * overrides GeoElement method : this is a "constant" element, so the label is set
	 */
	public boolean isLabelSet() {
		return true;
	}	

	public String getUnitLabel() {
		// TODO Auto-generated method stub
		return "";
	}


	public int getTickStyle() {
		// TODO Auto-generated method stub
		return 0;
	}


	public boolean getShowNumbers() {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	
	/** update decorations (ticks, numbers, labels)
	 * @param distance
	 * @param numberFormat
	 * @param xOffset 
	 * @param yOffset 
	 * @param labelXOffset 
	 * @param labelYOffset 
	 */
	public void updateDecorations(double distance, NumberFormat numberFormat,
			int xOffset, int yOffset,
			int labelXOffset, int labelYOffset){
		this.numbersDistance = distance;
		this.numberFormat = numberFormat;
		this.numbersXOffset = xOffset;
		this.numbersYOffset = yOffset;
		setLabelOffset(labelXOffset, labelYOffset);
	}
	
	/**
	 * @return distance between ticks
	 */
	public double getNumbersDistance(){
		return numbersDistance;
	}
	
	/**
	 * @return number format
	 */
	public NumberFormat getNumberFormat(){
		return numberFormat;
	}
	
	/**
	 * @return numbers x offset
	 */
	public int getNumbersXOffset(){
		return numbersXOffset;
	}
	
	/**
	 * @return numbers y offset
	 */
	public int getNumbersYOffset(){
		return numbersYOffset;
	}
	
	
	/**
	 * @return tick size
	 */
	public int getTickSize(){
		return ticksize;
	}

}
