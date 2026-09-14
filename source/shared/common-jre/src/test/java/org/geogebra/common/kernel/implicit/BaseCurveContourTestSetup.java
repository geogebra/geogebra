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

package org.geogebra.common.kernel.implicit;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.MyPoint;

public class BaseCurveContourTestSetup {

	private ContourLinker linker;
	ContourAssembler contour;
	List<MyPoint> locusPoints = new ArrayList<>();

	protected void createContour() {
		linker = new CompleteContourLinker();
		contour = new ContourAssembler(linker);
	}

	void link(SegmentEndPoint p0, SegmentEndPoint p1) {
		linker.link(p0, p1, false);
	}

}
