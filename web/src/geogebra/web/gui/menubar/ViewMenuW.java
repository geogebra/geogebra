package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * The "Window" menu.
 */
public class ViewMenuW extends MenuBar {

	/**
	 * Application instance
	 */
	AppW app;

	/**
	 * Constructs the "Window" menu
	 * 
	 * @param application
	 *            The App instance
	 */
	public ViewMenuW(AppW application) {

		super(true);
		this.app = application;
		addStyleName("GeoGebraMenuBar");
		initActions();
	}

	private void initActions() {
		clearItems();

		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getPlain("AlgebraWindow")), true,
		        new Command() {
			        public void execute() {
			        	int viewId = App.VIEW_ALGEBRA;
			        	((GuiManagerW)app.getGuiManager()).setShowView(
								!((GuiManagerW)app.getGuiManager()).showView(viewId), viewId);
						
			        	/*
				        MySplitLayoutPanel mp = (MySplitLayoutPanel) app
				                .getAppFrame().getGGWSplitLayoutPanel();
				        if (mp.getGGWViewWrapper() != null) {
					        if (mp.getWidgetSize(mp.getGGWViewWrapper()) > 0) {
						        mp.setWidgetSize(mp.getGGWViewWrapper(), 0);
					        } else {
						        mp.setWidgetSize(mp.getGGWViewWrapper(),
						                GeoGebraAppFrame.GGWVIewWrapper_WIDTH);
						        if (mp.getGGWSpreadsheetView() != null
						                && mp.getWidgetSize(mp
						                        .getGGWSpreadsheetView()) > 0) {
							        // make sure that there is place left for
									// the center widget
							        mp.setWidgetSize(
							                mp.getGGWSpreadsheetView(),
							                GeoGebraAppFrame.GGWSpreadsheetView_WIDTH);
						        }
					        }
					        mp.onResize();
					        mp.forceLayout();
				        }
				        */
			        }
		        });

		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getPlain("Spreadsheet")), true,
		        new Command() {
			        public void execute() {
			        	int viewId = App.VIEW_SPREADSHEET;
			        	
			        	((GuiManagerW)app.getGuiManager()).setShowView(
								!((GuiManagerW)app.getGuiManager()).showView(viewId), viewId);
						
			        	/*
				        MySplitLayoutPanel mp = (MySplitLayoutPanel) app
				                .getAppFrame().getGGWSplitLayoutPanel();
				        mp.createSpreadsheet();
				        mp.showView(mp.getGGWSpreadsheetView());
				        */
			        }
		        });
		
		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getPlain("CAS")), true,
		        new Command() {
			        public void execute() {
			        	
			        	int viewId = App.VIEW_CAS;
			        	((GuiManagerW)app.getGuiManager()).setShowView(
								!((GuiManagerW)app.getGuiManager()).showView(viewId), viewId);
						
			        	/*
				        MySplitLayoutPanel mp = (MySplitLayoutPanel) app
				                .getAppFrame().getGGWSplitLayoutPanel();
				        mp.createCAS();
				        mp.showView(mp.getGGWCASView());
				        */
			        }
		        });

		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getPlain("DrawingPad")), true,
		        new Command() {
			        public void execute() {

			        	int viewId = App.VIEW_EUCLIDIAN;
			        	((GuiManagerW)app.getGuiManager()).setShowView(
								!((GuiManagerW)app.getGuiManager()).showView(viewId), viewId);
			        }
		        });

		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getPlain("DrawingPad2")), true,
		        new Command() {
			        public void execute() {

			        	int viewId = App.VIEW_EUCLIDIAN2;
			        	((GuiManagerW)app.getGuiManager()).setShowView(
								!((GuiManagerW)app.getGuiManager()).showView(viewId), viewId);

			        	/*// avoid one EuclidianView hiding the another (imperfect hack)
			        	app.getGuiManager().getEuclidianView2DockPanel()
			        		.getAbsolutePanel().setPixelSize(
			        				300,
			        				app.getEuclidianView1().getHeight());
			        	app.getGuiManager().getEuclidianView2DockPanel().onResize();*/
			        }
		        });

		addSeparator();

		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getMenu("Refresh")), true,
		        new Command() {
			        public void execute() {
				        app.refreshViews();
			        }
		        });

		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getMenu("RecomputeAllViews")),
		        true, new Command() {
			        public void execute() {
				        app.getKernel().updateConstruction();
			        }
		        });

	}

}
