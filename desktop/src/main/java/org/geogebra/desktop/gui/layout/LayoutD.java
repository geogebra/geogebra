package org.geogebra.desktop.gui.layout;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

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
import org.geogebra.common.util.debug.Log;
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

		this.perspectives = new ArrayList<Perspective>(
				getDefaultPerspectivesLength());
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
	 * @param dockPanel
	 */
	public void registerPanel(DockPanelD dockPanel) {
		dockManager.registerPanel(dockPanel);
	}

	/**
	 * Apply a new perspective.
	 * 
	 * TODO consider applet parameters
	 * 
	 * @param perspective
	 */
	@Override
	public boolean applyPerspective(Perspective perspective) {

		// ignore axes & grid settings for the document perspective

		app.getGuiManager()
				.setToolBarDefinition(perspective.getToolbarDefinition());

		app.setShowToolBarNoUpdate(perspective.getShowToolBar());
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
	 * Apply a new perspective using its id.
	 * 
	 * This is a wrapper for #applyPerspective(Perspective) to simplify the
	 * loading of default perspectives by name.
	 * 
	 * @param id
	 *            The ID of the perspective. For default perspectives the
	 *            hard-coded ID is used, ie the translation key, for all other
	 *            perspectives the ID chosen by the user is used.
	 * @throws IllegalArgumentException
	 *             If no perspective with the given name could be found.
	 */
	@Override
	public void applyPerspective(String id) throws IllegalArgumentException {
		Perspective perspective = getPerspective(id);

		if (perspective != null) {
			applyPerspective(perspective);
		} else {
			throw new IllegalArgumentException(
					"Could not find perspective with the given name.");
		}
	}

	/**
	 * Create a perspective for the current layout.
	 * 
	 * @param id
	 * @return a perspective for the current layout.
	 */
	@Override
	public Perspective createPerspective(String id) {
		if (app == null || dockManager.getRoot() == null) {
			return null;
		}

		// return the default perspective in case we're creating new preferences
		// of
		// a virgin application.
		EuclidianView ev = app.getEuclidianView1();
		Perspective perspective = new Perspective(id);

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
		Arrays.sort(dockPanelInfo, new Comparator<DockPanelData>() {
			@Override
			public int compare(DockPanelData o1, DockPanelData o2) {
				int diff = o2.getEmbeddedDef().length()
						- o1.getEmbeddedDef().length();
				return diff;
			}
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
	 * Get all current perspectives as array.
	 * 
	 * @return all current perspectives as array.
	 */
	public Perspective[] getPerspectives() {
		Perspective[] array = new Perspective[perspectives.size()];
		return perspectives.toArray(array);
	}

	/**
	 * @param index
	 * @return perspective at given index
	 */
	public Perspective getPerspective(int index) {
		if (index >= perspectives.size()) {
			throw new IndexOutOfBoundsException();
		}

		return perspectives.get(index);
	}

	/**
	 * @param id
	 *            name of the perspective
	 * @return perspective with 'id' as name or null
	 */
	public Perspective getPerspective(String id) {
		for (int i = 0; i < getDefaultPerspectivesLength(); ++i) {
			if (id.equals(getDefaultPerspectives(i).getId())) {
				return getDefaultPerspectives(i);
			}
		}

		for (Perspective perspective : perspectives) {
			if (id.equals(perspective.getId())) {
				return perspective;
			}
		}

		return null;
	}

	/**
	 * Add a new perspective to the list of available perspectives.
	 * 
	 * @param perspective
	 */
	public void addPerspective(Perspective perspective) {
		perspectives.add(perspective);
	}

	/**
	 * Remove a perspective identified by the object.
	 * 
	 * @param perspective
	 */
	public void removePerspective(Perspective perspective) {
		if (perspectives.contains(perspective)) {
			perspectives.remove(perspective);
		}
	}

	/**
	 * Remove a perspective identified by the index.
	 * 
	 * @param index
	 */
	public void removePerspective(int index) {
		if (index >= 0 && index < perspectives.size()) {
			perspectives.remove(index);
		} else {
			Log.debug("Invalid perspective index: " + index);
		}
	}

	/**
	 * Checks if the given component is in an external window. Used for key
	 * dispatching.
	 * 
	 * @param component
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
	 * @param viewId
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
		if (dockManager == null) {
			return null;
		}

		return dockManager.getRoot();
	}

	/**
	 * Show the prompt which is used to save the current perspective.
	 */
	public void showSaveDialog() {
		// unused
	}

}
