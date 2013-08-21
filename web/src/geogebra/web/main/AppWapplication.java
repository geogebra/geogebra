package geogebra.web.main;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.main.App;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.GuiManagerInterfaceW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.infobar.InfoBarW;
import geogebra.web.helper.MyGoogleApis;
import geogebra.web.helper.MySkyDriveApis;
import geogebra.web.helper.ObjectPool;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;


public class AppWapplication extends AppW {

	private GeoGebraAppFrame appFrame = null;
	private geogebra.web.gui.app.SplashDialog splashDialog = null;

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
		this.objectPool.setMyGoogleApis(new MyGoogleApis(this));
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

		//TODO delete following profiling
				StringBuilder sb = new StringBuilder(1000);
				long l = System.currentTimeMillis();
				FunctionVariable fv = new FunctionVariable(this.getKernel());
				ExpressionNode n = fv.wrap().plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv);
				for(int i = 0;i<100000;i++){
					sb.append(n.toValueString(StringTemplate.defaultTemplate));
				}
				App.debug("Plus node serialized in"+(System.currentTimeMillis() - l));
				
				l = System.currentTimeMillis();
				StringBuilder sbm = new StringBuilder(1000);
				ExpressionNode nm = fv.wrap().subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv);
				for(int i = 0;i<100000;i++){
					sbm.append(nm.toValueString(StringTemplate.defaultTemplate));
				}
				App.debug("Minus node serialized in"+(System.currentTimeMillis() - l));
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

	@Override
	public GuiManagerInterfaceW getGuiManager() {
		if (guiManager == null) {
			// TODO: add getGuiManager(), see #1783
			if (getUseFullGui() || showToolBar) {
				guiManager = new GuiManagerW(this);
			}
		}

		return guiManager;
	}

	public GeoGebraAppFrame getAppFrame() {
		return appFrame;
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
}
