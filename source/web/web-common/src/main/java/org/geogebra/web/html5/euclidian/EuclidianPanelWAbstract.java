package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.EuclidianView;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.AbsolutePanel;
import org.gwtproject.user.client.ui.Panel;

public interface EuclidianPanelWAbstract {

	AbsolutePanel getAbsolutePanel();

	Panel getEuclidianPanel();

	Canvas getCanvas();

	EuclidianView getEuclidianView();

	void setPixelSize(int x, int y);

	int getOffsetWidth();

	int getOffsetHeight();

	void onResize();

	void deferredOnResize();

	void updateNavigationBar();

	Element getElement();

	void reset();

	boolean isAttached();

	void enableZoomPanelEvents(boolean enable);

}
