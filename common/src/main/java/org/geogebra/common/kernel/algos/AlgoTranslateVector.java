/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoTranslatePoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Vector w = v starting at A
 * 
 * @author Markus
 * @version
 */
public class AlgoTranslateVector extends AlgoElement {

	private GeoPointND A; // input
	protected GeoVectorND v; // input
	protected GeoVectorND w; // output

	public AlgoTranslateVector(Construction cons, String label, GeoVector v,
			GeoPointND A) {
		this(cons, label, (GeoVectorND) v, A);
	}

	protected AlgoTranslateVector(Construction cons, String label,
			GeoVectorND v, GeoPointND A) {
		super(cons);
		this.A = A;
		this.v = v;

		// create new Point
		w = newGeoVector(cons);

		try {
			w.setStartPoint(A);
		} catch (CircularDefinitionException e) {
		}

		setInputOutput();

		compute();
		w.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @return new vector
	 */
	protected GeoVectorND newGeoVector(Construction cons) {
		return new GeoVector(cons);
	}

	@Override
	public Commands getClassName() {
		return Commands.Translate;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TRANSLATE_BY_VECTOR;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) v;
		input[1] = (GeoElement) A;

		setOutputLength(1);
		setOutput(0, (GeoElement) w);
		setDependencies(); // done by AlgoElement
	}

	GeoPointND getPoint() {
		return A;
	}

	GeoVectorND getVector() {
		return v;
	}

	public GeoVectorND getTranslatedVector() {
		return w;
	}

	// simply copy v
	@Override
	public void compute() {
		((GeoVector) w).setCoords((GeoVec3D) v);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("TranslationOfAtoB", v.getLabel(tpl),
				A.getLabel(tpl));

	}

	// TODO Consider locusequability
}
