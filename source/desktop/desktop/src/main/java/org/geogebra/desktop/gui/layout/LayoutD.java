/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.gui.layout;

import java.awt.Component;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.main.AppD;

/**
 * Manage layout related stuff.
 * 
 * @author Florian Sonner
 */
public class LayoutD extends Layout implements SettingListener {

	private AppD app;
	private DockManagerD dockManager;

	/**
	 * {@link #initialize(AppD)} has to be called once in order to use this
	 * class.
	 */
	public LayoutD(App app) {
		initializeDefaultPerspectives(app, 0.25);
	}

	/**
	 * Initialize the layout component.
	 * 
	 * @param appd
	 *            Application
	 */
	public void initialize(AppD appd) {
		if (!initializeCommon(appd)) {
			return;
		}

		this.app = appd;

		this.dockManager = new DockManagerD(this);
	}

	/**
	 * Add a new dock panel to the list of known panels.
	 * 
	 * Attention: This method has to be called as early as possible in the
	 * application life cycle (e.g. before loading a file, before constructing
	 * the ViewMenu).
	 * 
	 * @param dockPanel dock panel
	 */
	public void registerPanel(DockPanelD dockPanel) {
		dockManager.registerPanel(dockPanel);
	}

	/**
	 * Apply a new perspective.
	 * 
	 * @param perspective perspective
	 */
	@Override
	public boolean applyPerspective(Perspective perspective) {

		// ignore axes & grid settings for the document perspective

		app.getGuiManager()
				.setToolBarDefinition(perspective.getToolbarDefinition());

		app.setShowToolBar(perspective.getShowToolBar());
		app.setShowToolBarHelpNoUpdate(perspective.getShowToolBarHelp());
		app.setToolbarPosition(perspective.getToolBarPosition(), false);

		app.setShowAlgebraInput(perspective.getShowInputPanel(), false);
		app.setInputPosition(perspective.getInputPosition(), false);

		app.setDockBarEast(perspective.isDockBarEast());
		app.setShowDockBar(perspective.getShowDockBar(), false);

		// change the dock panel layout
		dockManager.applyPerspective(perspective.getSplitPaneData(),
				perspective.getDockPanelData());

		// apply ev settings after focus on view
		boolean changed = setEVsettingsFromPerspective(app, perspective);

		if (!app.isIniting()) {
			app.updateToolBar();
			app.updateMenubar();
			app.updateContentPane();
		}

		app.dispatchEvent(new Event(EventType.PERSPECTIVE_CHANGE));
		return changed;
	}

	/**
	 * Create a perspective for the current layout.
	 *
	 * @return a perspective for the current layout.
	 */
	@Override
	public Perspective createPerspective() {
		if (app == null || dockManager.getRoot() == null) {
			return null;
		}

		// return the default perspective in case we're creating new preferences
		// of
		// a virgin application.
		EuclidianView ev = app.getEuclidianView1();
		Perspective perspective = new Perspective();

		// get the information about the split panes
		DockSplitPane.TreeReader spTreeReader = new DockSplitPane.TreeReader(
				app);
		perspective
				.setSplitPaneData(spTreeReader.getInfo(dockManager.getRoot()));

		// get the information about the dock panels
		DockPanelD[] panels = dockManager.getPanels();
		DockPanelData[] dockPanelInfo = new DockPanelData[panels.length];

		for (int i = 0; i < panels.length; ++i) {
			// just the width of the panels isn't updated every time the panel
			// is updated, so we have to take care of this by ourself
			if (!panels[i].isOpenInFrame() && panels[i].isVisible()) {
				DockSplitPane parent = panels[i].getParentSplitPane();
				if (parent != null) {
					if (parent
							.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
						panels[i].setEmbeddedSize(panels[i].getWidth());
					} else {
						panels[i].setEmbeddedSize(panels[i].getHeight());
					}
				}
				panels[i].setEmbeddedDef(panels[i].calculateEmbeddedDef());
			}
			dockPanelInfo[i] = panels[i].createInfo();
		}

		// Sort the dock panels as the entries with the smallest amount of
		// definition should
		// be read first by the loading algorithm.
		Arrays.sort(dockPanelInfo, (o1, o2) -> {
			int diff = o2.getEmbeddedDef().length()
					- o1.getEmbeddedDef().length();
			return diff;
		});

		perspective.setDockPanelData(dockPanelInfo);

		perspective.setToolbarDefinition(
				((GuiManagerD) app.getGuiManager()).getToolbarDefinition());
		perspective.setShowToolBar(app.showToolBar());
		perspective.setShowAxes(ev.getShowXaxis() && ev.getShowYaxis());
		perspective.setShowGrid(ev.getShowGrid());
		perspective.setShowInputPanel(app.showAlgebraInput());
		perspective.setShowInputPanelCommands(app.showInputHelpToggle());
		perspective.setInputPosition(app.getInputPosition());

		perspective.setToolBarPosition(app.getToolbarPosition());
		perspective.setShowToolBarHelp(app.showToolBarHelp());
		perspective.setShowDockBar(app.isShowDockBar());
		perspective.setDockBarEast(app.isDockBarEast());

		return perspective;
	}

	/**
	 * Checks if the given component is in an external window. Used for key
	 * dispatching.
	 * 
	 * @param component component
	 * @return whether the given component is in an external window. Used for
	 *         key dispatching.
	 */
	public boolean inExternalWindow(Component component) {
		DockPanelD[] panels = dockManager.getPanels();

		for (int i = 0; i < panels.length; ++i) {
			if (panels[i].isOpenInFrame()) {
				if (component == SwingUtilities.getRootPane(panels[i])) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * @param viewId view ID
	 * @return If just the view associated to viewId is visible
	 */
	@Override
	public boolean isOnlyVisible(int viewId) {
		DockPanelD[] panels = dockManager.getPanels();
		boolean foundView = false;

		for (int i = 0; i < panels.length; ++i) {
			// check if the view is visible at all
			if (panels[i].getViewId() == viewId) {
				foundView = true;

				if (!panels[i].isVisible()) {
					return false;
				}
			}

			// abort if any other view is visible
			else {
				if (panels[i].isVisible()) {
					return false;
				}
			}
		}

		// if we reach this point each other view is invisible, but
		// if the view wasn't found at all we return false as well
		return foundView;
	}

	/**
	 * Layout settings changed.
	 */
	@Override
	public void settingsChanged(AbstractSettings abstractSettings) {
		dockManager.updatePanels();
	}

	/**
	 * @return The application object.
	 */
	public AppD getApplication() {
		return app;
	}

	/**
	 * @return The management class for the docking behavior.
	 */
	@Override
	public DockManagerD getDockManager() {
		return dockManager;
	}

	public JComponent getRootComponent() {
		return dockManager == null ? null : dockManager.getRoot();
	}

}
