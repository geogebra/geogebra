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

package org.geogebra.common.euclidian.tools;

import org.geogebra.common.kernel.algos.AlgoMirror;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.jspecify.annotations.NonNull;

/** Shared assertions specific to reflection tool tests. */
abstract class BaseMirrorToolTest extends BaseTransformationToolTest {

	/** Checks the original source and mirror of a reflected object. */
	protected void assertMirrorInputs(
			@NonNull GeoElementND reflected, @NonNull GeoElement source, @NonNull GeoElement mirror) {
		assertTransformationInputs(AlgoMirror.class, reflected, source, mirror);
	}

	/** Checks every reflected vertex dependency and the polygon edge connections. */
	protected void assertPolygonStructure(
			@NonNull GeoPolygon reflected, @NonNull GeoPolygon source, @NonNull GeoElement mirror) {
		assertPolygonStructure(AlgoMirror.class, reflected, source, mirror);
	}
}
