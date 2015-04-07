package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.event.PointerEventType;

public interface IsEuclidianController {

	void setExternalHandling(boolean b);

	void twoTouchStart(double x1, double y1, double x2, double y2);

	void setDefaultEventType(PointerEventType pointerEventType);

	void twoTouchMove(double x1, double y1, double x2, double y2);

	int getEvNo();

}
