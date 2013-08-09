package geogebra.web.euclidian;

import geogebra.common.euclidian.EuclidianView;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;

public interface EuclidianPanelWAbstract {
	public AbsolutePanel getAbsolutePanel();
	public Panel getEuclidianPanel();
	public Canvas getCanvas();
	public EuclidianView getEuclidianView();
}
