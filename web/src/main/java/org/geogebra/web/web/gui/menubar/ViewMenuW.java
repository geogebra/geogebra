package org.geogebra.web.web.gui.menubar;

import java.util.HashMap;

import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.view.Views;
import org.geogebra.web.web.gui.view.Views.ViewType;
import org.geogebra.web.web.javax.swing.GCheckBoxMenuItem;

import com.google.gwt.user.client.Timer;

/**
 * The "View" menu for the applet. For application use ViewMenuApplicationW
 * class
 */
public class ViewMenuW extends GMenuBar {

	/**
	 * Menuitem with checkbox for show algebra view
	 */
	HashMap<Integer, GCheckBoxMenuItem> items = new HashMap<Integer, GCheckBoxMenuItem>();
	/** app */
	AppW app;
	/** item for input */
	GCheckBoxMenuItem inputBarItem;
	/** item for sensor app */
	GCheckBoxMenuItem dataCollection;
	/** item for navigation bar */
	GCheckBoxMenuItem consProtNav;

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

	/**
	 * Init actions for Refresh views, recompute objects
	 */
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
				.getSafeUri().asString(), app.getMenu("RecomputeAllViews"),
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
		addSeparator();
		for (final ViewType e : Views.getViewExtensions()) {
			if (!app.supportsView(e.getID())) {
				continue;
			}
			addToMenu(e);
		}

		inputBarItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
				AppResources.INSTANCE.empty().getSafeUri().asString(),
				app.getMenu("InputField"), true), new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.persistWidthAndHeight();
						app.getArticleElement().setAttribute(
								"data-param-showAlgebraInput", "true");
						boolean visibleBelow = app
								.getInputPosition() == InputPosition.algebraView
								|| !app.showAlgebraInput();
						app.addToHeight(
								visibleBelow ? -GLookAndFeelI.COMMAND_LINE_HEIGHT
										: GLookAndFeelI.COMMAND_LINE_HEIGHT);
						app.setShowAlgebraInput(true, false);
						app.setInputPositon(
								app.getInputPosition() == InputPosition.algebraView
										? InputPosition.bottom
										: InputPosition.algebraView,
								true);
						app.updateCenterPanel(true);

						app.updateViewSizes();
						if (app.getGuiManager() != null
								&& app.getGuiManager().getLayout() != null) {
							app.getGuiManager().getLayout().getDockManager()
									.resizePanels();
						}
						inputBarItem.setSelected(app
								.getInputPosition() != InputPosition.algebraView);

						Timer timer = new Timer() {
							@Override
							public void run() {
								// false, because we have just closed the menu
								app.getGuiManager()
										.updateStyleBarPositions(false);
								app.updateCenterPanel(true);
							}
						};
						timer.schedule(0);
					}
				}, true);
		addItem(inputBarItem.getMenuItem());

		consProtNav = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
				AppResources.INSTANCE.empty().getSafeUri().asString(),
				app.getMenu("NavigationBar"), true), new MenuCommand(app) {

			@Override
			public void doExecute() {
				if (consProtNav.isSelected()) {
					app.setShowConstructionProtocolNavigation(false);
				} else {
					int id = app.getActiveEuclidianView().getViewID();
					app.setShowConstructionProtocolNavigation(true, id);
					app.getGuiManager()
							.updateCheckBoxesForShowConstructinProtocolNavigation(
									id);
				}
			}
		}, true);
		addItem(consProtNav.getMenuItem());


		if (app.has(Feature.DATA_COLLECTION)) {
			dataCollection = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
					AppResources.INSTANCE.empty().getSafeUri().asString(), app
							.getLocalization().getMenu("Sensors"), true),
					new MenuCommand(app) {

						@Override
						public void execute() {
							app.getGuiManager().setShowView(
									!app.getGuiManager().showView(
											App.VIEW_DATA_COLLECTION),
									App.VIEW_DATA_COLLECTION);
							dataCollection.setSelected(app.getGuiManager()
									.showView(App.VIEW_DATA_COLLECTION));
							app.toggleMenu();
						}
					}, true);
			if (!app.isExam()) {
			addItem(dataCollection.getMenuItem());
			}
		}


		addSeparator();

		initRefreshActions();

		update();
	}


	private void addToMenu(final ViewType e) {

		final GCheckBoxMenuItem newItem = new GCheckBoxMenuItem(
				MainMenu.getMenuBarHtml(ImgResourceHelper.safeURI(e.getIcon()),
						app.getPlain(e.getKey()), true), true);
		newItem.setCommand(new MenuCommand(app) {

			@Override
			public void doExecute() {
				boolean shown = app.getGuiManager().showView(e.getID());

				if (e.getID() == App.VIEW_ALGEBRA && shown == false) {
					app.setInputPositon(InputPosition.algebraView, true);
				}
				app.getGuiManager().setShowView(!shown, e.getID());
				newItem.setSelected(app.getGuiManager().showView(e.getID()));
				// reset activePerspective so that no perspective is
				// highlighted in apps picker when view is customized
				PerspectivesPopup.setActivePerspective(-1);

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
		for (int viewID : this.items.keySet()) {
			this.items.get(viewID).setSelected(
					app.getGuiManager().showView(viewID));
		}
		boolean linearInput = app.showAlgebraInput()
				&& app.getInputPosition() != InputPosition.algebraView;
		inputBarItem.setSelected(linearInput);
		consProtNav.setSelected(app.showConsProtNavigation());
		if (app.has(Feature.DATA_COLLECTION)) {
			dataCollection.setSelected(app.getGuiManager().showView(
					App.VIEW_DATA_COLLECTION));
		}
	}
}
