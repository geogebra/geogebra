package org.geogebra.web.web.gui;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.gui.menubar.MyActionListener;
import org.geogebra.common.gui.menubar.RadioButtonMenuBar;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.StyleBarResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.gui.menubar.RadioButtonMenuBarW;
import org.geogebra.web.web.javax.swing.CheckMarkSubMenu;
import org.geogebra.web.web.javax.swing.GCheckBoxMenuItem;
import org.geogebra.web.web.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.web.javax.swing.GCollapseMenuItem;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class ContextMenuGraphicsWindowW extends ContextMenuGeoElementW
        implements MyActionListener {

	protected double px;
	protected double py;

	protected ContextMenuGraphicsWindowW(AppW app) {
		super(app);
		/*
		 * if (isWhiteboard() && !app.isUnbundled()) {
		 * wrappedPopup.getPopupPanel().addStyleName("contextMenu"); } else
		 */
		if (app.isUnbundled() || hasWhiteboardContextMenu()) {
			wrappedPopup.getPopupPanel().addStyleName("matMenu");
		}
	}

	public ContextMenuGraphicsWindowW(AppW app, double px, double py) {
		this(app);

		this.px = px;
		this.py = py;

		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		OptionType ot = OptionType.EUCLIDIAN;
		if (ev.getEuclidianViewNo() == 2) {
			ot = OptionType.EUCLIDIAN2;
			setTitle(loc.getMenu("DrawingPad2"));
		} else {
			setTitle(loc.getMenu("DrawingPad"));
		}

		if (app.isUnbundled() || app.isWhiteboardActive()) {
			addAxesMenuItem();
			addGridMenuItem();
			addSnapToGridMenuItem();
			addClearTraceMenuItem();
		}

		addShowAllObjAndStandView();

		addMiProperties("DrawingPad", ot);

	}

	private void addClearTraceMenuItem() {
		String imgClearTrace = MaterialDesignResources.INSTANCE.refresh_black()
				.getSafeUri().asString();
		MenuItem miClearTrace = new MenuItem(MainMenu.getMenuBarHtml(imgClearTrace, loc.getMenu("ClearTrace")), true,
				new Command() {

			        @Override
					public void execute() {
						app.refreshViews();
			        }

		        });
		wrappedPopup.addItem(miClearTrace);
	}

	private void addShowAllObjAndStandView() {
		String img;

		if (app.isUnbundled() || app.isWhiteboardActive()) {
			img = MaterialDesignResources.INSTANCE.show_all_objects_black()
					.getSafeUri().asString();
		} else {
			img = AppResources.INSTANCE.empty().getSafeUri().asString();
		}

		MenuItem miShowAllObjectsView = new MenuItem(MainMenu.getMenuBarHtml(img, loc.getMenu("ShowAllObjects")), true,
				new Command() {

			        @Override
					public void execute() {
				        setViewShowAllObject();
			        }

		        });

		String img2;

		if (app.isUnbundled() || app.isWhiteboardActive()) {
			img2 = MaterialDesignResources.INSTANCE.home_black().getSafeUri().asString();
		} else {
			img2 = AppResources.INSTANCE.empty().getSafeUri().asString();
		}

		MenuItem miStandardView = new MenuItem(MainMenu.getMenuBarHtml(img2, loc.getMenu("StandardView")), true,
				new Command() {

			        @Override
					public void execute() {
				        setStandardView();
			        }
		        });


		if (!hasWhiteboardContextMenu()) {

			addAxesAndGridCheckBoxes();

			addNavigationBar();

			RadioButtonMenuBar yaxisMenu = new RadioButtonMenuBarW(
					(AppW) this.app, false);
			addAxesRatioItems(yaxisMenu);

			MenuItem mi = new MenuItem(
					loc.getMenu("xAxis") + " : " + loc.getMenu("yAxis"), true,
					(MenuBar) yaxisMenu);

			mi.addStyleName("mi_no_image_new");

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

		if (!app.getActiveEuclidianView().isZoomable()) {
			miShowAllObjectsView.setEnabled(false);
			miStandardView.setEnabled(false);
		}

		if (!app.isUnbundled() && !app.isWhiteboardActive()) {
			addZoomMenu();
		}

		wrappedPopup.addItem(miShowAllObjectsView);
		if (!app.isUnbundled() && !app.isWhiteboardActive()) {
			wrappedPopup.addItem(miStandardView);
		}
	}

	private void addGridMenuItem() {
		String htmlString = MainMenu
				.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.grid_black()
								.getSafeUri().asString(),
						loc.getMenu("ShowGrid"));
		final GCollapseMenuItem ci = new GCollapseMenuItem(htmlString,
				MaterialDesignResources.INSTANCE.expand_black().getSafeUri()
						.asString(),
				MaterialDesignResources.INSTANCE.collapse_black().getSafeUri()
						.asString(),
				false, null);
		wrappedPopup.addItem(ci.getMenuItem(), false);
		GridSubmenu gridSubMenu = new GridSubmenu(ci);
		gridSubMenu.update();

	}

	private void addSnapToGridMenuItem() {
		String img = MaterialDesignResources.INSTANCE.snap_to_grid()
				.getSafeUri().asString();

		final GCheckmarkMenuItem snapToGrid = new GCheckmarkMenuItem(
				MainMenu.getMenuBarHtml(img, loc.getMenu("SnapToGrid")),
				MaterialDesignResources.INSTANCE.check_black().getSafeUri()
						.asString(),
				app.getSettings().getEuclidian(1)
						.getPointCapturingMode() == EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC);
		snapToGrid.setCommand(new Command() {
			@Override
			public void execute() {
				boolean isSnapToGrid = app.getSettings().getEuclidian(1)
						.getPointCapturingMode() == EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC;
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
			}
		});
		wrappedPopup.addItem(snapToGrid);
	}

	private void addAxesMenuItem() {
		String img = MaterialDesignResources.INSTANCE.axes_black()
					.getSafeUri().asString();

		final GCheckmarkMenuItem showAxes = new GCheckmarkMenuItem(
				MainMenu.getMenuBarHtml(img, loc.getMenu("ShowAxes")),
				MaterialDesignResources.INSTANCE.check_black().getSafeUri()
						.asString(),
				app.getSettings().getEuclidian(1).getShowAxis(0)
						&& app.getSettings().getEuclidian(1).getShowAxis(1));
		showAxes.setCommand(new Command() {

			@Override
			public void execute() {
				boolean axisShown = app.getSettings().getEuclidian(1)
						.getShowAxis(0)
						&& app.getSettings().getEuclidian(1).getShowAxis(1);
				app.getSettings().getEuclidian(1).setShowAxes(!axisShown);
				app.getActiveEuclidianView().setShowAxis(!axisShown);
				showAxes.setChecked(!axisShown);
				app.getActiveEuclidianView().repaintView();
			}
		});
		wrappedPopup.addItem(showAxes);
	}

	void toggleShowConstructionProtocolNavigation() {
		((AppW) app).toggleShowConstructionProtocolNavigation(app
				.getActiveEuclidianView().getViewID());
	}

	protected void addMiProperties(String name, final OptionType type) {

		String img;

		if (app.isUnbundled() || hasWhiteboardContextMenu()) {
			img = MaterialDesignResources.INSTANCE.gere().getSafeUri()
					.asString();
		} else {
			img = AppResources.INSTANCE.view_properties16().getSafeUri().asString();
		}

		MenuItem miProperties = new MenuItem(MainMenu.getMenuBarHtml(img,
				app.isUnbundled() || hasWhiteboardContextMenu()
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

	protected void showOptionsDialog(OptionType type) {
		if (app.getGuiManager() != null) {
			app.getDialogManager().showPropertiesDialog(type, null);
		}
	}

	protected void setStandardView() {
		app.setStandardView();
	}

	public void setViewShowAllObject() {
		app.setViewShowAllObjects(false);
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
		        && !Kernel.isEqual(Double.parseDouble(actionCommands[selPos]),
		                scaleRatio)) {
			selPos++;
		}

		menu.addRadioButtonMenuItems(this, items, actionCommands, selPos, false);

	}

	protected void zoomYaxis(double axesRatio) {
		app.zoomAxesRatio(axesRatio);
	}

	protected void addZoomMenu() {
		// zoom for both axes
		MenuBar zoomMenu = new MenuBar(true);

		String img;

		if (app.isUnbundled() || hasWhiteboardContextMenu()) {
			img = MaterialDesignResources.INSTANCE.zoom_in_black().getSafeUri()
					.asString();
		} else {
			img = AppResources.INSTANCE.zoom16().getSafeUri().asString();
		}

		MenuItem zoomMenuItem = new MenuItem(
				MainMenu.getMenuBarHtml(img,
				loc.getMenu("Zoom")), true, zoomMenu);
		if (!hasWhiteboardContextMenu()) {
			zoomMenuItem.addStyleName("mi_with_image");
		}

		wrappedPopup.addItem(zoomMenuItem);
		addZoomItems(zoomMenu);

		if (!app.getActiveEuclidianView().isZoomable()) {
			zoomMenuItem.setEnabled(false);
		}

	}

	private void addZoomItems(MenuBar menu) {
		int perc;

		MenuItem mi;
		boolean separatorAdded = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < zoomFactors.length; i++) {
			perc = (int) (zoomFactors[i] * 100.0);
			// build text like "125%" or "75%"
			sb.setLength(0);

			if ((perc <= 100) && (!separatorAdded)) {
				if (!app.isUnbundled() && !hasWhiteboardContextMenu()) {
					menu.addSeparator();
				}
				separatorAdded = true;
			}

			sb.append(perc);
			sb.append('%');
			final int index = i;
			// TODO: it is terrible, should be used ONE listener for each
			// menuItem, this kills the memory, if GWT changes this
			// get it right!
			mi = new MenuItem(sb.toString(), new Command() {

				@Override
				public void execute() {
					zoom(zoomFactors[index]);
				}
			});
			menu.addItem(mi);
			if (app.isUnbundled() || hasWhiteboardContextMenu()) {
				mi.addStyleName("no-image");
			}
		}

	}

	protected void zoom(double zoomFactor) {
		app.zoom(px, py, zoomFactor);
	}

	protected void addAxesAndGridCheckBoxes() {

		if (app.getGuiManager() == null) {
			return;
		}

		String img;
		if (hasWhiteboardContextMenu()) {
			img = AppResources.INSTANCE.axes20().getSafeUri().asString();
		} else {
			img = StyleBarResources.INSTANCE.axes().getSafeUri().asString();
		}

		String htmlString = MainMenu.getMenuBarHtml(img, loc.getMenu("Axes"));

		if (!app.isUnbundled() && !hasWhiteboardContextMenu()) {
			GCheckBoxMenuItem cbMenuItem = new GCheckBoxMenuItem(htmlString,
					((AppW) app).getGuiManager().getShowAxesAction(), true,
					app);
			cbMenuItem.setSelected(app.getActiveEuclidianView().getShowXaxis()
					&& (app.getActiveEuclidianView().getShowYaxis()));
			wrappedPopup.addItem(cbMenuItem);
		} else {
			GCheckmarkMenuItem checkmarkMenuItem = new GCheckmarkMenuItem(
				htmlString,
				MaterialDesignResources.INSTANCE.check_black().getSafeUri()
						.asString(),
				true, ((AppW) app).getGuiManager().getShowAxesAction());

			checkmarkMenuItem
					.setChecked(app.getActiveEuclidianView().getShowXaxis()
		        && (app.getActiveEuclidianView().getShowYaxis()));

			wrappedPopup.addItem(checkmarkMenuItem.getMenuItem());
		}

		String img2;
		if (hasWhiteboardContextMenu()) {
			img2 = AppResources.INSTANCE.grid20().getSafeUri().asString();
		} else {
			img2 = StyleBarResources.INSTANCE.grid().getSafeUri().asString();
		}

		htmlString = MainMenu.getMenuBarHtml(img2, loc.getMenu("Grid"));
		if (!app.isUnbundled() && !hasWhiteboardContextMenu()) {
			GCheckBoxMenuItem cbShowGrid = new GCheckBoxMenuItem(htmlString,
				((AppW) app).getGuiManager().getShowGridAction(), true, app);
			cbShowGrid.setSelected(app.getActiveEuclidianView().getShowGrid());
			wrappedPopup.addItem(cbShowGrid);
		} else {
			GCheckmarkMenuItem checkmarkMenuItem = new GCheckmarkMenuItem(
					htmlString,
					MaterialDesignResources.INSTANCE.check_black().getSafeUri()
							.asString(),
					true, ((AppW) app).getGuiManager().getShowGridAction());

			checkmarkMenuItem
					.setChecked(app.getActiveEuclidianView().getShowGrid());
			wrappedPopup.addItem(checkmarkMenuItem.getMenuItem());
		}

	}

	protected void addNavigationBar() {
		if (app.isUnbundled() || hasWhiteboardContextMenu()) {
			return;
		}
		// Show construction protocol navigation bar checkbox item
		Command showConstructionStepCommand = new Command() {
			@Override
			public void execute() {
				toggleShowConstructionProtocolNavigation();
			}
		};
		String htmlString = MainMenu.getMenuBarHtml(AppResources.INSTANCE
				.empty().getSafeUri().asString(), loc.getMenu("NavigationBar"));
		GCheckBoxMenuItem cbShowConstructionStep = new GCheckBoxMenuItem(
				htmlString, showConstructionStepCommand, true, app);
		cbShowConstructionStep.setSelected(app.showConsProtNavigation(app
				.getActiveEuclidianView().getViewID()));
		wrappedPopup.addItem(cbShowConstructionStep);

		wrappedPopup.addSeparator();
	}

	@Override
	public void actionPerformed(String command) {
		try {
			// zoomYaxis(Double.parseDouble(e.getActionCommand()));
			zoomYaxis(Double.parseDouble(command));
		} catch (Exception ex) {
		}
	}

	@Override
	protected void updateEditItems() {
		if (!hasWhiteboardContextMenu()) {
			return;
		}

		updatePasteItem();
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
			super(wrappedPopup, parentMenu);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void initActions() {
			addNoGridItem();
			addMajorGridlines();
			addMajorMinorGridlines();
			addPolar();
			addIsometric();
		}

		private void addIsometric() {
			String text = app.getLocalization().getMenu("Isometric");
			boolean isSelected = app.getSettings().getEuclidian(1)
					.getGridType() == EuclidianView.GRID_ISOMETRIC
					&& app.getSettings().getEuclidian(1).getShowGrid();
			addItem(text, isSelected, new Command() {

				@Override
				public void execute() {
					app.getSettings().getEuclidian(1).setShowGridSetting(true);
					app.getSettings().getEuclidian(1)
							.setGridType(EuclidianView.GRID_ISOMETRIC);
					app.getActiveEuclidianView()
							.setGridType(EuclidianView.GRID_ISOMETRIC);
					app.getActiveEuclidianView().repaintView();
				}
			});
		}

		private void addPolar() {
			String text = app.getLocalization().getMenu("Polar");
			boolean isSelected = app.getSettings().getEuclidian(1)
					.getGridType() == EuclidianView.GRID_POLAR
					&& app.getSettings().getEuclidian(1).getShowGrid();
			addItem(text, isSelected, new Command() {

				@Override
				public void execute() {
					app.getSettings().getEuclidian(1).setShowGridSetting(true);
					app.getSettings().getEuclidian(1)
							.setGridType(EuclidianView.GRID_POLAR);
					app.getActiveEuclidianView()
							.setGridType(EuclidianView.GRID_POLAR);
					app.getActiveEuclidianView().repaintView();
				}
			});
		}

		private void addMajorMinorGridlines() {
			String text = app.getLocalization().getMenu("Grid.MajorAndMinor");
			boolean isSelected = app.getSettings().getEuclidian(1)
					.getGridType() == EuclidianView.GRID_CARTESIAN_WITH_SUBGRID
					&& app.getSettings().getEuclidian(1).getShowGrid();
			addItem(text, isSelected, new Command() {

				@Override
				public void execute() {
					app.getSettings().getEuclidian(1).setShowGridSetting(true);
					app.getSettings().getEuclidian(1)
							.setGridType(EuclidianView.GRID_CARTESIAN_WITH_SUBGRID);
					app.getActiveEuclidianView().showGrid(true);
					app.getActiveEuclidianView()
							.setGridType(EuclidianView.GRID_CARTESIAN_WITH_SUBGRID);
					app.getActiveEuclidianView().repaintView();
				}
			});
		}

		private void addMajorGridlines() {
			String text = app.getLocalization().getMenu("Grid.Major");
			boolean isSelected = app.getSettings().getEuclidian(1)
					.getGridType() == EuclidianView.GRID_CARTESIAN
					&& app.getSettings().getEuclidian(1).getShowGrid();
			addItem(text, isSelected, new Command() {

				@Override
				public void execute() {
					app.getSettings().getEuclidian(1).setShowGridSetting(true);
					app.getSettings().getEuclidian(1)
							.setGridType(EuclidianView.GRID_CARTESIAN);
					app.getActiveEuclidianView()
							.setGridType(EuclidianView.GRID_CARTESIAN);
					app.getActiveEuclidianView().repaintView();
				}
			});
		}

		private void addNoGridItem() {
			String text = app.getLocalization().getMenu("Grid.No");
			boolean isSelected = !app.getSettings().getEuclidian(1)
					.getShowGrid();
			addItem(text, isSelected, new Command() {

				@Override
				public void execute() {
					app.getSettings().getEuclidian(1)
							.setGridType(EuclidianView.GRID_NOT_SHOWN);
					app.getActiveEuclidianView()
							.setGridType(EuclidianView.GRID_NOT_SHOWN);
					app.getSettings().getEuclidian(1).setShowGridSetting(false);
					app.getActiveEuclidianView().repaintView();

				}
			});
		}


		@Override
		public void update() {
			// do nothing now
		}

	}

}
