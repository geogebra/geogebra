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

import java.util.ArrayList;
import java.util.List;

class PolyTree extends PolyNode {
	private final List<PolyNode> allPolys = new ArrayList<>();

	public void clear() {
		allPolys.clear();
		children.clear();
	}

	public List<PolyNode> getAllPolys() {
		return allPolys;
	}

	/**
	 * @return first child
	 */
	public PolyNode getFirst() {
		if (!children.isEmpty()) {
			return children.get(0);
		}
		return null;
	}

	/**
	 * @return number of polygons except hidden outer one
	 */
	public int getTotalSize() {
		int result = allPolys.size();
		// with negative offsets, ignore the hidden outer polygon ...
		if (result > 0 && children.get(0) != allPolys.get(0)) {
			result--;
		}
		return result;

	}

}
