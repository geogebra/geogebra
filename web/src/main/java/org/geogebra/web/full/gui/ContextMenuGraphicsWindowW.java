package org.geogebra.web.full.gui;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.dropdown.grid.GridDialog;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.gui.menubar.RadioButtonMenuBarW;
import org.geogebra.web.full.javax.swing.CheckMarkSubMenu;
import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.full.javax.swing.GCollapseMenuItem;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.Command;

/**
 * euclidian view/graphics view context menu
 */
public class ContextMenuGraphicsWindowW extends ContextMenuGeoElementW {

	/**
	 * x position of popup
	 */
	protected double px;
	/**
	 * y position of popup
	 */
	protected double py;
	private GCollapseMenuItem gridCollapseItem;

	/**
	 * @param app
	 *            application
	 */
	protected ContextMenuGraphicsWindowW(AppW app) {
		super(app, new ContextMenuItemFactory());
	}

	/**
	 * @param app
	 *            application
	 * @param px
	 *            x pos of popup
	 * @param py
	 *            y pos of popup
	 * @param showPaste
	 *            whether to show the paste button (false for the graphics settings button)
	 */
	public ContextMenuGraphicsWindowW(AppW app, double px, double py, boolean showPaste) {
		this(app);
		this.px = px;
		this.py = py;

		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		OptionType optionType = ev.getEuclidianViewNo() == 1
				? OptionType.EUCLIDIAN : OptionType.EUCLIDIAN2;

		if (!app.isWhiteboardActive()) {
			if (app.isUnbundled()) {
				addAxesMenuItem();
				addGridMenuItem();
				addSnapToGridMenuItem();
				addClearTraceMenuItem();
			} else {
				if (ev.getEuclidianViewNo() == 1) {
					setTitle(loc.getMenu("DrawingPad"));
				} else {
					setTitle(loc.getMenu("DrawingPad2"));
				}

				addCheckboxes();
			}
			addShowAllObjAndStandView();
		} else {
			if (showPaste) {
				addPasteItem();
				wrappedPopup.addSeparator();
			}
			addRulingMenuItem();
			addBackgroundMenuItem();
		}

		addMiProperties("DrawingPad", optionType);
	}

	private void addCheckboxes() {
		addAxesMenuItem();
		addGridMenuItem();
		addNavigationBar();
		RadioButtonMenuBarW yaxisMenu = new RadioButtonMenuBarW(app.getLocalization());
		addAxesRatioItems(yaxisMenu);
		AriaMenuItem mi = new AriaMenuItem(
				loc.getMenu("xAxis") + " : " + loc.getMenu("yAxis"), null,
				yaxisMenu);
		if (!app.isUnbundled()) {
			wrappedPopup.addItem(mi);
		}
		if (!app.getActiveEuclidianView().isZoomable()) {
			mi.setEnabled(false);
		}
		if (app.getActiveEuclidianView().isLockedAxesRatio()) {
			mi.setEnabled(false);
		}
	}

	private void addRulingMenuItem() {
		AriaMenuItem rulingMenuItem =
				MainMenu.getMenuBarItem(
						MaterialDesignResources.INSTANCE.minor_gridlines(),
						loc.getMenu("Ruling"),
				() -> {
					DialogData data = new DialogData("Ruling", "Cancel", "Save");
					GridDialog gridDialog = new GridDialog((AppW) app, data,
							app.getActiveEuclidianView());
					gridDialog.show();
				});

		wrappedPopup.addItem(rulingMenuItem);
	}

	private void addBackgroundMenuItem() {
		AriaMenuItem miBackgroundCol =
				MainMenu.getMenuBarItem(
						MaterialDesignResources.INSTANCE.color_black(),
						loc.getMenu("BackgroundColor"),
				this::openColorChooser);
		wrappedPopup.addItem(miBackgroundCol);
	}

	/**
	 * open color chooser dialog to select graphics background
	 */
	protected void openColorChooser() {
		((DialogManagerW) (app.getDialogManager())).showColorChooserDialog(
				app.getSettings().getEuclidian(1).getBackground(),
				new ColorChangeHandler() {

					@Override
					public void onForegroundSelected() {
						// do nothing
					}

					@Override
					public void onColorChange(GColor color) {
						// change graphics background color
						app.getSettings().getEuclidian(1).setBackground(color);
					}

					@Override
					public void onClearBackground() {
						// do nothing
					}

					@Override
					public void onBarSelected() {
						// do nothing
					}

					@Override
					public void onBackgroundSelected() {
						// do nothing
					}

					@Override
					public void onAlphaChange() {
						// do nothing
					}
				});
	}

	private void addClearTraceMenuItem() {
		SVGResource imgClearTrace = MaterialDesignResources.INSTANCE.refresh_black();
		AriaMenuItem miClearTrace = MainMenu.getMenuBarItem(
				imgClearTrace, loc.getMenu("ClearTrace"),
				app::refreshViews);
		wrappedPopup.addItem(miClearTrace);
	}

	private void addShowAllObjAndStandView() {
		ResourcePrototype img = MaterialDesignResources.INSTANCE.show_all_objects_black();
		AriaMenuItem miShowAllObjectsView =
				MainMenu.getMenuBarItem(img, loc.getMenu("ShowAllObjects"),
				this::setViewShowAllObject
		);

		ResourcePrototype img2 = MaterialDesignResources.INSTANCE.home_black();
		AriaMenuItem miStandardView =
				MainMenu.getMenuBarItem(img2, loc.getMenu("StandardView"),
				() -> app.setStandardView()
		);

		if (!app.getActiveEuclidianView().isZoomable()) {
			miShowAllObjectsView.setEnabled(false);
			miStandardView.setEnabled(false);
		}
		if (!app.isUnbundledOrWhiteboard()) {
			addZoomMenu();
		}
		wrappedPopup.addItem(miShowAllObjectsView);
		if (!app.isUnbundledOrWhiteboard()) {
			wrappedPopup.addItem(miStandardView);
		}
	}

	private void addGridMenuItem() {
		gridCollapseItem = new GCollapseMenuItem(MaterialDesignResources.INSTANCE.grid_black(),
				loc.getMenu("ShowGrid"),
				MaterialDesignResources.INSTANCE.expand_black().getSafeUri()
						.asString(),
				MaterialDesignResources.INSTANCE.collapse_black().getSafeUri()
						.asString(),
				false, wrappedPopup);
		wrappedPopup.addItem(gridCollapseItem);
		GridSubmenu gridSubMenu = new GridSubmenu(gridCollapseItem);
		gridSubMenu.update();
		gridCollapseItem.attachToParent();
	}

	/**
	 * add snap to grid menu item
	 */
	public void addSnapToGridMenuItem() {
		SVGResource img = MaterialDesignResources.INSTANCE.snap_to_grid();
		final boolean isSnapToGrid = EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC == app
				.getSettings().getEuclidian(1).getPointCapturingMode();
		final GCheckmarkMenuItem snapToGrid = new GCheckmarkMenuItem(
				img, loc.getMenu("SnapToGrid"),
				isSnapToGrid, () -> {});
		snapToGrid.setCommand(() -> {
			app.getEuclidianView1().setPointCapturing(isSnapToGrid
					? EuclidianStyleConstants.POINT_CAPTURING_OFF
					: EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC);
			if (app.hasEuclidianView2EitherShowingOrNot(1)) {
				app.getEuclidianView2(1).setPointCapturing(isSnapToGrid
						? EuclidianStyleConstants.POINT_CAPTURING_OFF
						: EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC);
			}
			snapToGrid.setChecked(!isSnapToGrid);
			app.getGuiManager().updatePropertiesView();
			app.storeUndoInfo();
		});
		wrappedPopup.addItem(snapToGrid);
	}

	/**
	 * add axes menu item with check mark
	 *
	 */
	protected void addAxesMenuItem() {
		SVGResource img = MaterialDesignResources.INSTANCE.axes_black();

		boolean checked = app.getActiveEuclidianView().getShowXaxis()
				&& (app.getActiveEuclidianView().getShowYaxis());

		final GCheckmarkMenuItem showAxes = new GCheckmarkMenuItem(
				img, loc.getMenu("ShowAxes"),
				checked, app.getGuiManager()::showAxesCmd);

		wrappedPopup.addItem(showAxes);
	}

	/**
	 * show/hide construction protocol navigation
	 */
	void toggleShowConstructionProtocolNavigation() {
		app.toggleShowConstructionProtocolNavigation(app
				.getActiveEuclidianView().getViewID());
	}

	/**
	 * @param name
	 *            title
	 * @param type
	 *            of option
	 */
	protected void addMiProperties(String name, final OptionType type) {
		SVGResource img = MaterialDesignResources.INSTANCE.gear();

		AriaMenuItem miProperties =
				MainMenu.getMenuBarItem(img,
						app.isUnbundledOrWhiteboard()
						? loc.getMenu("Settings")
						: loc.getMenu(name) + " ...",
				() -> showOptionsDialog(type));
		miProperties.setEnabled(true); // TMP AG
		wrappedPopup.addItem(miProperties);
	}

	/**
	 * @param type
	 *            of option
	 */
	protected void showOptionsDialog(OptionType type) {
		if (app.getGuiManager() != null) {
			app.getDialogManager().showPropertiesDialog(type, null);
		}
	}

	/**
	 * set show all objects
	 */
	public void setViewShowAllObject() {
		app.setViewShowAllObjects();
	}

	private void addAxesRatioItems(RadioButtonMenuBarW menu) {
		double scaleRatio = app.getActiveEuclidianView()
				.getScaleRatio();
		String[] items = new String[axesRatios.length + 2];
		double[] options = new double[axesRatios.length + 2];
		boolean separatorAdded = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0, j = 0; i < axesRatios.length; i++, j++) {
			// build text like "1 : 2"
			sb.setLength(0);
			if (axesRatios[i] > 1.0) {
				sb.append((int) axesRatios[i]);
				sb.append(" : 1");
				if (!separatorAdded) {
					items[j++] = "---";
					separatorAdded = true;
				}
			} else { // factor
				if (axesRatios[i] == 1) {
					items[j++] = "---";
				}
				sb.append("1 : ");
				sb.append((int) (1.0 / axesRatios[i]));
			}
			items[j] = sb.toString();
			options[j] = axesRatios[i];
		}
		int selPos = 0;
		while ((selPos < options.length)
				&& !DoubleUtil.isEqual(options[selPos], scaleRatio)) {
			selPos++;
		}
		menu.addRadioButtonMenuItems(this::zoomYaxis, items, options, selPos);
	}

	/**
	 * @param axesRatio
	 *            ratio of y axis
	 */
	protected void zoomYaxis(double axesRatio) {
		app.zoomAxesRatio(axesRatio);
	}

	/**
	 * app zoom menu
	 */
	protected void addZoomMenu() {
		// zoom for both axes
		AriaMenuBar zoomMenu = new AriaMenuBar();
		ResourcePrototype img = GuiResourcesSimple.INSTANCE.zoom_in();

		AriaMenuItem zoomMenuItem = new AriaMenuItem(
				loc.getMenu("Zoom"), img, zoomMenu);

		wrappedPopup.addItem(zoomMenuItem);
		addZoomItems(zoomMenu);
		if (!app.getActiveEuclidianView().isZoomable()) {
			zoomMenuItem.setEnabled(false);
		}
	}

	private void addZoomItems(AriaMenuBar menu) {
		int perc;
		AriaMenuItem mi;
		boolean separatorAdded = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getZoomFactorLength(); i++) {
			perc = (int) (getZoomFactor(i) * 100.0);
			// build text like "125%" or "75%"
			sb.setLength(0);
			if ((perc <= 100) && (!separatorAdded)) {
				menu.addSeparator();
				separatorAdded = true;
			}
			sb.append(perc);
			sb.append('%');
			final int index = i;
			// TODO: it is terrible, should be used ONE listener for each
			// menuItem, this kills the memory, if GWT changes this
			// get it right!
			mi = new AriaMenuItem(sb.toString(), null, () -> zoom(getZoomFactor(index)));
			menu.addItem(mi);
		}
	}

	/**
	 * @param zoomFactor
	 *            zoom fasctor
	 */
	protected void zoom(double zoomFactor) {
		app.zoom(px, py, zoomFactor);
	}

	/**
	 * add navigation bar
	 */
	protected void addNavigationBar() {
		if (app.isUnbundledOrWhiteboard()) {
			return;
		}
		// Show construction protocol navigation bar checkbox item
		Command showConstructionStepCommand = this::toggleShowConstructionProtocolNavigation;

		boolean selected = app.showConsProtNavigation(app
				.getActiveEuclidianView().getViewID());

		GCheckmarkMenuItem showConstructionStep = new GCheckmarkMenuItem(null,
				loc.getMenu("NavigationBar"), selected, showConstructionStepCommand);

		wrappedPopup.addItem(showConstructionStep);

		wrappedPopup.addSeparator();
	}

	/**
	 * @author csilla expand/collapse submenu for major and minor grid setting
	 *
	 */
	public class GridSubmenu extends CheckMarkSubMenu {
		/**
		 * @param parentMenu
		 *            - parent menu item
		 */
		public GridSubmenu(GCollapseMenuItem parentMenu) {
			super(parentMenu);
		}

		@Override
		protected void initActions() {
			addNoGridItem();
			addGridItem("Grid.Major", EuclidianView.GRID_CARTESIAN);
			addGridItem("Grid.MajorAndMinor",
					EuclidianView.GRID_CARTESIAN_WITH_SUBGRID);
			addGridItem("Polar", EuclidianView.GRID_POLAR);
			addGridItem("Isometric", EuclidianView.GRID_ISOMETRIC);
		}

		/**
		 * @param gridType
		 *            new grid type
		 */
		protected void setGridType(int gridType) {
			app.getActiveEuclidianView().getSettings()
					.showGrid(gridType != EuclidianView.GRID_NOT_SHOWN);
			app.getActiveEuclidianView().getSettings().setGridType(gridType);
			app.getActiveEuclidianView().setGridType(gridType);
			app.getActiveEuclidianView().repaintView();
			app.storeUndoInfo();
			wrappedPopup.hideMenu();
		}

		private void addGridItem(String key, final int gridType) {
			String text = app.getLocalization().getMenu(key);
			boolean isSelected = app.getActiveEuclidianView()
					.getGridType() == gridType && app.getActiveEuclidianView().getShowGrid();
			addItem(null, text, isSelected, () -> setGridType(gridType), false);
		}

		private void addNoGridItem() {
			String text = app.getLocalization().getMenu("Grid.No");
			boolean isSelected = !app.getActiveEuclidianView().getShowGrid();
			addItem(null, text, isSelected, () -> setGridType(EuclidianView.GRID_NOT_SHOWN), false);
		}

		@Override
		public void update() {
			// do nothing now
		}
	}
}
