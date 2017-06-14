package org.geogebra.web.web.gui.menubar;

import java.util.HashMap;
import java.util.Map.Entry;

import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.view.Views;
import org.geogebra.web.web.gui.view.Views.ViewType;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.web.javax.swing.GCheckmarkMenuItem;

import com.google.gwt.user.client.Timer;

/**
 * The "View" menu for the applet. For application use ViewMenuApplicationW
 * class
 */
public class ViewMenuW2 extends GMenuBar {

	/**
	 * Menuitem with checkmark for show algebra view
	 */
	HashMap<Integer, GCheckmarkMenuItem> items = new HashMap<Integer, GCheckmarkMenuItem>();
	/** app */
	AppW app;
	/** item for input */
	GCheckmarkMenuItem inputBarItem;
	/** item for sensor app */
	GCheckmarkMenuItem dataCollection;
	/** item for navigation bar */
	GCheckmarkMenuItem consProtNav;

	/**
	 * Constructs the "View" menu
	 * 
	 * @param application
	 *            The App instance
	 */
	public ViewMenuW2(AppW application) {

		super(true, "view", application);
		this.app = application;
		if (app.has(Feature.NEW_TOOLBAR)) {
			addStyleName("matStackPanelNoOpacity");
		} else {
			addStyleName("GeoGebraMenuBar");
		}
		initActions();
	}

	/**
	 * Init actions for Refresh views, recompute objects
	 * 
	 * @param loc
	 *            localization
	 */
	protected void initRefreshActions(Localization loc) {
		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
				.getSafeUri().asString(), loc.getMenu("Refresh"), true), true,
				new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.refreshViews();
					}
				});

		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
				.getSafeUri().asString(), loc.getMenu("RecomputeAllViews"),
				true), true, new MenuCommand(app) {

			@Override
			public void doExecute() {
				app.getKernel().updateConstruction();
			}
		});
	}

	/**
	 * Initialize menu items
	 */
	protected void initActions() {
		for (final ViewType e : Views.getViews()) {
			if (!app.supportsView(e.getID())) {
				continue;
			}
			addToMenu(e);
		}
		if (!app.has(Feature.NEW_TOOLBAR)) {
			addSeparator();
		}
		for (final ViewType e : Views.getViewExtensions()) {
			if (!app.supportsView(e.getID())) {
				continue;
			}
			addToMenu(e);
		}
		Localization loc = app.getLocalization();
		inputBarItem = new GCheckmarkMenuItem(MainMenu.getMenuBarHtml(
				AppResources.INSTANCE.empty().getSafeUri().asString(),
				loc.getMenu("InputField")),
				MaterialDesignResources.INSTANCE.check_black().getSafeUri()
						.asString(),
				true, new MenuCommand(app) {
					// fill later
				});
		inputBarItem.setCommand(new MenuCommand(app) {

			@Override
			public void doExecute() {
				app.persistWidthAndHeight();
				app.getArticleElement()
						.setAttribute("data-param-showAlgebraInput", "true");
				boolean visibleBelow = app
						.getInputPosition() == InputPosition.algebraView
						|| !app.showAlgebraInput();
				if (!app.has(
						Feature.KEYBOARD_MESSED_WITH_OLD_INPUTBAR)) {
					app.addToHeight(
							visibleBelow ? -GLookAndFeelI.COMMAND_LINE_HEIGHT
									: GLookAndFeelI.COMMAND_LINE_HEIGHT);
				}
				app.setShowAlgebraInput(true, false);
				app.setInputPosition(
						app.getInputPosition() == InputPosition.algebraView
										? InputPosition.bottom
										: InputPosition.algebraView,
								true);
				if (app.has(
								Feature.KEYBOARD_MESSED_WITH_OLD_INPUTBAR)) {
					app.updateSplitPanelHeight();
				}
				app.updateCenterPanelAndViews();
				if (app.getGuiManager() != null
						&& app.getGuiManager().getLayout() != null) {
					app.getGuiManager().getLayout().getDockManager()
							.resizePanels();
				}
				inputBarItem.setChecked(
						app.getInputPosition() != InputPosition.algebraView);

						Timer timer = new Timer() {
							@Override
							public void run() {
								// false, because we have just closed the menu
								app.getGuiManager()
										.updateStyleBarPositions(false);
								app.updateCenterPanel();
							}
						};
						timer.schedule(0);
					}
		});
		addItem(inputBarItem.getMenuItem());

		consProtNav = new GCheckmarkMenuItem(MainMenu.getMenuBarHtml(
				AppResources.INSTANCE.empty().getSafeUri().asString(),
				loc.getMenu("NavigationBar")),
				MaterialDesignResources.INSTANCE.check_black().getSafeUri()
						.asString(),
				true, new MenuCommand(app) {
					// fill later
				});
		consProtNav.setCommand(new MenuCommand(app) {
			@Override
			public void doExecute() {
				if (consProtNav.isChecked()) {
					app.setShowConstructionProtocolNavigation(false);
				} else {
					int id = app.getActiveEuclidianView().getViewID();
					app.setShowConstructionProtocolNavigation(true, id);
					app.getGuiManager()
							.updateCheckBoxesForShowConstructinProtocolNavigation(
									id);
				}
			}
		});

		addItem(consProtNav.getMenuItem());


		if (app.has(Feature.DATA_COLLECTION)) {
			dataCollection = new GCheckmarkMenuItem(
					MainMenu.getMenuBarHtml(
					AppResources.INSTANCE.empty().getSafeUri().asString(), app
									.getLocalization().getMenu("Sensors")),
					MaterialDesignResources.INSTANCE.check_black().getSafeUri()
							.asString(),
					true, new MenuCommand(app) {
						// fill later
					});
			dataCollection.setCommand(new MenuCommand(app) {

				@Override
				public void execute() {
					app.getGuiManager().setShowView(
							!app.getGuiManager()
									.showView(App.VIEW_DATA_COLLECTION),
							App.VIEW_DATA_COLLECTION);
					dataCollection.setChecked(app.getGuiManager()
							.showView(App.VIEW_DATA_COLLECTION));
					app.toggleMenu();
				}
			});
			if (!app.isExam()) {
			addItem(dataCollection.getMenuItem());
			}
		}

		if (!app.has(Feature.NEW_TOOLBAR)) {
			addSeparator();
		}

		initRefreshActions(loc);

		update();
	}


	private void addToMenu(final ViewType e) {

		String htmlString = MainMenu.getMenuBarHtml(
				ImgResourceHelper.safeURI(e.getIcon()),
				app.getLocalization().getMenu(e.getKey()));
		final GCheckmarkMenuItem newItem = new GCheckmarkMenuItem(htmlString,
				MaterialDesignResources.INSTANCE.check_black().getSafeUri()
						.asString(),
				true, new MenuCommand(app) {
					// filled later
				});

		newItem.setCommand(new MenuCommand(app) {

			@Override
			public void execute() {
				boolean shown = app.getGuiManager().showView(e.getID());

				if (e.getID() == App.VIEW_ALGEBRA && !shown) {
					app.setInputPosition(InputPosition.algebraView, true);
					((AlgebraViewW) app.getAlgebraView()).setDefaultUserWidth();
				}
				app.getGuiManager().setShowView(!shown, e.getID());
						newItem.setChecked(
								app.getGuiManager().showView(e.getID()));
				// reset activePerspective so that no perspective is
				// highlighted in apps picker when view is customized
				app.setActivePerspective(-1);

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

	/**
	 * Update menu items
	 */
	public void update() {
		for (Entry<Integer, GCheckmarkMenuItem> entry : this.items.entrySet()) {

			int viewID = entry.getKey();

			entry.getValue().setChecked(
					app.getGuiManager().showView(viewID));
		}
		boolean linearInput = app.showAlgebraInput()
				&& app.getInputPosition() != InputPosition.algebraView;
		inputBarItem.setChecked(linearInput);
		consProtNav.setChecked(app.showConsProtNavigation());
		if (app.has(Feature.DATA_COLLECTION)) {
			dataCollection.setChecked(
					app.getGuiManager().showView(
					App.VIEW_DATA_COLLECTION));
		}
	}
}
