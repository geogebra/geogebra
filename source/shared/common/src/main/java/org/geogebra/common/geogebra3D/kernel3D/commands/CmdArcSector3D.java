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

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoConicPartConicParameters3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoConicPartConicPoints3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdArcSector;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Conic part commands
 *
 */
public class CmdArcSector3D extends CmdArcSector {
	/**
	 * @param kernel
	 *            Kernel
	 * @param type
	 *            arc type
	 */
	public CmdArcSector3D(Kernel kernel, int type) {
		super(kernel, type);
	}

	@Override
	protected GeoElement arcSector(String label, GeoConicND conic,
			GeoNumberValue start, GeoNumberValue end) {

		if (conic.isGeoElement3D()) {
			AlgoConicPartConicParameters3D algo = new AlgoConicPartConicParameters3D(
					cons, label, conic, start, end, type);

			return algo.getConicPart();
		}

		return super.arcSector(label, conic, start, end);
	}

	@Override
	protected GeoElement arcSector(String label, GeoConicND conic,
			GeoPointND start, GeoPointND end) {

		if (conic.isGeoElement3D() || start.isGeoElement3D()
				|| end.isGeoElement3D()) {
			AlgoConicPartConicPoints3D algo = new AlgoConicPartConicPoints3D(
					cons, label, conic, start, end, type);

			return algo.getConicPart();
		}

		return super.arcSector(label, conic, start, end);
	}
}
