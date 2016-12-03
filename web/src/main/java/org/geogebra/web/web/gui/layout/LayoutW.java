package org.geogebra.web.web.gui.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.util.LaTeXHelper;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Widget;

public class LayoutW extends Layout implements SettingListener {
	
	private boolean isInitialized = false;
	
	private AppW app;

	private DockManagerW dockManager;
	
	/**
	 * instantiates layout for Web
	 */
	public LayoutW(App app) {
		initializeDefaultPerspectives(app, 0.2);

		this.perspectives = new ArrayList<Perspective>(
				getDefaultPerspectivesLength());
	}

	/**
	 * Initialize the layout component.
	 * 
	 * @param app
	 */
	public void initialize(AppW appw) {
		if(isInitialized)
			return;
		
		isInitialized = true;
		
		this.app = appw;
		this.settings = appw.getSettings().getLayout();
		this.settings.addListener(this);
		this.dockManager = new DockManagerW(this);

		// change inputPosition to default inputPosition
		for (int i = 0; i < Layout.getDefaultPerspectivesLength(); i++) {
			Perspective p = Layout.getDefaultPerspectives(i);
			if (p != null) {
				p.setInputPosition(appw.getInputPosition());
			}
		}
	}

		
	/**
	 * Add a new dock panel to the list of known panels.
	 * 
	 * Attention: This method has to be called as early as possible in the application
	 * life cycle (e.g. before loading a file, before constructing the ViewMenu). 
	 * 
	 * @param dockPanel
	 */
	public void registerPanel(DockPanelW dockPanel) {
		dockManager.registerPanel(dockPanel);
	}
	
	/* Many of this not implemented yet, later we can make it togehter */
	@Override
	public boolean applyPerspective(Perspective perspective) {
		// ignore axes & grid settings for the document perspective
		boolean changed = setEVsettingsFromPerspective(app, perspective);

		app.getGuiManager().setGeneralToolBarDefinition(
		        perspective.getToolbarDefinition());
		app.setToolbarPosition(perspective.getToolBarPosition(), false);
		// override the previous command with the data-param-customToolBar
		// setting
		if (!app.isFullAppGui() && app.isApplet()) {
			app.setCustomToolBar();

			app.setShowToolBarNoUpdate(
			/*
			 * perspective.getShowToolBar() &&
			 * app.getArticleElement().getDataParamShowToolBarDefaultTrue() ||
			 */
			app.getArticleElement().getDataParamShowToolBar(false));

			app.setShowAlgebraInput(
			/*
			 * perspective.getShowInputPanel() &&
			 * app.getArticleElement().getDataParamShowAlgebraInputDefaultTrue()
			 * ||
			 */
			app.getArticleElement().getDataParamShowAlgebraInput(false), false);

		}

		// app.setShowInputTop(perspective.getShowInputPanelOnTop(), false);
		if (((LaTeXHelper) GWT.create(LaTeXHelper.class)).supportsAV()
				|| app.has(Feature.RETEX_EDITOR)) {
			app.setInputPosition(app.getArticleElement()
					.getAlgebraPosition(perspective.getInputPosition()), false);
		} else {
			app.setInputPosition(InputPosition.bottom, false);
		}
		
		String toolbar3D = "";

		// change the dock panel layout
		app.setKeyboardNeeded(false);
		for (DockPanelData dp : perspective.getDockPanelData()) {
			if (dp.isVisible()
					&& (dp.getViewId() == App.VIEW_ALGEBRA || dp.getViewId() == App.VIEW_CAS)) {
				app.setKeyboardNeeded(true);
			}

			if (dp.getViewId() == App.VIEW_EUCLIDIAN3D) {
				toolbar3D = dp.getToolbarString();
				// Log.debug("TADAM " + toolbar3D);
			}
		}
		dockManager.applyPerspective(perspective.getSplitPaneData(),
		        perspective.getDockPanelData());


		app.setMacroViewIds(toolbar3D);

		if (!app.isIniting()) {
			app.updateToolBar();
			app.updateMenubar();
			app.updateContentPane();
			app.getGuiManager().refreshCustomToolsInToolBar();
			app.updateToolBar();
		} else if (app.showAlgebraInput()
		        && app.getInputPosition() != InputPosition.algebraView) {
			app.updateContentPane();
		}


		app.dispatchEvent(new Event(EventType.PERSPECTIVE_CHANGE, null));
		return changed;
		// old behaviour: just updating center, instead of updateContentPane
		// app.refreshSplitLayoutPanel();

	}

	
	/**
	 * Apply a new perspective using its id. 
	 * 
	 * This is a wrapper for #applyPerspective(Perspective) to simplify the loading of default
	 * perspectives by name. 
	 * 
	 * @param id The ID of the perspective. For default perspectives the hard-coded ID is used, ie
	 * 			 the translation key, for all other perspectives the ID chosen by the user is
	 * 			 used.
	 * @throws IllegalArgumentException If no perspective with the given name could be found.
	 */
	public void applyPerspective(String id) throws IllegalArgumentException {
		Perspective perspective = getPerspective(id);
		
		if(perspective != null) {
			applyPerspective(perspective);
		} else {
			throw new IllegalArgumentException("Could not find perspective with the given name.");
		}		
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
		if(index >= perspectives.size())
			throw new IndexOutOfBoundsException();
		
		return perspectives.get(index);
	}
	
	/**
	 * @param id name of the perspective
	 * @return perspective with 'id' as name or null 
	 */
	public Perspective getPerspective(String id) {
		for (int i = 0; i < getDefaultPerspectivesLength(); ++i) {
			if (id.equals(getDefaultPerspectives(i).getId())) {
				return getDefaultPerspectives(i);
			}
		}
		
		for(Perspective perspective : perspectives) {
			if(id.equals(perspective.getId())) {
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
		if(perspectives.contains(perspective)) {
			perspectives.remove(perspective);
		}
	}
	
	/**
	 * Remove a perspective identified by the index.
	 * 
	 * @param index
	 */
	public void removePerspective(int index) {
		if(index >= 0 && index < perspectives.size()) {
			perspectives.remove(index);
		} else {
			Log.debug("Invalid perspective index: " + index);
		}
	}
	
	
	public void settingsChanged(AbstractSettings settings) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Create a perspective for the current layout.
	 * 
	 * @param id
	 * @return a perspective for the current layout.
	 */
	public Perspective createPerspective(String id) {
		if(app == null || dockManager.getRoot() == null)
			return null;
		
		// return the default perspective in case we're creating new preferences of
		// a virgin application.		
		EuclidianView ev = app.getEuclidianView1();
		Perspective perspective = new Perspective(id);

		// get the information about the split panes
		DockSplitPaneW.TreeReader spTreeReader = new DockSplitPaneW.TreeReader(
				app);
		perspective.setSplitPaneData(spTreeReader.getInfo(dockManager.getRoot()));

		// get the information about the dock panels
		DockPanelW[] panels = dockManager.getPanels();
		DockPanelData[] dockPanelInfo = new DockPanelData[panels.length];

		for (int i = 0; i < panels.length; ++i) {
			// just the width of the panels isn't updated every time the panel
			// is updated, so we have to take care of this by ourself
			if (!panels[i].isOpenInFrame() && panels[i].isVisible()) {
				DockSplitPaneW parent = panels[i].getParentSplitPane();
				if (parent.getOrientation() == SwingConstants.HORIZONTAL_SPLIT) {
					panels[i].setEmbeddedSize(panels[i].getWidth());
				} else {
					panels[i].setEmbeddedSize(panels[i].getHeight());
				}
				panels[i].setEmbeddedDef(panels[i].calculateEmbeddedDef());
			}
			dockPanelInfo[i] = panels[i].createInfo();
		}

		// Sort the dock panels as the entries with the smallest amount of
		// definition should
		// be read first by the loading algorithm.
		Arrays.sort(dockPanelInfo, new Comparator<DockPanelData>() {
			public int compare(DockPanelData o1, DockPanelData o2) {
				int diff = o2.getEmbeddedDef().length()
						- o1.getEmbeddedDef().length();
				return diff;
			}
		});
		
		perspective.setDockPanelData(dockPanelInfo);

		perspective.setToolbarDefinition(((GuiManagerW) app.getGuiManager())
		        .getGeneralToolbarDefinition());
		perspective.setShowToolBar(app.showToolBar());
		perspective.setShowAxes(ev.getShowXaxis() && ev.getShowYaxis());
		perspective.setShowGrid(ev.getShowGrid());
		perspective.setShowInputPanel(app.showAlgebraInput());
		perspective.setShowInputPanelCommands(app.showInputHelpToggle());
		perspective.setInputPosition(app.getInputPosition());
		
		perspective.setToolBarPosition(app.getToolbarPosition());
		//perspective.setShowToolBarHelp(app.showToolBarHelp());
		//perspective.setShowDockBar(app.isShowDockBar());
		//perspective.setDockBarEast(app.isDockBarEast());

		return perspective;
	}

	/**
	 * @param viewId
	 * @return If just the view associated to viewId is visible
	 */
	@Override
    public boolean isOnlyVisible(int viewId) {

		DockPanelW[] panels = dockManager.getPanels();
		boolean foundView = false;

		for(int i = 0; i < panels.length; ++i) {
			// check if the view is visible at all
			if(panels[i].getViewId() == viewId) {
				foundView = true;

				if(!panels[i].isVisible()) {
					return false;
				}
			}
			
			// abort if any other view is visible
			else {
				if(panels[i].isVisible()) {
					return false;
				}
			}
		}
		
		// if we reach this point each other view is invisible, but
		// if the view wasn't found at all we return false as well
		return foundView;
    }


	public AppW getApplication() {
	    return app;
    }
	
	/**
	 * @return The management class for the docking behavior.
	 */
	public DockManagerW getDockManager() {
		return dockManager;
	}

	public Widget getRootComponent() {
		if(dockManager == null) {
			return null;
		}		
		return dockManager.getRoot();
	}
}
