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
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;

/**
 *
 * @author Markus
 */
public class AlgoAsymptote extends AlgoElement {

	private GeoConic c; // input
	private GeoLine[] asymptotes; // output

	private GeoVec2D[] eigenvec;
	private double[] halfAxes;
	private GeoVec2D b;
	private GeoPoint P; // point on asymptotes = b

	/**
	 * Creates new algo for Asymptote
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param c
	 *            conic
	 */
	AlgoAsymptote(Construction cons, String label, GeoConic c) {
		this(cons, c);
		LabelManager.setLabels(label, asymptotes);
	}

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param c
	 *            conic
	 */
	public AlgoAsymptote(Construction cons, String[] labels, GeoConic c) {
		this(cons, c);
		LabelManager.setLabels(labels, asymptotes);
	}

	@Override
	public Commands getClassName() {
		return Commands.Asymptote;
	}

	private AlgoAsymptote(Construction cons, GeoConic c) {
		super(cons);
		this.c = c;

		eigenvec = c.eigenvec;
		halfAxes = c.halfAxes;
		b = c.getB();

		asymptotes = new GeoLine[2];
		asymptotes[0] = new GeoLine(cons);
		asymptotes[1] = new GeoLine(cons);

		P = new GeoPoint(cons);
		asymptotes[0].setStartPoint(P);
		asymptotes[1].setStartPoint(P);

		setInputOutput(); // for AlgoElement

		compute();
	}

	// for AlgoElement
	@Override
	public void setInputOutput() {
		input = new GeoElement[1];
		input[0] = c;

		super.setOutput(asymptotes);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return asymptotes
	 */
	public GeoLine[] getAsymptotes() {
		return asymptotes;
	}

	/**
	 * @return input conic
	 */
	GeoConic getConic() {
		return c;
	}

	// calc asymptotes
	@Override
	public final void compute() {
		// only hyperbolas have asymptotes
		if (c.isDefined() && c.type == GeoConicNDConstants.CONIC_HYPERBOLA) {
			// direction0 = a * eigenvec1 + b * eigenvec2
			// direction1 = -a * eigenvec1 + b * eigenvec2
			// lines through midpoint b

			double vec2x = halfAxes[1] * eigenvec[1].getX();
			double vec2y = halfAxes[1] * eigenvec[1].getY();
			double vec1x = halfAxes[0] * eigenvec[0].getX();
			double vec1y = halfAxes[0] * eigenvec[0].getY();

			asymptotes[0].x = -(vec2y + vec1y);
			asymptotes[0].y = vec2x + vec1x;
			asymptotes[0].z = -(asymptotes[0].x * b.getX()
					+ asymptotes[0].y * b.getY());

			asymptotes[1].x = -(vec2y - vec1y);
			asymptotes[1].y = vec2x - vec1x;
			asymptotes[1].z = -(asymptotes[1].x * b.getX()
					+ asymptotes[1].y * b.getY());

			// point on lines
			P.setCoords(b.getX(), b.getY(), 1.0);
		} else {
			asymptotes[0].setUndefined();
			asymptotes[1].setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("AsymptoteToA", "Asymptote to %0",
				c.getLabel(tpl));
	}

}
