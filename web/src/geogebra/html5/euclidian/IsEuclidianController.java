package geogebra.html5.euclidian;

import geogebra.common.euclidian.event.PointerEventType;

public interface IsEuclidianController {

	void setExternalHandling(boolean b);

	void twoTouchStart(double x1, double y1, double x2, double y2);

	void setDefaultEventType(PointerEventType pointerEventType);

	void twoTouchMove(double x1, double y1, double x2, double y2);

}
