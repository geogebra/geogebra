/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.kernel;



/**
 * Adapted from AlgoPerimeterPoly
 */
public class AlgoPathParameter extends AlgoElement {

	// Take a polygon as input
	private GeoPoint point;

	// Output is a GeoNumeric (= a number)
	private GeoNumeric value;

	AlgoPathParameter(Construction cons, String label, GeoPoint point) {
		this(cons, point);
		value.setLabel(label);
	}

	AlgoPathParameter(Construction cons, GeoPoint point) {
		super(cons);
		this.point = point;

		value = new GeoNumeric(cons);
		setInputOutput();
		compute();
	}

	public String getClassName() {
		return "AlgoPathParameter";
	}

	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = point;

		output = new GeoElement[1];
		output[0] = value;
		setDependencies();
	}

	protected final void compute() {
		if (!point.isDefined() || !point.isPointOnPath()) {
			value.setUndefined();
			return;
		}
		
		Path p = point.getPath();
		
		//Application.debug(point.getPathParameter().getT()+" "+p.getMinParameter()+" "+p.getMaxParameter());
		
		value.setValue(PathNormalizer.toNormalizedPathParameter(point.getPathParameter().getT(), p.getMinParameter(), p.getMaxParameter()));
	
	}
				

	GeoNumeric getResult() {
		return value;
	}	
}
