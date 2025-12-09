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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.algos.AlgoClosestPoint;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoClosestPoint3D extends AlgoClosestPoint {

	/**
	 * @param c
	 *            construction
	 * @param path
	 *            path
	 * @param point
	 *            initial point
	 */
	public AlgoClosestPoint3D(Construction c, Path path, GeoPointND point) {
		super(c, path, point);
	}

	@Override
	protected void createOutputPoint(Construction cons1, Path path) {
		P = new GeoPoint3D(cons1);
		P.setPath(path);
	}

	@Override
	public Commands getClassName() {
		return Commands.ClosestPoint;
	}

	@Override
	protected void setCoords() {
		P.setCoords(point.getCoordsInD3(), false);
	}

	@Override
	protected void addIncidence() {
		// TODO
	}

}
