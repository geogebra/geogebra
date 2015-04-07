package org.geogebra.web.html5.awt;

public class GPathIteratorW implements org.geogebra.common.awt.GPathIterator {

	org.geogebra.ggbjdk.java.awt.geom.PathIterator impl;

	public GPathIteratorW(
	        org.geogebra.ggbjdk.java.awt.geom.PathIterator pathIterator) {
		impl = pathIterator;
	}

	public int getWindingRule() {
		return impl.getWindingRule();
	}

	public boolean isDone() {
		return impl.isDone();
	}

	public void next() {
		impl.next();
	}

	public int currentSegment(float[] coords) {
		return impl.currentSegment(coords);
	}

	public int currentSegment(double[] coords) {
		return impl.currentSegment(coords);
	}

}
