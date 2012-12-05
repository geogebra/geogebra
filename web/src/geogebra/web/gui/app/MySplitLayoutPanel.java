package geogebra.web.gui.app;

import geogebra.common.main.App;
import geogebra.web.gui.layout.panels.AlgebraDockPanelW;
import geogebra.web.gui.layout.panels.EuclidianDockPanelW;
import geogebra.web.gui.layout.panels.SpreadsheetDockPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

public class MySplitLayoutPanel extends SplitLayoutPanel {

	private EuclidianDockPanelW ggwGraphicView;
	private AlgebraDockPanelW ggwViewWrapper;
	private SpreadsheetDockPanelW ggwSpreadsheetView = null;

	private App application;

	public MySplitLayoutPanel(){
		super();
		addWest(ggwViewWrapper = new AlgebraDockPanelW(), GeoGebraAppFrame.GGWVIewWrapper_WIDTH);

		addEast(ggwSpreadsheetView = new SpreadsheetDockPanelW(), 0);

		add(ggwGraphicView = new EuclidianDockPanelW(true));
    }

	@Override
    public void onResize() {
		super.onResize();

		if (ggwSpreadsheetView.getSpreadsheet() == null) {
			if (getWidgetSize(getGGWSpreadsheetView()) > 0) {
				ggwSpreadsheetView.showSpreadsheetView(true);
			}
		} else {
			if (getWidgetSize(getGGWSpreadsheetView()) <= 0) {
				ggwSpreadsheetView.showSpreadsheetView(false);
			}
		}

		Element wrapper = getWidgetContainerElement(ggwGraphicView);
		if (application != null)
			((AppW) application).ggwGraphicsViewDimChanged(
				wrapper.getOffsetWidth(), wrapper.getOffsetHeight());
	}

	public SplitLayoutPanel getSplitLayoutPanel() {
	    return this;
    }

	public EuclidianDockPanelW getGGWGraphicsView() {
	    // TODO Auto-generated method stub
	    return ggwGraphicView;
    }

	public SpreadsheetDockPanelW getGGWSpreadsheetView() {
		return ggwSpreadsheetView;
	}

	public AlgebraDockPanelW getGGWViewWrapper() {
		return ggwViewWrapper;// this is for the algebra view, by the way
	}

	public void attachApp(App app) {
	   this.application = app;
	   ggwViewWrapper.attachApp(app);
	   ggwGraphicView.attachApp(app);

	   if (ggwSpreadsheetView != null)
		   ggwSpreadsheetView.attachApp(app);
    }
}
