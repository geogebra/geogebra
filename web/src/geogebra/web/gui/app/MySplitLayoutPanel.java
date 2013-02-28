package geogebra.web.gui.app;

import geogebra.common.main.App;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.layout.panels.AlgebraDockPanelW;
import geogebra.web.gui.layout.panels.CASDockPanelW;
import geogebra.web.gui.layout.panels.EuclidianDockPanelW;
import geogebra.web.gui.layout.panels.SpreadsheetDockPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

public class MySplitLayoutPanel extends SplitLayoutPanel {

	private App application;
	private EuclidianDockPanelW ggwGraphicView;

	private AlgebraDockPanelW ggwViewWrapper = null;
	private EuclidianDockPanelW ggwGraphicsView2 = null;
	private SpreadsheetDockPanelW ggwSpreadsheetView = null;
	private CASDockPanelW ggwCASView = null;

	private boolean isApplication = false;
	private boolean showAlgebra = false;
	private boolean showEV2 = false;
	private boolean showSpreadsheet = false;
	private boolean showCAS = false;

	public MySplitLayoutPanel(boolean isApplication, boolean showAlgebra, boolean showEV2, boolean showSpreadsheet, boolean showCAS) {
		super();
		if (this.showAlgebra = showAlgebra)
			addWest(ggwViewWrapper = new AlgebraDockPanelW(), GeoGebraAppFrame.GGWVIewWrapper_WIDTH);
		this.showEV2 = showEV2;
		if (this.showSpreadsheet = showSpreadsheet)
			createSpreadsheet();
		if (this.showCAS = showCAS)
			createCAS();

		add(ggwGraphicView = new EuclidianDockPanelW(this.isApplication = isApplication));
    }

	public void createSpreadsheet(){
		showCAS=false;
		App app = null;
		if(ggwCASView!=null){
			remove(ggwCASView);
			app = ggwCASView.getApp();
		}
		if(ggwSpreadsheetView==null)
			addEast(ggwSpreadsheetView = new SpreadsheetDockPanelW(app), 0);
	}

	public void createCAS(){
		showCAS=true;
		App app = null;
		if(ggwSpreadsheetView!=null){
			remove(ggwSpreadsheetView);
			app = ggwSpreadsheetView.getApp();
		}
		if(ggwCASView==null)
			addEast(ggwCASView = new CASDockPanelW(app), 0);
	}

	@Override
    public void onResize() {
		super.onResize();

		if (isApplication) {
			if(!showCAS){
				if (ggwSpreadsheetView.getSpreadsheet() == null) {
					if (getWidgetSize(getGGWSpreadsheetView()) > 0) {
						ggwSpreadsheetView.showView(true);
					}
				} else {
					if (getWidgetSize(getGGWSpreadsheetView()) <= 0) {
						ggwSpreadsheetView.showView(false);
					}
				}
			} else {
				if (ggwCASView.getCAS() == null) {
					if (getWidgetSize(getGGWCASView()) > 0) {
						ggwCASView.showView(true);
					}
				} else {
					if (getWidgetSize(getGGWCASView()) <= 0) {
						ggwCASView.showView(false);
					}
				}
			}

			Element wrapper = ggwGraphicView.getEuclidianPanel().getElement();
			if (application != null)
				((AppW) application).ggwGraphicsViewDimChanged(
					wrapper.getOffsetWidth(), wrapper.getOffsetHeight());
		}
	}

	public SplitLayoutPanel getSplitLayoutPanel() {
	    return this;
    }

	public EuclidianDockPanelW getGGWGraphicsView() {
	    return ggwGraphicView;
    }

	public EuclidianDockPanelW getGGWGraphicsView2() {
		return ggwGraphicsView2;
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

	   if (showEV2) {
		   addEast(ggwGraphicsView2 =
		   		((GuiManagerW)application.getGuiManager()).getEuclidianView2DockPanel(),
		   		400);
		   ggwGraphicsView2.attachApp(app);
	   }

	   if (ggwViewWrapper != null)
		   ggwViewWrapper.attachApp(app);
	   if (ggwGraphicView != null)
		   ggwGraphicView.attachApp(app);
	   if (ggwGraphicsView2 != null)
		   ggwGraphicsView2.attachApp(app);
	   if (ggwSpreadsheetView != null)
		   ggwSpreadsheetView.attachApp(app);
	   if (ggwCASView != null)
		   ggwCASView.attachApp(app);
    }

	public void showView(DockPanelW view) {
		if (view != null) {
    		if (getWidgetSize(view) > 0) {
    			setWidgetSize(view, 0);
    			getGGWSpreadsheetView().showView(false);
    		} else {
    			//this might be the first time to show
    			//in that case, attachment is done too
    			view.showView(true);

    			setWidgetSize(view, GeoGebraAppFrame.GGWSpreadsheetView_WIDTH);
				view.onResize();
    			if (getGGWViewWrapper() != null &&
    				getWidgetSize(getGGWViewWrapper()) > 0) {
    				// make sure that there is place left for the center widget
    				setWidgetSize(getGGWViewWrapper(), GeoGebraAppFrame.GGWVIewWrapper_WIDTH);
    			}
    		}
			onResize();
			forceLayout();
    	}
	}
}
