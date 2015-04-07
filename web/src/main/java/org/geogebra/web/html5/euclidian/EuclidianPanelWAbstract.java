package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.EuclidianView;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;

public interface EuclidianPanelWAbstract {

	public AbsolutePanel getAbsolutePanel();

	public Panel getEuclidianPanel();

	public Canvas getCanvas();

	public EuclidianView getEuclidianView();

	public void setPixelSize(int x, int y);

	public int getOffsetWidth();

	public int getOffsetHeight();

	public void onResize();

	public void deferredOnResize();

	public void updateNavigationBar();

	public Element getElement();
}
