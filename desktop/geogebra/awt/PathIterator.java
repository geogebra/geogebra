package geogebra.awt;

public class PathIterator implements geogebra.common.awt.PathIterator {

	java.awt.geom.PathIterator impl;
	
	public PathIterator(java.awt.geom.PathIterator pathIterator) {
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
