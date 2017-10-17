package org.geogebra.web.web.gui;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.layout.GUITabs;
import org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelW;

import com.google.gwt.user.client.ui.FocusWidget;

public class AccessibilityManagerW implements AccessibilityManagerInterface {
	private enum TabModeChange {
		None, LastGeoToGUI, FirstGeoToGUI
	}

	private GuiManagerW gm;
	private AppW app;
	private boolean tabOverGeos = false;
	private TabModeChange tabModeChange = TabModeChange.None;
	public AccessibilityManagerW(AppW app) {
		this.app = app;
		gm = (GuiManagerW) app.getGuiManager();
	}

	@Override
	public void focusNext(Object source) {
		if (source instanceof FocusWidget) {
			focusNextWidget((FocusWidget) source);
		}
	}
	
	@Override
	public void focusPrevious(Object source) {
		if (source instanceof FocusWidget) {
			focusPreviousWidget((FocusWidget) source);
		}
	}
	
	public void focusNextWidget(FocusWidget source) {
		switch (source.getTabIndex()) {
		case GUITabs.EV_SETTINGS:
			if (!app.getKernel().getConstruction().isEmpty()) {
				focusFirstGeo();
			} else {
				focusNextAfterGeos();
			}
			break;
		case GUITabs.AV_PLUS:
			focusMenu();
			break;
		case GUITabs.HEADER_TAB_START:
			break;
			
		}
	}

	public void focusPreviousWidget(FocusWidget source) {
		switch (source.getTabIndex()) {
		case GUITabs.EV_SETTINGS:
			break;
		case GUITabs.AV_PLUS:
			focusLastGeo();
			break;
		case GUITabs.HEADER_TAB_START:
			break;
			
		}
	}

	private void focusNextAfterGeos() {
		EuclidianDockPanelW dp = (EuclidianDockPanelW) gm.getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN);
		dp.focusNextGUIElement();
		setTabOverGeos(false);
}

	private boolean focusFirstGeo() {
		GeoElement geo = app.getKernel().getConstruction().getGeoSetLabelOrder().first();
		if (geo != null) {
			app.getSelectionManager().addSelectedGeo(geo);
			tabOverGeos = true;
			return true;
		}
		return false;
	}

	private boolean focusLastGeo() {
		GeoElement geo = app.getKernel().getConstruction().getGeoSetLabelOrder().first();
		if (geo != null) {
			app.getSelectionManager().addSelectedGeo(geo);
			return true;
		}
		return false;
	}
	
	@Override
	public void focusMenu() {
		gm.getToolbarPanelV2().focusMenu();
	}

	@Override
	public boolean isTabOverGeos() {
		return tabOverGeos;
	}

	@Override
	public void setTabOverGeos(boolean tabOverGeos) {
		this.tabOverGeos = tabOverGeos;
	}

	public void onTabModeChange(boolean lastgeo) {
		if (!isTabOverGeos()) {
			EuclidianDockPanelW dp = (EuclidianDockPanelW) gm.getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN);
			if (lastgeo) {
				dp.focusNextGUIElement();
			} else {
				dp.focusLastGUIElement();
			}
			if (gm.getToolbarPanelV2() != null) {
				gm.getToolbarPanelV2().setTabIndexes();
			}
		} else {
			// TODO: switch back to geo mode.
		}
	}
	
	@Override
	public boolean hasTabModeChanged(boolean shift) {
		boolean ret = false;
		switch (tabModeChange) {
		case FirstGeoToGUI:
			if (shift) {
				setTabOverGeos(false);
				onTabModeChange(false);
				ret = true;
			}
			break;
		case LastGeoToGUI:
			if (!shift) {
				setTabOverGeos(false);
				onTabModeChange(true);
				ret = true;
			}
			break;
		case None:
			return false;
		default:
			break;
		}

		tabModeChange = TabModeChange.None;
		return ret;
	}

	public void setTabFromGeosToGui() {
		if (app.getSelectionManager().isFirstGeoSelected()) {
			tabModeChange = TabModeChange.FirstGeoToGUI;
		} else if (app.getSelectionManager().isLastGeoSelected()) {
			tabModeChange = TabModeChange.LastGeoToGUI;
		}
	}
}
