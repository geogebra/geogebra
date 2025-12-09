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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author ggb3D
 *
 */
public abstract class AlgoQuadricPointPointNumber
		extends AlgoQuadricPointNumber {

	/**
	 * @param c
	 *            construction
	 * @param label
	 *            output label
	 * @param origin
	 *            base center
	 * @param secondPoint
	 *            second point
	 * @param r
	 *            radius
	 * @param computer
	 *            conic computer
	 */
	public AlgoQuadricPointPointNumber(Construction c, String label,
			GeoPointND origin, GeoPointND secondPoint, GeoNumberValue r,
			AlgoQuadricComputer computer) {
		super(c, label, origin, secondPoint, r, computer);
	}

	@Override
	protected Coords getDirection() {
		return ((GeoPointND) getSecondInput()).getInhomCoordsInD3()
				.sub(getOrigin().getInhomCoordsInD3());
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain(getPlainName(), getOrigin().getLabel(tpl),
				getSecondInput().getLabel(tpl), getNumber().getLabel(tpl));

	}

}
