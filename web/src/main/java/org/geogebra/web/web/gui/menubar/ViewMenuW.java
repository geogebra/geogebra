package org.geogebra.web.web.gui.menubar;

import java.util.HashMap;

import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPositon;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.view.Views;
import org.geogebra.web.web.gui.view.Views.ViewType;
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
	HashMap<Integer, GCheckBoxMenuItem> items = new HashMap<Integer, GCheckBoxMenuItem>();
	AppW app;
	GCheckBoxMenuItem inputBarItem;
	private GCheckBoxMenuItem dataCollection;
	private GCheckBoxMenuItem consProtNav;
	
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
		for (final ViewType e : Views.getViews()) {
			if (!app.supportsView(e.getID())) {
				continue;
			}
			addToMenu(e);
		}
		addSeparator();
		for (final ViewType e : Views.getViewExtensions()) {
			if (!app.supportsView(e.getID())) {
				continue;
			}
			addToMenu(e);
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
		
		consProtNav = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getMenu("NavigationBar"), true),
		        new MenuCommand(app) {
			
			        @Override
                    public void execute() {
			        	app.toggleShowConstructionProtocolNavigation();
			        }
		        });
		addItem(consProtNav.getMenuItem());
		
		if (app.has(Feature.DATA_COLLECTION)) {
			dataCollection = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
					AppResources.INSTANCE.empty().getSafeUri().asString(),
					app.getMenu("Data Collection"), true),
					new MenuCommand(app) {

						@Override
						public void execute() {
							app.getGuiManager().setShowView(
									!app.getGuiManager().showView(
											AppW.VIEW_DATA_COLLECTION),
									AppW.VIEW_DATA_COLLECTION);
							dataCollection.setSelected(app.getGuiManager()
									.showView(AppW.VIEW_DATA_COLLECTION));
							app.toggleMenu();
						}
					});
			addItem(dataCollection.getMenuItem());
		}
		
		addSeparator();
		
		initRefreshActions();
		
		update();
	}
	
	private GCheckBoxMenuItem newItem;
	private void addToMenu(final ViewType e) {
		newItem = new GCheckBoxMenuItem(
				MainMenu.getMenuBarHtml(
				GGWToolBar.safeURI(e.getIcon()),
				app.getPlain(e.getKey()), true), new MenuCommand(app) {

			@Override
			public void doExecute() {
				app.getGuiManager().setShowView(
						!app.getGuiManager().showView(e.getID()), e.getID());
						newItem.setSelected(app.getGuiManager().showView(
								e.getID()));

				Timer timer = new Timer() {
					@Override
					public void run() {
						// false, because we have just closed the menu
						app.getGuiManager().updateStyleBarPositions(false);
					}
				};
				timer.schedule(0);
			}
		});
		items.put(e.getID(), newItem);
		addItem(newItem.getMenuItem());
	}

	public void update(){
		for (int viewID : this.items.keySet()) {
			this.items.get(viewID).setSelected(
					app.getGuiManager().showView(viewID));
		}
		inputBarItem.setSelected(app.getInputPosition() != InputPositon.algebraView);
		consProtNav.setSelected(app.showConsProtNavigation());
	}
}
