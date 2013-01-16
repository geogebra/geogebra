package geogebra.web.gui.app;

import geogebra.common.main.App;
import geogebra.web.gui.layout.panels.AlgebraDockPanelW;
import geogebra.web.gui.layout.panels.CASDockPanelW;
import geogebra.web.gui.layout.panels.EuclidianDockPanelW;
import geogebra.web.gui.layout.panels.SpreadsheetDockPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

public class MySplitLayoutPanel extends SplitLayoutPanel {

	private EuclidianDockPanelW ggwGraphicView;
	private AlgebraDockPanelW ggwViewWrapper;
	private SpreadsheetDockPanelW ggwSpreadsheetView = null;
	private CASDockPanelW ggwCASView = null;

	private App application;
	private boolean showCAS = false;
	
	public MySplitLayoutPanel(boolean showCAS){
		super();
		addWest(ggwViewWrapper = new AlgebraDockPanelW(), GeoGebraAppFrame.GGWVIewWrapper_WIDTH);
		this.showCAS = showCAS;
		if(!showCAS){
			addEast(ggwSpreadsheetView = new SpreadsheetDockPanelW(), 0);
		}
		else{
			addEast(ggwCASView = new CASDockPanelW(), 0);
		}
		add(ggwGraphicView = new EuclidianDockPanelW(true));
    }

	@Override
    public void onResize() {
		super.onResize();
		if(!showCAS){
			if (ggwSpreadsheetView.getSpreadsheet() == null) {
				if (getWidgetSize(getGGWSpreadsheetView()) > 0) {
					ggwSpreadsheetView.showSpreadsheetView(true);
				}
			} else {
				if (getWidgetSize(getGGWSpreadsheetView()) <= 0) {
					ggwSpreadsheetView.showSpreadsheetView(false);
				}
			}
		}
		else{
			if (ggwCASView.getCAS() == null) {
				if (getWidgetSize(getGGWCASView()) > 0) {
					ggwCASView.showCASView(true);
				}
			} else {
				if (getWidgetSize(getGGWCASView()) <= 0) {
					ggwCASView.showCASView(false);
				}
			}
		}
		Element wrapper = ggwGraphicView.getEuclidianPanel().getElement();
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
	
	public CASDockPanelW getGGWCASView() {
		return ggwCASView;
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
	   if (ggwCASView != null)
		   ggwCASView.attachApp(app);
    }
}
