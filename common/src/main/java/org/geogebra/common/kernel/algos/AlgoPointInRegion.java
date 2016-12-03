/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Point in region algorithm
 * 
 * @author mathieu
 *
 */
public class AlgoPointInRegion extends AlgoElement {

	protected Region region; // input
	protected GeoPoint P; // output

	public AlgoPointInRegion(Construction cons, Region region) {

		super(cons);

		this.region = region;
	}

	public AlgoPointInRegion(Construction cons, String label, Region region,
			double x, double y) {

		this(cons, region);

		P = new GeoPoint(cons, region);
		P.setCoords(x, y, 1.0);

		setInputOutput(); // for AlgoElement

		compute();
		P.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.PointIn;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_POINT_ON_OBJECT;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		input = new GeoElement[1];
		input[0] = region.toGeoElement();

		setOutputLength(1);
		setOutput(0, P);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * returns the point
	 * 
	 * @return resulting point
	 */
	public GeoPoint getP() {
		return P;
	}

	/**
	 * Returns the region
	 * 
	 * @return region
	 */
	Region getRegion() {
		return region;
	}

	@Override
	public void compute() {

		if (region.isDefined()) {
			region.regionChanged(P);
			P.updateCoords();
		} else {
			P.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("PointInA", input[0].getLabel(tpl));

	}

	
}
