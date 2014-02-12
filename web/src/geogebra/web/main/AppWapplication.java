package geogebra.web.main;

import geogebra.common.main.App;
import geogebra.common.main.DialogManager;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.html5.gui.browser.BrowseGUI;
import geogebra.html5.move.ggtapi.models.AuthenticationModelW;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.GuiManagerInterfaceW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.infobar.InfoBarW;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.helper.ObjectPool;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.LayoutPanel;


public class AppWapplication extends AppW {

	private GeoGebraAppFrame appFrame = null;
	protected GuiManagerInterfaceW guiManager = null;
	private geogebra.web.gui.app.SplashDialog splashDialog = null;

	//Event flow operations
	

	/********************************************************
	 * Constructs AppW for full GUI based GeoGebraWeb
	 * 
	 * @param article
	 * @param geoGebraAppFrame
	 * @param undoActive
	 */
	public AppWapplication(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame,
	        boolean undoActive) {
		super(article);
		this.appFrame = geoGebraAppFrame;
		appFrame.app = this;
		this.objectPool = new ObjectPool();
		createAppSplash();
		App.useFullAppGui = true;
		this.useFullGui = true;
		appCanvasHeight = appFrame.getCanvasCountedHeight();
		appCanvasWidth = appFrame.getCanvasCountedWidth();

		setCurrentFileId();
		
		infobar = new InfoBarW(this);

		initCommonObjects();
		// user authentication handling
		initSignInEventFlow();

		this.euclidianViewPanel = appFrame.getEuclidianView1Panel();
		this.canvas = euclidianViewPanel.getCanvas();

		initCoreObjects(undoActive, this);

		// initing = true;
		removeDefaultContextMenu();
	}

	/*************************************************
	 * Constructs AppW for full GUI based GeoGebraWeb with undo enabled
	 * 
	 * @param article
	 * @param geoGebraAppFrame
	 */
	public AppWapplication(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame) {
		this(article, geoGebraAppFrame, true);
	}

	public GeoGebraAppFrame getAppFrame() {
		return appFrame;
	}
	
	private AuthenticationModelW authenticationModel = null;
	
	@Override
	public GuiManagerInterfaceW getGuiManager() {
		return guiManager;
	}

	@Override
	public void initGuiManager() {
		// this should not be called from AppWsimple!
		setWaitCursor();
		guiManager = newGuiManager();
		getGuiManager().setLayout(new geogebra.web.gui.layout.LayoutW());
		getGuiManager().initialize();
		setDefaultCursor();
	}

	/**
	 * @return a GuiManager for GeoGebraWeb
	 */
	protected GuiManagerW newGuiManager() {
		return new GuiManagerW(AppWapplication.this);
	}

	private void createAppSplash() {
		splashDialog = new geogebra.web.gui.app.SplashDialog();
	}

	@Override
	protected void afterCoreObjectsInited() {
		initGuiManager();
		appFrame.onceAfterCoreObjectsInited();
		appFrame.finishAsyncLoading(articleElement, appFrame, this);
	}

	@Override
    public void appSplashCanNowHide() {
		if (splashDialog != null) {
			splashDialog.canNowHide();
			attachViews();
		}

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

	@Override
    public void afterLoadFileAppOrNot() {

		getGuiManager().getLayout().setPerspectives(getTmpPerspectives());

		getScriptManager().ggbOnInit();	// put this here from Application constructor because we have to delay scripts until the EuclidianView is shown

		kernel.initUndoInfo();

		splashDialog.canNowHide();

		stopCollectingRepaints();
		// Well, it may cause freeze if we attach this too early
		attachViews();

		// this is needed otherwise the coordinate system of EV would not be OK
		// put in a deferred call because of drag & dropping files
		getEuclidianViewpanel().deferredOnResize();
		if (hasEuclidianView2()) {
			getGuiManager().getEuclidianView2DockPanel().deferredOnResize();
		}
		if (getGuiManager().hasSpreadsheetView()) {
			DockPanelW sp = getGuiManager().getLayout().getDockManager().getPanel(App.VIEW_SPREADSHEET);
			if (sp != null) {
				sp.deferredOnResize();
			}
		}

		this.getEuclidianViewpanel().updateNavigationBar();
		setDefaultCursor();
		GeoGebraProfiler.getInstance().profileEnd();
		((GGWToolBar)this.getToolbar()).updateToolbarPanel();
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
		LayoutPanel centerPanel = null;
		
		if (isUsingFullGui()) {
			appFrame.setFrameLayout();
		} else {
			//TODO: handle applets?
			//centerPanel.add(this.getEuclidianViewpanel());
		}

		appFrame.onResize();
		
		if (updateUI) {
			//SwingUtilities.updateComponentTreeUI(centerPanel);
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
	public Object getToolbar() {
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
			if (getGoogleDriveOperation() != null){
				getGoogleDriveOperation().getView().add((DialogManagerW)dialogManager);
			}
		}
		return dialogManager;
	}
	
    @Override
    public Element getFrameElement(){
		return  appFrame.getElement();
	}
    
	@Override
    public void showBrowser(BrowseGUI bg) {
	    appFrame.showBrowser(bg);
    }
}
