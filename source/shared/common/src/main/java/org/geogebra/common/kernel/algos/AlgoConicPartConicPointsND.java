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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Arc or sector defined by a conic, start- and end-point.
 */
public abstract class AlgoConicPartConicPointsND extends AlgoConicPart {

	protected GeoPointND startPoint;
	protected GeoPointND endPoint;

	/**
	 * Creates a new arc or sector algorithm. The type is either
	 * GeoConicPart.CONIC_PART_ARC or GeoConicPart.CONIC_PART_ARC
	 */
	public AlgoConicPartConicPointsND(Construction cons, String label,
			GeoConicND circle, GeoPointND startPoint, GeoPointND endPoint,
			int type) {
		super(cons, type);
		conic = circle;
		this.startPoint = startPoint;
		this.endPoint = endPoint;

		// temp points
		setTempValues();

		conicPart = newGeoConicPart(cons, type);

		setInputOutput(); // for AlgoElement
		initCoords();
		compute();
		setIncidence();

		conicPart.setLabel(label);
	}

	/**
	 * init Coords values
	 */
	protected void initCoords() {
		// none here
	}

	/**
	 * set temp values used for compute
	 */
	abstract protected void setTempValues();

	/**
	 * 
	 * @param cons1
	 *            construction
	 * @param partType
	 *            arc/sector
	 * @return new conic part
	 */
	protected GeoConicND newGeoConicPart(Construction cons1, int partType) {
		return new GeoConicPart(cons1, partType);
	}

	private void setIncidence() {
		// TODO Auto-generated method stub

	}

	public GeoPointND getStartPoint() {
		return startPoint;
	}

	public GeoPointND getEndPoint() {
		return endPoint;
	}

	public GeoConicND getConic() {
		return conic;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = conic;
		input[1] = (GeoElement) startPoint;
		input[2] = (GeoElement) endPoint;

		setOnlyOutput(conicPart);

		setDependencies();
	}

	@Override
	public final void compute() {

		computeParameters();

		// now take the parameters from the temp points
		conicPart.set(conic);

		((GeoConicPartND) conicPart).setParameters(getStartParameter(),
				getEndParameter(), true);
	}

	/**
	 * compute start and end parameters
	 */
	abstract protected void computeParameters();

	/**
	 * 
	 * @return start parameter value
	 */
	abstract protected double getStartParameter();

	/**
	 * 
	 * @return end parameter value
	 */
	abstract protected double getEndParameter();

}
