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
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author ggb3D
 *
 */
public abstract class AlgoQuadricPointVectorNumber
		extends AlgoQuadricPointNumber {

	/**
	 * @param c
	 *            construction
	 * @param label
	 *            output label
	 * @param origin
	 *            bottom center
	 * @param direction
	 *            direction
	 * @param r
	 *            cylinder radius or cone angle
	 * @param computer
	 *            quadric computer
	 */
	public AlgoQuadricPointVectorNumber(Construction c, String label,
			GeoPointND origin, GeoVectorND direction, GeoNumberValue r,
			AlgoQuadricComputer computer) {
		super(c, label, origin, direction, r, computer);
	}

	@Override
	protected Coords getDirection() {
		return ((GeoVectorND) getSecondInput()).getCoordsInD3();
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain(getPlainName(), getOrigin().getLabel(tpl),
				getSecondInput().getLabel(tpl), getNumber().getLabel(tpl));

	}

}
