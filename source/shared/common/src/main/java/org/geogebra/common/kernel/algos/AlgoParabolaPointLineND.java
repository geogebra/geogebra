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
import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Markus
 */
public abstract class AlgoParabolaPointLineND extends AlgoElement {

	protected GeoPointND F; // input
	protected GeoLineND line; // input
	protected GeoConicND parabola; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param F
	 *            focus point
	 * @param l
	 *            directrix
	 */
	public AlgoParabolaPointLineND(Construction cons, String label,
			GeoPointND F, GeoLineND l) {
		this(cons, F, l);
		parabola.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param F
	 *            focus point
	 * @param l
	 *            directrix
	 */
	public AlgoParabolaPointLineND(Construction cons, GeoPointND F,
			GeoLineND l) {
		super(cons);
		this.F = F;
		this.line = l;
		parabola = newGeoConic(cons);
		setInputOutput(); // for AlgoElement

		compute();

		parabola.setEquationForm(QuadraticEquationRepresentable.Form.IMPLICIT);
		EquationBehaviour equationBehaviour = kernel.getEquationBehaviour();
		if (equationBehaviour != null) {
			parabola.setEquationForm(equationBehaviour.getConicCommandEquationForm());
		}
	}

	abstract protected GeoConicND newGeoConic(Construction cons1);

	@Override
	public Commands getClassName() {
		return Commands.Parabola;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_PARABOLA;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) F;
		input[1] = (GeoElement) line;

		setOnlyOutput(parabola);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return parabola
	 */
	public GeoConicND getParabola() {
		return parabola;
	}

	/**
	 * @return focus point
	 */
	public GeoPointND getFocus() {
		return F;
	}

	/**
	 * @return directrix
	 */
	public GeoLineND getLine() {
		return line;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("ParabolaWithFocusAandDirectrixB",
				"Parabola with focus %0 and directrix %1",
				F.getLabel(tpl), line.getLabel(tpl));

	}

}
