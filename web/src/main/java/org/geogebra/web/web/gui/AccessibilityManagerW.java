package org.geogebra.web.web.gui;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.web.html5.gui.util.ZoomPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.layout.GUITabs;
import org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.web.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.web.gui.view.algebra.LatexTreeItemController;

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
	private SelectionManager selection;
	/**
	 * Constructor.
	 * 
	 * @param app
	 *            The application.
	 */
	public AccessibilityManagerW(AppW app) {
		this.app = app;
		gm = (GuiManagerW) app.getGuiManager();
		selection = app.getSelectionManager();
	}

	@Override
	public void focusNext(Object source) {
		if (app.isMenuShowing()) {
			return;
		}

		if (source instanceof LatexTreeItemController) {
			focusMenu();
		} else if (source instanceof ZoomPanel) {
			if (!focusFirstGeo()) {
				focusInputAsNext();
			}
		} else if (source instanceof FocusWidget) {
			focusNextWidget((FocusWidget) source);
		} else if (source instanceof GeoElement) {
			focusInputAsNext();
		}
	}
	
	@Override
	public boolean focusInput() {
		if (gm.getToolbarPanelV2() != null) {
			return gm.getToolbarPanelV2().focusInput();
		}
		return false;
	}

	private void focusInputAsNext() {
		if (!focusInput()) {
			focusMenu();
		}

	}

	@Override
	public void focusPrevious(Object source) {
		if (app.isMenuShowing()) {
			return;
		}

		if (source instanceof LatexTreeItemController) {
			focusLastGeo();
		} else if (source instanceof ZoomPanel) {
			focusSettings();
		} else if (source instanceof FocusWidget) {
			focusPreviousWidget((FocusWidget) source);
		} else if (source instanceof GeoElement) {
			focusZoom(false);
		}
	}
	

	private void focusNextWidget(FocusWidget source) {
		if (app.isMenuShowing()) {
			return;
		}

		if (source.getTabIndex() == GUITabs.SETTINGS) {
			focusZoom(true);
		}
	}

	private void focusPreviousWidget(FocusWidget source) {
		if (app.isMenuShowing()) {
			return;
		}


		if (source.getTabIndex() == GUITabs.MENU) {
			if (!focusInput()) {
				if (!focusLastGeo()) {
					focusZoom(false);
				}
			}
		}
	}

	private void focusZoom(boolean first) {
		EuclidianDockPanelW dp = (EuclidianDockPanelW) gm.getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN);
		if (first) {
			dp.focusNextGUIElement();
		} else {
			dp.focusLastZoomButton();
		}
		setTabOverGeos(false);
	}

	private void focusSettings() {
		EuclidianDockPanelW dp = (EuclidianDockPanelW) gm.getLayout()
				.getDockManager().getPanel(App.VIEW_EUCLIDIAN);
		dp.focusLastGUIElement();
		setTabOverGeos(false);
	}

	private boolean focusFirstGeo() {
		Construction cons = app.getKernel().getConstruction();
		if (cons.isEmpty()) {
			return false;
		}

		focusGeo(cons.getGeoSetLabelOrder().first());
		return true;
	}

	private boolean focusLastGeo() {
		Construction cons = app.getKernel().getConstruction();
		if (cons.isEmpty()) {
			return false;
		}

		focusGeo(cons.getGeoSetLabelOrder().last());
		return true;
	}

	@Override
	public void focusMenu() {
		if (gm.getToolbarPanelV2() != null) {
			gm.getToolbarPanelV2().focusMenu();
		}
	}

	@Override
	public boolean isTabOverGeos() {
		return tabOverGeos;
	}

	@Override
	public void setTabOverGeos(boolean tabOverGeos) {
		this.tabOverGeos = tabOverGeos;
	}

	@Override
	public boolean isCurrentTabExitGeos(boolean isShiftDown) {
		if (selection.getSelectedGeos().size() != 1) {
			return false;
		}
		GeoElement geo = selection.getSelectedGeos().get(0);
		boolean exitOnFirst = selection.isFirstGeoSelected() && isShiftDown;
		boolean exitOnLast = selection.isLastGeoSelected() && !isShiftDown;
		if (exitOnFirst) {
			focusPrevious(geo);
		} else if (exitOnLast) {
			focusNext(geo);
		}

		if (exitOnFirst || exitOnLast) {
			selection.clearSelectedGeos();
			return true;
		}
		return false;
	}

	@Override
	public void focusGeo(GeoElement geo) {
		if (geo != null) {
			app.getSelectionManager().addSelectedGeo(geo);
			setTabOverGeos(true);
			app.getActiveEuclidianView().requestFocus();
		} else {
			ToolbarPanel tp = ((GuiManagerW) app.getGuiManager())
					.getToolbarPanelV2();
			if (tp != null) {
				tp.focusMenu();
			}
		}
	}
}
