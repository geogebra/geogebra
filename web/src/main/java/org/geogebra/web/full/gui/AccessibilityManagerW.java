package org.geogebra.web.full.gui;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.gui.AccessibilityManagerNoGui;
import org.geogebra.common.gui.SliderInput;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.GUITabs;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelWAbstract;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.view.algebra.LatexTreeItemController;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.speechRec.SpeechRecognitionPanel;
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
	private List<Integer> visitedIds = new ArrayList<>();

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

	private void focusFirst() {
		focusFirstElement();
	}

	@Override
	public void focusNext(Object source) {
		if (source == null) {
			focusFirst();
		} else if (source instanceof LatexTreeItemController) {
			nextFromInput();
		} else if (source instanceof ZoomPanel) {
			nextFromZoomPanel();
		} else if (isSpeechButton(source)) {
			nextFromSpeechRecognitionPanel();
		} else if (source instanceof FocusWidget) {
			nextFromWidget((FocusWidget) source);
		} else if (source instanceof GeoElement) {
			nextFromLastGeo();
		}
	}

	private boolean isSpeechButton(Object source) {
		return app.has(Feature.SPEECH_RECOGNITION) && source instanceof SpeechRecognitionPanel;
	}

	@Override
	public void focusPrevious(Object source) {
		if (source instanceof LatexTreeItemController) {
			previousFromInput();
		} else if (source instanceof ZoomPanel) {
			previousFromZoomPanel();
		} else if (isSpeechButton(source)) {
			previousFromSpeechRecognition();
		} else if (source instanceof FocusWidget) {
			previousFromWidget((FocusWidget) source);
		} else if (source instanceof GeoElement) {
			previousFromFirstGeo();
		}
	}

	private void previousFromInput() {
		if (focusLastGeo()) {
			return;
		}
		focusMenu();
	}

	private void previousFromSpeechRecognition() {
		if (focusLastGeo()) {
			return;
		}
		focusZoom(false);
	}

	private void previousFromFirstGeo() {
		focusZoom(false);
	}

	private void nextFromInput() {
		focusMenu();
	}

	private void nextFromZoomPanel() {
		if (app.has(Feature.SPEECH_RECOGNITION)) {
			focusNextSpeechRec();
		} else {
			focusZoomPanel();
		}
	}

	private void nextFromSpeechRecognitionPanel() {
		if (focusNextView()) {
			return;
		}

		if (focusFirstGeo()) {
			return;
		}

	}

	private boolean focusNextView() {
		int viewId = app.getActiveEuclidianView().getViewID();
		if (viewId == App.VIEW_EUCLIDIAN) {
			if (app.hasEuclidianView2(1)) {
				app.getEuclidianView2(1).requestFocus();
			} else if (app.is3DViewEnabled()) {
				app.getEuclidianView3D().requestFocus();
			}
		} else if (viewId == App.VIEW_EUCLIDIAN3D) {
			if (app.hasEuclidianView2(1)) {
				app.getEuclidianView2(1).requestFocus();
			} else if (app.getEuclidianView1().isShowing()) {
				app.getEuclidianView1().requestFocus();
			}
		}

		return false;
	}

	private void nextFromLastGeo() {
		focusFirstWidget();
	}

	private void focusNextSpeechRec() {
		EuclidianDockPanelWAbstract dp = getEuclidianPanel();
		dp.focusSpeechRecBtn();
	}

	private List<EuclidianDockPanelWAbstract> getViewsWithZoomPanel(boolean active) {
		DockManagerW dm = (DockManagerW) (app.getGuiManager().getLayout().getDockManager());
		List<EuclidianDockPanelWAbstract> list = new ArrayList<>();
		for (DockPanelW panel : dm.getPanels()) {
			EuclidianDockPanelWAbstract ev = isEuclidianViewWithZoomPanel(panel);
			if (ev != null && (active || ev.getViewId() != app.getActiveEuclidianView().getViewID())) {
				list.add(ev);
			}
		}
		return list;
	}

	private boolean focusZoomPanel() {
		return focusZoomPanel(true);
	}

	private boolean focusZoomPanel(boolean active) {
		for (EuclidianDockPanelWAbstract ev : getViewsWithZoomPanel(active)) {
			if (ev != null) {
				setTabOverGeos(false);
				ev.focusNextGUIElement();
				visitedIds.add(ev.getViewId());
				return true;
			}
		}
		return false;
	}

	private EuclidianDockPanelWAbstract isEuclidianViewWithZoomPanel(DockPanelW panel) {
		if (!(panel instanceof EuclidianDockPanelWAbstract)) {
			return null;
		}

		EuclidianDockPanelWAbstract ev = (EuclidianDockPanelWAbstract) panel;

		boolean zoomButtons = ev.isAttached() && !visitedIds.contains(ev.getViewId())
				&& ev.hasZoomButtons();
		return zoomButtons ? ev : null;
	}

	private void focusFirstElement() {
		if (gm.getUnbundledToolbar() != null) {
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

	private void focusFirstWidget() {
		ToolbarPanel toolbar = gm.getUnbundledToolbar();
		if (toolbar != null) {
			if (toolbar.focusInput(false)) {
				return;
			}

			gm.getUnbundledToolbar().focusMenu();
			return;
		}

		focusZoom(true);
	}

	private void previousFromZoomPanel() {
		if (focusSettings()) {
			return;
		}

		if (isPlayVisible()) {
			setPlaySelectedIfVisible(true);
			setTabOverGeos(true);
			return;
		}

		if (app.has(Feature.SPEECH_RECOGNITION)) {
			focusNextSpeechRec();
		} else {
			focusLastGeo();
		}
	}

	private void nextFromWidget(FocusWidget source) {
		if (app.isMenuShowing()) {
			return;
		}

		if (source.getTabIndex() == GUITabs.SETTINGS) {
			focusZoom(true);
		}
	}

	private void previousFromWidget(FocusWidget source) {
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
		visitedIds.add(app.getActiveEuclidianView().getViewID());
		setTabOverGeos(false);
	}

	private EuclidianDockPanelWAbstract getEuclidianPanel() {
		return getEuclidianPanel(app.getActiveEuclidianView().getViewID());
	}

	private EuclidianDockPanelWAbstract getEuclidianPanel(int viewId) {
		return (EuclidianDockPanelWAbstract) gm.getLayout().getDockManager()
				.getPanel(viewId);
	}

	private boolean focusSettings() {
		if (getEuclidianPanel().focusLastGUIElement()) {
			setTabOverGeos(false);
			return true;
		}
		return false;
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
		} else {
			focusFirstElement();
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
			visitedIds.clear();
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
		if (!forward && selection.isFirstGeoSelected()) {
			focusZoom(false);
		}
		boolean voiceover = Browser.isiOS();
		if (app.getKernel().needToShowAnimationButton()) {
			this.activeButton = null;
			setPlaySelectedIfVisible(true);
			return true;
		}
		if (app.getActiveEuclidianView().getDimension() == 3 && voiceover
				&& forward) {
			this.activeButton = SliderInput.ROTATE_Z;
			return true;
		}
		return voiceover;
	}

	private void exitGeosFromPlayButton() {
		setPlaySelectedIfVisible(false);
		focusZoomPanel();
		tabOverGeos = false;
	}

	@Override
	public void setPlaySelectedIfVisible(boolean b) {
		if (isPlayVisible()) {
			app.getActiveEuclidianView().setAnimationButtonSelected(b);
		}
	}

	private boolean isPlayVisible() {
		return app.getKernel().needToShowAnimationButton();
	}

	@Override
	public boolean tabEuclidianControl(boolean forward) {
		if (app.getActiveEuclidianView().isAnimationButtonSelected()) {
			if (forward) {
				exitGeosFromPlayButton();
			} else {
				focusLastGeo();
				this.activeButton = null;
			}
			setPlaySelectedIfVisible(false);
			return true;
		}
		if (app.getActiveEuclidianView().getDimension() == 3 && forward
				&& activeButton == SliderInput.ROTATE_Z) {
			activeButton = SliderInput.TILT;
			return true;
		}
		if (app.getActiveEuclidianView().getDimension() == 3 && !forward
				&& activeButton == null && this.getSelectedGeo() == null) {
			activeButton = SliderInput.TILT;
			return true;
		}
		if (app.getActiveEuclidianView().getDimension() == 3 && !forward
				&& activeButton == SliderInput.TILT) {
			activeButton = SliderInput.ROTATE_Z;
			return true;
		}
		if (app.getActiveEuclidianView().getDimension() == 3 && !forward
				&& activeButton == SliderInput.ROTATE_Z) {
			activeButton = null;
			return false;
		}
		if (app.getActiveEuclidianView().getDimension() == 3 && forward
				&& activeButton == SliderInput.TILT) {
			activeButton = null;
			return true; // tilt is the last => leave
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
		if (sel != null && sel.getScript(EventType.CLICK) != null) {
			return ScreenReader.getAuralText(sel);
		}

		return null;
	}

	/**
	 *
	 * @return the geo that is currently selected.
	 */
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
		if (activeButton == SliderInput.TILT) {
			app.getEuclidianView3D().rememberOrigins();
			app.getEuclidianView3D().shiftRotAboutY(step);
			app.getEuclidianView3D().repaintView();
		}
	}

	@Override
	public void onEmptyConstuction(boolean forward) {
		focusZoom(false);
	}

	@Override
	public boolean onSelectFirstGeo(boolean forward) {
		if (!forward) {
			setPlaySelectedIfVisible(false);
			focusZoom(false);
			return true;
		}

		return handleTabExitGeos(true);
	}

	@Override
	public boolean onSelectLastGeo(boolean forward) {
		if (forward) {
			if (isPlayVisible()) {
				setPlaySelectedIfVisible(true);
				return true;
			}
			setTabOverGeos(false);
			if (!focusZoomPanel()) {
				focusNextSpeechRec();
			}
			return true;
		}
		return handleTabExitGeos(false);
	}
}