package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.EuclidianView;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;

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

}
