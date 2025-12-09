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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Compute a line through a point and orthogonal to ...
 *
 * @author matthieu
 */
public abstract class AlgoOrtho extends AlgoElement3D {

	protected GeoPointND point; // input
	protected GeoElement inputOrtho; // input
	protected GeoLine3D line; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param point
	 *            point
	 * @param ortho
	 *            orthogonal object (line or plane)
	 */
	public AlgoOrtho(Construction cons, String label, GeoPointND point,
			GeoElement ortho) {
		super(cons);
		this.point = point;
		this.inputOrtho = ortho;
		line = new GeoLine3D(cons);

		setSpecificInputOutput();

		// compute line
		compute();
		line.setLabel(label);
	}

	/**
	 * set specific input/output for this algo
	 */
	protected void setSpecificInputOutput() {
		setInputOutput(new GeoElement[] { (GeoElement) point, inputOrtho },
				new GeoElement[] { line });
	}

	public GeoLine3D getLine() {
		return line;
	}

	protected GeoPointND getPoint() {
		return point;
	}

	protected GeoElement getInputOrtho() {
		return inputOrtho;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return getLoc().getPlain("LineThroughAPerpendicularToB",
				point.getLabel(tpl), inputOrtho.getLabel(tpl));
	}
}
