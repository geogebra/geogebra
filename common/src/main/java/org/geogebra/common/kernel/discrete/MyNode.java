package org.geogebra.common.kernel.discrete;

import org.geogebra.common.kernel.kernelND.GeoPointND;

public class MyNode {
	public GeoPointND id;

	public MyNode(GeoPointND id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Vertex:" + id;
	}
}