package geogebra.html5.awt;

public class GPathIteratorW implements geogebra.common.awt.GPathIterator {

	geogebra.html5.openjdk.awt.geom.PathIterator impl;
	
	public GPathIteratorW(geogebra.html5.openjdk.awt.geom.PathIterator pathIterator) {
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
