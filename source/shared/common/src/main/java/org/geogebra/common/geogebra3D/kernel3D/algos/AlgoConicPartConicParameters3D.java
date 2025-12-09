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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicPart3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoConicPartConicParameters;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicND;

public class AlgoConicPartConicParameters3D
		extends AlgoConicPartConicParameters {

	public AlgoConicPartConicParameters3D(Construction cons, String label,
			GeoConicND circle, GeoNumberValue startParameter,
			GeoNumberValue endParameter, int type) {
		super(cons, label, circle, startParameter, endParameter, type);
	}

	@Override
	protected GeoConicND newGeoConicPart(Construction cons1, int type1) {
		return new GeoConicPart3D(cons1, type1);
	}

}
