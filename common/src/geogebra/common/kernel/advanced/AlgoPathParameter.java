/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.PathNormalizer;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;



/**
 * Adapted from AlgoPerimeterPoly
 */
public class AlgoPathParameter extends AlgoElement {

	// Take a polygon as input
	private GeoPoint point;

	// Output is a GeoNumeric (= a number)
	private GeoNumeric value;

	public AlgoPathParameter(Construction cons, String label, GeoPoint point) {
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

	@Override
	public Commands getClassName() {
		return Commands.PathParameter;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = point;

		super.setOutputLength(1);
        super.setOutput(0, value);
		setDependencies();
	}

	@Override
	public final void compute() {
		if (!point.isDefined() || !point.isPointOnPath()) {
			value.setUndefined();
			return;
		}
		
		Path p = point.getPath();
		
		//Application.debug(point.getPathParameter().getT()+" "+p.getMinParameter()+" "+p.getMaxParameter());
		
		value.setValue(PathNormalizer.toNormalizedPathParameter(point.getPathParameter().getT(), p.getMinParameter(), p.getMaxParameter()));
	
	}
				

	public GeoNumeric getResult() {
		return value;
	}	

	// TODO Consider locusequability
}
