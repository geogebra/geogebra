package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.app.MySplitLayoutPanel;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * The "Window" menu.
 */
public class ViewMenuW extends MenuBar{

	/**
	 * Application instance
	 */
	App app;

	/**
	 * Constructs the "Window" menu
	 * @param application The App instance
	 */
	public ViewMenuW(App application) {

		super(true);
		this.app = application;
		addStyleName("GeoGebraMenuBar");
		initActions();
	}
	
	private void initActions(){
		clearItems();

		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
				.empty().getSafeUri().asString(), app.getMenu("Algebra")),
		        true, new Command() {
			        public void execute() {
			        	MySplitLayoutPanel mp = (MySplitLayoutPanel)
			        		((AppW)app).getAppFrame().getGGWSplitLayoutPanel();
			        	if (mp.getGGWViewWrapper() != null) {
			        		if (mp.getWidgetSize(mp.getGGWViewWrapper()) > 0) {
			        			mp.setWidgetSize(mp.getGGWViewWrapper(), 0);
			        		} else {
			        			mp.setWidgetSize(mp.getGGWViewWrapper(), GeoGebraAppFrame.GGWVIewWrapper_WIDTH);
			        			if (mp.getGGWSpreadsheetView() != null &&
			        				mp.getWidgetSize(mp.getGGWSpreadsheetView()) > 0) {
			        				// make sure that there is place left for the center widget
			        				mp.setWidgetSize(mp.getGGWSpreadsheetView(), GeoGebraAppFrame.GGWSpreadsheetView_WIDTH);
			        			}
			        		}
		        			mp.onResize();
		        			mp.forceLayout();
			        	}
			        }
		        });

		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
				.empty().getSafeUri().asString(), app.getMenu("Spreadsheet")),
		        true, new Command() {
			        public void execute() {
			        	MySplitLayoutPanel mp = (MySplitLayoutPanel)
			        		((AppW)app).getAppFrame().getGGWSplitLayoutPanel();
			        	if (mp.getGGWSpreadsheetView() != null) {
			        		if (mp.getWidgetSize(mp.getGGWSpreadsheetView()) > 0) {
			        			mp.setWidgetSize(mp.getGGWSpreadsheetView(), 0);
			        		} else {
			        			mp.setWidgetSize(mp.getGGWSpreadsheetView(), GeoGebraAppFrame.GGWSpreadsheetView_WIDTH);
		        				mp.getGGWSpreadsheetView().onResize();
			        			if (mp.getGGWViewWrapper() != null &&
			        				mp.getWidgetSize(mp.getGGWViewWrapper()) > 0) {
			        				// make sure that there is place left for the center widget
			        				mp.setWidgetSize(mp.getGGWViewWrapper(), GeoGebraAppFrame.GGWVIewWrapper_WIDTH);
			        			}
			        		}
		        			mp.onResize();
		        			mp.forceLayout();
			        	}
			        }
		        });

		addSeparator();

		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
				.empty().getSafeUri().asString(), app.getMenu("Refresh")),
		        true, new Command() {
			        public void execute() {
			        	app.refreshViews();
			        }
		        });

		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
				.empty().getSafeUri().asString(), app.getMenu("RecomputeAllViews")),
		        true, new Command() {
			        public void execute() {
			        	app.getKernel().updateConstruction();
			        }
		        });

	}

}
