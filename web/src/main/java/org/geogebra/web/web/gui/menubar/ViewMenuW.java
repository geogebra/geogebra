package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPositon;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.view.Views;
import org.geogebra.web.web.javax.swing.GCheckBoxMenuItem;

import com.google.gwt.user.client.Timer;

/**
 * The "View" menu for the applet.
 * For application use ViewMenuApplicationW class
 */
public class ViewMenuW extends GMenuBar {

	
	/**
	 * Menuitem with checkbox for show algebra view
	 */
	GCheckBoxMenuItem[] items;
	AppW app;
	GCheckBoxMenuItem inputBarItem;
	
	/**
	 * Constructs the "View" menu
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
	
	protected void initRefreshActions() {

		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getMenu("Refresh"), true), true,
		        new MenuCommand(app) {
			
			        @Override
                    public void doExecute() {
				        app.refreshViews();
			        }
		        });

		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getMenu("RecomputeAllViews"), true),
		        true, new MenuCommand(app) {
			
			        @Override
			        public void doExecute() {
				        app.getKernel().updateConstruction();
			        }
		        });
	}
	
	
	protected void initActions() {
		items = new GCheckBoxMenuItem[Views.ids.length];
		for(int k = 0; k< Views.ids.length; k++){
			final int i = k;
			if(!app.supportsView(Views.ids[i])){
				continue;
			}
			items[i] = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(GGWToolBar.safeURI(Views.icons[i]
			        ), app.getPlain(Views.keys[i]), true),
			        new MenuCommand(app) {
				
				        @Override
                        public void doExecute() {
				        	app.getGuiManager().setShowView(
									!app.getGuiManager().showView(Views.ids[i]), Views.ids[i]);
				        	items[i].setSelected(app.getGuiManager().showView(Views.ids[i]));

							Timer timer = new Timer() {
								@Override
								public void run() {
									//false, because we have just closed the menu
									app.getGuiManager().updateStyleBarPositions(false);
								}
							};
							timer.schedule(0);
				        }}
				      );
			addItem(items[i].getMenuItem());
		}
		inputBarItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getMenu("InputField"), true),
		        new MenuCommand(app) {
			
			        @Override
                    public void execute() {
			        	App.debug("AI:"+app.showAlgebraInput());
			        	app.persistWidthAndHeight();
				        // app.setShowAlgebraInput(!app.showAlgebraInput(), true);
			        	app.setInputPositon(app.getInputPosition() == InputPositon.algebraView ? 
			        			InputPositon.bottom : InputPositon.algebraView, true);
				        app.updateCenterPanel(true);
				        app.updateViewSizes();
				        inputBarItem.setSelected(app.getInputPosition() != InputPositon.algebraView);

						Timer timer = new Timer() {
							@Override
							public void run() {
								//true, because this can only be executed, if menu is open
								app.getGuiManager().updateStyleBarPositions(true);
							}
						};
						timer.schedule(0);
			        }
		        });
		addItem(inputBarItem.getMenuItem());
		
		addSeparator();
		
		initRefreshActions();
		
		update();
	}
	
	public void update(){
		for(int k = 0; k < items.length; k++){
			if(items[k] != null){
				items[k].setSelected(app.getGuiManager().showView(Views.ids[k]));
			}
		}
		inputBarItem.setSelected(app.getInputPosition() != InputPositon.algebraView);
	}

}
