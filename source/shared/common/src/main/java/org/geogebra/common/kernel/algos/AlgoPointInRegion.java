/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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

	/**
	 * @param cons
	 *            construction
	 * @param region
	 *            region
	 */
	public AlgoPointInRegion(Construction cons, Region region) {
		super(cons);
		this.region = region;
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param region
	 *            region
	 * @param x
	 *            estimated x-coord
	 * @param y
	 *            estimated y-coord
	 */
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

		setOnlyOutput(P);
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
		return getLoc().getPlainDefault("PointInA", "Point in %0",
				input[0].getLabel(tpl));

	}

}
