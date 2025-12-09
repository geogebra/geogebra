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

package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdTangent;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Tangent command
 *
 */
public class CmdTangent3D extends CmdTangent {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdTangent3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] tangent(String[] labels, GeoPointND a,
			GeoConicND c) {
		return kernel.getManager3D().tangent3D(labels, a, c);
	}

	@Override
	protected GeoElement[] tangent(String[] labels, GeoLineND l, GeoConicND c) {
		return kernel.getManager3D().tangent3D(labels, l, c);
	}

	@Override
	protected GeoElement[] tangent(String[] labels, GeoConicND c1,
			GeoConicND c2) {
		return kernel.getManager3D().commonTangents3D(labels, c1, c2);
	}

	@Override
	protected GeoElement tangentToCurve(String label, GeoPointND point,
			GeoCurveCartesianND curve) {
		return kernel.getManager3D().tangent3D(label, point, curve);
	}

}
