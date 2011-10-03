/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

public class GeoAxis extends GeoLine {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int X_AXIS = 1;
	public static final int Y_AXIS = 2;
	
	private GeoPoint origin;
	private int type;

	public GeoAxis(Construction cons, int type) {
		super(cons);
		this.type = type;
		origin = new GeoPoint(cons);
		origin.setCoords(0,0,1);
		setStartPoint(origin);
		
		switch (type) {
			case X_AXIS:
				setCoords(0, 1, 0);
				label = "xAxis";
				break;
			
			case Y_AXIS:
				setCoords(-1, 0, 0);
				label = "yAxis";
				break;
		}
		setFixed(true);
	}
	
	public int getType() {
		return type;
	}
	
	/**
	 * Returns whether this object is available at
	 * the given construction step (this depends on
	 * this object's construction index).
	 */
	protected boolean isAvailableAtConstructionStep(int step) {
		// this method is overwritten
		// in order to make the axes available
		// in empty constructions too (for step == -1)
		return true;
	}
	
	public String getLabel() {
		if (kernel.isPrintLocalizedCommandNames())
			return app.getPlain(label);
		else
			return label;
	}
	
	/**
	 * Returns whether str is equal to this axis' label.
	 * @param str string for comparsion
	 * @return whether str is equal to this axis' label.
	 */
	public boolean equalsLabel(String str) {
		if (str == null) return false;
		return str.equals(label) ||    
					str.equals(app.getPlain(label));		
	}
	
	public String typeString() {
		return "Line";
	}
	
    public int getGeoClassType() {
    	return GEO_CLASS_AXIS;
    }
}
