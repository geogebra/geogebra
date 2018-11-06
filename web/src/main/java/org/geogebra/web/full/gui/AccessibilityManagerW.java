package org.geogebra.web.full.gui;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.gui.AccessibilityManagerNoGui;
import org.geogebra.common.gui.SliderInput;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.web.full.gui.layout.GUITabs;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelWAbstract;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.view.algebra.LatexTreeItemController;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.ZoomPanel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Widget;

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
	private Widget anchor;
	private SliderInput activeButton;

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
		if (source == null) {
			focusFirstElement();
		} else if (source instanceof LatexTreeItemController) {
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

	private void focusFirstElement() {
		if (app.isUnbundled()) {
			focusMenu();
		} else {
			if (app.is3DViewEnabled()) {
				setTabOverGeos(true);
				focusFirstGeo();
			}
		}
	}

	@Override
	public boolean focusInput(boolean force) {
		if (gm.getUnbundledToolbar() != null) {
			return gm.getUnbundledToolbar().focusInput(force);
		}
		return false;
	}

	private void focusInputAsNext() {
		if (!focusInput(false)) {
			focusMenu();
		}
	}

	@Override
	public void focusPrevious(Object source) {
		if (source instanceof LatexTreeItemController) {
			if (!focusLastGeo()) {
				focusMenu();
			}
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
			if (!focusInput(false)) {
				focusZoom(false);
			}
		}
	}

	private void focusZoom(boolean first) {
		EuclidianDockPanelWAbstract dp = getEuclidianPanel();
		if (first) {
			dp.focusNextGUIElement();
		} else {
			dp.focusLastZoomButton();
		}
		setTabOverGeos(false);
	}

	private EuclidianDockPanelWAbstract getEuclidianPanel() {
		return (EuclidianDockPanelWAbstract) gm.getLayout().getDockManager()
				.getPanel(app.getActiveEuclidianView().getViewID());
	}

	private void focusSettings() {
		getEuclidianPanel().focusLastGUIElement();
		setTabOverGeos(false);
	}

	private boolean focusFirstGeo() {
		Construction cons = app.getKernel().getConstruction();
		if (cons.isEmpty()) {
			return false;
		}

		focusGeo(app.getSelectionManager().getTabbingSet().first());
		return true;
	}

	private boolean focusLastGeo() {
		Construction cons = app.getKernel().getConstruction();
		if (cons.isEmpty()) {
			return false;
		}

		focusGeo(app.getSelectionManager().getTabbingSet().last());
		return true;
	}

	@Override
	public void focusMenu() {
		if (gm.getUnbundledToolbar() != null) {
			gm.getUnbundledToolbar().focusMenu();
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
		if (selection.getSelectedGeos().size() != 1 || !app.isUnbundled()) {
			return false;
		}
		GeoElement geo = selection.getSelectedGeos().get(0);
		boolean exitOnFirst = selection.isFirstGeoSelected() && isShiftDown;
		boolean exitOnLast = selection.isLastGeoSelected() && !isShiftDown;
		this.activeButton = null;
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
					.getUnbundledToolbar();
			if (tp != null) {
				tp.focusMenu();
			}
		}
	}

	@Override
	public void setAnchor(Object anchor) {
		this.anchor = anchor instanceof Widget ? (Widget) anchor : null;
	}

	@Override
	public Object getAnchor() {
		return anchor;
	}

	@Override
	public void focusAnchor() {
		if (anchor == null) {
			return;
		}
		anchor.getElement().focus();
		cancelAnchor();
	}

	@Override
	public void focusAnchorOrMenu() {
		if (anchor == null) {
			focusMenu();
		} else {
			focusAnchor();
		}
	}

	@Override
	public void cancelAnchor() {
		anchor = null;
	}

	@Override
	public boolean handleTabExitGeos(boolean forward) {
		if (!app.has(Feature.TAB_ON_EV_PLAY)) {
			return false;
		}
		boolean voiceover = Browser.isiOS()
				&& app.has(Feature.VOICEOVER_APPLETS);
		if (app.getKernel().needToShowAnimationButton()) {
			setPlaySelectedIfVisible(true);
			return true;
		}
		if (app.getActiveEuclidianView().getDimension() == 3 && voiceover) {
			this.activeButton = SliderInput.ROTATE_Z;
			return true;
		}
		return voiceover;
	}

	@Override
	public void setPlaySelectedIfVisible(boolean b) {
		this.activeButton = null;
		if (app.getKernel().needToShowAnimationButton()) {
			app.getActiveEuclidianView().setAnimationButtonSelected(b);
		}
	}

	@Override
	public boolean tabEuclidianControl(boolean forward) {
		if (app.getActiveEuclidianView().isAnimationButtonSelected()) {
			if (!forward) {
				focusLastGeo();
				setPlaySelectedIfVisible(false);
				return true;
			}
			if (app.getActiveEuclidianView().getDimension() == 3 && forward
					&& activeButton == null) {
				activeButton = SliderInput.ROTATE_Z;
				return true;
			}
		}
		if (app.getActiveEuclidianView().getDimension() == 3 && forward
				&& activeButton == SliderInput.ROTATE_Z) {
			activeButton = SliderInput.ROTATE_Y;
			return true;
		}

		return false;
	}

	@Override
	public String getSpaceAction() {
		if (app.getActiveEuclidianView().isAnimationButtonSelected()) {
			return app.getLocalization().getMenu("Animation");
		}

		GeoElement sel = getSelectedGeo();
		if (sel instanceof GeoButton || sel instanceof GeoBoolean) {
			return sel.getCaption(StringTemplate.screenReader);
		}

		return null;
	}

	public GeoElement getSelectedGeo() {
		return AccessibilityManagerNoGui.getSelectedGeo(app);
	}

	@Override
	public SliderInput getSliderAction() {
		return activeButton;
	}

	@Override
	public void sliderChange(double step) {
		if (activeButton == SliderInput.ROTATE_Z) {
			app.getEuclidianView3D().rememberOrigins();
			app.getEuclidianView3D().shiftRotAboutZ(step);
			app.getEuclidianView3D().repaintView();
		}
		if (activeButton == SliderInput.ROTATE_Y) {
			app.getEuclidianView3D().rememberOrigins();
			app.getEuclidianView3D().shiftRotAboutY(step);
			app.getEuclidianView3D().repaintView();
		}

	}
}