/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.matrix.Coords;

public class AlgoPoint3DInRegion extends AlgoElement3D {

	private Region region; // input
	private GeoPoint3D P; // output

	/**
	 * @param cons
	 *            construction
	 * @param region
	 *            region
	 * @param coords
	 *            close coords
	 */
	public AlgoPoint3DInRegion(Construction cons, Region region,
			Coords coords) {
		super(cons);
		this.region = region;
		P = new GeoPoint3D(cons, region);

		setInputOutput(); // for AlgoElement
		if (coords != null) {
			P.setCoords(coords);
		}

		// compute
		compute();

	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param region
	 *            region
	 * @param coords
	 *            close coords
	 */
	public AlgoPoint3DInRegion(Construction cons, String label, Region region,
			Coords coords) {
		this(cons, region, coords);
		P.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.PointIn;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = region.toGeoElement();

		setOnlyOutput(P);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting point
	 */
	public GeoPoint3D getP() {
		return P;
	}

	Region getRegion() {
		return region;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined()) {
			region.regionChanged(P);
			// P.updateCoords();
		} else {
			P.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("PointInA", "Point in %0", input[0].getLabel(tpl));
	}

}
