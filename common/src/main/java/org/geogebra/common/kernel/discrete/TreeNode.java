package org.geogebra.common.kernel.discrete;

import org.geogebra.common.kernel.kernelND.GeoPointND;

public class TreeNode {
	public GeoPointND id;

	public TreeNode(GeoPointND id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Vertex:" + id;
	}
}