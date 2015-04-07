/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoAxes.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 *
 * @author Markus
 * @version
 */
public abstract class AlgoAxesQuadricND extends AlgoElement {

	protected GeoQuadricND c; // input
	protected GeoLineND[] axes; // output

	private GeoVec2D[] eigenvec;
	private GeoVec2D b;

	protected AlgoAxesQuadricND(Construction cons, String label, GeoQuadricND c) {
		this(cons, c);
		GeoElement.setLabels(label, (GeoElement[]) axes);
	}

	protected AlgoAxesQuadricND(Construction cons, String[] labels,
			GeoQuadricND c) {
		this(cons, c);
		GeoElement.setLabels(labels, (GeoElement[]) axes);
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

	public GeoLineND[] getAxes() {
		return axes;
	}

	GeoQuadricND getConic() {
		return c;
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
		return getLoc().getPlain("AxisOfA", c.getLabel(tpl));
	}

	// TODO Consider locusequability
}
