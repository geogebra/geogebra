/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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
		setOutputLength(1);
		setOutput(0, turtle);
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

	private StringBuilder sb;

	@Override
	final public String toString(StringTemplate tpl) {

		if (sb == null)
			sb = new StringBuilder();
		else
			sb.setLength(0);

		sb.append(getLoc().getPlain("Turtle"));

		return sb.toString();
	}

	
}
