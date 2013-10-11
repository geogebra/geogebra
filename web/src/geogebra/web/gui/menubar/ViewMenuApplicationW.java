package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;
import geogebra.web.javax.swing.GCheckBoxMenuItem;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;

public class ViewMenuApplicationW extends ViewMenuW{
	
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
	GCheckBoxMenuItem itemConsprot;

	public ViewMenuApplicationW(AppW application) {
	    super(application);
	    // TODO Auto-generated constructor stub
    }

	protected void initActions() {

		itemAlgebra = new GCheckBoxMenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.view_algebra24()
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
		
		itemSpreadsheet = new GCheckBoxMenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.view_spreadsheet24()
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
		
		itemCAS = new GCheckBoxMenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.view_cas24()
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
		
		itemEuclidian = new GCheckBoxMenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.view_graphics24()
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
		
		itemEuclidian2 = new GCheckBoxMenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.view_graphics224()
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

		
		itemConsprot = new GCheckBoxMenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.view_constructionprotocol24()
		        .getSafeUri().asString(), app.getPlain("ConstructionProtocol")),
		        new Command() {
			        public void execute() {

			        	int viewId = App.VIEW_CONSTRUCTION_PROTOCOL;
			        	app.getGuiManager().setShowView(
								!app.getGuiManager().showView(viewId), viewId);
			        	itemConsprot.setSelected(app.getGuiManager().showView(App.VIEW_CONSTRUCTION_PROTOCOL));

			        }
		        });
		
		addItem(itemConsprot.getMenuItem());

		addSeparator();
		
		super.initActions();
		
		update();
	}
	
	public void update(){
		itemAlgebra.setSelected(app.getGuiManager().showView(App.VIEW_ALGEBRA));	
		itemSpreadsheet.setSelected(app.getGuiManager().showView(App.VIEW_SPREADSHEET));
		itemCAS.setSelected(app.getGuiManager().showView(App.VIEW_CAS));
		itemEuclidian.setSelected(app.getGuiManager().showView(App.VIEW_EUCLIDIAN));
		itemEuclidian2.setSelected(app.getGuiManager().showView(App.VIEW_EUCLIDIAN2));
		itemConsprot.setSelected(app.getGuiManager().showView(App.VIEW_CONSTRUCTION_PROTOCOL));
	}
	
}
