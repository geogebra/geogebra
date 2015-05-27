package org.geogebra.web.web.main;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.kernel.View;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.GeoGebraFrame;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.javax.swing.GOptionPaneW;
import org.geogebra.web.html5.main.FileManagerI;
import org.geogebra.web.html5.main.GeoGebraTubeAPIWSimple;
import org.geogebra.web.html5.main.HasAppletProperties;
import org.geogebra.web.html5.main.StringHandler;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.URL;
import org.geogebra.web.web.gui.CustomizeToolbarGUI;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.LanguageGUI;
import org.geogebra.web.web.gui.MyHeaderPanel;
import org.geogebra.web.web.gui.app.GGWCommandLine;
import org.geogebra.web.web.gui.app.GGWMenuBar;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.layout.LayoutW;
import org.geogebra.web.web.gui.layout.ZoomSplitLayoutPanel;
import org.geogebra.web.web.gui.layout.panels.AlgebraStyleBarW;
import org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.web.helper.ObjectPool;
import org.geogebra.web.web.move.ggtapi.operations.LoginOperationW;
import org.geogebra.web.web.move.googledrive.operations.GoogleDriveOperationW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AppWapplet extends AppWFull {

	private GuiManagerInterfaceW guiManager = null;

	// Event flow operations - are these needed in AppWapplet?

	// private LogInOperation loginOperation;
	private GGWMenuBar ggwMenuBar;
	private GGWToolBar ggwToolBar = null;
	private int spWidth;
	private int spHeight;
	private boolean menuShowing = false;
	private boolean menuInited = false;
	private ObjectPool objectPool;
	// TODO remove GUI stuff from appW
	private LanguageGUI lg;
	private GeoGebraFrameBoth frame;

	/******************************************************
	 * Constructs AppW for applets with undo enabled
	 * 
	 * @param ae
	 * @param gf
	 */
	public AppWapplet(ArticleElement ae, GeoGebraFrameBoth gf, int dimension,
	        GLookAndFeel laf) {
		this(ae, gf, true, dimension, laf);
	}

	/******************************************************
	 * Constructs AppW for applets
	 * 
	 * @param undoActive
	 *            if true you can undo by CTRL+Z and redo by CTRL+Y
	 */
	public AppWapplet(ArticleElement ae, GeoGebraFrameBoth gf,
	        final boolean undoActive, int dimension, GLookAndFeel laf) {
		super(ae, dimension, laf);
		this.frame = gf;
		this.objectPool = new ObjectPool();
		setAppletHeight(frame.getComputedHeight());
		setAppletWidth(frame.getComputedWidth());

		this.useFullGui = !isApplet() || ae.getDataParamShowAlgebraInput(false)
		        || ae.getDataParamShowToolBar(false)
		        || ae.getDataParamShowMenuBar(false)
		        || ae.getDataParamEnableRightClick();

		Log.info("GeoGebra " + GeoGebraConstants.VERSION_STRING + " "
		        + GeoGebraConstants.BUILD_DATE + " "
		        + Window.Navigator.getUserAgent());
		initCommonObjects();
		initing = true;

		this.euclidianViewPanel = new EuclidianDockPanelW(this,
		        getArticleElement().getDataParamShowMenuBar(false)
		                || getArticleElement().getDataParamAllowStyleBar(false));
		// (EuclidianDockPanelW)getGuiManager().getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN);
		this.canvas = this.euclidianViewPanel.getCanvas();
		canvas.setWidth("1px");
		canvas.setHeight("1px");
		canvas.setCoordinateSpaceHeight(1);
		canvas.setCoordinateSpaceWidth(1);
		initCoreObjects(undoActive, this);
		afterCoreObjectsInited();
		resetFonts();
		removeDefaultContextMenu(this.getArticleElement());
		if (this.showMenuBar()) {
			this.initSignInEventFlow(new LoginOperationW(this), true);
		} else {
			if (Browser.runningLocal()) {
				new GeoGebraTubeAPIWSimple(has(Feature.TUBE_BETA))
						.checkAvailable(null);
			}
		}
	}

	public GGWMenuBar getMenuBar() {
		if (ggwMenuBar == null) {
			ggwMenuBar = new GGWMenuBar();
			((GuiManagerW) getGuiManager()).getObjectPool().setGgwMenubar(
			        ggwMenuBar);
		}
		return ggwMenuBar;
	}

	@Override
	public HasAppletProperties getAppletFrame() {
		return frame;
	}

	@Override
	public GuiManagerInterfaceW getGuiManager() {
		return guiManager;
	}

	@Override
	public void initGuiManager() {
		// this should not be called from AppWsimple!
		setWaitCursor();
		guiManager = newGuiManager();
		getGuiManager().setLayout(new org.geogebra.web.web.gui.layout.LayoutW(this));
		getGuiManager().initialize();
		setDefaultCursor();
	}

	/**
	 * @return a GuiManager for GeoGebraWeb
	 */
	protected GuiManagerW newGuiManager() {
		return new GuiManagerW(AppWapplet.this, new BrowserDevice());
	}

	@Override
	protected void afterCoreObjectsInited() {
		// Code to run before buildApplicationPanel
		initGuiManager();
		if (this.showConsProtNavigation()) {
			((EuclidianDockPanelW) euclidianViewPanel).addNavigationBar();
		}
		// following lines were swapped before but for async file loading it
		// does not matter
		// and for sync file loading this makes sure perspective setting is not
		// blocked by initing flag
		initing = false;
		GeoGebraFrame.finishAsyncLoading(articleElement, frame, this);

	}

	public void buildSingleApplicationPanel() {
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

	private Widget oldSplitLayoutPanel = null; // just a technical helper
	                                           // variable
	private HorizontalPanel splitPanelWrapper = null;

	private CustomizeToolbarGUI ct;

	@Override
	public void buildApplicationPanel() {

		if (!isUsingFullGui()) {
			if (showConsProtNavigation || !isJustEuclidianVisible()) {
				useFullGui = true;
			}
		}

		if (!isUsingFullGui()) {
			buildSingleApplicationPanel();
			return;
		}

		frame.clear();

		// showMenuBar should come from data-param,
		// this is just a 'second line of defense'
		// otherwise it can be used for taking ggb settings into account too
		if (articleElement.getDataParamShowMenuBar(showMenuBar)) {
			attachMenubar();
		}

		// showToolBar should come from data-param,
		// this is just a 'second line of defense'
		// otherwise it can be used for taking ggb settings into account too
		if (articleElement.getDataParamShowToolBar(showToolBar)) {
			attachToolbar();
		}
		if (this.getInputPosition() == InputPositon.top
		        && articleElement
		                .getDataParamShowAlgebraInput(showAlgebraInput)) {
			attachAlgebraInput();
		}
		attachSplitLayoutPanel();

		// showAlgebraInput should come from data-param,
		// this is just a 'second line of defense'
		// otherwise it can be used for taking ggb settings into account too
		if (this.getInputPosition() == InputPositon.bottom
		        && articleElement
		                .getDataParamShowAlgebraInput(showAlgebraInput)) {
			attachAlgebraInput();
		}

		frame.attachGlass();
	}

	public void refreshSplitLayoutPanel() {
		if (frame != null && frame.getWidgetCount() != 0
		        && frame.getWidgetIndex(getSplitLayoutPanel()) == -1
		        && frame.getWidgetIndex(oldSplitLayoutPanel) != -1) {
			int wi = frame.getWidgetIndex(oldSplitLayoutPanel);
			frame.remove(oldSplitLayoutPanel);
			frame.insert(getSplitLayoutPanel(), wi);
			oldSplitLayoutPanel = getSplitLayoutPanel();
			removeDefaultContextMenu(getSplitLayoutPanel().getElement());
		}
	}

	public void attachAlgebraInput() {
		// inputbar's width varies,
		// so it's probably good to regenerate every time
		GGWCommandLine inputbar = new GGWCommandLine();
		inputbar.attachApp(this);
		frame.add(inputbar);
		this.getGuiManager().getAlgebraInput()
		        .setInputFieldWidth(this.appletWidth);
	}

	public void attachMenubar() {
		if (ggwToolBar == null) {
			ggwToolBar = new GGWToolBar();
			ggwToolBar.init(this);
			frame.insert(ggwToolBar, 0);
		}
		ggwToolBar.attachMenubar();
	}

	public void attachToolbar() {
		// reusing old toolbar is probably a good decision
		if (ggwToolBar == null) {
			ggwToolBar = new GGWToolBar();
			ggwToolBar.init(this);
		}
		frame.insert(ggwToolBar, 0);
	}

	@Override
	public GGWToolBar getToolbar() {
		return ggwToolBar;
	}

	public void attachSplitLayoutPanel() {
		oldSplitLayoutPanel = getSplitLayoutPanel();
		
		if (oldSplitLayoutPanel != null) {
			if (getArticleElement().getDataParamShowMenuBar(false)) {
				this.splitPanelWrapper = new HorizontalPanel();

				splitPanelWrapper.add(oldSplitLayoutPanel);
				if (this.menuShowing) {
				splitPanelWrapper.add(getMenuBar());
				}
				frame.add(splitPanelWrapper);

			} else {
				frame.add(oldSplitLayoutPanel);
			}
			removeDefaultContextMenu(getSplitLayoutPanel().getElement());
		
			ClickStartHandler.init(oldSplitLayoutPanel, new ClickStartHandler() {
				@Override
				public void onClickStart(int x, int y, final PointerEventType type) {
							AlgebraStyleBarW styleBar = ((AlgebraViewW)
							getView(App.VIEW_ALGEBRA)).getStyleBar(false);
					if (styleBar != null) {
						styleBar.update(null);
					}
	
					if (!CancelEventTimer.cancelKeyboardHide()) {
						Timer timer = new Timer() {
							@Override
							public void run() {
								frame.keyBoardNeeded(false, null);
							}
						};
						timer.schedule(0);
					}
				}
			});
		}
	}

	@Override
	public void afterLoadFileAppOrNot() {
		String perspective = getArticleElement().getDataParamPerspective();
		if (!isUsingFullGui()) {
			if (showConsProtNavigation || !isJustEuclidianVisible()
			        || perspective.length() > 0) {
				useFullGui = true;
			}
		}

		if (!isUsingFullGui()) {
			buildSingleApplicationPanel();
		} else {
			// a small thing to fix a rare bug
			((LayoutW) getGuiManager().getLayout()).getDockManager()
			        .kickstartRoot(frame);
			Perspective p = null;
			if (perspective != null) {
				p = PerspectiveDecoder.decode(perspective, this.getKernel()
				        .getParser(), ToolBar.getAllToolsNoMacros(true, false));
			}
			getGuiManager().getLayout()
			        .setPerspectives(getTmpPerspectives(), p);
		}

		getScriptManager().ggbOnInit(); // put this here from Application
		                                // constructor because we have to delay
		                                // scripts until the EuclidianView is
		                                // shown

		initUndoInfoSilent();

		getEuclidianView1().synCanvasSize();

		getAppletFrame().resetAutoSize();

		getEuclidianView1().doRepaint2();
		stopCollectingRepaints();
		frame.splash.canNowHide();
		if (!articleElement.preventFocus()) {
			requestFocusInWindow();
		}

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

		if (isUsingFullGui())
			this.getEuclidianViewpanel().updateNavigationBar();
		setDefaultCursor();
		GeoGebraFrame.useDataParamBorder(getArticleElement(),
 frame);
		GeoGebraProfiler.getInstance().profileEnd();
		onOpenFile();
		showStartTooltip();
	}

	private View focusedView;
	@Override
	public void focusLost(View v) {
		super.focusLost(v);
		if (v != focusedView) {
			return;
		}
		focusedView = null;
		GeoGebraFrame.useDataParamBorder(getArticleElement(),
 frame);
	}

	@Override
	public void focusGained(View v) {
		super.focusGained(v);
		focusedView = v;
		GeoGebraFrame.useFocusedBorder(getArticleElement(), frame);
	}

	@Override
	public boolean hasFocus() {
		return focusedView != null;
	}

	@Override
	protected CustomizeToolbarGUI getCustomizeToolbarGUI() {
		if (this.ct == null) {
			this.ct = new CustomizeToolbarGUI(this);
		}
		return this.ct;
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

	@Override
	public void syncAppletPanelSize(int widthDiff, int heightDiff, int evno) {
		if (evno == 1 && getEuclidianView1().isShowing()) {
			// this should follow the resizing of the EuclidianView
			if (getSplitLayoutPanel() != null)
				getSplitLayoutPanel().setPixelSize(
				        getSplitLayoutPanel().getOffsetWidth() + widthDiff,
				        getSplitLayoutPanel().getOffsetHeight() + heightDiff);
		} else if (evno == 2 && hasEuclidianView2(1)
		        && getEuclidianView2(1).isShowing()) {// or the EuclidianView 2
			if (getSplitLayoutPanel() != null)
				getSplitLayoutPanel().setPixelSize(
				        getSplitLayoutPanel().getOffsetWidth() + widthDiff,
				        getSplitLayoutPanel().getOffsetHeight() + heightDiff);
		}
	}

	@Override
	public DialogManager getDialogManager() {
		if (dialogManager == null) {
			dialogManager = new DialogManagerW(this);
		}
		return dialogManager;
	}

	/**
	 * Check if just the euclidian view is visible in the document just loaded.
	 * 
	 * @return
	 */
	private boolean isJustEuclidianVisible() {
		if (tmpPerspectives == null) {
			return true; // throw new OperationNotSupportedException();
		}

		Perspective docPerspective = null;

		for (Perspective perspective : tmpPerspectives) {
			if (perspective.getId().equals("tmp")) {
				docPerspective = perspective;
			}
		}

		if (docPerspective == null) {
			return true; // throw new OperationNotSupportedException();
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
	public Element getFrameElement() {
		return frame.getElement();
	}

	@Override
	public String getArticleId() {
		return articleElement.getId();
	}

	@Override
	public void updateCenterPanel(boolean b) {

		// int left = this.oldSplitLayoutPanel.getAbsoluteLeft();
		// int top = this.oldSplitLayoutPanel.getAbsoluteLeft();
		buildApplicationPanel();
		this.oldSplitLayoutPanel.setPixelSize(spWidth, spHeight);
		// we need relative position to make sure the menubar / toolbar are not
		// hiddn
		this.oldSplitLayoutPanel.getElement().getStyle()
		        .setPosition(Position.RELATIVE);
		
		// TODO

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
			spWidth = this.oldSplitLayoutPanel.getOffsetWidth();
			spHeight = this.oldSplitLayoutPanel.getOffsetHeight();
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
			if (!menuInited) {
				this.getMenuBar().init(this);
				this.menuInited = true;
			}
			this.splitPanelWrapper.add(this.getMenuBar());
			this.oldSplitLayoutPanel.setPixelSize(
			        this.oldSplitLayoutPanel.getOffsetWidth()
			                - GLookAndFeel.MENUBAR_WIDTH,
			        this.oldSplitLayoutPanel.getOffsetHeight());
			this.getMenuBar().setPixelSize(GLookAndFeel.MENUBAR_WIDTH,
			        this.oldSplitLayoutPanel.getOffsetHeight());
			this.getGuiManager().refreshDraggingViews();
		} else {
			hideMenu();
		}
	}

	@Override
	public void hideMenu() {
		if (!menuInited || !menuShowing) {
			return;
		}
		this.menuShowing = false;
		this.oldSplitLayoutPanel.setPixelSize(
				this.oldSplitLayoutPanel.getOffsetWidth()
						+ GLookAndFeel.MENUBAR_WIDTH,
				this.oldSplitLayoutPanel.getOffsetHeight());

		this.splitPanelWrapper.remove(this.getMenuBar());
		if (this.getGuiManager() != null
				&& this.getGuiManager().getLayout() != null) {
			this.getGuiManager().getLayout().getDockManager().resizePanels();
		}

		if (this.getGuiManager() != null) {
			this.getGuiManager().setDraggingViews(false, true);
		}
	}

	@Override
	public boolean isMenuShowing() {
		return this.menuShowing;
	}

	@Override
	public Object getGlassPane() {
		return frame.getGlassPane();
	}

	@Override
	public void showBrowser(HeaderPanel bg) {
		frame.showBrowser(bg);
	}

	@Override
	public void openSearch(String query) {
		showBrowser((MyHeaderPanel) getGuiManager().getBrowseView(query));
	}

	@Override
	public LanguageGUI getLanguageGUI() {
		if (this.lg == null) {
			this.lg = new LanguageGUI(this);
		}
		return this.lg;
	}

	@Override
	public void uploadToGeoGebraTube() {
		showURLinBrowserWaiterFixedDelay();
		final GeoGebraTubeExportWeb ggbtube = new GeoGebraTubeExportWeb(this);
		getGgbApi().getBase64(true, new StringHandler() {

			@Override
			public void handle(String s) {
				ggbtube.uploadWorksheetSimple(s);

			}
		});
	}

	@Override
	public void set1rstMode() {
		GGWToolBar.set1rstMode(this);
	}

	@Override
	protected void initGoogleDriveEventFlow() {

		googleDriveOperation = new GoogleDriveOperationW(this);
		String state = URL.getQueryParameterAsString("state");
		if (getNetworkOperation().isOnline() && state != null
		        && !"".equals(state)) {
			googleDriveOperation.initGoogleDriveApi();
		}

	}

	@Override
	protected void updateTreeUI() {

		((ZoomSplitLayoutPanel) getSplitLayoutPanel()).forceLayout();
		// updateComponentTreeUI();

	}

	@Override
	public FileManagerI getFileManager() {
		if (this.fm == null) {
			this.fm = new FileManagerW(this);
		}
		return this.fm;
	}

	@Override
	public void setLabels() {
		super.setLabels();
		if (this.lg != null) {
			lg.setLabels();
		}
	}

	@Override
	public boolean isSelectionRectangleAllowed() {
		return getToolbar() != null;
	}

	@Override
	public native void copyBase64ToClipboardChromeWebAppCase(String str) /*-{
		// solution copied from geogebra.web.gui.view.spreadsheet.CopyPasteCutW.copyToSystemClipboardChromeWebapp
		// although it's strange that .contentEditable is not set to true
		var copyFrom = @org.geogebra.web.web.gui.view.spreadsheet.CopyPasteCutW::getHiddenTextArea()();
		copyFrom.value = str;
		copyFrom.select();
		$doc.execCommand('copy');
	}-*/;

	@Override
	public void showConfirmDialog(String title, String mess) {
		GOptionPaneW.INSTANCE.showInputDialog(this, "", title, mess,
		        GOptionPane.OK_CANCEL_OPTION, GOptionPane.PLAIN_MESSAGE, null,
		        null, null);
	}

	@Override
	public void addToHeight(int i) {
		this.spHeight += i;
	}

	public void updateViewSizes() {
		getEuclidianViewpanel().deferredOnResize();
		if (hasEuclidianView2(1)) {
			((GuiManagerW) getGuiManager()).getEuclidianView2DockPanel(1)
			        .deferredOnResize();
		}
		if (getGuiManager().hasSpreadsheetView()) {
			DockPanel sp = getGuiManager().getLayout().getDockManager()
			        .getPanel(App.VIEW_SPREADSHEET);
			if (sp != null) {
				sp.deferredOnResize();
			}
		}
	}
}
