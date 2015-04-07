package org.geogebra.common.awt;

public interface GArea extends GShape {

	void subtract(GArea shape);

	void intersect(GArea shape);

	void exclusiveOr(GArea shape);

	void add(GArea shape);

	boolean isEmpty();

	public GPathIterator getPathIterator(GAffineTransform t);

}
