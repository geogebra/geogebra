/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Compute a line through a point and ...
 *
 * @author matthieu
 */
public abstract class AlgoLinePoint extends AlgoElement3D {

	private GeoPointND point; // input
	private GeoElement inputParallel; // input
	private GeoLine3D line; // output

	/**
	 * @param cons
	 *            construction
	 * @param point
	 *            point
	 * @param inputParallel
	 *            parallel line or vector
	 */
	public AlgoLinePoint(Construction cons, GeoPointND point,
			GeoElement inputParallel) {
		super(cons);
		this.point = point;
		this.inputParallel = inputParallel;
		line = createLine(cons);
		line.showUndefinedInAlgebraView(true);

		setInputOutput(new GeoElement[] { (GeoElement) point, inputParallel },
				new GeoElement[] { line });

		// compute line
		compute();
	}

	public AlgoLinePoint(Construction cons) {
		super(cons);
	}

	/**
	 * create the line
	 * 
	 * @param cons1
	 *            construction
	 * @return the line
	 */
	protected GeoLine3D createLine(Construction cons1) {
		return new GeoLine3D(cons1);
	}

	public GeoLine3D getLine() {
		return line;
	}

	protected GeoPointND getPoint() {
		return point;
	}

	protected GeoElement getInputParallel() {
		return inputParallel;
	}

	@Override
	public final void compute() {

		Coords v = getDirection();

		if (v.equalsForKernel(0, Kernel.STANDARD_PRECISION)) {
			getLine().setUndefined();
		} else {
			getLine().setCoord(getPoint().getInhomCoordsInD3(), v.normalize());
		}
	}

	abstract protected Coords getDirection();

	@Override
	public String toString(StringTemplate tpl) {
		return getLoc().getPlain("LineThroughAParallelToB", point.getLabel(tpl),
				inputParallel.getLabel(tpl));
	}

	/*
	 * This should apply to every subclass. In case it does not, a case per case
	 * should be used. It produces a GeoNumeric, so beware GeoNumeric will be
	 * treated differently than points.
	 */

}
