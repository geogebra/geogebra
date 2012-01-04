package geogebra.common.euclidian.event;

import geogebra.common.awt.Point;

public abstract class AbstractEvent {

	public abstract Point getPoint();

	public abstract boolean isAltDown();

	public abstract boolean isShiftDown();

}
