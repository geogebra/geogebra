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

package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoTurtle;

/**
 * Creates a GeoTurtle.
 * 
 * @author G. Sturr
 */
public class AlgoTurtle extends AlgoElement {
	/** output turtle */
	protected GeoTurtle turtle;
	private StringBuilder sb;

	/**
	 * @param cons
	 *            the construction
	 * @param label
	 *            label for output
	 */
	public AlgoTurtle(Construction cons, String label) {
		super(cons);
		createTurtle();
		compute();
		setInputOutput(); // for AlgoElement

		turtle.setLabel(label);

	}

	/**
	 * @param cons
	 *            construction
	 */
	protected AlgoTurtle(Construction cons) {
		super(cons);

		createTurtle();
		compute();
		setInputOutput(); // for AlgoElement

	}

	/**
	 * create the turtle
	 */
	protected void createTurtle() {
		turtle = new GeoTurtle(this.cons);
	}

	@Override
	public Commands getClassName() {
		return Commands.Turtle;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		// set dependencies

		// (none for now)
		input = new GeoElement[0];

		// set output
		setOnlyOutput(turtle);
		setDependencies();
	}

	@Override
	public void update() {
		// compute output from input
		compute();
		getOutput(0).update();
	}

	/**
	 * @return this turtle
	 */
	public GeoTurtle getTurtle() {
		return turtle;
	}

	@Override
	public void compute() {

		// do nothing for now
	}

	@Override
	final public String toString(StringTemplate tpl) {

		if (sb == null) {
			sb = new StringBuilder();
		} else {
			sb.setLength(0);
		}

		sb.append(getLoc().getMenu("Turtle"));

		return sb.toString();
	}

}
