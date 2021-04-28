package org.geogebra.web.full.gui;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.menubar.MyActionListener;
import org.geogebra.common.gui.menubar.RadioButtonMenuBar;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.gui.menubar.RadioButtonMenuBarW;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.full.javax.swing.CheckMarkSubMenu;
import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.full.javax.swing.GCollapseMenuItem;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.Command;

/**
 * euclidian view/graphics view context menu
 */
public class ContextMenuGraphicsWindowW extends ContextMenuGeoElementW
		implements MyActionListener {

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
		super(app, new ContextMenuFactory());
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
		OptionType ot = ev.getEuclidianViewNo() == 1
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
			addRulingMenuItem(ot);
			addBackgroundMenuItem();
		}

		addMiProperties("DrawingPad", ot);
	}

	private void addCheckboxes() {
		addAxesMenuItem();
		addGridMenuItem();
		addNavigationBar();
		RadioButtonMenuBar yaxisMenu = new RadioButtonMenuBarW((AppW) this.app,
				false);
		addAxesRatioItems(yaxisMenu);
		AriaMenuItem mi = new AriaMenuItem(
				loc.getMenu("xAxis") + " : " + loc.getMenu("yAxis"), true,
				(AriaMenuBar) yaxisMenu);
		if (!app.isUnbundled()) {
			wrappedPopup.addItem(mi);
		}
		if (!app.getActiveEuclidianView().isZoomable()) {
			yaxisMenu.setEnabled(false);
		}
		if (app.getActiveEuclidianView().isLockedAxesRatio()) {
			yaxisMenu.setEnabled(false);
		}
	}

	private void addRulingMenuItem(final OptionType optionType) {
		AriaMenuItem rulingMenuItem = new AriaMenuItem(
				MainMenu.getMenuBarHtmlClassic(
						MaterialDesignResources.INSTANCE.minor_gridlines()
								.getSafeUri().asString(),
						loc.getMenu("Ruling")),
				true, new Command() {

			@Override
			public void execute() {
				showOptionsDialog(optionType);
				((PropertiesViewW) app.getGuiManager().getPropertiesView())
						.getOptionPanel(optionType, -1)
						.getTabPanel().selectTab(3);
			}
		});

		wrappedPopup.addItem(rulingMenuItem);
	}

	private void addBackgroundMenuItem() {
		AriaMenuItem miBackgroundCol = new AriaMenuItem(
				MainMenu.getMenuBarHtmlClassic(
						MaterialDesignResources.INSTANCE.color_black()
								.getSafeUri().asString(),
						loc.getMenu("BackgroundColor")),
				true, new Command() {

					@Override
					public void execute() {
						openColorChooser();
					}
				});
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
		String imgClearTrace = MaterialDesignResources.INSTANCE.refresh_black()
				.getSafeUri().asString();
		AriaMenuItem miClearTrace = new AriaMenuItem(MainMenu.getMenuBarHtmlClassic(
				imgClearTrace, loc.getMenu("ClearTrace")), true,
				(Command) () -> app.refreshViews());
		wrappedPopup.addItem(miClearTrace);
	}

	private void addShowAllObjAndStandView() {
		ResourcePrototype img = MaterialDesignResources.INSTANCE.show_all_objects_black();
		AriaMenuItem miShowAllObjectsView = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img, loc.getMenu("ShowAllObjects")),
				true,
				this::setViewShowAllObject
		);

		ResourcePrototype img2 = MaterialDesignResources.INSTANCE.home_black();
		AriaMenuItem miStandardView = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img2, loc.getMenu("StandardView")),
				true,
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
		String htmlString = MainMenu.getMenuBarHtmlClassic(
						MaterialDesignResources.INSTANCE.grid_black().getSafeUri().asString(),
						loc.getMenu("ShowGrid"));
		gridCollapseItem = new GCollapseMenuItem(htmlString,
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
		String img = MaterialDesignResources.INSTANCE.snap_to_grid()
				.getSafeUri().asString();
		final boolean isSnapToGrid = EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC == app
				.getSettings().getEuclidian(1).getPointCapturingMode();
		final GCheckmarkMenuItem snapToGrid = new GCheckmarkMenuItem(
				MainMenu.getMenuBarHtmlClassic(img, loc.getMenu("SnapToGrid")),
				isSnapToGrid);
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
		String img = MaterialDesignResources.INSTANCE.axes_black()
					.getSafeUri().asString();

		boolean checked = app.getActiveEuclidianView().getShowXaxis()
				&& (app.getActiveEuclidianView().getShowYaxis());

		final GCheckmarkMenuItem showAxes = new GCheckmarkMenuItem(
				MainMenu.getMenuBarHtmlClassic(img, loc.getMenu("ShowAxes")),
				checked, ((AppW) app).getGuiManager().getShowAxesAction());

		wrappedPopup.addItem(showAxes);
	}

	/**
	 * show/hide construction protocol navigation
	 */
	void toggleShowConstructionProtocolNavigation() {
		((AppW) app).toggleShowConstructionProtocolNavigation(app
				.getActiveEuclidianView().getViewID());
	}

	/**
	 * @param name
	 *            title
	 * @param type
	 *            of option
	 */
	protected void addMiProperties(String name, final OptionType type) {
		String img = MaterialDesignResources.INSTANCE.gear().getSafeUri().asString();

		AriaMenuItem miProperties = new AriaMenuItem(
				MainMenu.getMenuBarHtmlClassic(img,
						app.isUnbundledOrWhiteboard()
						? loc.getMenu("Settings")
						: loc.getMenu(name) + " ..."),
				true,
		        new Command() {

			        @Override
					public void execute() {
						showOptionsDialog(type);
			        }
		        });
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
		boolean keepRatio = app.getConfig().shouldKeepRatioEuclidian();
		app.setViewShowAllObjects(keepRatio);
	}

	private void addAxesRatioItems(RadioButtonMenuBar menu) {
		double scaleRatio = app.getActiveEuclidianView()
		        .getScaleRatio();
		String[] items = new String[axesRatios.length + 2];
		String[] actionCommands = new String[axesRatios.length + 2];
		boolean separatorAdded = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0, j = 0; i < axesRatios.length; i++, j++) {
			// build text like "1 : 2"
			sb.setLength(0);
			if (axesRatios[i] > 1.0) {
				sb.append((int) axesRatios[i]);
				sb.append(" : 1");
				if (!separatorAdded) {
					// ((MenuBar) menu).addSeparator();
					actionCommands[j] = "0.0";
					items[j++] = "---";
					separatorAdded = true;
				}
			} else { // factor
				if (axesRatios[i] == 1) {
					// ((MenuBar) menu).addSeparator();
					actionCommands[j] = "0.0";
					items[j++] = "---";
				}
				sb.append("1 : ");
				sb.append((int) (1.0 / axesRatios[i]));
			}
			items[j] = sb.toString();
			actionCommands[j] = "" + axesRatios[i];
		}
		int selPos = 0;
		while ((selPos < actionCommands.length)
		        && !DoubleUtil.isEqual(Double.parseDouble(actionCommands[selPos]),
		                scaleRatio)) {
			selPos++;
		}
		menu.addRadioButtonMenuItems(this, items, actionCommands, selPos, false);
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
		String img = GuiResourcesSimple.INSTANCE.zoom_in().getSafeUri()
					.asString();

		AriaMenuItem zoomMenuItem = new AriaMenuItem(
				MainMenu.getMenuBarHtmlClassic(img,
				loc.getMenu("Zoom")), true, zoomMenu);

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
			mi = new AriaMenuItem(sb.toString(), false, new Command() {
				@Override
				public void execute() {
					zoom(getZoomFactor(index));
				}
			});
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

		GCheckmarkMenuItem showConstructionStep = new GCheckmarkMenuItem(
				loc.getMenu("NavigationBar"), selected, showConstructionStepCommand);

		wrappedPopup.addItem(showConstructionStep);

		wrappedPopup.addSeparator();
	}

	@Override
	public void actionPerformed(String command) {
		try {
			// zoomYaxis(Double.parseDouble(e.getActionCommand()));
			zoomYaxis(Double.parseDouble(command));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
			addItem(text, isSelected, new Command() {

				@Override
				public void execute() {
					setGridType(gridType);
				}
			}, false);
		}

		private void addNoGridItem() {
			String text = app.getLocalization().getMenu("Grid.No");
			boolean isSelected = !app.getActiveEuclidianView().getShowGrid();
			addItem(text, isSelected, new Command() {

				@Override
				public void execute() {
					setGridType(EuclidianView.GRID_NOT_SHOWN);
				}
			}, false);
		}

		@Override
		public void update() {
			// do nothing now
		}
	}
}
