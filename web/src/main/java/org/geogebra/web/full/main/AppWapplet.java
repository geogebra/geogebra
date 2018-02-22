package org.geogebra.web.full.main;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.kernel.View;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.Log;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.Persistable;
import org.geogebra.web.full.gui.app.FloatingMenuPanel;
import org.geogebra.web.full.gui.app.GGWCommandLine;
import org.geogebra.web.full.gui.app.GGWMenuBar;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.full.gui.dialog.DialogBoxW;
import org.geogebra.web.full.gui.inputbar.AlgebraInputW;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.DockSplitPaneW;
import org.geogebra.web.full.gui.layout.ZoomSplitLayoutPanel;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.full.gui.menubar.FileMenuW;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.move.ggtapi.operations.LoginOperationW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.GeoGebraTubeAPIWSimple;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.CSSAnimation;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Full-featured GeoGebra instance that needs part of the screen only.
 */
public class AppWapplet extends AppWFull {
	private int spWidth;
	private int spHeight;
	private boolean menuInited = false;

	// helper
	// variable
	private HorizontalPanel splitPanelWrapper = null;
	/** floating menu */
	FloatingMenuPanel floatingMenuPanel = null;

	/******************************************************
	 * Constructs AppW for applets
	 * 
	 * @param ae
	 *            article element
	 * @param gf
	 *            frame
	 * @param dimension
	 *            3 for 3d, 2 otherwise
	 * @param laf
	 *            look and feel
	 * @param device
	 *            browser or tablet
	 */
	public AppWapplet(ArticleElement ae, GeoGebraFrameBoth gf,
			int dimension, GLookAndFeel laf,
			GDevice device) {
		super(ae, dimension, laf, device, gf);
		setAppletHeight(frame.getComputedHeight());
		setAppletWidth(frame.getComputedWidth());

		this.useFullGui = !isApplet() || ae.getDataParamShowAlgebraInput(false)
				|| ae.getDataParamShowToolBar(false)
				|| ae.getDataParamShowMenuBar(false)
				|| ae.getDataParamEnableRightClick()
				|| !isStartedWithFile();

		Log.info("GeoGebra " + GeoGebraConstants.VERSION_STRING + " "
				+ GeoGebraConstants.BUILD_DATE + " "
				+ Window.Navigator.getUserAgent());
		initCommonObjects();
		initing = true;

		this.euclidianViewPanel = new EuclidianDockPanelW(this, allowStylebar());
		// (EuclidianDockPanelW)getGuiManager().getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN);
		this.canvas = this.euclidianViewPanel.getCanvas();
		canvas.setWidth("1px");
		canvas.setHeight("1px");
		canvas.setCoordinateSpaceHeight(1);
		canvas.setCoordinateSpaceWidth(1);
		initCoreObjects(this);
		afterCoreObjectsInited();
		resetFonts();
		Browser.removeDefaultContextMenu(this.getArticleElement());
		if (ae.getDataParamApp() && !this.getLAF().isSmart()) {
			RootPanel.getBodyElement().addClassName("application");
		}
		if (this.showMenuBar()) {
			// opening file -> this was inited before
			if (getLoginOperation() == null) {
				initSignInEventFlow(new LoginOperationW(this),
						ArticleElement.isEnableUsageStats());
			}
		} else {
			if (Browser.runningLocal() && ArticleElement.isEnableUsageStats()) {
				new GeoGebraTubeAPIWSimple(has(Feature.TUBE_BETA), ae)
						.checkAvailable(null);
			}
		}
	}

	@Override
	protected void afterCoreObjectsInited() {
		// Code to run before buildApplicationPanel
		initGuiManager();
		if (this.showConsProtNavigation(App.VIEW_EUCLIDIAN)) {
			((EuclidianDockPanelW) euclidianViewPanel).addNavigationBar();
		}
		// following lines were swapped before but for async file loading it
		// does not matter
		// and for sync file loading this makes sure perspective setting is not
		// blocked by initing flag
		initing = false;
		GeoGebraFrameW.handleLoadFile(articleElement, this);

	}

	private void buildSingleApplicationPanel() {
		if (frame != null) {
			frame.clear();
			frame.add((Widget) getEuclidianViewpanel());
			// we need to make sure trace works after this, see #4373 or #4236
			this.getEuclidianView1().createImage();
			((DockPanelW) getEuclidianViewpanel()).setVisible(true);
			((DockPanelW) getEuclidianViewpanel())
					.setEmbeddedSize(getSettings().getEuclidian(1)
							.getPreferredSize().getWidth());
			((DockPanelW) getEuclidianViewpanel()).updatePanel(false);
			getEuclidianViewpanel()
					.setPixelSize(
							getSettings().getEuclidian(1).getPreferredSize()
									.getWidth(),
							getSettings().getEuclidian(1).getPreferredSize()
									.getHeight());

			// FIXME: temporary hack until it is found what causes
			// the 1px difference
			// getEuclidianViewpanel().getAbsolutePanel().getElement().getStyle().setLeft(1,
			// Style.Unit.PX);
			// getEuclidianViewpanel().getAbsolutePanel().getElement().getStyle().setTop(1,
			// Style.Unit.PX);
			getEuclidianViewpanel().getAbsolutePanel().getElement().getStyle()
					.setBottom(-1, Style.Unit.PX);
			getEuclidianViewpanel().getAbsolutePanel().getElement().getStyle()
					.setRight(-1, Style.Unit.PX);
			oldSplitLayoutPanel = null;
		}
	}

	@Override
	public void buildApplicationPanel() {
		if (!isUsingFullGui()) {
			if (showConsProtNavigation() || !isJustEuclidianVisible()) {
				useFullGui = true;
			}
		}

		if (!isUsingFullGui()) {
			buildSingleApplicationPanel();
			return;
		}
		if (isWhiteboardActive()) {
			this.setToolbarPosition(SwingConstants.SOUTH, false);
		}
		for (int i = frame.getWidgetCount() - 1; i >= 0; i--) {
			if (!(frame.getWidget(i) instanceof HasKeyboardPopup
					|| frame.getWidget(i) instanceof TabbedKeyboard
					|| (frame.getWidget(i) instanceof FloatingMenuPanel)
					|| (isUnbundledOrWhiteboard()
							&& frame.getWidget(i) instanceof Persistable)
					|| frame.getWidget(i) instanceof DialogBoxW)) {
				frame.remove(i);
			}
		}

		// showMenuBar should come from data-param,
		// this is just a 'second line of defense'
		// otherwise it can be used for taking ggb settings into account too
		if (articleElement.getDataParamShowMenuBar(showMenuBar)) {
			frame.attachMenubar(this);
		}
		// showToolBar should come from data-param,
		// this is just a 'second line of defense'
		// otherwise it can be used for taking ggb settings into account too
		if (articleElement.getDataParamShowToolBar(showToolBar)
				&& this.getToolbarPosition() != SwingConstants.SOUTH) {
			frame.attachToolbar(this);
		}
		if (this.getInputPosition() == InputPosition.top
				&& articleElement
						.getDataParamShowAlgebraInput(showAlgebraInput)) {
			attachAlgebraInput();
		}

		attachSplitLayoutPanel();

		// showAlgebraInput should come from data-param,
		// this is just a 'second line of defense'
		// otherwise it can be used for taking ggb settings into account too
		if (this.getInputPosition() == InputPosition.bottom
				&& articleElement
						.getDataParamShowAlgebraInput(showAlgebraInput)) {
			attachAlgebraInput();
		}
		if (articleElement.getDataParamShowToolBar(showToolBar)
				&& this.getToolbarPosition() == SwingConstants.SOUTH) {
			frame.attachToolbar(this);
		}
		if (has(Feature.SHOW_ONE_KEYBOARD_BUTTON_IN_FRAME)
				// we do not need keyboard in whiteboard
				&& !isWhiteboardActive()) {
			frame.attachKeyboardButton();
		}
		frame.attachGlass();
	}

	private void refreshSplitLayoutPanel() {
		if (frame != null && frame.getWidgetCount() != 0
				&& frame.getWidgetIndex(getSplitLayoutPanel()) == -1
				&& frame.getWidgetIndex(oldSplitLayoutPanel) != -1) {
			int wi = frame.getWidgetIndex(oldSplitLayoutPanel);
			frame.remove(oldSplitLayoutPanel);
			frame.insert(getSplitLayoutPanel(), wi);
			oldSplitLayoutPanel = getSplitLayoutPanel();
			Browser.removeDefaultContextMenu(
					getSplitLayoutPanel().getElement());
		}
	}

	/**
	 * Attach algebra input
	 */
	public void attachAlgebraInput() {
		// inputbar's width varies,
		// so it's probably good to regenerate every time
		GGWCommandLine inputbar = new GGWCommandLine();
		inputbar.attachApp(this);
		frame.add(inputbar);

		updateSplitPanelHeight();

		this.getGuiManager().getAlgebraInput()
				.setInputFieldWidth(this.appletWidth);
	}

	@Override
	protected final void updateTreeUI() {
		if (getSplitLayoutPanel() != null) {
			((ZoomSplitLayoutPanel) getSplitLayoutPanel()).forceLayout();
		}
		// updateComponentTreeUI();
	}

	/**
	 * @return main panel
	 */
	public DockSplitPaneW getSplitLayoutPanel() {
		if (getGuiManager() == null) {
			return null;
		}
		if (getGuiManager().getLayout() == null) {
			return null;
		}
		return ((GuiManagerW) getGuiManager()).getRootComponent();
	}
	private void attachSplitLayoutPanel() {
		boolean oldSLPanelChanged = oldSplitLayoutPanel == getSplitLayoutPanel() ? false
				: true;
		oldSplitLayoutPanel = getSplitLayoutPanel();

		if (oldSplitLayoutPanel != null) {
			if (!isFloatingMenu()
					&& getArticleElement().getDataParamShowMenuBar(false)) {
				this.splitPanelWrapper = new HorizontalPanel();
				// TODO
				splitPanelWrapper.add(oldSplitLayoutPanel);
				if (this.menuShowing) {
					splitPanelWrapper.add(frame.getMenuBar(this));
				}
				frame.add(splitPanelWrapper);

			} else {
				frame.add(oldSplitLayoutPanel);
			}
			Browser.removeDefaultContextMenu(
					getSplitLayoutPanel().getElement());

			if (!oldSLPanelChanged) {
				return;
			}

			ClickStartHandler.init(oldSplitLayoutPanel,
					new ClickStartHandler() {
						@Override
						public void onClickStart(int x, int y,
								final PointerEventType type) {
							onUnhandledClick();
						}
					});
		}
	}

	@Override
	public void onUnhandledClick() {
		updateAVStylebar();

		if (!isWhiteboardActive() && !CancelEventTimer.cancelKeyboardHide()) {
			Timer timer = new Timer() {
				@Override
				public void run() {
					getAppletFrame().keyBoardNeeded(false, null);
				}
			};
			timer.schedule(0);
		}
	}

	@Override
	public void afterLoadFileAppOrNot(boolean asSlide) {
		closePerspectivesPopup();
		if (!getLAF().isSmart()) {
			removeSplash();
		}
		updateHeaderVisible();
		String perspective = getArticleElement().getDataParamPerspective();
		if (!isUsingFullGui()) {
			if (showConsProtNavigation() || !isJustEuclidianVisible()
					|| perspective.length() > 0) {
				useFullGui = true;
			}
		}
		frame.setApplication(this);
		if (!isUsingFullGui()) {
			Log.printStacktrace("");
			buildSingleApplicationPanel();
			Perspective current = getTmpPerspective(null);
			if (current != null && current.getToolbarDefinition() != null) {
				getGuiManager().setGeneralToolBarDefinition(
						current.getToolbarDefinition());
			}
		} else if (!asSlide) {
			((DockManagerW) getGuiManager().getLayout().getDockManager())
					.init(frame);
			Perspective p = null;
			if (perspective != null) {
				p = PerspectiveDecoder.decode(perspective, this.getKernel()
						.getParser(), ToolBar.getAllToolsNoMacros(true, false, this));
			}
			getGuiManager().updateFrameSize();
			if (articleElement.getDataParamShowAlgebraInput(false)
					&& !isUnbundledOrWhiteboard()) {
				Perspective p2 = getTmpPerspective(p);
				if (!algebraVisible(p2)
						&& getInputPosition() == InputPosition.algebraView) {
					setInputPosition(InputPosition.bottom, false);
					p2.setInputPosition(InputPosition.bottom);
				}
			}
			updatePerspective(p);
		}

		getScriptManager().ggbOnInit(); // put this here from Application
										// constructor because we have to delay
										// scripts until the EuclidianView is
										// shown
		if (!asSlide) {
			initUndoInfoSilent();
		}

		getEuclidianView1().synCanvasSize();

		if (!articleElement.getDataParamFitToScreen()) {
			getAppletFrame().resetAutoSize();
		}

		getEuclidianView1().doRepaint2();
		stopCollectingRepaints();
		frame.splash.canNowHide();

		if (isUsingFullGui()) {
			if (needsSpreadsheetTableModel()) {
				getSpreadsheetTableModel();
			}
			refreshSplitLayoutPanel();

			// probably this method can be changed by more,
			// to be more like AppWapplication's method with the same name,
			// but preferring to change what is needed only to avoid new unknown
			// bugs
			if (getGuiManager().hasSpreadsheetView()) {
				DockPanel sp = getGuiManager().getLayout().getDockManager()
						.getPanel(App.VIEW_SPREADSHEET);
				if (sp != null) {
					sp.deferredOnResize();
				}
			}
		}

		if (isUsingFullGui()) {
			updateNavigationBars();
		}
		this.setPreferredSize(new GDimensionW((int) this.getWidth(), (int) this
				.getHeight()));
		setDefaultCursor();
		GeoGebraFrameW.useDataParamBorder(getArticleElement(), frame);
		GeoGebraProfiler.getInstance().profileEnd();
		onOpenFile();
		showStartTooltip(0);
		setAltText();
		if (!has(Feature.INITIAL_PORTRAIT) && isPortrait()) {
			adjustViews(false, false);
		}
		kernel.notifyScreenChanged();
		resetPenTool();

	}

	private void updatePerspective(Perspective p) {
		if (!isUnbundled() || this.isStartedWithFile()) {
			getGuiManager().getLayout().setPerspectives(getTmpPerspectives(),
					p);
			if (isUnbundled()) {
				getGuiManager().getLayout().getDockManager().adjustViews(true);
			}
		}
	}

	private static boolean algebraVisible(Perspective p2) {
		if (p2 == null || p2.getDockPanelData() == null) {
			return false;
		}
		for (DockPanelData dp : p2.getDockPanelData()) {
			if (dp.getViewId() == App.VIEW_ALGEBRA) {
				return dp.isVisible() && !dp.isOpenInFrame();
			}
		}
		return false;
	}

	@Override
	public void focusLost(View v, Element el) {
		super.focusLost(v, el);
		if (v != focusedView) {
			return;
		}
		focusedView = null;
		GeoGebraFrameW.useDataParamBorder(getArticleElement(), frame);

		// if it is there in focusGained, why not put it here?
		this.getGlobalKeyDispatcher().setFocused(false);
	}

	@Override
	public boolean hasFocus() {
		return focusedView != null;
	}


	@Override
	public void setCustomToolBar() {
		String customToolbar = articleElement.getDataParamCustomToolBar();
		if ((customToolbar != null) && (customToolbar.length() > 0)
				&& (articleElement.getDataParamShowToolBar(false))
				&& (getGuiManager() != null)) {
			getGuiManager().setGeneralToolBarDefinition(customToolbar);
		}
	}

	/**
	 * Check if just the euclidian view is visible in the document just loaded.
	 * 
	 * @return
	 */
	private boolean isJustEuclidianVisible() {
		Perspective docPerspective = getTmpPerspective(null);

		if (docPerspective == null) {
			return true;
		}

		boolean justEuclidianVisible = false;

		for (DockPanelData panel : docPerspective.getDockPanelData()) {
			if ((panel.getViewId() == App.VIEW_EUCLIDIAN) && panel.isVisible()) {
				justEuclidianVisible = true;
			} else if (panel.isVisible()) {
				justEuclidianVisible = false;
				break;
			}
		}

		return justEuclidianVisible;
	}

	@Override
	public void updateCenterPanel() {
		buildApplicationPanel();
		if (oldSplitLayoutPanel == null) {
			return; // simple GUI: avoid NPE
		}
		this.oldSplitLayoutPanel.setPixelSize(spWidth, spHeight);
		// we need relative position to make sure the menubar / toolbar are not
		// hidden
		this.oldSplitLayoutPanel.getElement().getStyle()
				.setPosition(Position.RELATIVE);
		if (!isUnbundled() && getGuiManager().hasAlgebraView()
				&& showView(App.VIEW_ALGEBRA)) {
			((AlgebraViewW) getAlgebraView())
					.setShowAlgebraInput(showAlgebraInput()
							&& getInputPosition() == InputPosition.algebraView);
		}
	}

	@Override
	public double getWidth() {
		if (spWidth > 0) {
			return menuShowing && !isFloatingMenu()
					? spWidth + GLookAndFeel.MENUBAR_WIDTH : spWidth;
		}
		return super.getWidth();
	}

	@Override
	public void updateContentPane() {
		super.updateContentPane();
		frame.setApplication(this);
		frame.refreshKeyboard();
	}

	@Override
	public void persistWidthAndHeight() {
		if (this.oldSplitLayoutPanel != null) {
			spWidth = this.oldSplitLayoutPanel.getEstimateWidth();
			spHeight = this.oldSplitLayoutPanel.getEstimateHeight();
		}
	}

	@Override
	public int getWidthForSplitPanel(int fallback) {
		if (spWidth > 0) {
			return spWidth;
		}
		return super.getWidthForSplitPanel(fallback);
	}

	@Override
	public int getHeightForSplitPanel(int fallback) {
		if (spHeight > 0) {
			return spHeight;
		}
		return super.getHeightForSplitPanel(fallback);
	}

	@Override
	public void toggleMenu() {
		if (!this.menuShowing) {
			this.menuShowing = true;
			boolean needsUpdate = menuInited;
			if (!menuInited) {
				frame.getMenuBar(this).init(this);
				this.menuInited = true;
			}
			if (isFloatingMenu()) {
				toggleFloatingMenu(needsUpdate);
				updateMenuBtnStatus(true);
				return;
			}
			this.splitPanelWrapper.add(frame.getMenuBar(this));
			this.oldSplitLayoutPanel.setPixelSize(
					this.oldSplitLayoutPanel.getOffsetWidth()
							- GLookAndFeel.MENUBAR_WIDTH,
					this.oldSplitLayoutPanel.getOffsetHeight());
			updateMenuHeight();
			if (needsUpdate) {
				frame.getMenuBar(this).getMenubar().updateMenubar();
			}
			this.getGuiManager().refreshDraggingViews();
			oldSplitLayoutPanel.getElement().getStyle()
					.setOverflow(Overflow.HIDDEN);
			getGuiManager().updateStyleBarPositions(true);
			frame.getMenuBar(this).getMenubar().dispatchOpenEvent();
		} else {
			if (isFloatingMenu()) {
				removeFloatingMenu();
				menuShowing = false;
				updateMenuBtnStatus(false);
			} else {
				hideMenu();
			}
		}
	}

	private void updateMenuBtnStatus(boolean expanded) {
		if (getGuiManager() instanceof GuiManagerW) {
			ToolbarPanel toolbarPanel = ((GuiManagerW) getGuiManager())
					.getToolbarPanelV2();
			if (toolbarPanel != null) {
				toolbarPanel.markMenuAsExpanded(expanded);
			}
		}
	}

	private void removeFloatingMenu() {
		this.updateCenterPanelAndViews();
		floatingMenuPanel.addStyleName("animateOut");
		getFrameElement().getStyle().setOverflow(Overflow.HIDDEN);
		CSSAnimation.runOnAnimation(new Runnable() {
			@Override
			public void run() {
				floatingMenuPanel.setVisible(false);
				getFrameElement().getStyle().setOverflow(Overflow.VISIBLE);
			}
		}, floatingMenuPanel.getElement(), "animateOut");

	}

	private void addFloatingMenu() {
		this.updateCenterPanelAndViews();
		floatingMenuPanel.addStyleName("animateIn");
		getFrameElement().getStyle().setOverflow(Overflow.HIDDEN);
		CSSAnimation.runOnAnimation(new Runnable() {
			@Override
			public void run() {
				floatingMenuPanel.setVisible(true);
				getFrameElement().getStyle().setOverflow(Overflow.VISIBLE);
				floatingMenuPanel.focusDeferred();
			}
		}, floatingMenuPanel.getElement(), "animateIn");
	}

	private void toggleFloatingMenu(boolean needsUpdate) {
		if (!isFloatingMenu()) {
			return;
		}
		persistWidthAndHeight();
		if (floatingMenuPanel == null) {
			floatingMenuPanel = new FloatingMenuPanel(
					getAppletFrame().getMenuBar(this));
			if (!isUnbundledOrWhiteboard()) {
				floatingMenuPanel.addStyleName("classic");
			}
			frame.add(floatingMenuPanel);
		}

		if (needsUpdate) {
			frame.getMenuBar(this).getMenubar().updateMenubar();
		}
		if (isFloatingMenu() && menuShowing) {
			this.addFloatingMenu();
			floatingMenuPanel.setVisible(true);

			return;
		}
		final GGWMenuBar menubar = getAppletFrame().getMenuBar(this);
		floatingMenuPanel.add(menubar);
		floatingMenuPanel.setVisible(menuShowing);
		if (has(Feature.TAB_ON_GUI) && menuShowing) {
			menubar.focusDeferred();
		}
		// this.splitPanelWrapper.insert(frame.getMenuBar(this), 0);
	}



	@Override
	public void hideMenu() {
		if (!menuInited || !menuShowing) {
			if (this.getGuiManager() != null) {
				this.getGuiManager().updateStyleBarPositions(false);
			}
			return;
		}

		if (this.isFloatingMenu()) {
			this.toggleMenu();
		} else {
			this.oldSplitLayoutPanel.setPixelSize(
					this.oldSplitLayoutPanel.getOffsetWidth()
							+ GLookAndFeel.MENUBAR_WIDTH,
					this.oldSplitLayoutPanel.getOffsetHeight());
			if (this.splitPanelWrapper != null) {
				this.splitPanelWrapper.remove(frame.getMenuBar(this));
			}
			oldSplitLayoutPanel.getElement().getStyle()
					.setOverflow(Overflow.VISIBLE);
		}
		this.menuShowing = false;

		if (this.getGuiManager() != null
				&& this.getGuiManager().getLayout() != null) {
			this.getGuiManager().getLayout().getDockManager().resizePanels();
		}

		if (this.getGuiManager() != null) {
			this.getGuiManager().setDraggingViews(false, true);
			this.getGuiManager().updateStyleBarPositions(false);
		}
	}

	@Override
	public boolean isMenuShowing() {
		return this.menuShowing;
	}


	@Override
	public void addToHeight(int i) {
		this.spHeight += i;
	}

	/**
	 * Updates height of split panel accordingly if there is algebra input
	 * and/or toolbar or not.
	 */
	@Override
	public void updateSplitPanelHeight() {
		int newHeight = getArticleElement().computeHeight();
		if (this.showAlgebraInput()
				&& getInputPosition() != InputPosition.algebraView
				&& getGuiManager().getAlgebraInput() != null) {
			newHeight -= ((AlgebraInputW) getGuiManager().getAlgebraInput())
					.getOffsetHeight();
		}
		if (getToolbar() != null && getToolbar().isShown()) {
			newHeight -= ((GGWToolBar) getToolbar()).getOffsetHeight();
		}

		if (frame.isKeyboardShowing()) {
			newHeight -= frame.getKeyboardHeight();

		}
		if (newHeight >= 0) {
			this.spHeight = newHeight;
			if (oldSplitLayoutPanel != null) {
				oldSplitLayoutPanel.setHeight(spHeight + "px");
			}
		}
	}

	@Override
	public Panel getPanel() {
		return frame;
	}


	@Override
	public double getInnerWidth() {
		return getWidth();
	}

	@Override
	public void centerAndResizePopups() {
		for (int i = 0; i < popups.size(); i++) {
			Widget w = popups.get(i);
			if (w instanceof HasKeyboardPopup) {
				if (w instanceof DialogBoxW) {
					((DialogBoxW) w).centerAndResize(
							this.getAppletFrame().getKeyboardHeight());
				}
			}
		}
		if (getGuiManager().hasPropertiesView()
				&& has(Feature.FLOATING_SETTINGS)) {
			((PropertiesViewW) getGuiManager().getPropertiesView())
					.resize(getWidth(),
							getHeight() - this.frame.getKeyboardHeight());
		}
	}

	@Override
	public void share() {
		FileMenuW.share(this);
	}

	@Override
	public void setFileVersion(String version, String appName) {
		super.setFileVersion(version, appName);
		if (has(Feature.WEB_SWITCH_APP_FOR_FILE)
				&& "auto".equals(getArticleElement().getDataParamAppName())) {
			getArticleElement().setAttribute("data-param-appName",
					appName);
			Versions v = getVersion();
			if ("graphing".equals(appName)) {
				v = Versions.WEB_GRAPHING;
			}
			else if ("geometry".equals(appName)) {
				v = Versions.WEB_GEOMETRY;
			} else if ("3d".equalsIgnoreCase(appName)) {
				v = Versions.WEB_3D_GRAPHING;
			}
			else if ("classic".equals(appName) || StringUtil.empty(appName)) {
				v = Versions.WEB_FOR_BROWSER_3D;
				removeHeader();
			}
			Log.debug(getVersion() + " change to " + v);
			if (v != getVersion()) {
				setVersion(v);
				((GuiManagerW) getGuiManager()).resetPanels();
			}
		}
	}
}
