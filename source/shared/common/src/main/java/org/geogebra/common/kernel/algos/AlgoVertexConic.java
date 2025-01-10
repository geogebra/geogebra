/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoVertex.java
 *
 * Created on 11. November 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Algorithm for conic vertices
 * 
 * @author Markus
 */
public class AlgoVertexConic extends AlgoElement {

	protected GeoConicND c; // input
	protected GeoPointND[] vertex; // output

	private double temp1;
	private double temp2;
	private GeoVec2D b;
	private GeoVec2D[] eigenvec;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for outputs (for A outputs A_1,A_2,... will be created)
	 * @param c
	 *            conic
	 */
	AlgoVertexConic(Construction cons, String label, GeoConicND c) {
		this(cons, c);
		LabelManager.setLabels(label, vertex);
	}

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels for outputs
	 * @param c
	 *            conic
	 */
	public AlgoVertexConic(Construction cons, String[] labels, GeoConicND c) {
		this(cons, c);
		LabelManager.setLabels(labels, vertex);
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param c
	 *            conic
	 */
	public AlgoVertexConic(Construction cons, GeoConicND c) {
		super(cons);
		this.c = c;

		createVertex(cons);

		for (int i = 1; i < vertex.length; i++) {
			// only first undefined point should be shown in algebra window
			vertex[i].showUndefinedInAlgebraView(false);
		}

		setInputOutput(); // for AlgoElement

		b = c.getB();
		eigenvec = c.eigenvec;

		compute();
	}

	/**
	 * create the vertices
	 * 
	 * @param cons1
	 *            construction
	 */
	protected void createVertex(Construction cons1) {
		vertex = new GeoPoint[4];
		for (int i = 0; i < vertex.length; i++) {
			vertex[i] = new GeoPoint(cons1);
		}
	}

	@Override
	public Commands getClassName() {
		return Commands.Vertex;
	}

	// for AlgoElement
	@Override
	public void setInputOutput() {
		input = new GeoElement[1];
		input[0] = c;

		super.setOutput((GeoElement[]) vertex);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return input conic
	 */
	GeoConicND getConic() {
		return c;
	}

	/**
	 * 
	 * @return array of conic vertices
	 */
	public GeoPointND[] getVertex() {
		return vertex;
	}

	/**
	 * set the coords of the i-th vertex
	 * 
	 * @param i
	 *            index
	 * @param x
	 *            x-coord in plane
	 * @param y
	 *            y-coord in plane
	 */
	protected void setCoords(int i, double x, double y) {
		vertex[i].setCoords(x, y, 1.0);
	}

	@Override
	public final void compute() {
		switch (c.type) {
		case GeoConicNDConstants.CONIC_CIRCLE:
		case GeoConicNDConstants.CONIC_ELLIPSE:
			temp1 = c.halfAxes[0] * eigenvec[0].getX();
			temp2 = c.halfAxes[0] * eigenvec[0].getY();
			setCoords(0, b.getX() - temp1, b.getY() - temp2);
			setCoords(1, b.getX() + temp1, b.getY() + temp2);

			temp1 = c.halfAxes[1] * eigenvec[1].getX();
			temp2 = c.halfAxes[1] * eigenvec[1].getY();
			setCoords(2, b.getX() - temp1, b.getY() - temp2);
			setCoords(3, b.getX() + temp1, b.getY() + temp2);
			break;

		case GeoConicNDConstants.CONIC_HYPERBOLA:
			temp1 = c.halfAxes[0] * eigenvec[0].getX();
			temp2 = c.halfAxes[0] * eigenvec[0].getY();
			setCoords(0, b.getX() - temp1, b.getY() - temp2);
			setCoords(1, b.getX() + temp1, b.getY() + temp2);
			// third and fourth vertex undefined
			vertex[2].setUndefined();
			vertex[3].setUndefined();
			break;

		case GeoConicNDConstants.CONIC_PARABOLA:
		case GeoConicNDConstants.CONIC_PARALLEL_LINES:
		case GeoConicNDConstants.CONIC_DOUBLE_LINE:
			setCoords(0, b.getX(), b.getY());

			// other vertex undefined
			vertex[1].setUndefined();
			vertex[2].setUndefined();
			vertex[3].setUndefined();
			break;

		default:
			// no vertex defined
			vertex[0].setUndefined();
			vertex[1].setUndefined();
			vertex[2].setUndefined();
			vertex[3].setUndefined();
		}
	}

	@Override
	public final String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("VertexOfA", "Vertex of %0",
				c.getLabel(tpl));

	}

}
