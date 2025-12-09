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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Markus
 */
public class AlgoAxis extends AlgoElement {

	private GeoConicND c; // input
	private GeoLine axis; // output

	private GeoVec2D[] eigenvec;
	private GeoVec2D b;
	protected GeoPointND P;
	/** 0 for major, 1 for minor */
	protected int axisId;

	protected AlgoAxis(Construction cons, GeoConicND c, int axisId) {
		super(cons);
		this.c = c;
		this.axisId = axisId;
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param c
	 *            conic
	 * @param axisId
	 *            0 for major, 1 for minor
	 */
	public AlgoAxis(Construction cons, String label, GeoConicND c,
			int axisId) {
		this(cons, c, axisId);

		eigenvec = c.eigenvec;
		b = c.getB();

		axis = new GeoLine(cons);
		finishSetup(label);
	}

	protected void finishSetup(String label) {
		P = new GeoPoint(cons);
		getAxis().setStartPoint(P);

		setInputOutput(); // for AlgoElement
		compute();
		getAxis().setLabel(label);

	}

	@Override
	public Commands getClassName() {
		return axisId == 0 ? Commands.FirstAxis : Commands.SecondAxis;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = c;

		setOnlyOutput(getAxis());
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting axis
	 */
	public GeoLineND getAxis() {
		return axis;
	}

	protected GeoConicND getConic() {
		return c;
	}

	// calc axes
	@Override
	public void compute() {
		// axes are lines with directions of eigenvectors
		// through midpoint b

		axis.x = -eigenvec[axisId].getY();
		axis.y = eigenvec[axisId].getX();
		axis.z = -(axis.x * b.getX() + axis.y * b.getY());

		P.setCoords(b.getX(), b.getY(), 1.0);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		if (axisId == 1) {
			return getLoc().getPlainDefault("SecondAxisOfA", "Minor axis of %0",
					c.getLabel(tpl));
		}
		return getLoc().getPlainDefault("FirstAxisOfA", "Major axis of %0",
				c.getLabel(tpl));
	}

}
