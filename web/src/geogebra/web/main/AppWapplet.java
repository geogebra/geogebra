package geogebra.web.main;

import geogebra.common.GeoGebraConstants;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.io.layout.Perspective;
import geogebra.common.main.App;
import geogebra.common.main.DialogManager;
import geogebra.common.move.ggtapi.operations.LogInOperation;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.common.util.debug.Log;
import geogebra.html5.main.HasAppletProperties;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.GuiManagerInterfaceW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.app.GGWCommandLine;
import geogebra.web.gui.app.GGWMenuBar;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.applet.GeoGebraFrame;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.infobar.InfoBarW;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.layout.panels.EuclidianDockPanelW;
import geogebra.web.helper.ObjectPool;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class AppWapplet extends AppW {

	private GeoGebraFrame frame = null;
	protected GuiManagerInterfaceW guiManager = null;

	//Event flow operations - are these needed in AppWapplet?
	
	private LogInOperation loginOperation;

	/******************************************************
	 * Constructs AppW for applets with undo enabled
	 * 
	 * @param ae
	 * @param gf
	 */
	public AppWapplet(ArticleElement ae, GeoGebraFrame gf) {
		this(ae, gf, true);
	}

	/******************************************************
	 * Constructs AppW for applets
	 * 
	 * @param undoActive
	 *            if true you can undo by CTRL+Z and redo by CTRL+Y
	 */
	public AppWapplet(ArticleElement ae, GeoGebraFrame gf, final boolean undoActive) {
		this.articleElement = ae;
		this.frame = gf;
		this.objectPool = new ObjectPool();
		setAppletHeight(frame.getComputedHeight());
		setAppletWidth(frame.getComputedWidth());

		this.useFullGui = !isApplet() ||
				ae.getDataParamShowAlgebraInput() ||
				ae.getDataParamShowToolBar() ||
				ae.getDataParamShowMenuBar() ||
				ae.getDataParamEnableRightClick();

		infobar = new InfoBarW(this);

		Log.info("GeoGebra " + GeoGebraConstants.VERSION_STRING + " "
		        + GeoGebraConstants.BUILD_DATE + " "
		        + Window.Navigator.getUserAgent());
		initCommonObjects();
		initing = true;

		this.euclidianViewPanel = new EuclidianDockPanelW(this, false);
		//(EuclidianDockPanelW)getGuiManager().getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN);
		this.canvas = this.euclidianViewPanel.getCanvas();
		canvas.setWidth("1px");
		canvas.setHeight("1px");
		canvas.setCoordinateSpaceHeight(1);
		canvas.setCoordinateSpaceWidth(1);
		initCoreObjects(undoActive, this);
		removeDefaultContextMenu(this.getArticleElement());
	}

	public GeoGebraFrame getGeoGebraFrame() {
		return frame;
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
		getGuiManager().setLayout(new geogebra.web.gui.layout.LayoutW());
		getGuiManager().initialize();
		setDefaultCursor();
	}

	/**
	 * @return a GuiManager for GeoGebraWeb
	 */
	protected GuiManagerW newGuiManager() {
		return new GuiManagerW(AppWapplet.this);
	}

	@Override
	protected void afterCoreObjectsInited() {
		// Code to run before buildApplicationPanel
		initGuiManager();
		((EuclidianDockPanelW)euclidianViewPanel).addNavigationBar();
		GeoGebraFrame.finishAsyncLoading(articleElement, frame, this);
		initing = false;
	}

	public void buildSingleApplicationPanel() {
		if (frame != null) {
			frame.clear();
			frame.add((Widget)getEuclidianViewpanel());
			((DockPanelW)getEuclidianViewpanel()).setVisible(true);
			((DockPanelW)getEuclidianViewpanel()).setEmbeddedSize(getSettings().getEuclidian(1).getPreferredSize().getWidth());
			((DockPanelW)getEuclidianViewpanel()).updatePanel();
			getEuclidianViewpanel().setPixelSize(
					getSettings().getEuclidian(1).getPreferredSize().getWidth(),
					getSettings().getEuclidian(1).getPreferredSize().getHeight());

			// FIXME: temporary hack until it is found what causes
			// the 1px difference
			//getEuclidianViewpanel().getAbsolutePanel().getElement().getStyle().setLeft(1, Style.Unit.PX);
			//getEuclidianViewpanel().getAbsolutePanel().getElement().getStyle().setTop(1, Style.Unit.PX);
			getEuclidianViewpanel().getAbsolutePanel().getElement().getStyle().setBottom(-1, Style.Unit.PX);
			getEuclidianViewpanel().getAbsolutePanel().getElement().getStyle().setRight(-1, Style.Unit.PX);
			oldSplitLayoutPanel = null;
		}
	}

	private Widget oldSplitLayoutPanel = null;	// just a technical helper variable

	@Override
    public void buildApplicationPanel() {

		if (!isUsingFullGui()) {
			if (showConsProtNavigation
					|| !isJustEuclidianVisible()) {
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
		if (showMenuBar && articleElement.getDataParamShowMenuBarDefaultTrue() ||
			articleElement.getDataParamShowMenuBar()) {
			attachMenubar();
		}

		// showToolBar should come from data-param,
		// this is just a 'second line of defense'
		// otherwise it can be used for taking ggb settings into account too
		if (showToolBar && articleElement.getDataParamShowToolBarDefaultTrue() ||
			articleElement.getDataParamShowToolBar()) {
			attachToolbar();
		}

		attachSplitLayoutPanel();

		// showAlgebraInput should come from data-param,
		// this is just a 'second line of defense'
		// otherwise it can be used for taking ggb settings into account too
		if (showAlgebraInput && articleElement.getDataParamShowAlgebraInputDefaultTrue() ||
			articleElement.getDataParamShowAlgebraInput()) {
			attachAlgebraInput();
		}
		
		
	}

	public void refreshSplitLayoutPanel() {
		if (frame != null && frame.getWidgetCount() != 0 &&
			frame.getWidgetIndex(getSplitLayoutPanel()) == -1 &&
			frame.getWidgetIndex(oldSplitLayoutPanel) != -1) {
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
	}

	public void attachMenubar() {
		// reusing old menubar is probably a good decision
		GGWMenuBar menubar = objectPool.getGgwMenubar();
		if (menubar == null) {
			menubar = new GGWMenuBar();
			menubar.init(this);
			objectPool.setGgwMenubar(menubar);
		}
		frame.insert(menubar, 0);
	}

	private GGWToolBar ggwToolBar = null;

	public void attachToolbar() {
		GGWMenuBar menubar = getObjectPool().getGgwMenubar();
		// reusing old toolbar is probably a good decision
		if (ggwToolBar == null) {
			ggwToolBar = new GGWToolBar();
			ggwToolBar.init(this);
		}
		if (menubar != null) {
			frame.insert(ggwToolBar, frame.getWidgetIndex(menubar) + 1);
		} else {
			frame.insert(ggwToolBar, 0);
		}
	}

	@Override
    public GGWToolBar getToolbar() {
		return ggwToolBar;
	}

	public void attachSplitLayoutPanel() {
		oldSplitLayoutPanel = getSplitLayoutPanel();
		if (oldSplitLayoutPanel != null) {
			frame.add(oldSplitLayoutPanel);
			removeDefaultContextMenu(getSplitLayoutPanel().getElement());
		}
	}

	@Override
    public void afterLoadFileAppOrNot() {

		if (!isUsingFullGui()) {
			if (showConsProtNavigation
					|| !isJustEuclidianVisible()) {
				useFullGui = true;
			}
		}

		if (!isUsingFullGui()) {
			buildSingleApplicationPanel();
		} else {
			// a small thing to fix a rare bug
			getGuiManager().getLayout().getDockManager().kickstartRoot(frame);
			getGuiManager().getLayout().setPerspectives(getTmpPerspectives());
		}
		
		getScriptManager().ggbOnInit();	// put this here from Application constructor because we have to delay scripts until the EuclidianView is shown

		kernel.initUndoInfo();

		getEuclidianView1().synCanvasSize();
		
		getEuclidianView1().doRepaint2();
		stopCollectingRepaints();
		frame.splash.canNowHide();
		requestFocusInWindow();

		if (isUsingFullGui()) {
			if (needsSpreadsheetTableModel()) {
				getSpreadsheetTableModel();
			}
			refreshSplitLayoutPanel();

			// probably this method can be changed by more,
			// to be more like AppWapplication's method with the same name,
			// but preferring to change what is needed only to avoid new unknown bugs
			if (getGuiManager().hasSpreadsheetView()) {
				DockPanelW sp = getGuiManager().getLayout().getDockManager().getPanel(App.VIEW_SPREADSHEET);
				if (sp != null) {
					sp.deferredOnResize();
				}
			}
		}

		if (isUsingFullGui())
			this.getEuclidianViewpanel().updateNavigationBar();
		setDefaultCursor();
		GeoGebraProfiler.getInstance().profileEnd();
    }

	@Override
	public void focusLost() {
		GeoGebraFrame.useDataParamBorder(
				getArticleElement(),
				getGeoGebraFrame());
	}

	@Override
	public void focusGained() {
		GeoGebraFrame.useFocusedBorder(
				getArticleElement(),
				getGeoGebraFrame());
	}

	@Override
	public void setCustomToolBar() {
		String customToolbar = articleElement.getDataParamCustomToolBar();
		if ((customToolbar != null) &&
			(customToolbar.length() > 0) &&
			(articleElement.getDataParamShowToolBar()) &&
			(getGuiManager() != null)) {
			getGuiManager().setToolBarDefinition(customToolbar);
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
		} else if (evno == 2 && hasEuclidianView2() && getEuclidianView2().isShowing()) {// or the EuclidianView 2
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
			return true; //throw new OperationNotSupportedException();
		}

		Perspective docPerspective = null;

		for (Perspective perspective : tmpPerspectives) {
			if (perspective.getId().equals("tmp")) {
				docPerspective = perspective;
			}
		}

		if (docPerspective == null) {
			return true; //throw new OperationNotSupportedException();
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
    public Element getFrameElement(){
		return  frame.getElement();
	}
	
	
}
