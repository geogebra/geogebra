package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GetViewId;
import org.geogebra.common.main.Feature;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.util.ZoomPanelMow;
import org.geogebra.web.full.gui.view.consprotocol.ConstructionProtocolNavigationW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.gui.accessibility.EuclidianViewAccessibiliyAdapter;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.voiceInput.SpeechRecognitionPanel;
import org.geogebra.web.html5.gui.zoompanel.ZoomPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract class for all "euclidian" panels.
 *
 * Remark: {@link #getEuclidianView()} has to be overridden if
 * {@link #getComponent()} does not return the euclidian view directly
 *
 * @author arpad (based on EuclidianDockPanelAbstract by Mathieu)
 */
public abstract class EuclidianDockPanelWAbstract extends DockPanelW
		implements GetViewId, EuclidianViewAccessibiliyAdapter {

	private ConstructionProtocolNavigationW consProtNav;

	private boolean hasEuclidianFocus;
	private boolean mayHaveZoomButtons = false;
	/**
	 * panel with home,+,-,fullscreen btns
	 */
	ZoomPanel zoomPanel;
	/** Zoom panel for MOW */
	ZoomPanelMow mowZoomPanel;
	/**
	 * button panel for speech recognition
	 */
	public SpeechRecognitionPanel speechRecPanel;

	/**
	 * default constructor
	 *
	 * @param id
	 *            id
	 * @param title
	 *            title
	 * @param toolbar
	 *            toolbar string
	 * @param hasStyleBar
	 *            whether to show stylebar
	 * @param hasZoomPanel
	 *            - true if it has zoom panel
	 * @param menuOrder
	 *            The location of this view in the view menu, -1 if the view
	 *            should not appear at all
	 * @param shortcut
	 *            letter for Ctrl+Shift+letter shortcut
	 */
	public EuclidianDockPanelWAbstract(int id, String title, String toolbar,
			boolean hasStyleBar, boolean hasZoomPanel, int menuOrder,
			char shortcut) {
		super(id, title, toolbar, hasStyleBar, menuOrder,
				shortcut);
		this.mayHaveZoomButtons = hasZoomPanel;
	}

	/**
	 * sets this euclidian panel to have the "euclidian focus"
	 *
	 * @param hasFocus
	 *            whether to focus
	 */
	public final void setEuclidianFocus(boolean hasFocus) {
		hasEuclidianFocus = hasFocus;
	}

	@Override
	protected boolean titleIsBold() {
		return super.titleIsBold() || hasEuclidianFocus;
	}

	@Override
	public boolean updateResizeWeight() {
		return true;
	}

	/**
	 * @return view in this dock panel
	 */
	@Override
	abstract public EuclidianView getEuclidianView();

	@Override
	public void setVisible(boolean sv) {
		super.setVisible(sv);
		// if (getEuclidianView() != null) {// also included in:
		if (getEuclidianView() instanceof EuclidianViewWInterface) {
			((EuclidianViewWInterface) getEuclidianView()).updateFirstAndLast(
					sv,
						false);
			}
		// }
	}

	/**
	 * Adds navigation bar
	 */
	public final void addNavigationBar() {
		consProtNav = (ConstructionProtocolNavigationW) (app.getGuiManager()
				.getConstructionProtocolNavigation(id));
		consProtNav.getImpl().addStyleName("consProtNav");
		if (getEuclidianPanel() == null) {
			loadComponent();
		}
		getEuclidianPanel().add(consProtNav.getImpl()); // may be invisible, but
														// made
													// visible later
		updateNavigationBar();
	}

	@Override
	public final void updateNavigationBar() {
		// ConstructionProtocolSettings cps = app.getSettings()
		// .getConstructionProtocol();
		// ((ConstructionProtocolNavigationW) consProtNav).settingsChanged(cps);
		// cps.addListener((ConstructionProtocolNavigation)consProtNav);

		if (app.getShowCPNavNeedsUpdate(id)) {
			app.setShowConstructionProtocolNavigation(
					app.showConsProtNavigation(id), id);
		}
		if (app.showConsProtNavigation(id)
				&& consProtNav == null) {
			this.addNavigationBar();
		}
		if (consProtNav != null) {
			consProtNav.update();
			consProtNav.setVisible(app.showConsProtNavigation(id));
			getEuclidianPanel().onResize();
		}
	}

	@Override
	public int navHeight() {
		if (this.consProtNav != null
				&& this.consProtNav.getImpl().getOffsetHeight() != 0) {
			return this.consProtNav.getImpl().getOffsetHeight();
		}
		return 30;
	}

	/**
	 * Wrapper of euclidian view
	 */
	public static class EuclidianPanel extends FlowPanel
			implements RequiresResize {

		/** dock panel */
		EuclidianDockPanelWAbstract dockPanel;
		/** panel for positioning furniture */
		AbsolutePanel absoluteEuclidianPanel;
		/** current height */
		int oldHeight = 0;
		/** current width */
		int oldWidth = 0;

		/**
		 * @param dockPanel
		 *            parent dock panel
		 */
		public EuclidianPanel(EuclidianDockPanelWAbstract dockPanel) {
			this(dockPanel, new AbsolutePanel());
		}

		/**
		 * @param dockPanel
		 *            parent dock panel
		 * @param absPanel
		 *            absolute panel (for positioning stuff over canvas)
		 */
		public EuclidianPanel(EuclidianDockPanelWAbstract dockPanel,
				AbsolutePanel absPanel) {
			super();
			this.dockPanel = dockPanel;
			add(absoluteEuclidianPanel = absPanel);
			absoluteEuclidianPanel.addStyleName("EuclidianPanel");
			absoluteEuclidianPanel.getElement().getStyle()
					.setOverflow(Overflow.VISIBLE);
			checkFocus();
			getElement().setAttribute("role", "application");
		}

		@Override
		public void onResize() {
			if (dockPanel.getApp() != null) {
				int h = dockPanel.getComponentInteriorHeight()
						- dockPanel.navHeightIfShown();
				int w = dockPanel.getComponentInteriorWidth();
				// TODO handle this better?
				// exit if new size cannot be determined
				// one dimension may be intentionally 0, resize to avoid DOM
				// overflow
				if (h < 0 || w < 0 || (w == 0 && h == 0)) {
					return;
				}
				if (h != oldHeight || w != oldWidth) {
					dockPanel.resizeView(w, h);
					oldHeight = h;
					oldWidth = w;
				} else {
					// it's possible that the width/height didn't change but the
					// position of EV did
					dockPanel.calculateEnvironment();
				}
				dockPanel.checkZoomPanelFits(h);
			}
		}

		// hack to fix GGB-697
		private native void checkFocus() /*-{
			var that = this;
			var forceResize = function() {
				that.@org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelWAbstract.EuclidianPanel::forceResize()()
			};

			$wnd.visibilityEventMain(forceResize, forceResize);
		}-*/ ;

		private void forceResize() {
			EuclidianView view = dockPanel.getEuclidianView();
			EuclidianViewW.forceResize(view);
		}

		@Override
		public boolean remove(Widget w) {
			return absoluteEuclidianPanel.remove(w) || super.remove(w);
		}

		/**
		 * @return absolute panel
		 */
		public AbsolutePanel getAbsolutePanel() {
			return absoluteEuclidianPanel;
		}

	}

	/**
	 * @return panel wrapping the Euclidian view
	 */
	protected abstract EuclidianPanel getEuclidianPanel();

	/**
	 * @return (foreground) canvas of the view
	 */
	public abstract Canvas getCanvas();

	/**
	 * @return application
	 */
	public AppW getApp() {
		return app;
	}

	/**
	 * @return panel for positioning overlay elements (e.g. input boxes)
	 */
	public final AbsolutePanel getAbsolutePanel() {
		return getEuclidianPanel() == null ? null : getEuclidianPanel()
				.getAbsolutePanel();
	}

	private boolean allowZoomPanel() {
		return ZoomPanel.neededFor(app);
	}

	@Override
	protected void addZoomPanel(MyDockLayoutPanel dockPanel) {
		if (allowZoomPanel()) {
			// This causes EV overlap toolbar
			// dockPanel.getElement().getStyle().setProperty("minHeight",
			// zoomPanel.getMinHeight());
			dockPanel.addSouth(zoomPanel, 0);
		}
		if (app.isWhiteboardActive() && mowZoomPanel != null) {
			dockPanel.addNorth(mowZoomPanel, 0);
		}
		if (app.has(Feature.SPEECH_RECOGNITION)) {
			if (speechRecPanel == null) {
				speechRecPanel = new SpeechRecognitionPanel(getApp(),
						getViewId());
			}
			dockPanel.addSouth(speechRecPanel, 0);
		}
	}

	@Override
	public void tryBuildZoomPanel() {
		if (zoomPanel != null) {
			zoomPanel.removeFromParent();
			zoomPanel = null;
		}
		if (allowZoomPanel()) {
			boolean bottomRight = isBottomRight();
			zoomPanel = new ZoomPanel(getEuclidianView(), app,
					bottomRight,
					this.mayHaveZoomButtons);
			if (bottomRight) {
				app.setZoomPanel(zoomPanel);
			}
		}
		tryBuildMowZoomPanel();
	}

	private boolean isBottomRight() {
		DockManagerW dm = (DockManagerW) app.getGuiManager().getLayout().getDockManager();
		return dm.getRoot() == null || dm.getRoot().isBottomRight(this);
	}

	private void tryBuildMowZoomPanel() {
		if (mowZoomPanel != null) {
			mowZoomPanel.removeFromParent();
			mowZoomPanel = null;
		}
		if (allowZoomPanel()) {
			mowZoomPanel = new ZoomPanelMow(app);
			((AppWFull) app).setMowZoomPanel(mowZoomPanel);
		}
	}

	/**
	 * Recompute offset/scale for event handling
	 */
	public abstract void calculateEnvironment();

	/**
	 * Resize wrapped panel to given dimensions
	 *
	 * @param width
	 *            view width
	 * @param height
	 *            view height
	 */
	public abstract void resizeView(int width, int height);

	@Override
	public final void setLabels() {
		super.setLabels();
		if (zoomPanel != null) {
			zoomPanel.setLabels();
		}
		if (mowZoomPanel != null) {
			mowZoomPanel.setLabels();
		}
		if (graphicsContextMenuBtn != null) {
			String titletext = app.getLocalization().getMenu("Settings");
			graphicsContextMenuBtn.setTitle(titletext);
			graphicsContextMenuBtn.setAltText(titletext);
		}
	}

	/**
	 * Hides zoom buttons.
	 */
	public void hideZoomPanel() {
		if (zoomPanel != null) {
			zoomPanel.setHidden(true);
		}
		if (mowZoomPanel != null) {
			mowZoomPanel.addStyleName("hidden");
		}
	}

	/**
	 * Shows zoom buttons.
	 */
	public void showZoomPanel() {
		if (zoomPanel != null) {
			zoomPanel.setHidden(false);
		}
		if (mowZoomPanel != null) {
			mowZoomPanel.removeStyleName("hidden");
		}
	}

	/**
	 * Moves the zoom panel up for MOW toolbar
	 *
	 * @param up
	 *            true if zoom panel should move up, false if zoom panel should
	 *            move down
	 */
	public void moveZoomPanelUpOrDown(boolean up) {
		if (zoomPanel != null) {
			Dom.toggleClass(zoomPanel, "showMowSubmenu", "hideMowSubmenu", up);
		}
	}

	/**
	 * Move zoom panel to bottom
	 */
	public void moveZoomPanelToBottom() {
		if (zoomPanel != null) {
			zoomPanel.removeStyleName("narrowscreen");
		}
	}

	/**
	 * Move zoom panel to avoid conflicts with toolbar
	 */
	public void moveZoomPanelAboveToolbar() {
		if (zoomPanel != null) {
			zoomPanel.addStyleName("narrowscreen");
		}
	}

	@Override
	public void focusNextGUIElement() {
		if (zoomPanel != null) {
			zoomPanel.focusFirstButton();
		}
	}

	@Override
	public boolean focusSpeechRecBtn() {
		if (speechRecPanel != null) {
			speechRecPanel.focusSpeechRec();
			return true;
		}
		return false;
	}

	/**
	 * @return if the EV panel has zoom or fullscreen buttons at all.
	 */
	public boolean hasZoomButtons() {
		return zoomPanel != null && zoomPanel.hasButtons();
	}

	@Override
	public boolean focusSettings() {
		if (graphicsContextMenuBtn != null) {
			graphicsContextMenuBtn.getElement().focus();
			return true;
		}
		return false;
	}

	@Override
	public void focusLastZoomButton() {
		if (zoomPanel != null) {
			zoomPanel.focusLastButton();
		} else {
			focusSettings();
		}
	}

	/**
	 * Checks if zoom panel fit on Euclidian View with given height and
	 * shows/hides it respectively.
	 *
	 * @param height
	 *            Height of EV.
	 */
	public void checkZoomPanelFits(int height) {
		if (zoomPanel != null && ZoomPanel.neededFor(app)) {
			zoomPanel.setMaxHeight(height);
			zoomPanel.updateFullscreenVisibility(isBottomRight());
		}
	}

	/**
	 * Reset old size
	 */
	public final void reset() {
		if (getEuclidianPanel() != null) {
			getEuclidianPanel().oldWidth = 0;
			getEuclidianPanel().oldHeight = 0;
		}
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		EuclidianView ev = getEuclidianView();
		if (ev instanceof EuclidianViewW) {
			return ((EuclidianViewW) ev).getKeyboardListener();
		}
		return null;
	}
}
