/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDynamicCoordinatesInterface;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Michael
 */
public class AlgoDynamicCoordinates3D extends AlgoElement implements
		AlgoDynamicCoordinatesInterface {

	private GeoNumberValue x, y, z; // input
	private GeoPoint3D P; // input
	private GeoPoint3D M; // output

	public AlgoDynamicCoordinates3D(Construction cons, String label,
			GeoPoint3D arg, GeoNumberValue x, GeoNumberValue y, GeoNumberValue z) {
		super(cons);
		this.P = arg;
		this.x = x;
		this.y = y;
		this.z = z;
		// create new Point
		M = new GeoPoint3D(cons);
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
		input = new GeoElement[4];
		input[0] = P;
		input[1] = x.toGeoElement();
		input[2] = y.toGeoElement();
		input[3] = z.toGeoElement();

		super.setOutputLength(1);
		super.setOutput(0, M);
		setDependencies(); // done by AlgoElement
	}

	public GeoPointND getPoint() {
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
		double zCoord = z.getDouble();

		if (Double.isNaN(xCoord) || Double.isInfinite(xCoord)
				|| Double.isNaN(yCoord) || Double.isInfinite(yCoord)
				|| Double.isNaN(zCoord) || Double.isInfinite(zCoord)) {
			P.setUndefined();
			return;
		}

		M.setCoords(xCoord, yCoord, zCoord, 1.0);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("DynamicCoordinatesOfA", P.getLabel(tpl));
	}

	// TODO Consider locusequability
}
