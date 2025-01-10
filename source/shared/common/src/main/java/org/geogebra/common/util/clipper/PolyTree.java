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
