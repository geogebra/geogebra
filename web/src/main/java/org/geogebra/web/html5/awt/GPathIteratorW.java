package org.geogebra.web.html5.awt;

import java.awt.geom.PathIterator;

import org.geogebra.common.awt.GPathIterator;

public class GPathIteratorW implements GPathIterator {

	PathIterator impl;

	public GPathIteratorW(PathIterator pathIterator) {
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
