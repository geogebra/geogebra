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
			// vector just created, no cycle possible
		}

		setInputOutput();

		compute();
		w.setLabel(label);
	}

	/**
	 * @param cons1
	 *            construction
	 * @return new vector
	 */
	protected GeoVectorND newGeoVector(Construction cons1) {
		return new GeoVector(cons1);
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

		setOnlyOutput(w);
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
		return getLoc().getPlainDefault("TranslationOfAtoB",
				"Translation of %0 to %1", v.getLabel(tpl), A.getLabel(tpl));

	}

}
