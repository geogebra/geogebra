package org.geogebra.web.web.gui;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.gui.menubar.MyActionListener;
import org.geogebra.common.gui.menubar.RadioButtonMenuBar;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.StyleBarResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.gui.menubar.RadioButtonMenuBarW;
import org.geogebra.web.web.javax.swing.GCheckBoxMenuItem;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class ContextMenuGraphicsWindowW extends ContextMenuGeoElementW
        implements MyActionListener {

	protected double px;
	protected double py;

	protected ContextMenuGraphicsWindowW(AppW app) {
		super(app);
		if (isWhiteboard() && !app.has(Feature.NEW_TOOLBAR)) {
			wrappedPopup.getPopupPanel().addStyleName("contextMenu");
		} else if (app.has(Feature.NEW_TOOLBAR)) {
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




		String img;
		if (isWhiteboard() && !app.has(Feature.NEW_TOOLBAR)) {
			img = AppResources.INSTANCE.show_all_objects20().getSafeUri().asString();
		} else if (app.has(Feature.NEW_TOOLBAR)) {
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
		if (isWhiteboard() && !app.has(Feature.NEW_TOOLBAR)) {
			img2 = AppResources.INSTANCE.standard_view20().getSafeUri().asString();
		} else if (app.has(Feature.NEW_TOOLBAR)){
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


		if (isWhiteboard()) {
			addPasteItem();
		} else {
			addAxesAndGridCheckBoxes();

			addNavigationBar();

			RadioButtonMenuBar yaxisMenu = new RadioButtonMenuBarW(app, false);
			addAxesRatioItems(yaxisMenu);

			MenuItem mi = new MenuItem(
					loc.getMenu("xAxis") + " : " + loc.getMenu("yAxis"), true,
					(MenuBar) yaxisMenu);

			mi.addStyleName("mi_no_image_new");

			wrappedPopup.addItem(mi);

			if (!ev.isZoomable()) {
				yaxisMenu.setEnabled(false);
			}

			if (ev.isLockedAxesRatio()) {
				yaxisMenu.setEnabled(false);
			}

		}

		if (!ev.isZoomable()) {
			miShowAllObjectsView.setEnabled(false);
			miStandardView.setEnabled(false);
		}

		addZoomMenu();

		wrappedPopup.addItem(miShowAllObjectsView);
		wrappedPopup.addItem(miStandardView);
		addMiProperties("DrawingPad", ot);

	}

	void toggleShowConstructionProtocolNavigation() {
		((AppW) app).toggleShowConstructionProtocolNavigation(app
				.getActiveEuclidianView().getViewID());
	}

	protected void addMiProperties(String name, final OptionType type) {

		String img;
		if (isWhiteboard() && !app.has(Feature.NEW_TOOLBAR)) {
			img = AppResources.INSTANCE.properties20().getSafeUri().asString();
		} else if (app.has(Feature.NEW_TOOLBAR)) {
			img = MaterialDesignResources.INSTANCE.settings_black().getSafeUri()
					.asString();
		} else {
			img = AppResources.INSTANCE.view_properties16().getSafeUri().asString();
		}

		MenuItem miProperties = new MenuItem(MainMenu.getMenuBarHtml(img,
				loc.getMenu(name) + " ..."), true,
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
		if (isWhiteboard() && !app.has(Feature.NEW_TOOLBAR)) {
			img = AppResources.INSTANCE.zoom20().getSafeUri().asString();
		} else if (app.has(Feature.NEW_TOOLBAR)) {
			img = MaterialDesignResources.INSTANCE.zoom_in_black().getSafeUri()
					.asString();
		} else {
			img = AppResources.INSTANCE.zoom16().getSafeUri().asString();
		}

		MenuItem zoomMenuItem = new MenuItem(
				MainMenu.getMenuBarHtml(img,
				loc.getMenu("Zoom")), true, zoomMenu);
		if (!isWhiteboard()) {
			zoomMenuItem.addStyleName("mi_with_image");
		}
		wrappedPopup.addItem(zoomMenuItem);
		addZoomItems(zoomMenu);
		if (app.has(Feature.NEW_TOOLBAR)) {
			// zoomMenu.addStyleName("matMenu");
		}

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
				menu.addSeparator();
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
			if (app.has(Feature.NEW_TOOLBAR)) {
				menu.addStyleName("matMenu");
			}
		}

	}

	protected void zoom(double zoomFactor) {
		app.zoom(px, py, zoomFactor);
	}

	protected void addAxesAndGridCheckBoxes() {
		// MenuItem cbShowAxes =
		// addAction(((AppW)app).getGuiManager().getShowAxesAction(),
		// MainMenu.getMenuBarHtml(AppResources.INSTANCE.axes().getSafeUri().asString(),
		// loc.getMenu("Axes")), loc.getMenu("Axes"));
		// SafeHtml cbHtml =
		// SafeHtmlUtils.fromSafeConstant(MainMenu.getMenuBarHtml(AppResources.INSTANCE.axes().getSafeUri().asString(),
		// loc.getMenu("Axes")));

		if (app.getGuiManager() == null) {
			return;
		}

		String img;
		if (isWhiteboard()) {
			img = AppResources.INSTANCE.axes20().getSafeUri().asString();
		} else {
			img = StyleBarResources.INSTANCE.axes().getSafeUri().asString();
		}

		String htmlString = MainMenu.getMenuBarHtml(img, loc.getMenu("Axes"));
		GCheckBoxMenuItem cbShowAxes = new GCheckBoxMenuItem(htmlString,
				((AppW) app).getGuiManager().getShowAxesAction(), true, app);

		cbShowAxes.setSelected(app.getActiveEuclidianView().getShowXaxis()
		        && (app.getActiveEuclidianView().getShowYaxis()));

		wrappedPopup.addItem(cbShowAxes);

		// MenuItem cbShowGrid =
		// addAction(((AppW)app).getGuiManager().getShowGridAction(),
		// MainMenu.getMenuBarHtml(AppResources.INSTANCE.grid().getSafeUri().asString(),
		// loc.getMenu("Grid")), loc.getMenu("Grid"));

		String img2;
		if (isWhiteboard()) {
			img2 = AppResources.INSTANCE.grid20().getSafeUri().asString();
		} else {
			img2 = StyleBarResources.INSTANCE.grid().getSafeUri().asString();
		}

		htmlString = MainMenu.getMenuBarHtml(img2, loc.getMenu("Grid"));
		GCheckBoxMenuItem cbShowGrid = new GCheckBoxMenuItem(htmlString,
				((AppW) app).getGuiManager().getShowGridAction(), true, app);
		cbShowGrid.setSelected(app.getActiveEuclidianView().getShowGrid());
		wrappedPopup.addItem(cbShowGrid);

	}

	protected void addNavigationBar() {
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
		if (!isWhiteboard()) {
			return;
		}

		updatePasteItem();
	}

}
