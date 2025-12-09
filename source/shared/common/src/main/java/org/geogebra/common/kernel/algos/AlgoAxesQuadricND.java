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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 *
 * @author Markus
 */
public abstract class AlgoAxesQuadricND extends AlgoElement {

	protected GeoQuadricND c; // input
	protected GeoLineND[] axes; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param c
	 *            quadric / conic
	 */
	protected AlgoAxesQuadricND(Construction cons, String label,
			GeoQuadricND c) {
		this(cons, c);
		LabelManager.setLabels(label, axes);
	}

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param c
	 *            quadric / conic
	 */
	protected AlgoAxesQuadricND(Construction cons, String[] labels,
			GeoQuadricND c) {
		this(cons, c);
		LabelManager.setLabels(labels, axes);
	}

	@Override
	public Commands getClassName() {
		return Commands.Axes;
	}

	private AlgoAxesQuadricND(Construction cons, GeoQuadricND c) {
		super(cons);
		this.c = c;

		createInput();

		setInputOutput(); // for AlgoElement

		compute();
	}

	/**
	 * create axes, create and set the start point
	 */
	protected abstract void createInput();

	// for AlgoElement
	@Override
	public void setInputOutput() {

		input = new GeoElement[1];
		input[0] = c;

		setOutputLength(axes.length);
		for (int i = 0; i < axes.length; i++) {
			setOutput(i, (GeoElement) axes[i]);
		}

		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting axes
	 */
	public GeoLineND[] getAxes() {
		return axes;
	}

	// calc axes
	@Override
	public void compute() {

		for (int i = 0; i < axes.length; i++) {
			setAxisCoords(i);
		}

	}

	/**
	 * set coords to i-th axis
	 * 
	 * @param i
	 *            axis
	 */
	protected abstract void setAxisCoords(int i);

	@Override
	public final String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("AxisOfA", "Axis of %0",
				c.getLabel(tpl));
	}

}
