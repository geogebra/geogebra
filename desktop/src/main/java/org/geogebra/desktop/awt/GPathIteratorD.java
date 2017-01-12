package org.geogebra.desktop.awt;

import java.awt.geom.PathIterator;

import org.geogebra.common.awt.GPathIterator;

public class GPathIteratorD implements GPathIterator {

	PathIterator impl;

	public GPathIteratorD(PathIterator pathIterator) {
		impl = pathIterator;
	}

	@Override
	public int getWindingRule() {
		return impl.getWindingRule();
	}

	@Override
	public boolean isDone() {
		return impl.isDone();
	}

	@Override
	public void next() {
		impl.next();
	}

	public int currentSegment(float[] coords) {
		return impl.currentSegment(coords);
	}

	@Override
	public int currentSegment(double[] coords) {
		return impl.currentSegment(coords);
	}

}
