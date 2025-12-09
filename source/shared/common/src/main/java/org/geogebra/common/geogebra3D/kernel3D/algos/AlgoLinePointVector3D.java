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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Compute a line through a point and parallel to a vector
 *
 * @author matthieu
 */
public class AlgoLinePointVector3D extends AlgoLinePoint {

	public AlgoLinePointVector3D(Construction cons,
			GeoPointND point, GeoVectorND v) {
		super(cons, point, (GeoElement) v);
	}

	@Override
	public Commands getClassName() {
		return Commands.Line;
	}

	@Override
	protected Coords getDirection() {
		return ((GeoVectorND) getInputParallel()).getCoordsInD3();
	}

	@Override
	public String toString(StringTemplate tpl) {
		return getLoc().getPlain("LineThroughAwithDirectionB",
				getPoint().getLabel(tpl), getInputParallel().getLabel(tpl));
	}
}
