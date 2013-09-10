package geogebra.web.main;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.main.App;
import geogebra.common.main.DialogManager;
import geogebra.common.move.ggtapi.operations.LogOutOperation;
import geogebra.common.move.ggtapi.operations.LoginOperation;
import geogebra.common.move.ggtapi.views.LogOutView;
import geogebra.common.move.ggtapi.views.LoginView;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.html5.move.ggtapi.models.AuthenticationModelWeb;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.GuiManagerInterfaceW;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.infobar.InfoBarW;
import geogebra.web.helper.MySkyDriveApis;
import geogebra.web.helper.ObjectPool;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;


public class AppWapplication extends AppW {

	private GeoGebraAppFrame appFrame = null;
	private geogebra.web.gui.app.SplashDialog splashDialog = null;

	//Event flow operations
	
	private LoginOperation loginOperation;
	private LogOutOperation logoutOperation;

	/********************************************************
	 * Constructs AppW for full GUI based GeoGebraWeb
	 * 
	 * @param article
	 * @param geoGebraAppFrame
	 * @param undoActive
	 */
	public AppWapplication(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame,
	        boolean undoActive) {
		this.articleElement = article;
		this.appFrame = geoGebraAppFrame;
		this.objectPool = new ObjectPool();
		this.objectPool.setMyGoogleApis(new MyGoogleApisFactory(this));
		this.objectPool.setMySkyDriveApis(new MySkyDriveApis(this));
		createAppSplash();
		App.useFullAppGui = true;
		this.useFullGui = true;
		appCanvasHeight = appFrame.getCanvasCountedHeight();
		appCanvasWidth = appFrame.getCanvasCountedWidth();

		setCurrentFileId();
		
		infobar = new InfoBarW(this);

		initCommonObjects();

		this.euclidianViewPanel = appFrame.getEuclidianView1Panel();
		this.canvas = euclidianViewPanel.getCanvas();

		initCoreObjects(undoActive, this);

		// initing = true;
		//this may only be called after factories are initialized
		StringTemplate.latexIsMathQuill = true;
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

	@Override
	public GuiManagerInterfaceW getGuiManager() {
		return guiManager;
	}

	private void createAppSplash() {
		splashDialog = new geogebra.web.gui.app.SplashDialog();
	}

	@Override
	protected void afterCoreObjectsInited() {
		initGuiManager();
		getGuiManager().getLayout().setPerspectives(tmpPerspectives);

		getSettings().getEuclidian(1).setPreferredSize(
		        geogebra.common.factories.AwtFactory.prototype
		                .newDimension(appCanvasWidth, appCanvasHeight));
		getEuclidianView1().synCanvasSize();
		getEuclidianView1().doRepaint2();
		stopCollectingRepaints();
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

		getEuclidianView1().synCanvasSize();

		splashDialog.canNowHide();
		updateCenterPanel(true);
		getEuclidianView1().doRepaint2();
		stopCollectingRepaints();
		// Well, it may cause freeze if we attach this too early
		attachViews();
		((SplitLayoutPanel)getSplitLayoutPanel()).forceLayout();
		((SplitLayoutPanel)getSplitLayoutPanel()).onResize();
		this.getEuclidianViewpanel().onResize();
		getEuclidianView1().doRepaint2();

		this.getEuclidianViewpanel().updateNavigationBar();
		GeoGebraProfiler.getInstance().profileEnd();
	}

	@Override
	public boolean menubarRestricted() {
		return false;
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
		}
		return dialogManager;
	}

	/**
	 * @return LogInOperation eventFlow
	 */
	@Override
	public LoginOperation getLoginOperation() {
		return loginOperation;
	}
	
	/**
	 * @return LogoutOperation logOutOperation
	 */
	@Override
	public LogOutOperation getLogOutOperation() {
		return logoutOperation;
	}

	private void initAuthenticationEventFlow() {
		loginOperation = new LoginOperation();
		AuthenticationModelWeb authenticationModel = new AuthenticationModelWeb();
		LoginView loginView = new LoginView();
		
		loginOperation.setModel(authenticationModel);
		loginOperation.setView(loginView);
		
		logoutOperation = new LogOutOperation();		
		LogOutView logOutView = new LogOutView();		
		
		logoutOperation.setModel(authenticationModel);
		logoutOperation.setView(logOutView);
		
	}

	protected void initCommonObjects() {
		super.initCommonObjects();
		//Login - Logout operation event handling begins here
		initAuthenticationEventFlow();
	}
}
