package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;
import geogebra.web.javax.swing.GCheckBoxMenuItem;
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
	 * Menuitem with checkbox for show algebra view
	 */
	GCheckBoxMenuItem itemAlgebra;
	/**
	 * Menuitem with checkbox for show spreadsheet view
	 */
	GCheckBoxMenuItem itemSpreadsheet;
	/**
	 * Menuitem with checkbox for show CAS view
	 */
	GCheckBoxMenuItem itemCAS;
	/**
	 * Menuitem with checkbox for show Euclidian view
	 */
	GCheckBoxMenuItem itemEuclidian;
	/**
	 * Menuitem with checkbox for show Euclidian2 view
	 */
	GCheckBoxMenuItem itemEuclidian2;
	
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

		itemAlgebra = new GCheckBoxMenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getPlain("AlgebraWindow")),
		        new Command() {
			        public void execute() {
			        	int viewId = App.VIEW_ALGEBRA;
			        	app.getGuiManager().setShowView(
								!app.getGuiManager().showView(viewId), viewId);
			    		itemAlgebra.setSelected(app.getGuiManager().showView(App.VIEW_ALGEBRA));

						
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

		addItem(itemAlgebra.getMenuItem());
		
		itemSpreadsheet = new GCheckBoxMenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getPlain("Spreadsheet")),
		        new Command() {
			        public void execute() {
			        	int viewId = App.VIEW_SPREADSHEET;
			        	
			        	app.getGuiManager().setShowView(
								!app.getGuiManager().showView(viewId), viewId);
			        	itemSpreadsheet.setSelected(app.getGuiManager().showView(App.VIEW_SPREADSHEET));
			        	
			        	/*
				        MySplitLayoutPanel mp = (MySplitLayoutPanel) app
				                .getAppFrame().getGGWSplitLayoutPanel();
				        mp.createSpreadsheet();
				        mp.showView(mp.getGGWSpreadsheetView());
				        */
			        }
		        });
		
		addItem(itemSpreadsheet.getMenuItem());
		
		itemCAS = new GCheckBoxMenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getPlain("CAS")),
		        new Command() {
			        public void execute() {
			        	
			        	int viewId = App.VIEW_CAS;
			        	app.getGuiManager().setShowView(
								!app.getGuiManager().showView(viewId), viewId);
			        	itemCAS.setSelected(app.getGuiManager().showView(App.VIEW_CAS));
			        	
			        	/*
				        MySplitLayoutPanel mp = (MySplitLayoutPanel) app
				                .getAppFrame().getGGWSplitLayoutPanel();
				        mp.createCAS();
				        mp.showView(mp.getGGWCASView());
				        */
			        }
		        });

		addItem(itemCAS.getMenuItem());
		
		itemEuclidian = new GCheckBoxMenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getPlain("DrawingPad")),
		        new Command() {
			        public void execute() {

			        	int viewId = App.VIEW_EUCLIDIAN;
			        	app.getGuiManager().setShowView(
								!app.getGuiManager().showView(viewId), viewId);
			        	itemEuclidian.setSelected(app.getGuiManager().showView(App.VIEW_EUCLIDIAN));
			        }
		        });

		addItem(itemEuclidian.getMenuItem());
		
		itemEuclidian2 = new GCheckBoxMenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getPlain("DrawingPad2")),
		        new Command() {
			        public void execute() {

			        	int viewId = App.VIEW_EUCLIDIAN2;
			        	app.getGuiManager().setShowView(
								!app.getGuiManager().showView(viewId), viewId);
			        	itemEuclidian2.setSelected(app.getGuiManager().showView(App.VIEW_EUCLIDIAN2));

			        	/*// avoid one EuclidianView hiding the another (imperfect hack)
			        	app.getGuiManager().getEuclidianView2DockPanel()
			        		.getAbsolutePanel().setPixelSize(
			        				300,
			        				app.getEuclidianView1().getHeight());
			        	app.getGuiManager().getEuclidianView2DockPanel().onResize();*/
			        }
		        });
		
		addItem(itemEuclidian2.getMenuItem());

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

		update();
	}
	
	public void update(){
		itemAlgebra.setSelected(app.getGuiManager().showView(App.VIEW_ALGEBRA));	
		itemSpreadsheet.setSelected(app.getGuiManager().showView(App.VIEW_SPREADSHEET));
		itemCAS.setSelected(app.getGuiManager().showView(App.VIEW_CAS));
		itemEuclidian.setSelected(app.getGuiManager().showView(App.VIEW_EUCLIDIAN));
		itemEuclidian2.setSelected(app.getGuiManager().showView(App.VIEW_EUCLIDIAN2));
	}
	
//	public void updateCheckboxAlgebra(){
//		itemAlgebra.setSelected(app.getGuiManager().showView(App.VIEW_ALGEBRA));
//	}
//
//	public void updateSpreadsheet(){
//		itemSpreadsheet.setSelected(app.getGuiManager().showView(App.VIEW_SPREADSHEET));
//	}
//
//	public void updateCheckboxCas(){
//		itemCas.setSelected(app.getGuiManager().showView(App.VIEW_CAS));
//	}
//
//	public void updateCheckboxEuclidian(){
//		itemEuclidian.setSelected(app.getGuiManager().showView(App.VIEW_EUCLIDIAN));
//	}
//
//	public void updateCheckboxEuclidian2(){
//		itemEuclidian2.setSelected(app.getGuiManager().showView(App.VIEW_EUCLIDIAN2));
//	}

}
