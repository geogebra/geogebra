package org.geogebra.web.web.main;

import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.HeaderPanelDeck;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.app.GeoGebraAppFrame;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.move.ggtapi.models.AuthenticationModelW;
import org.geogebra.web.web.move.ggtapi.operations.LoginOperationW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;

public class AppWapplication extends AppWFull {

	private final int AUTO_SAVE_PERIOD = 60000;
	private GeoGebraAppFrame appFrame = null;
	// TODO remove GUI stuff from appW
	private AuthenticationModelW authenticationModel = null;
	private boolean menuInited = false;
	protected final GDevice device;
	private boolean macroRestored;
	/********************************************************
	 * Constructs AppW for full GUI based GeoGebraWeb
	 * 
	 * @param article
	 *            {@link ArticleElement}
	 * @param geoGebraAppFrame
	 *            {@link GeoGebraAppFrame}
	 * @param undoActive
	 *            boolean
	 * @param dimension
	 *            int
	 * @param laf
	 *            {@link GLookAndFeel}
	 * @param device
	 *            {@link GDevice}
	 */
	public AppWapplication(ArticleElement article,
	        GeoGebraAppFrame geoGebraAppFrame, boolean undoActive,
	        int dimension, GLookAndFeel laf, GDevice device) {
		super(article, dimension, laf);
		this.device = device;

		maybeStartAutosave();

		this.appFrame = geoGebraAppFrame;
		if (this.getLAF().isSmart()) {
			if (article.getScaleX() < 0.75) {
				appFrame.getElement().addClassName("zoomed2");
				article.setAttribute("data-scalex", "0.6");
				article.setAttribute("data-scaley", "0.6");
			} else {
				article.setAttribute("data-scalex", "0.8");
				article.setAttribute("data-scaley", "0.8");
				appFrame.getElement().addClassName("zoomed");
			}
			RootPanel.getBodyElement().addClassName("zoomedBody");

			Element el = DOM.getElementById("ggbsplash");
			if (el != null) {
				el.removeFromParent();
			}
		} else {
			RootPanel.getBodyElement().addClassName("application");
		}
		appFrame.app = this;
		App.useFullAppGui = true;
		this.useFullGui = true;
		appCanvasHeight = appFrame.getCanvasCountedHeight();
		appCanvasWidth = appFrame.getCanvasCountedWidth();

		initCommonObjects();

		this.euclidianViewPanel = appFrame.getEuclidianView1Panel();
		this.canvas = euclidianViewPanel.getCanvas();

		initCoreObjects(undoActive, this);
		// user authentication handling
		String token = Location.getParameter("token");
		initSignInEventFlow(new LoginOperationW(this),
		        (token == null || "".equals(token))
		                && Cookies.getCookie("SSID") == null);

		afterCoreObjectsInited();
		App.debug("after core");

		resetFonts();
		// initing = true;
		removeDefaultContextMenu();

		App.debug("checked token");
		if (token != null && !"".equals(token)) {
			App.debug("LTOKEN set via URL");
			this.getLoginOperation().performTokenLogin(token, false);
			this.showBrowser((HeaderPanel) ((GuiManagerW) this.getGuiManager())
			        .getBrowseView());
			nativeLoggedIn();
		} else {
			if (Cookies.getCookie("SSID") != null) {
				this.getLoginOperation().performCookieLogin(
				        Cookies.getCookie("SSID"));
			}
		}

		restoreMacro();
		
	}

	private void maybeStartAutosave() {
		if (hasMacroToRestore() || !this.getLAF().autosaveSupported()) {
			return;
		}

		if (getFileManager().isAutoSavedFileAvailable()
		        && this.getArticleElement().getDataParamTubeID().length() == 0
		        && this.getArticleElement().getDataParamBase64String().length() == 0
				&& this.getArticleElement().getDataParamJSON().length() == 0
				&& this.getExam() == null) {
			((DialogManagerW) getDialogManager())
			        .showRecoverAutoSavedDialog(this);
		} else {
			this.startAutoSave();
		}

	}

	private native void nativeLoggedIn() /*-{
		if (typeof ggbOnLoggedIn == "function") {
			ggbOnLoggedIn();
		}
	}-*/;

	/*************************************************
	 * Constructs AppW for full GUI based GeoGebraWeb with undo enabled
	 * 
	 * @param article
	 *            {@link ArticleElement}
	 * @param geoGebraAppFrame
	 *            {@link GeoGebraAppFrame}
	 * @param dimension
	 *            int
	 * @param laf
	 *            {@link GLookAndFeel}
	 */
	public AppWapplication(ArticleElement article,
	        GeoGebraAppFrame geoGebraAppFrame, int dimension, GLookAndFeel laf,
	        GDevice device) {
		this(article, geoGebraAppFrame, true, dimension, laf, device);
		App.debug("Application created");
	}

	/**
	 * @return {@link GeoGebraAppFrame}
	 */
	public GeoGebraAppFrame getAppFrame() {
		return appFrame;
	}

	/**
	 * if there are unsaved changes, the file is saved to the localStorage.
	 */
	public void startAutoSave() {
		Timer timer = new Timer() {

			@Override
			public void run() {
				if (!isSaved()) {
					getFileManager().autoSave();
				}
			}
		};
		timer.scheduleRepeating(AUTO_SAVE_PERIOD);
	}

	@Override
	public void setSaved() {
		super.setSaved();
		getFileManager().deleteAutoSavedFile();
		getLAF().removeWindowClosingHandler();
	}

	@Override
	public void setUnsaved() {
		super.setUnsaved();
		getLAF().addWindowClosingHandler(this);
	}

	@Override
	protected final void afterCoreObjectsInited() {
		initGuiManager();
		appFrame.onceAfterCoreObjectsInited();
		appFrame.finishAsyncLoading(articleElement, this);
	}

	@Override
	public void appSplashCanNowHide() {

		attachViews();

		// Well, it may cause freeze if we attach this too early

		// allow eg ?command=A=(1,1);B=(2,2) in URL
		String cmd = com.google.gwt.user.client.Window.Location
				.getParameter("command");

		if (cmd != null) {

			App.debug("exectuing commands: " + cmd);

			String[] cmds = cmd.split(";");
			for (int i = 0; i < cmds.length; i++) {
				getKernel().getAlgebraProcessor()
						.processAlgebraCommandNoExceptionsOrErrors(cmds[i],
								false);
			}
		}

		// allow eg
		// http://web.geogebra.org/app/?filename=http://test.geogebra.org/~mike/milestones/ggbFiles/Ellipses.ggb
		// in URL
		String filename = com.google.gwt.user.client.Window.Location
				.getParameter("filename");

		if (filename != null) {
			App.debug("loading file: " + filename);
			GeoGebraAppFrame.fileLoader.getView().processFileName(filename);
		}

	}

	private boolean first = true;

	@Override
	public void afterLoadFileAppOrNot() {
		if (openMacroFromStorage()) {
			return;
		}

		String perspective = getArticleElement().getDataParamPerspective();

		getGuiManager().getLayout().setPerspectives(
		        getTmpPerspectives(),
		        PerspectiveDecoder.decode(perspective, this.getKernel()
		                        .getParser(), ToolBar.getAllToolsNoMacros(true,
		                        false)));

		getScriptManager().ggbOnInit(); // put this here from Application
		                                // constructor because we have to delay
		                                // scripts until the EuclidianView is
		                                // shown

		if (first) {
			initUndoInfoSilent();
		} else {
			kernel.initUndoInfo();
		}
		first = false;

		stopCollectingRepaints();
		// Well, it may cause freeze if we attach this too early
		attachViews();

		// this is needed otherwise the coordinate system of EV would not be OK
		// put in a deferred call because of drag & dropping files
		updateViewSizes();

		updateNavigationBars();

		setDefaultCursor();
		GeoGebraProfiler.getInstance().profileEnd();
		((GGWToolBar) this.getToolbar()).updateToolbarPanel();
		onOpenFile();
		setAltText();
	}

	@Override
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
		getAppletFrame().setMenuHeight(
				getInputPosition() == InputPositon.bottom);
	}

	@Override
	public void buildApplicationPanel() {
		updateCenterPanel(true);
	}

	@Override
	public void updateCenterPanel(boolean updateUI) {
		if (showAlgebraInput()) {
			appFrame.getAlgebraInput().attachApp(this);
		}
		if (isUsingFullGui()) {
			appFrame.setFrameLayout();
		} else {
			// TODO: handle applets?
			// centerPanel.add(this.getEuclidianViewpanel());
		}

		appFrame.onResize();

		if (updateUI) {
			// SwingUtilities.updateComponentTreeUI(centerPanel);
		}
	}

	@Override
	public Object getGlassPane() {
		return getAppFrame().getGlassPane();
	}

	@Override
	public int getOWidth() {
		int width = getAppFrame().getOffsetWidth();
		if (width <= 1 && Location.getParameter("GeoGebraTargetWidth") != null) {
			width = getIntParam("GeoGebraTargetWidth");
		}
		return width;
	}

	private static int getIntParam(String string) {
		int res = 0;
		try {
			res = (int) Float.parseFloat(Location.getParameter(string).replace(
					',', '.'));
		} catch (Exception ex) {
			App.debug("Invalid parameter " + string + ":"
					+ Location.getParameter(string));
		}
		return res;
	}

	@Override
	public int getOHeight() {
		int height = getAppFrame().getOffsetHeight();
		if (height <= 1
				&& Location.getParameter("GeoGebraTargetHeight") != null) {
			height = getIntParam("GeoGebraTargetHeight");
		}
		return height;
	}

	@Override
	public void doOnResize() {
		getAppFrame().onResize();
	}



	@Override
	public void loadURL_GGB(String ggburl) {
		getAppFrame().fileLoader.getView().processFileName(ggburl);
	}

	@Override
	public void syncAppletPanelSize(int widthDiff, int heightDiff, int evno) {
		// this method is overridden in each subclass of AppW,
		// in order to override the behaviour in AppWeb
	}

	@Override
	public void toggleMenu() {
		if (!this.menuInited) {
			appFrame.getMenuBar().init(this);
			this.menuInited = true;
		}
		boolean menuOpen = appFrame.toggleMenu();
		if (!menuOpen && this.getGuiManager() != null) {
			this.getGuiManager().setDraggingViews(false, true);
		}
		if (menuOpen) {
			this.getGuiManager().refreshDraggingViews();
		}
	}

	@Override
	public void hideMenu() {
		if (!this.menuInited) {
			return;
		}
		appFrame.hideMenu();
		if (this.getGuiManager() != null) {
			this.getGuiManager().setDraggingViews(false, true);
		}
	}

	@Override
	public boolean isMenuShowing() {
		return appFrame.isMenuOpen();
	}

	@Override
	public HeaderPanelDeck getAppletFrame() {
		return appFrame;
	}

	@Override
	public Panel getPanel() {
		return RootPanel.get();
	}

	@Override
	protected GDevice getDevice() {
		return device;
	}
}
