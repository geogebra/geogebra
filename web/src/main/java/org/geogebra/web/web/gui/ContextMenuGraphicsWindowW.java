package org.geogebra.web.web.gui;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.gui.menubar.MyActionListener;
import org.geogebra.common.gui.menubar.RadioButtonMenuBar;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.StyleBarResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
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
	}

	public ContextMenuGraphicsWindowW(AppW app, double px, double py) {
		this(app);

		this.px = px;
		this.py = py;

		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		if (ev.getEuclidianViewNo() == 2) {
			setTitle(app.getPlain("DrawingPad2"));
		} else {
			setTitle(app.getPlain("DrawingPad"));
		}

		addAxesAndGridCheckBoxes();

		addNavigationBar();



		// zoom for both axes
		MenuBar zoomMenu = new MenuBar(true);
		MenuItem zoomMenuItem = new MenuItem(MainMenu.getMenuBarHtml(
		        AppResources.INSTANCE.zoom16().getSafeUri().asString(),
		        app.getMenu("Zoom")), true, zoomMenu);
		zoomMenuItem.addStyleName("mi_with_image");
		wrappedPopup.addItem(zoomMenuItem);
		addZoomItems(zoomMenu);

		RadioButtonMenuBar yaxisMenu = new org.geogebra.web.web.gui.menubar.RadioButtonMenuBarW(
		        app, false);
		addAxesRatioItems(yaxisMenu);

		MenuItem mi = new MenuItem(app.getPlain("xAxis") + " : "
		        + app.getPlain("yAxis"), true, (MenuBar) yaxisMenu);
		mi.addStyleName("mi_no_image");
		wrappedPopup.addItem(mi);

		MenuItem miShowAllObjectsView = new MenuItem(
		        app.getPlain("ShowAllObjects"), new Command() {

			        public void execute() {
				        setViewShowAllObject();
			        }

		        });
		miShowAllObjectsView.addStyleName("mi_no_image");
		wrappedPopup.addItem(miShowAllObjectsView);

		MenuItem miStandardView = new MenuItem(app.getPlain("StandardView"),
		        new Command() {

			        public void execute() {
				        setStandardView();
			        }
		        });
		miStandardView.addStyleName("mi_no_image");
		wrappedPopup.addItem(miStandardView);

		if (!ev.isZoomable()) {
			zoomMenuItem.setEnabled(false);
			((MenuItem) yaxisMenu).setEnabled(false);
			miShowAllObjectsView.setEnabled(false);
			miStandardView.setEnabled(false);
		}

		if (ev.isLockedAxesRatio()) {
			yaxisMenu.setEnabled(false);
		}

		addMiProperties("DrawingPad");

	}

	void toggleShowConstructionProtocolNavigation() {
		((AppW) app).toggleShowConstructionProtocolNavigation(app
				.getActiveEuclidianView().getViewID());
	}

	protected void addMiProperties(String name) {
		MenuItem miProperties = new MenuItem(MainMenu.getMenuBarHtml(
		        AppResources.INSTANCE.view_properties16().getSafeUri()
		                .asString(), app.getPlain(name) + " ..."), true,
		        new Command() {

			        public void execute() {
				        showOptionsDialog();
			        }
		        });
		miProperties.setEnabled(true); // TMP AG
		wrappedPopup.addItem(miProperties);
	}

	protected void showOptionsDialog() {
		if (app.getGuiManager() != null) {
			app.getGuiManager().setShowView(true, App.VIEW_PROPERTIES);
		}
	}

	protected void setStandardView() {
		app.setStandardView();
	}

	public void setViewShowAllObject() {
		app.setViewShowAllObjects();
	}

	private void addAxesRatioItems(RadioButtonMenuBar menu) {

		double scaleRatio = ((EuclidianView) app.getActiveEuclidianView())
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

	private void addZoomItems(MenuBar menu) {
		int perc;

		MenuItem mi;
		boolean separatorAdded = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < zoomFactors.length; i++) {
			perc = (int) (zoomFactors[i] * 100.0);
			// build text like "125%" or "75%"
			sb.setLength(0);
			if (perc > 100) {

			} else {
				if (!separatorAdded) {
					menu.addSeparator();
					separatorAdded = true;
				}
			}
			sb.append(perc);
			sb.append('%');
			final int index = i;
			// TODO: it is terrible, should be used ONE listener for each
			// menuItem, this kills the memory, if GWT changes this
			// get it right!
			mi = new MenuItem(sb.toString(), new Command() {

				public void execute() {
					zoom(zoomFactors[index]);
				}
			});
			menu.addItem(mi);
		}

	}

	private void zoom(double zoomFactor) {
		app.zoom(px, py, zoomFactor);
	}

	protected void addAxesAndGridCheckBoxes() {
		// MenuItem cbShowAxes =
		// addAction(((AppW)app).getGuiManager().getShowAxesAction(),
		// MainMenu.getMenuBarHtml(AppResources.INSTANCE.axes().getSafeUri().asString(),
		// app.getMenu("Axes")), app.getMenu("Axes"));
		// SafeHtml cbHtml =
		// SafeHtmlUtils.fromSafeConstant(MainMenu.getMenuBarHtml(AppResources.INSTANCE.axes().getSafeUri().asString(),
		// app.getMenu("Axes")));

		if (app.getGuiManager() == null)
			return;

		String htmlString = MainMenu.getMenuBarHtml(StyleBarResources.INSTANCE
		        .axes().getSafeUri().asString(), app.getMenu("Axes"));
		GCheckBoxMenuItem cbShowAxes = new GCheckBoxMenuItem(htmlString,
				((AppW) app).getGuiManager().getShowAxesAction(), true);

		cbShowAxes.setSelected(app.getActiveEuclidianView().getShowXaxis()
		        && (app.getActiveEuclidianView().getShowYaxis()));

		wrappedPopup.addItem(cbShowAxes);

		// MenuItem cbShowGrid =
		// addAction(((AppW)app).getGuiManager().getShowGridAction(),
		// MainMenu.getMenuBarHtml(AppResources.INSTANCE.grid().getSafeUri().asString(),
		// app.getMenu("Grid")), app.getMenu("Grid"));
		htmlString = MainMenu.getMenuBarHtml(StyleBarResources.INSTANCE.grid()
		        .getSafeUri().asString(), app.getMenu("Grid"));
		GCheckBoxMenuItem cbShowGrid = new GCheckBoxMenuItem(htmlString,
				((AppW) app).getGuiManager().getShowGridAction(), true);
		cbShowGrid.setSelected(app.getActiveEuclidianView().getShowGrid());
		wrappedPopup.addItem(cbShowGrid);

	}

	protected void addNavigationBar() {
		// Show construction protocol navigation bar checkbox item
		Command showConstructionStepCommand = new Command() {
			public void execute() {
				toggleShowConstructionProtocolNavigation();
			}
		};
		String htmlString = MainMenu.getMenuBarHtml(AppResources.INSTANCE
				.empty().getSafeUri().asString(), app.getMenu("NavigationBar"));
		GCheckBoxMenuItem cbShowConstructionStep = new GCheckBoxMenuItem(
				htmlString, showConstructionStepCommand, true);
		cbShowConstructionStep.setSelected(app.showConsProtNavigation(app
				.getActiveEuclidianView().getViewID()));
		wrappedPopup.addItem(cbShowConstructionStep);

		wrappedPopup.addSeparator();
	}

	public void actionPerformed(String command) {
		try {
			// zoomYaxis(Double.parseDouble(e.getActionCommand()));
			zoomYaxis(Double.parseDouble(command));
		} catch (Exception ex) {
		}
	}

}
