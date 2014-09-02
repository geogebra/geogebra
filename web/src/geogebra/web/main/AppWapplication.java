package geogebra.web.main;

import geogebra.common.gui.layout.DockPanel;
import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.io.layout.PerspectiveDecoder;
import geogebra.common.main.App;
import geogebra.common.main.DialogManager;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.html5.gui.GuiManagerInterfaceW;
import geogebra.html5.main.AppW;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.MyHeaderPanel;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.helper.ObjectPool;
import geogebra.web.move.ggtapi.models.AuthenticationModelW;
import geogebra.web.move.ggtapi.operations.LoginOperationW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.RootPanel;


public class AppWapplication extends AppW {

	private GeoGebraAppFrame appFrame = null;
	protected GuiManagerInterfaceW guiManager = null;
	protected ObjectPool objectPool;
	//Event flow operations
	

	/********************************************************
	 * Constructs AppW for full GUI based GeoGebraWeb
	 * 
	 * @param article
	 * @param geoGebraAppFrame
	 * @param undoActive
	 */
	public AppWapplication(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame,
	        boolean undoActive, int dimension, GLookAndFeel laf) {
		super(article, dimension, laf);
		this.appFrame = geoGebraAppFrame;
		if(this.getLAF().isSmart()){
			appFrame.getElement().addClassName("zoomed");
			RootPanel.getBodyElement().addClassName("zoomedBody");
			article.setAttribute("data-scalex", "0.8");
			article.setAttribute("data-scaley", "0.8");
		}
		else {
			RootPanel.getBodyElement().addClassName("application");
		}
		appFrame.app = this;
		this.objectPool = new ObjectPool();
		App.useFullAppGui = true;
		this.useFullGui = true;
		appCanvasHeight = appFrame.getCanvasCountedHeight();
		appCanvasWidth = appFrame.getCanvasCountedWidth();

		setCurrentFileId();

		initCommonObjects();
		
		

		this.euclidianViewPanel = appFrame.getEuclidianView1Panel();
		this.canvas = euclidianViewPanel.getCanvas();

		initCoreObjects(undoActive, this);
		// user authentication handling
		initSignInEventFlow(new LoginOperationW(this));
		
		afterCoreObjectsInited();
		App.debug("after core");
		resetFonts();
		// initing = true;
		removeDefaultContextMenu();
		
		String token = Location.getParameter("token");
		App.debug("checked token");
	    if(token != null && !"".equals(token)){
	    	App.debug("LTOKEN set via URL");
	    	this.getLoginOperation().performTokenLogin(token, false);
			this.showBrowser(((GuiManagerW) this.getGuiManager()).getBrowseGUI());
			nativeLoggedIn();
	    }else{
	    	if(Cookies.getCookie("SSID") != null){
	    		this.getLoginOperation().performCookieLogin(Cookies.getCookie("SSID"));
			}
	    }
	}

	private native void nativeLoggedIn() /*-{
		if(typeof ggbOnLoggedIn == "function"){
			ggbOnLoggedIn();
		}
    }-*/;

	/*************************************************
	 * Constructs AppW for full GUI based GeoGebraWeb with undo enabled
	 * 
	 * @param article
	 * @param geoGebraAppFrame
	 */
	public AppWapplication(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame, int dimension, GLookAndFeel laf) {
		this(article, geoGebraAppFrame, true, dimension, laf);
		App.debug("Application created");
	}

	public GeoGebraAppFrame getAppFrame() {
		return appFrame;
	}
	
	private AuthenticationModelW authenticationModel = null;
	private boolean menuInited = false;
	
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

	

	@Override
	protected void afterCoreObjectsInited() {
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

	@Override
    public void afterLoadFileAppOrNot() {
		String perspective = getArticleElement().getDataParamPerspective();
		getGuiManager().getLayout().setPerspectives(getTmpPerspectives(),PerspectiveDecoder.decode(perspective, this.getKernel().getParser(), 
				ToolBar.getAllToolsNoMacros(true, true)));

		getScriptManager().ggbOnInit();	// put this here from Application constructor because we have to delay scripts until the EuclidianView is shown
		
		initUndoInfoSilent();


		stopCollectingRepaints();
		// Well, it may cause freeze if we attach this too early
		attachViews();

		// this is needed otherwise the coordinate system of EV would not be OK
		// put in a deferred call because of drag & dropping files
		updateViewSizes();

		this.getEuclidianViewpanel().updateNavigationBar();
		setDefaultCursor();
		GeoGebraProfiler.getInstance().profileEnd();
		((GGWToolBar)this.getToolbar()).updateToolbarPanel();
		onOpenFile();
	}

	@Override
	public void updateViewSizes() {
		getEuclidianViewpanel().deferredOnResize();
		if (hasEuclidianView2(1)) {
			getGuiManager().getEuclidianView2DockPanel(1).deferredOnResize();
		}
		if (getGuiManager().hasSpreadsheetView()) {
			DockPanel sp = getGuiManager().getLayout().getDockManager().getPanel(App.VIEW_SPREADSHEET);
			if (sp != null) {
				sp.deferredOnResize();
			}
		}
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
		if(showAlgebraInput()){
			appFrame.getAlgebraInput().attachApp(this);
		}
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
    public void showBrowser(MyHeaderPanel bg) {
	    appFrame.showBrowser(bg);
    }
	
	@Override
	public void toggleMenu(){
		if(!this.menuInited ){
			appFrame.getMenuBar().init(this);
			this.menuInited = true;
		}
		boolean menuOpen = appFrame.toggleMenu();
		if(!menuOpen && this.getGuiManager()!=null){
			this.getGuiManager().setDraggingViews(false, true);
		}
		if(menuOpen){
			this.getGuiManager().refreshDraggingViews();
		}
	}
}
