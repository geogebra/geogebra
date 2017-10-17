package org.geogebra.web.web.gui;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.util.ZoomPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.layout.GUITabs;
import org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelW;

import com.google.gwt.user.client.ui.FocusWidget;

/**
 * Web implementation of AccessibilityManager.
 * 
 * @author laszlo
 *
 */
public class AccessibilityManagerW implements AccessibilityManagerInterface {
	private GuiManagerW gm;
	private AppW app;
	private boolean tabOverGeos = false;

	/**
	 * Constructor.
	 * 
	 * @param app
	 *            The application.
	 */
	public AccessibilityManagerW(AppW app) {
		this.app = app;
		gm = (GuiManagerW) app.getGuiManager();
	}

	@Override
	public void focusNext(Object source) {
		if (source instanceof FocusWidget) {
			focusNextWidget((FocusWidget) source);
		} else if (source instanceof GeoElement) {
			focusNextAfterGeos();
		}
	}
	
	@Override
	public void focusPrevious(Object source) {
		if (source instanceof ZoomPanel) {
			focusLastGeo();
		} else if (source instanceof FocusWidget) {
			focusPreviousWidget((FocusWidget) source);
		} else if (source instanceof GeoElement) {
			focusPreviousBeforeGeos();
		}
	}
	

	private void focusNextWidget(FocusWidget source) {
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

	private void focusPreviousWidget(FocusWidget source) {
		if (source.getTabIndex() == GUITabs.AV_PLUS) {
			EuclidianDockPanelW dp = (EuclidianDockPanelW) gm.getLayout()
					.getDockManager().getPanel(App.VIEW_EUCLIDIAN);
			dp.focusLastZoomButton();
			setTabOverGeos(true);
		}
	}

	private void focusNextAfterGeos() {
		EuclidianDockPanelW dp = (EuclidianDockPanelW) gm.getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN);
		dp.focusNextGUIElement();
		setTabOverGeos(false);
	}

	private void focusPreviousBeforeGeos() {
		EuclidianDockPanelW dp = (EuclidianDockPanelW) gm.getLayout()
				.getDockManager().getPanel(App.VIEW_EUCLIDIAN);
		dp.focusLastGUIElement();
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
		GeoElement geo = app.getKernel().getConstruction().getGeoSetLabelOrder()
				.last();
		if (geo != null) {
			app.getSelectionManager().addSelectedGeo(geo);
			setTabOverGeos(true);
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
}
