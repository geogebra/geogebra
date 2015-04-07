package org.geogebra.desktop.awt;

public class GPathIteratorD implements org.geogebra.common.awt.GPathIterator {

	java.awt.geom.PathIterator impl;

	public GPathIteratorD(java.awt.geom.PathIterator pathIterator) {
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
