package geogebra.kernel.discrete;

import geogebra.common.kernel.kernelND.GeoPointND;

public class MyNode {
	GeoPointND id; 
	public MyNode(GeoPointND id) {
		this.id = id;
	}
	public String toString() { 
		return "Vertex:"+id; 
	}
}