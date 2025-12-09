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

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.util.clipper.Point.DoublePoint;

public interface Clipper {
	// InitOptions that can be passed to the constructor ...
	public final static int REVERSE_SOLUTION = 1;

	public final static int STRICTLY_SIMPLE = 2;

	public final static int PRESERVE_COLLINEAR = 4;

	public enum ClipType {
		INTERSECTION, UNION, DIFFERENCE, XOR
	}

	enum Direction {
		RIGHT_TO_LEFT, LEFT_TO_RIGHT
	}

	public enum EndType {
		CLOSED_POLYGON, CLOSED_LINE, OPEN_BUTT, OPEN_SQUARE, OPEN_ROUND
	}

	public enum JoinType {
		SQUARE, ROUND, MITER
	}

	public enum PolyFillType {
		EVEN_ODD, NON_ZERO, POSITIVE, NEGATIVE
	}

	public enum PolyType {
		SUBJECT, CLIP
	}

	/**
	 * modified to be compatible with double
	 */
	public interface ZFillCallback {
		@MissingDoc
		void zFill(DoublePoint bot1, DoublePoint top1, DoublePoint bot2,
				DoublePoint top2, DoublePoint pt);
	}

	@MissingDoc
	boolean addPath(Path pg, PolyType polyType, boolean Closed);

	@MissingDoc
	boolean addPaths(Paths ppg, PolyType polyType, boolean closed);

	@MissingDoc
	void clear();

	@MissingDoc
	boolean execute(ClipType clipType, Paths solution);

	@MissingDoc
	boolean execute(ClipType clipType, Paths solution,
			PolyFillType subjFillType, PolyFillType clipFillType);

	@MissingDoc
	boolean execute(ClipType clipType, PolyTree polytree);

	@MissingDoc
	public boolean execute(ClipType clipType, PolyTree polytree,
			PolyFillType subjFillType, PolyFillType clipFillType);
}
