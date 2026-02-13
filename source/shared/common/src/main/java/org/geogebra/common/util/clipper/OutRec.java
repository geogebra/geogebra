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

package org.geogebra.common.util.clipper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

class OutRec {
	int Idx;

	boolean isHole;

	boolean isOpen;
	OutRec firstLeft; // see comments in clipper.pas
	OutPt pts;
	OutPt bottomPt;
	PolyNode polyNode;

	double area() {
		OutPt op = pts;
		if (op == null) {
			return 0;
		}
		double a = 0;
		do {
			a = a + (op.prev.getPt().getX() + op.getPt().getX())
					* (op.prev.getPt().getY() - op.getPt().getY());
			op = op.next;
		} while (op != pts);
		return a * 0.5;
	}

	@SuppressFBWarnings(value = "SA_FIELD_SELF_ASSIGNMENT",
			justification = "https://github.com/spotbugs/spotbugs/issues/2258")
	void fixHoleLinkage() {
		// skip if an outermost polygon or
		// already already points to the correct FirstLeft ...
		if (firstLeft == null
				|| isHole != firstLeft.isHole && firstLeft.pts != null) {
			return;
		}

		OutRec orfl = firstLeft;
		while (orfl != null && (orfl.isHole == isHole || orfl.pts == null)) {
			orfl = orfl.firstLeft;
		}
		firstLeft = orfl;
	}

	OutPt getPoints() {
		return pts;
	}

	void setPoints(OutPt pts) {
		this.pts = pts;
	}

	OutRec parseFirstLeft() {
		OutRec ret = this;
		while (ret != null && ret.pts == null) {
			ret = ret.firstLeft;
		}
		return ret;
	}

}