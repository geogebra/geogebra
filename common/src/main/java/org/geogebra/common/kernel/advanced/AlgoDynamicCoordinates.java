/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDynamicCoordinatesInterface;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Michael
 */
public class AlgoDynamicCoordinates extends AlgoElement implements
		AlgoDynamicCoordinatesInterface {

	private GeoNumberValue x, y; // input
	private GeoPoint P; // input
	private GeoPoint M; // output

	public AlgoDynamicCoordinates(Construction cons, String label, GeoPoint P,
			GeoNumberValue x, GeoNumberValue y) {
		super(cons);
		this.P = P;
		this.x = x;
		this.y = y;
		// create new Point
		M = new GeoPoint(cons);
		setInputOutput();

		compute();
		M.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.DynamicCoordinates;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = P;
		input[1] = x.toGeoElement();
		input[2] = y.toGeoElement();

		super.setOutputLength(1);
		super.setOutput(0, M);
		setDependencies(); // done by AlgoElement
	}

	public GeoPoint getPoint() {
		return M;
	}

	public GeoPointND getParentPoint() {
		return P;
	}

	// calc midpoint
	@Override
	public final void compute() {

		double xCoord = x.getDouble();
		double yCoord = y.getDouble();

		if (Double.isNaN(xCoord) || Double.isInfinite(xCoord)
				|| Double.isNaN(yCoord) || Double.isInfinite(yCoord)) {
			P.setUndefined();
			return;
		}

		M.setCoords(xCoord, yCoord, 1.0);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("DynamicCoordinatesOfA", P.getLabel(tpl));
	}

	public boolean isChangeable(GeoElement out) {
		return true;
	}

	// TODO Consider locusequability
}
