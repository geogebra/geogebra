package geogebra.web.main;

import geogebra.common.gui.layout.DockPanel;
import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.io.layout.PerspectiveDecoder;
import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.main.App;
import geogebra.common.main.DialogManager;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.html5.euclidian.EuclidianViewW;
import geogebra.html5.gui.GuiManagerInterfaceW;
import geogebra.html5.gui.ToolBarInterface;
import geogebra.html5.gui.util.CancelEventTimer;
import geogebra.html5.javax.swing.GOptionPaneW;
import geogebra.html5.main.AppW;
import geogebra.html5.main.FileManagerI;
import geogebra.html5.main.StringHandler;
import geogebra.html5.util.ArticleElement;
import geogebra.html5.util.URL;
import geogebra.web.gui.CustomizeToolbarGUI;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.LanguageGUI;
import geogebra.web.gui.MyHeaderPanel;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.gui.layout.ZoomSplitLayoutPanel;
import geogebra.web.helper.ObjectPool;
import geogebra.web.move.ggtapi.models.AuthenticationModelW;
import geogebra.web.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.move.ggtapi.models.MaterialCallback;
import geogebra.web.move.ggtapi.operations.LoginOperationW;
import geogebra.web.move.googledrive.operations.GoogleDriveOperationW;
import geogebra.web.util.keyboard.OnScreenKeyBoard;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class AppWapplication extends AppW {

	private final int AUTO_SAVE_PERIOD = 60000;
	private GeoGebraAppFrame appFrame = null;
	private GuiManagerInterfaceW guiManager = null;
	private ObjectPool objectPool;
	// TODO remove GUI stuff from appW
	private LanguageGUI lg;
	private AuthenticationModelW authenticationModel = null;
	private boolean menuInited = false;
	private CustomizeToolbarGUI ct;
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
		this.objectPool = new ObjectPool();
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
		        && this.getArticleElement().getDataParamJSON().length() == 0) {
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
	public GuiManagerInterfaceW getGuiManager() {
		return guiManager;
	}

	@Override
	public void initGuiManager() {
		// this should not be called from AppWsimple!
		setWaitCursor();
		guiManager = newGuiManager();
		getGuiManager().setLayout(new geogebra.web.gui.layout.LayoutW(this));
		getGuiManager().initialize();
		setDefaultCursor();
	}

	/**
	 * @return a GuiManager for GeoGebraWeb
	 */
	protected GuiManagerW newGuiManager() {
		return new GuiManagerW(AppWapplication.this, this.device);
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


		this.getEuclidianViewpanel().updateNavigationBar();
		setDefaultCursor();
		GeoGebraProfiler.getInstance().profileEnd();
		((GGWToolBar) this.getToolbar()).updateToolbarPanel();
		onOpenFile();
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
		appFrame.setMenuHeight(getInputPosition() == InputPositon.bottom);
	}

	@Override
	public boolean menubarRestricted() {
		return false;
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
		return getAppFrame().getOffsetWidth();
	}

	@Override
	public int getOHeight() {
		return getAppFrame().getOffsetHeight();
	}

	@Override
	public void doOnResize() {
		getAppFrame().onResize();
	}

	@Override
	public ToolBarInterface getToolbar() {
		return getAppFrame().getGGWToolbar();
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
	public DialogManager getDialogManager() {
		if (dialogManager == null) {
			dialogManager = new DialogManagerW(this);
			if (getGoogleDriveOperation() != null) {
				((GoogleDriveOperationW) getGoogleDriveOperation()).getView()
				        .add((DialogManagerW) dialogManager);
			}
		}
		return dialogManager;
	}

	@Override
	public Element getFrameElement() {
		return appFrame.getElement();
	}

	@Override
	public void showBrowser(HeaderPanel bg) {
		appFrame.showBrowser(bg);
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
	protected CustomizeToolbarGUI getCustomizeToolbarGUI() {
		if (this.ct == null) {
			this.ct = new CustomizeToolbarGUI(this);
		}
		return this.ct;
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
	public final FileManagerI getFileManager() {
		if (this.fm == null) {
			this.fm = this.device.createFileManager(this);
		}
		return this.fm;
	}

	@Override
	protected void updateTreeUI() {
		((ZoomSplitLayoutPanel) getSplitLayoutPanel()).forceLayout();
		// updateComponentTreeUI();
	}

	@Override
	public void setLabels() {
		super.setLabels();
		if (this.lg != null) {
			this.lg.setLabels();
		}
	}

	@Override
	public void showKeyboard(Widget textField) {
		getAppFrame().showKeyBoard(true, textField);
		if (textField != null) {
			CancelEventTimer.keyboardSetVisible();
		}
	}

	@Override
	public void updateKeyBoardField(Widget field) {
		OnScreenKeyBoard.setInstanceTextField(field);
	}

	@Override
	public void hideKeyboard() {
		getAppFrame().showKeyBoard(false, null);
	}

	@Override
	public void openMaterial(String s, final Runnable onError) {
		((GeoGebraTubeAPIW) getLoginOperation().getGeoGebraTubeAPI()).getItem(
		        s, new MaterialCallback() {

			        @Override
			        public void onLoaded(final List<Material> parseResponse) {
				        if (parseResponse.size() == 1) {
					        Material material = parseResponse.get(0);
					        material.setSyncStamp(parseResponse.get(0)
					                .getModified());
					        getGgbApi().setBase64(material.getBase64());
					        setActiveMaterial(material);
				        } else {
					        onError.run();
				        }
			        }

			        @Override
			        public void onError(Throwable error) {
				        onError.run();
			        }
		        });

	}

	@Override
	public void copyEVtoClipboard() {
		device.copyEVtoClipboard(getEuclidianView1());
	}

	@Override
	public void copyEVtoClipboard(EuclidianViewW ev) {
		device.copyEVtoClipboard(ev);
	}

	@Override
	public boolean isOffline() {
		return device.isOffline(this);
	}

	@Override
	public boolean isSelectionRectangleAllowed() {
		return true;
	}

	@Override
	public native void copyBase64ToClipboardChromeWebAppCase(String str) /*-{
		// solution copied from geogebra.web.gui.view.spreadsheet.CopyPasteCutW.copyToSystemClipboardChromeWebapp
		// although it's strange that .contentEditable is not set to true
		var copyFrom = @geogebra.web.gui.view.spreadsheet.CopyPasteCutW::getHiddenTextArea()();
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
}
