/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoTangentLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.AlgoIntersectND;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Markus
 * @version
 */
public abstract class AlgoTangentLineND extends AlgoElement implements
		TangentAlgo {

	protected GeoLineND g; // input
	protected GeoConicND c; // input
	protected GeoLineND[] tangents; // output

	protected GeoLine diameter;
	protected AlgoIntersectND algoIntersect;
	protected GeoPointND[] tangentPoints;
	protected int i;

	/** Creates new AlgoTangentLine */
	protected AlgoTangentLineND(Construction cons, String label, GeoLineND g,
			GeoConicND c) {
		this(cons, g, c);
		GeoElement.setLabels(label, getOutput());
	}

	public AlgoTangentLineND(Construction cons, String[] labels, GeoLineND g,
			GeoConicND c) {
		this(cons, g, c);
		GeoElement.setLabels(labels, getOutput());
	}

	@Override
	public Commands getClassName() {
		return Commands.Tangent;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TANGENTS;
	}

	AlgoTangentLineND(Construction cons, GeoLineND g, GeoConicND c) {
		super(cons);
		this.g = g;
		this.c = c;

		initDiameterAndDirection();

		setTangents();

		setInputOutput(); // for AlgoElement

		compute();
	}

	/**
	 * init diameter and direction
	 */
	abstract protected void initDiameterAndDirection();

	/**
	 * set tangents
	 */
	abstract protected void setTangents();

	// for AlgoElement
	@Override
	public void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) g;
		input[1] = c;

		GeoElement[] out = new GeoElement[tangents.length];
		for (int i = 0; i < tangents.length; i++) {
			out[i] = (GeoElement) tangents[i];
		}
		super.setOutput(out);
		setDependencies(); // done by AlgoElement
	}

	public GeoLineND[] getTangents() {
		return tangents;
	}

	GeoLineND getLine() {
		return g;
	}

	GeoConicND getConic() {
		return c;
	}

	public GeoPointND getTangentPoint(GeoElement conic, GeoLine line) {
		if (conic != c)
			return null;

		if (line == tangents[0]) {
			return tangentPoints[0];
		} else if (line == tangents[1]) {
			return tangentPoints[1];
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return true if tangents will be defined
	 */
	protected boolean checkUndefined() {
		return c.isDegenerate();
	}

	// calc tangents parallel to g
	@Override
	public final void compute() {
		// degenerates should not have any tangents
		if (checkUndefined()) {
			tangents[0].setUndefined();
			tangents[1].setUndefined();
			return;
		}

		// update diameter line
		updateDiameterLine();

		// intersect diameter line with conic -> tangentPoints
		algoIntersect.update();

		// calc tangents through tangentPoints
		for (i = 0; i < tangents.length; i++) {
			updateTangent(i);
		}
	}

	/**
	 * update diameter line
	 */
	abstract protected void updateDiameterLine();

	/**
	 * update i-th tangent
	 * 
	 * @param index
	 *            index
	 */
	abstract protected void updateTangent(int index);

	@Override
	public final String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("TangentToAParallelToB", c.getLabel(tpl),
				g.getLabel(tpl));
	}

	// TODO Consider locusequability
}
