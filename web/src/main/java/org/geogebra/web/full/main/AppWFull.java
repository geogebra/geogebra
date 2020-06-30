package org.geogebra.web.full.main;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MaskWidgetList;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidian.inline.InlineFormulaController;
import org.geogebra.common.euclidian.inline.InlineTableController;
import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.euclidian.smallscreen.AdjustScreen;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.FormatCollada;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.FormatColladaHTML;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.inputfield.HasLastItem;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.spreadsheet.CopyPasteCut;
import org.geogebra.common.gui.view.spreadsheet.DataImport;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.kernel.AppState;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFormula;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.AppConfigDefault;
import org.geogebra.common.main.AppKeyboardType;
import org.geogebra.common.main.MaterialsManagerI;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.main.SaveController;
import org.geogebra.common.main.ShareController;
import org.geogebra.common.main.settings.updater.SettingsUpdaterBuilder;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.events.StayLoggedOutEvent;
import org.geogebra.common.move.ggtapi.TubeAvailabilityCheckEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.full.euclidian.EuclidianStyleBarW;
import org.geogebra.web.full.euclidian.inline.InlineFormulaControllerW;
import org.geogebra.web.full.euclidian.inline.InlineTableControllerW;
import org.geogebra.web.full.euclidian.inline.InlineTextControllerW;
import org.geogebra.web.full.gui.CustomizeToolbarGUI;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.full.gui.SaveControllerW;
import org.geogebra.web.full.gui.ShareControllerW;
import org.geogebra.web.full.gui.WhatsNewDialog;
import org.geogebra.web.full.gui.app.GGWCommandLine;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.exam.ExamDialog;
import org.geogebra.web.full.gui.exam.ExamUtil;
import org.geogebra.web.full.gui.keyboard.KeyboardManager;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.gui.layout.DockGlassPaneW;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.DockSplitPaneW;
import org.geogebra.web.full.gui.layout.LayoutW;
import org.geogebra.web.full.gui.layout.panels.AlgebraStyleBarW;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.full.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.full.gui.menu.MenuViewController;
import org.geogebra.web.full.gui.menu.MenuViewListener;
import org.geogebra.web.full.gui.menubar.FileMenuW;
import org.geogebra.web.full.gui.menubar.PerspectivesPopup;
import org.geogebra.web.full.gui.openfileview.OpenFileView;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.full.gui.toolbar.mow.ToolbarMow;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.util.FontSettingsUpdaterW;
import org.geogebra.web.full.gui.util.PopupBlockAvoider;
import org.geogebra.web.full.gui.util.ZoomPanelMow;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.ConstructionItemProvider;
import org.geogebra.web.full.helper.ResourcesInjectorFull;
import org.geogebra.web.full.main.activity.CASActivity;
import org.geogebra.web.full.main.activity.ClassicActivity;
import org.geogebra.web.full.main.activity.EvaluatorActivity;
import org.geogebra.web.full.main.activity.GeoGebraActivity;
import org.geogebra.web.full.main.activity.GeometryActivity;
import org.geogebra.web.full.main.activity.Graphing3DActivity;
import org.geogebra.web.full.main.activity.GraphingActivity;
import org.geogebra.web.full.main.activity.MebisNotesActivity;
import org.geogebra.web.full.main.activity.MixedRealityActivity;
import org.geogebra.web.full.main.activity.NotesActivity;
import org.geogebra.web.full.main.activity.ScientificActivity;
import org.geogebra.web.full.main.activity.SuiteActivity;
import org.geogebra.web.full.main.mask.MaskWidgetListW;
import org.geogebra.web.full.main.video.VideoManagerW;
import org.geogebra.web.full.move.googledrive.operations.GoogleDriveOperationW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.javax.swing.GImageIconW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.html5.main.ScriptManagerW;
import org.geogebra.web.html5.util.ArticleElementInterface;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.Persistable;
import org.geogebra.web.shared.DialogBoxW;
import org.geogebra.web.shared.GlobalHeader;
import org.geogebra.web.shared.ggtapi.LoginOperationW;
import org.geogebra.web.shared.ggtapi.models.MaterialCallback;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * App with all the GUI
 *
 */
public class AppWFull extends AppW implements HasKeyboard, MenuViewListener {

	private static final String RECENT_CHANGES_KEY = "RecentChangesInfo.Graphing";
	private static final boolean ALLOW_RECENT_CHANGES_DIALOG = false;
	private final static int AUTO_SAVE_PERIOD = 2000;

	private GuiManagerW guiManager = null;

	private CustomizeToolbarGUI ct;

	private ArrayList<Runnable> waitingForLocalization;
	private boolean localizationLoaded;
	/** browser / tablet / win store device */
	protected final GDevice device;
	/** material ID waiting for login */
	String toOpen = "";
	private PerspectivesPopup perspectivesPopup;

	private int activePerspective;

	private boolean menuShowing = false;
	private final GeoGebraFrameFull frame;
	private DockSplitPaneW oldSplitLayoutPanel = null; // just a
																// technical
	private int spWidth;
	private int spHeight;
	private boolean isMenuInited = false;
	// helper
	// variable
	private HorizontalPanel splitPanelWrapper = null;
	private MenuViewController menuViewController;

	private EmbedManagerW embedManager;
	private VideoManagerW videoManager;

	private SaveController saveController = null;

	private ShareControllerW shareController;
	private ZoomPanelMow mowZoomPanel;
	private GeoGebraActivity activity;
	private KeyboardManager keyboardManager;
	/** dialog manager */
	protected DialogManagerW dialogManager = null;
    private String autosavedMaterial = null;
	private MaskWidgetList maskWidgets;

	/**
	 *
	 * @param ae
	 *            article element
	 * @param dimension
	 *            2 or 3 (for 2D or 3D app)
	 * @param laf
	 *            look and feel
	 * @param device
	 *            browser / tablet / win store device
	 * @param frame
	 *            frame
	 */
	public AppWFull(ArticleElementInterface ae, int dimension,
			GLookAndFeelI laf,
			GDevice device, GeoGebraFrameFull frame) {
		super(ae, dimension, laf);
		this.frame = frame;
		this.device = device;

		if (getArticleElement().getDataParamApp()) {
			startDialogChain();
		}

		setAppletHeight(frame.getComputedHeight());
		setAppletWidth(frame.getComputedWidth());

		this.useFullGui = !isApplet() || ae.getDataParamShowAlgebraInput(false)
				|| ae.getDataParamShowToolBar(false)
				|| ae.getDataParamShowMenuBar(false)
				|| ae.getDataParamEnableRightClick() || !isStartedWithFile();

		Log.info("GeoGebra " + GeoGebraConstants.VERSION_STRING + " "
				+ GeoGebraConstants.BUILD_DATE + " "
				+ Window.Navigator.getUserAgent());
		initCommonObjects();
		initing = true;

		this.euclidianViewPanel = new EuclidianDockPanelW(this,
				allowStylebar());
		initCoreObjects();
		checkExamPerspective();
		afterCoreObjectsInited();
		getSettingsUpdater().getFontSettingsUpdater().resetFonts();
		Browser.removeDefaultContextMenu(this.getArticleElement().getElement());
		if (ae.getDataParamApp() && !this.getLAF().isSmart()) {
			RootPanel.getBodyElement().addClassName("application");
		}
		setupHeader();

		startActivity();
	}

	private void setupHeader() {
		GlobalHeader header = GlobalHeader.INSTANCE;
		header.setApp(this);
		header.setFrame(frame);
		if (showMenuBar()) {
			setupSignInButton(header);
		}
	}

	private void checkExamPerspective() {
		if (!articleElement.getDataParamPerspective().startsWith("exam")) {
			return;
		}

		setNewExam();
		articleElement.attr("perspective", "");
		afterLocalizationLoaded(new Runnable() {
			@Override
			public void run() {
				examWelcome();
			}
		});
	}

	private void setupSignInButton(GlobalHeader header) {
		if (getLoginOperation() == null) {
			initSignInEventFlow(new LoginOperationW(this));
		}
		header.addSignIn(this);
	}

	@Override
	public AppConfig getConfig() {
		initActivity();
		if (activity == null) {
			return new AppConfigDefault();
		}
		return activity.getConfig();
	}

	/**
	 * @return current activity (graphing, geometry, 3D, ...)
	 */
	public GeoGebraActivity getActivity() {
		return activity;
	}

	private void initActivity() {
		if (articleElement == null || activity != null) {
			return;
		}
		switch (articleElement.getDataParamAppName()) {
			case "graphing":
				activity = new GraphingActivity();
				break;
			case "geometry":
				activity = new GeometryActivity();
				break;
			case "3d":
				activity = new Graphing3DActivity();
				break;
			case "mr":
				activity = new MixedRealityActivity();
				break;
			case "cas":
				activity = new CASActivity();
				break;
			case "scientific":
				activity = new ScientificActivity();
				break;
			case "notes":
				activity = isMebis() ? new MebisNotesActivity() : new NotesActivity();
				break;
			case "evaluator":
				activity = new EvaluatorActivity();
				break;
			case "suite":
				activity = new SuiteActivity();
				break;
			default:
				activity = new ClassicActivity(new AppConfigDefault());
		}
	}

	/**
	 * Initialize the activity
	 */
	private void startActivity() {
		initActivity();
		activity.start(this);
	}

	/**
	 * shows the on-screen keyboard (or e.g. a show-keyboard-button)
	 *
	 * @param textField
	 *            keyboard listener
	 */
	public final void showKeyboard(MathKeyboardListener textField) {
		showKeyboard(textField, false);
	}

	@Override
	public final boolean showKeyboard(MathKeyboardListener textField,
			boolean forceShow) {
		boolean ret = getAppletFrame().showKeyBoard(true, textField, forceShow);
		if (textField != null && ret) {
			CancelEventTimer.keyboardSetVisible();
		}
		return ret;
	}

	@Override
	public final void updateKeyboardHeight() {
		getAppletFrame().updateKeyboardHeight();
	}

	@Override
	public final void updateKeyBoardField(MathKeyboardListener field) {
		getKeyboardManager().setOnScreenKeyboardTextField(field);
	}

	@Override
	public final GuiManagerW getGuiManager() {
		return guiManager;
	}

	@Override
	public final void initGuiManager() {
		// this should not be called from AppWsimple!
		setWaitCursor();
		guiManager = newGuiManager();
		getLocalization().registerLocalizedUI(guiManager);
		getGuiManager().setLayout(new LayoutW(this));
		getGuiManager().initialize();
		setDefaultCursor();
		initMenu();
	}

	private void initMenu() {
		if (isFloatingMenu()) {
			initSignInEventFlow(new LoginOperationW(this));
			menuViewController = new MenuViewController(this);
			menuViewController.setMenuViewListener(this);
			frame.add(menuViewController.getView());
			isMenuInited = true;
		}
	}

	/**
	 * @return a GuiManager for GeoGebraWeb
	 */
	protected GuiManagerW newGuiManager() {
		return new GuiManagerW(this, getDevice());
	}

	@Override
	public final void hideKeyboard() {
		getAppletFrame().showKeyBoard(false, null, false);
	}

	@Override
	public final boolean letShowPropertiesDialog() {
		return rightClickEnabled
				|| getArticleElement().getDataParamShowMenuBar(false)
				|| getArticleElement().getDataParamApp();
	}

	@Override
	public final void updateKeyboard() {
		invokeLater(new Runnable() {

			@Override
			public void run() {
				DockPanelW dp = getGuiManager().getLayout().getDockManager()
						.getPanelForKeyboard();
				MathKeyboardListener listener = getGuiManager()
						.getKeyboardListener(dp);
				if (listener != null) {
					// dp.getKeyboardListener().setFocus(true);
					listener.ensureEditing();
					listener.setFocus(true);
					if (isKeyboardNeeded() && (getExam() == null
							|| getExam().getStart() > 0)) {
						getAppletFrame().showKeyBoard(true, listener, true);
					}
				}
				if (!isKeyboardNeeded()) {
					getAppletFrame().showKeyBoard(false, null, true);
				}

			}
		});

	}

	@Override
	public void doSetLanguage(String lang, boolean asyncCall) {
		super.doSetLanguage(lang, asyncCall);
		if (asyncCall) {
			getKeyboardManager().updateKeyboardLanguage();
		}

		if (getLocalization().isRightToLeftReadingOrder()) {
			ResourcesInjectorFull.injectRTLstyles();
		} else {
			ResourcesInjectorFull.injectLTRstyles();
		}
	}

	@Override
	public final void notifyLocalizationLoaded() {
		localizationLoaded = true;
		if (waitingForLocalization == null) {
			return;
		}

		for (Runnable run : waitingForLocalization) {
			run.run();
		}

		waitingForLocalization.clear();
	}

	@Override
	public final void afterLocalizationLoaded(Runnable run) {
		if (localizationLoaded) {
			run.run();
		} else {
			if (waitingForLocalization == null) {
				waitingForLocalization = new ArrayList<>();
			}
			waitingForLocalization.add(run);
		}
	}

	@Override
	public final void showStartTooltip(final int perspID) {
		afterLocalizationLoaded(new Runnable() {

			@Override
			public void run() {
				doShowStartTooltip(perspID);
			}
		});
	}

	/**
	 * @param perspID
	 *            perspective ID
	 */
	void doShowStartTooltip(int perspID) {
		if (articleElement.getDataParamShowStartTooltip(perspID > 0)) {
			ToolTipManagerW.sharedInstance().setBlockToolTip(false);
			String tooltipText = getLocalization().getMenu("NewToGeoGebra")
					+ "<br/>"
					+ getLocalization().getPlain("CheckOutTutorial",
							getLocalization().getMenu(
									Perspective.getPerspectiveName(perspID)));
			String tooltipURL = getLocalization().getTutorialURL(getConfig());
			DockPanelW focused = getGuiManager().getLayout().getDockManager()
					.getPanelForKeyboard();
			ToolTipManagerW.sharedInstance().showBottomInfoToolTip(tooltipText,
					tooltipURL, ToolTipLinkType.Help, this,
					focused != null && focused.isVisible());
			ToolTipManagerW.sharedInstance().setBlockToolTip(true);
		}
	}

	@Override
	public final void checkSaved(AsyncOperation<Boolean> runnable) {
		getSaveController().showDialogIfNeeded(runnable);
	}

	@Override
	public final void openCSV(String csv) {
		String[][] data = DataImport.parseExternalData(this, csv, true);
		CopyPasteCut cpc = getGuiManager().getSpreadsheetView()
				.getSpreadsheetTable().getCopyPasteCut();
		cpc.pasteExternal(data, 0, 0, data.length > 0 ? data[0].length - 1 : 0,
				data.length);
		onOpenFile();
	}

	@Override
	public final void uploadToGeoGebraTube() {

		final PopupBlockAvoider popupBlockAvoider = new PopupBlockAvoider();
		final GeoGebraTubeExportW ggbtube = new GeoGebraTubeExportW(this);
		getGgbApi().getBase64(true, new AsyncOperation<String>() {

			@Override
			public void callback(String s) {
				ggbtube.uploadWorksheetSimple(s, popupBlockAvoider);

			}
		});
	}

	@Override
	protected void resetUI() {
		resetEVs();
		// make sure file->new->probability does not clear the prob. calc
		if (getGuiManager() != null
				&& getGuiManager().hasProbabilityCalculator()) {
			((ProbabilityCalculatorView) getGuiManager()
					.getProbabilityCalculator()).updateAll();
		}
		// remove all Macros before loading preferences
		kernel.removeAllMacros();
		// reload the saved/(default) preferences
		Perspective p = null;
		if (isUnbundledOrWhiteboard()) {
			LayoutW.resetPerspectives(this);
		}
		if (getGuiManager() != null) {
			p = getGuiManager().getLayout().createPerspective("tmp");
		}
		if (isUnbundledGeometry()) {
			p = Layout.getDefaultPerspectives(Perspective.GEOMETRY - 1);
		}
		if (isUnbundledGraphing()) {
			p = Layout.getDefaultPerspectives(Perspective.GRAPHING - 1);
		}
		if (isUnbundled3D()) {
			p = Layout.getDefaultPerspectives(Perspective.GRAPHER_3D - 1);
		}
		if (isWhiteboardActive()) {
			p = Layout.getDefaultPerspectives(Perspective.NOTES - 1);
		}

		if (isPortrait()) {
			p.getSplitPaneData()[0].setDivider(PerspectiveDecoder.portraitRatio(
					getHeight(),
					isUnbundledGraphing() || isUnbundled3D()
							|| "1".equals(
									articleElement.getDataParamPerspective())
							|| "5".equals(
									articleElement.getDataParamPerspective())));
		} else {
			p.getSplitPaneData()[0].setDivider(
					PerspectiveDecoder.landscapeRatio(this, getWidth()));

		}

		GeoGebraPreferencesW.getPref().loadForApp(this, p);

		resetAllToolbars();

		resetToolbarPanel();

		if (getGuiManager() != null) {
			getGuiManager().updateGlobalOptions();
		}

		if (isUnbundled() && getGuiManager() != null
				&& getGuiManager()
						.getUnbundledToolbar() != null) {
			getGuiManager().getUnbundledToolbar()
					.updateContent();
		}
	}

	private void resetAllToolbars() {

		GuiManagerW gm = getGuiManager();
		DockPanelW[] panels = gm.getLayout().getDockManager().getPanels();
		for (DockPanelW panel : panels) {
			if (panel.canCustomizeToolbar()) {
				panel.setToolbarString(panel.getDefaultToolbarString());
			}
		}
		gm.setToolBarDefinition(gm.getDefaultToolbarString());

	}

	/**
	 * Resets toolbar
	 */
	protected final void resetToolbarPanel() {
		GuiManagerW gm = getGuiManager();
		DockPanel avPanel = gm.getLayout().getDockManager()
				.getPanel(VIEW_ALGEBRA);
		if (avPanel instanceof ToolbarDockPanelW) {
			((ToolbarDockPanelW) avPanel).getToolbar().reset();
		}
	}

	/**
	 * Updates the stylebar in Algebra View
	 */
	public final void updateAVStylebar() {
		if (getGuiManager() != null && getGuiManager().hasAlgebraView()) {
			AlgebraStyleBarW styleBar = ((AlgebraViewW) getView(
					App.VIEW_ALGEBRA)).getStyleBar(false);
			if (styleBar != null) {
				styleBar.update(null);
			}
		}
	}

	/**
	 * Popup exam welcome message
	 */
	@Override
	public final void examWelcome() {
		if (isExam() && getExam().getStart() < 0) {
			resetViewsEnabled();
			new ExamDialog(this).show();
		}
	}

	@Override
	public GeoGebraFrameFull getAppletFrame() {
		return frame;
	}

	@Override
	public final ToolBarInterface getToolbar() {
		return getAppletFrame().getToolbar();
	}

	private CustomizeToolbarGUI getCustomizeToolbarGUI() {
		if (this.ct == null) {
			this.ct = new CustomizeToolbarGUI(this);
		}
		int toolbarId = getGuiManager().getActiveToolbarId();
		Log.debug("[CT] toolbarId: " + toolbarId);
		ct.setToolbarId(toolbarId);
		return this.ct;
	}

	@Override
	public final void set1rstMode() {
		GGWToolBar.set1rstMode(this);
	}

	@Override
	public final void setLabels() {
		super.setLabels();
		if (this.ct != null) {
			ct.setLabels();
		}
	}

	@Override
	public final boolean isSelectionRectangleAllowed() {
		return this.showToolBar;
	}

	@Override
	public final void toggleShowConstructionProtocolNavigation(int id) {
		super.toggleShowConstructionProtocolNavigation(id);
		if (getGuiManager() != null) {
			getGuiManager().updateMenubar();
		}
	}

	@Override
	public final MaterialsManagerI getFileManager() {
		if (this.fm == null) {
			this.fm = getDevice().createFileManager(this);
		}
		return this.fm;
	}

	@Override
	public DialogManagerW getDialogManager() {
		if (dialogManager == null) {
			dialogManager = new DialogManagerW(this);
		}
		return dialogManager;
	}

	private void showBrowser(MyHeaderPanel bg) {
		EuclidianController evController = getActiveEuclidianView().getEuclidianController();
		if (evController != null) {
			evController.hideDynamicStylebar();
		}
		getAppletFrame().setApplication(this);
		getAppletFrame().showPanel(bg);
	}

	@Override
	public final void openSearch(String query) {
		hideMenu();
		if (isWhiteboardActive()
				&& getGuiManager().browseGUIwasLoaded()
				&& StringUtil.emptyTrim(query)
				&& getGuiManager().getBrowseView() instanceof OpenFileView) {
				((OpenFileView) getGuiManager().getBrowseView())
						.updateMaterials();
		}
		showBrowser((MyHeaderPanel) getGuiManager().getBrowseView(query));
		if (getArticleElement().getDataParamPerspective()
				.startsWith("search:")) {
			getArticleElement().attr("perspective", "");
		}
	}

	@Override
	protected final void initGoogleDriveEventFlow() {
		googleDriveOperation = new GoogleDriveOperationW(this);
		String state = Location.getParameter("state");
		if (getNetworkOperation().isOnline() && state != null
				&& !"".equals(state)) {
			googleDriveOperation.initGoogleDriveApi();
		}
	}

	@Override
	public final Element getFrameElement() {
		return getAppletFrame().getElement();
	}

	@Override
	public final void openMaterial(final String id,
			final AsyncOperation<String> onError) {
		if (getLoginOperation() != null
				&& getLoginOperation().getGeoGebraTubeAPI()
						.isCheckDone()) {
			doOpenMaterial(id, onError);
		} else {
			if (getLoginOperation() == null) {
				this.initSignInEventFlow(new LoginOperationW(this));
			}
			toOpen = id;
			// not logged in to Mebis while opening shared link: show login
			// dialog first
			if (!getLoginOperation().isLoggedIn() && !getLoginOperation()
					.getGeoGebraTubeAPI().anonymousOpen()) {
				getLoginOperation().getView()
						.add(new SharedFileOpenCallback(this, onError));
			} else {
				getLoginOperation().getView().add(new EventRenderable() {

					@Override
					public void renderEvent(BaseEvent event) {
						if (event instanceof LoginEvent
								|| event instanceof StayLoggedOutEvent
								|| event instanceof TubeAvailabilityCheckEvent) {
							checkOpen(onError, this);
						}
					}
				});
			}
			Log.debug("listening");
		}
	}

	/**
	 * @param onError
	 *            error handler
	 * @param caller
	 *            temporary login listener, to be removed after opening
	 */
	protected void checkOpen(final AsyncOperation<String> onError,
			EventRenderable caller) {
		if (toOpen != null && toOpen.length() > 0) {
			doOpenMaterial(toOpen, onError);
			toOpen = "";
		}
		getLoginOperation().getView().remove(caller);
	}

	/**
	 * @param id
	 *            material ID
	 * @param onError
	 *            error callback
	 */
	public final void doOpenMaterial(String id,
			final AsyncOperation<String> onError) {
		getLoginOperation().getGeoGebraTubeAPI()
				.getItem(id, new MaterialCallback() {

					@Override
					public void onLoaded(
							final List<Material> parseResponse,
							ArrayList<Chapter> meta) {
						if (parseResponse.size() == 1) {
							Material material = parseResponse.get(0);
							material.setSyncStamp(
									parseResponse.get(0).getModified());
							AppWFull.this.setSyncStamp(
									parseResponse.get(0).getModified());
							registerOpenFileListener(
									getUpdateTitleCallback(material));
							if (!StringUtil.empty(material.getFileName())) {
								getViewW().processFileName(
										material.getFileName());
							} else {
								getGgbApi().setBase64(material.getBase64());
							}
							setActiveMaterial(material);
						} else {
							onError.callback(Errors.LoadFileFailed.getKey());
						}
					}

					@Override
					public void onError(Throwable error) {
						onError.callback(error.getMessage().contains("401")
								? Errors.NotAuthorized.getKey()
								: Errors.LoadFileFailed.getKey());
					}
				});
	}

	/**
	 * @param material
	 *            loaded material
	 * @return callback that updates browser title
	 */
	public final OpenFileListener getUpdateTitleCallback(
			final Material material) {
		return new OpenFileListener() {

			@Override
			public boolean onOpenFile() {
				AppWFull.this.updateMaterialURL(material.getId(),
						material.getSharingKey(), material.getTitle());
				return true;
			}
		};
	}

	@Override
	public final boolean isOffline() {
		return getDevice().isOffline(this);
	}

	/**
	 * @return glass pane
	 */
	public DockGlassPaneW getGlassPane() {
		return frame.getGlassPane();
	}

	@Override
	public final void showPerspectivesPopup() {
		if (isUnbundledOrWhiteboard()) {
			return;
		}
		afterLocalizationLoaded(new Runnable() {

			@Override
			public void run() {
				getPerspectivesPopup().showPerspectivesPopup();
			}
		});
	}

	/**
	 * Removed element called ggbsplash
	 */
	protected static void removeSplash() {
		Element el = DOM.getElementById("ggbsplash");
		if (el != null) {
			el.removeFromParent();
		}
	}

	@Override
	public final void appSplashCanNowHide() {
		String cmds = Location.getParameter("command");

		if (cmds != null) {
			Log.debug("exectuing commands: " + cmds);

			for (final String cmd : cmds.split(";")) {
				Runnable r = new Runnable() {
					@Override
					public void run() {
						getKernel().getAlgebraProcessor()
								.processAlgebraCommandNoExceptionsOrErrors(cmd,
										false);
					}
				};

				getAsyncManager().scheduleCallback(r);
			}
		}
		removeSplash();
	}

	@Override
	public final void setActivePerspective(int index) {
		activePerspective = index;
	}

	/**
	 * @return active perspective ID
	 */
	public final int getActivePerspective() {
		return activePerspective;
	}

	/**
	 * @return perspectives popup
	 */
	final PerspectivesPopup getPerspectivesPopup() {
		if (this.perspectivesPopup == null) {
			this.perspectivesPopup = new PerspectivesPopup(this);
		}
		return perspectivesPopup;
	}

	@Override
	public final boolean isPerspectivesPopupVisible() {
		return perspectivesPopup != null && perspectivesPopup.isShowing();
	}

	@Override
	public void updateViewSizes() {
		getEuclidianViewpanel().deferredOnResize();
		if (hasEuclidianView2(1)) {
			getGuiManager().getEuclidianView2DockPanel(1)
					.deferredOnResize();
		}
		if (getGuiManager().hasSpreadsheetView()) {
			DockPanel sp = getGuiManager().getLayout().getDockManager()
					.getPanel(App.VIEW_SPREADSHEET);
			if (sp != null) {
				sp.deferredOnResize();
			}
		}
		if (getGuiManager().hasCasView()) {
			DockPanelW sp = getGuiManager().getLayout().getDockManager()
					.getPanel(App.VIEW_CAS);
			if (sp != null) {
				sp.onResize();
			}
		}
		getAppletFrame()
				.setMenuHeight(getInputPosition() == InputPosition.bottom);
	}

	@Override
	public AppKeyboardType getKeyboardType() {
		if ("evaluator".equals(articleElement.getDataParamAppName())
				&& "normal".equals(articleElement.getParamKeyboardType("normal"))) {
			return AppKeyboardType.SUITE;
		}
		return getConfig().getKeyboardType();
	}

	@Override
	public boolean attachedToEqEditor() {
		return isWhiteboardActive();
	}

	@Override
	public final GImageIconW wrapGetModeIcon(int mode) {
		GImageIconW icon = new GImageIconW("");
		GGWToolBar.getImageResource(mode, this, icon);
		return icon;
	}

	@Override
	public final void closePopups() {
		super.closePopups();
		EuclidianStyleBarW.setCurrentPopup(null);
		if (getToolbar() != null && getToolbar().isMobileToolbar()) {
			((GGWToolBar) getToolbar()).getToolBar().closeAllSubmenu();
		}
		if (isUnbundledOrWhiteboard()) {
			boolean justClosed = menuShowing;
			hideMenu();
			justClosed = justClosed || closePageControlPanel();
			if (justClosed) {
				getEuclidianController().setPopupJustClosed(justClosed);
			}
		}
	}

	@Override
	public final void setToolbarPosition(int position, boolean update) {
		Log.debug("TOOLBAR POSITION " + position);
		toolbarPosition = position;
		if (update) {
			updateApplicationLayout();
			// updateMenubar(); TODO check if needed
		}
	}

	/**
	 * @return device (tablet / Win store device / browser)
	 */
	public final GDevice getDevice() {
		if (device == null) {
			return new BrowserDevice();
		}
		return device;
	}

	@Override
	public final void setSaved() {
		super.setSaved();
		if (hasAutosave()) {
			getFileManager().deleteAutoSavedFile();
			getLAF().removeWindowClosingHandler();
		}
	}

	private boolean hasAutosave() {
		return articleElement.getDataParamApp();
	}

	@Override
	public final void setUnsaved() {
		super.setUnsaved();
		if (hasAutosave() && kernel != null && kernel.getConstruction() != null
				&& kernel.getConstruction().isStarted()) {
			getLAF().addWindowClosingHandler(this);
		}
	}

	private void startDialogChain() {
		autosavedMaterial = getFileManager().getAutosaveJSON();
		afterLocalizationLoaded(new Runnable() {
			@Override
			public void run() {
				maybeShowRecentChangesDialog();
			}
		});
	}

	private void maybeShowRecentChangesDialog() {
		if (ALLOW_RECENT_CHANGES_DIALOG
				&& shouldShowRecentChangesDialog(RECENT_CHANGES_KEY)
				&& isUnbundledGraphing()) {
			LocalizationW localization = getLocalization();
			String message = localization.getMenu(RECENT_CHANGES_KEY);
			String readMore = localization.getMenu("tutorial_apps_comparison");
			String link = "https://www.geogebra.org/m/" + readMore;
			showRecentChangesDialog(message, link, new Runnable() {
				@Override
				public void run() {
					maybeStartAutosave();
				}
			});
			setHideRecentChanges(RECENT_CHANGES_KEY);
		} else {
			maybeStartAutosave();
		}
	}

	private String getRecentChangesCookieKey(String key) {
		return "RecentChanges" + key + "Shown";
	}

	private boolean shouldShowRecentChangesDialog(String key) {
		String shown = Cookies.getCookie(getRecentChangesCookieKey(key));
		return !"true".equals(shown);
	}

	private void setHideRecentChanges(String key) {
		Cookies.setCookie(getRecentChangesCookieKey(key), "true");
	}

	private void maybeStartAutosave() {
		if (hasMacroToRestore() || !getLAF().autosaveSupported()) {
			return;
		}
		if (autosavedMaterial != null && !isStartedWithFile() && getExam() == null) {
			afterLocalizationLoaded(new Runnable() {
				@Override
				public void run() {
					getDialogManager().showRecoverAutoSavedDialog(
							AppWFull.this, autosavedMaterial);
					autosavedMaterial = null;
				}
			});
		} else {
			startAutoSave();
		}
	}

	private void showRecentChangesDialog(String message, String link,
										 final Runnable closingCallback) {
		final WhatsNewDialog dialog = new WhatsNewDialog(this, message, link);
		dialog.addCloseHandler(new CloseHandler<GPopupPanel>() {
			@Override
			public void onClose(CloseEvent<GPopupPanel> closeEvent) {
				closingCallback.run();
			}
		});
		Timer timer = new Timer() {
			@Override
			public void run() {
				dialog.show();
				dialog.center();
			}
		};
		timer.schedule(0);
	}

	/**
	 * if there are unsaved changes, the file is saved to the localStorage.
	 */
	public final void startAutoSave() {
		Timer timer = new Timer() {
			private int counter = 0;

			@Override
			public void run() {
				counter++;
				if (!isSaved()) {
					getFileManager().autoSave(counter);
				}
				getFileManager().refreshAutosaveTimestamp();
			}
		};
		timer.scheduleRepeating(AUTO_SAVE_PERIOD);

	}

	@Override
	public final void loadPreferences(Perspective p) {
		GeoGebraPreferencesW.getPref().loadForApp(this, p);

	}

	/**
	 * Update the menu height
	 */
	public void updateMenuHeight() {
		if (menuShowing) {
			int h = this.oldSplitLayoutPanel.getOffsetHeight();
			if (!isFloatingMenu()) {
				frame.getMenuBar(this).setPixelSize(GLookAndFeel.MENUBAR_WIDTH,
						h);
			} else {
				frame.getMenuBar(this).setHeight(h + "px");
			}
		}
	}

	/**
	 * @return whether floating menu is used
	 */
	protected final boolean isFloatingMenu() {
		return isUnbundledOrWhiteboard();
	}

	@Override
	public final boolean isWhiteboardActive() {
		if (activity != null) {
			return activity.isWhiteboard();
		}
		return "notes"
						.equals(getArticleElement().getDataParamAppName());
	}

	@Override
	public final void ensureStandardView() {
		getActiveEuclidianView()
				.setKeepCenter(true);
	}

	@Override
	public final void onHeaderVisible() {
		ToolbarPanel toolbar = getGuiManager().getUnbundledToolbar();
		if (isPortrait() && toolbar != null && toolbar.isClosed()) {
			toolbar.doCloseInPortrait();
		}
	}

	/**
	 * Closes the page control panel
	 *
	 * @return whether it was closed
	 */
	public boolean closePageControlPanel() {
		if (!isWhiteboardActive()) {
			return false;
		}

		return frame.getPageControlPanel().close();
	}

	/**
	 * Empty the construction but don't initialize undo
	 */
	public void loadEmptySlide() {
		kernel.clearConstruction(true);
		getSelectionManager().clearSelectedGeos();
		resetMaxLayerUsed();
		setCurrentFile(null);
		resetUI();
		resetPenTool();
		clearMedia();
	}

	@Override
	public void executeAction(EventType action, AppState state, String[] args) {
		if (action == EventType.EMBEDDED_STORE_UNDO && embedManager != null) {
			embedManager.executeAction(EventType.REDO,
					Integer.parseInt(args[0]));
		} else if (getPageController() != null) {
			getPageController().executeAction(action, state, args);
		}
	}

	@Override
	public void setActiveSlide(String slideID) {
		if (getPageController() != null) {
			getPageController().setActiveSlide(slideID);
		}
	}

	private void afterCoreObjectsInited() {
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
			// we need to make sure trace works after this, see
			// https://jira.geogebra.org/browse/TRAC-4232
			// https://jira.geogebra.org/browse/TRAC-4034
			getEuclidianView1().createImage();
			getEuclidianView1().invalidateBackground();
			DockPanelW euclidianDockPanel = (DockPanelW) getEuclidianViewpanel();
			euclidianDockPanel.setVisible(true);
			euclidianDockPanel.setEmbeddedSize(getSettings()
					.getEuclidian(1).getPreferredSize().getWidth());
			getEuclidianViewpanel().setPixelSize(
					getSettings().getEuclidian(1).getPreferredSize().getWidth(),
					getSettings().getEuclidian(1).getPreferredSize()
							.getHeight());
			euclidianDockPanel.updatePanel(false);

			oldSplitLayoutPanel = null;
			updateVoiceover();
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
		for (int i = frame.getWidgetCount() - 1; i >= 0; i--) {
			if (!(frame.getWidget(i) instanceof HasKeyboardPopup
					|| frame.getWidget(i) instanceof TabbedKeyboard
					|| (menuViewController != null
					    && frame.getWidget(i) == menuViewController.getView())
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
		if (this.getInputPosition() == InputPosition.top && articleElement
				.getDataParamShowAlgebraInput(showAlgebraInput)) {
			attachAlgebraInput();
		}

		attachSplitLayoutPanel();

		// showAlgebraInput should come from data-param,
		// this is just a 'second line of defense'
		// otherwise it can be used for taking ggb settings into account too
		if (this.getInputPosition() == InputPosition.bottom && articleElement
				.getDataParamShowAlgebraInput(showAlgebraInput)) {
			attachAlgebraInput();
		}
		if (articleElement.getDataParamShowToolBar(showToolBar)
				&& this.getToolbarPosition() == SwingConstants.SOUTH) {
			frame.attachToolbar(this);
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

		getGuiManager().getAlgebraInput()
				.setInputFieldWidth(this.appletWidth);
	}

	@Override
	protected final void updateTreeUI() {
		if (getSplitLayoutPanel() != null) {
			getSplitLayoutPanel().forceLayout();
		}
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
		return getGuiManager().getRootComponent();
	}

	private void attachSplitLayoutPanel() {
		boolean oldSLPanelChanged = oldSplitLayoutPanel != getSplitLayoutPanel();
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
					if (getGuiManager().getKeyboardListener() != null) {
						getGuiManager().getKeyboardListener().setFocus(false);
					}
					getAppletFrame().keyBoardNeeded(false, null);
				}
			};
			timer.schedule(0);
		}
	}

	@Override
	public void afterLoadFileAppOrNot(boolean asSlide) {
		for (GeoElement geo : kernel.getConstruction().getGeoSetConstructionOrder()) {
			if (geo.hasScripts()) {
				getAsyncManager().loadAllCommands();
				break;
			}
		}

		if (!getLAF().isSmart()) {
			removeSplash();
		}
		frame.updateHeaderSize();
		String perspective = getArticleElement().getDataParamPerspective();
		if (!isUsingFullGui()) {
			if (showConsProtNavigation() || !isJustEuclidianVisible()
					|| perspective.length() > 0) {
				useFullGui = true;
			}
		}
		frame.setApplication(this);
		if (isUnbundled()) {
			Perspective current = getTmpPerspective(null);
			if (current != null && current.getToolbarDefinition() != null) {
				getGuiManager().setGeneralToolBarDefinition(
						current.getToolbarDefinition());
				if (getGuiManager() != null
						&& getGuiManager()
								.getUnbundledToolbar() != null) {
					updatePerspectiveForUnbundled(current);
					getGuiManager().getUnbundledToolbar()
							.updateContent();
				}
			}
		}
		if (!isUsingFullGui()) {
			buildSingleApplicationPanel();
			Perspective current = getTmpPerspective(null);
			if (current != null && current.getToolbarDefinition() != null) {
				getGuiManager().setGeneralToolBarDefinition(
						current.getToolbarDefinition());
				updatePerspective(current);
			}
		} else if (!asSlide) {
			getGuiManager().getLayout().getDockManager()
					.init(frame);
			Perspective p = null;
			if (perspective != null && !StringUtil.isNaN(perspective)) {
				p = PerspectiveDecoder.decode(perspective,
						this.getKernel().getParser(),
						ToolBar.getAllToolsNoMacros(true, false, this));
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
		frame.hideSplash();

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
		this.setPreferredSize(
				new GDimensionW((int) this.getWidth(), (int) this.getHeight()));
		setDefaultCursor();
		checkScaleContainer();
		frame.useDataParamBorder();
		onOpenFile();
		showStartTooltip(0);
		setAltText();
		if (!isUnbundled() && isPortrait()) {
			adjustViews(false, false);
		}
		kernel.notifyScreenChanged();
		if (isWhiteboardActive()) {
			AdjustScreen.adjustCoordSystem(getActiveEuclidianView());
		}
	}

	private void updatePerspective(Perspective p) {
		if (!isUnbundled() || isStartedWithFile()) {
			getGuiManager().getLayout().setPerspectives(getTmpPerspectives(),
					p);
		}
		if (isUnbundled() && isPortrait()) {
			getGuiManager().getLayout().getDockManager().adjustViews(true);
		}
	}

	private void updatePerspectiveForUnbundled(Perspective perspective) {
		DockManagerW dm = (getGuiManager().getLayout().getDockManager());
		DockPanelData[] dpDataArray = perspective.getDockPanelData();
		for (DockPanelData panelData : dpDataArray) {
			DockPanelW panel = dm.getPanel(panelData.getViewId());
			if (panel instanceof ToolbarDockPanelW) {
				updateToolbarPanelVisibility((ToolbarDockPanelW) panel, panelData.isVisible());
			}
			if (panel != null && !isPortrait()) {
				updateDividerLocation(dm, panelData);
			}
		}
		updateContentPane();
	}

	private void updateToolbarPanelVisibility(ToolbarDockPanelW toolbarDockPanel, boolean visible) {
		ToolbarPanel toolbarPanel = toolbarDockPanel.getToolbar();
		if (visible) {
			toolbarPanel.open();
		} else {
			toolbarPanel.close();
		}
	}

	private void updateDividerLocation(DockManagerW dockManager, DockPanelData panelData) {
		if (!panelData.isVisible() || panelData.isOpenInFrame()) {
			return;
		}

		int divLoc = panelData.getEmbeddedSize();
		dockManager.getRoot().setDividerLocation(divLoc);
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
	public boolean hasFocus() {
		return frame.getElement().isOrHasChild(Dom.getActiveElement());
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
	 * @return whether just ev1 is isible
	 */
	private boolean isJustEuclidianVisible() {
		Perspective docPerspective = getTmpPerspective(null);

		if (docPerspective == null) {
			return true;
		}

		for (DockPanelData panel : docPerspective.getDockPanelData()) {
			if ((panel.getViewId() != App.VIEW_EUCLIDIAN)
					&& panel.isVisible()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void updateCenterPanel() {
		buildApplicationPanel();
		if (oldSplitLayoutPanel == null) {
			return; // simple GUI: avoid NPE
		}
		this.oldSplitLayoutPanel.setPixelSize(spWidth, getSpHeight());
		// we need relative position to make sure the menubar / toolbar are not
		// hidden
		this.oldSplitLayoutPanel.getElement().getStyle()
				.setPosition(Position.RELATIVE);
		if (!isUnbundled() && getGuiManager().hasAlgebraView()
				&& showView(App.VIEW_ALGEBRA)) {
			getAlgebraView().setShowAlgebraInput(showAlgebraInput()
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
			spWidth = oldSplitLayoutPanel.getEstimateWidth();
			spHeight = oldSplitLayoutPanel.getEstimateHeight();
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
		if (getSpHeight() > 0) {
			return getSpHeight();
		}
		return super.getHeightForSplitPanel(fallback);
	}

	@Override
	public void toggleMenu() {
		if (!menuShowing) {
			getAppletFrame().hidePanel(null);
			menuShowing = true;
			boolean needsUpdate = isMenuInited;
			if (!isFloatingMenu() && !isMenuInited) {
				frame.getMenuBar(this).init(this);
				isMenuInited = true;
			} else if (isFloatingMenu()) {
				menuViewController.setMenuVisible(true);
				return;
			}
			splitPanelWrapper.add(frame.getMenuBar(this));
			oldSplitLayoutPanel.setPixelSize(
					oldSplitLayoutPanel.getOffsetWidth()
							- GLookAndFeel.MENUBAR_WIDTH,
					oldSplitLayoutPanel.getOffsetHeight());
			updateMenuHeight();
			if (needsUpdate) {
				frame.getMenuBar(this).getMenubar().updateMenubar();
			}
			getGuiManager().refreshDraggingViews();
			oldSplitLayoutPanel.getElement().getStyle()
					.setOverflow(Overflow.HIDDEN);
			frame.getMenuBar(this).getMenubar().dispatchOpenEvent();
		} else {
			if (isFloatingMenu()) {
				menuViewController.setMenuVisible(false);
			} else {
				hideMenu();
			}
		}
	}

	@Override
	public void onMenuOpened() {
		menuShowing = true;
		updateMenuBtnStatus(true);
	}

	@Override
	public void onMenuClosed() {
		menuShowing = false;
		updateMenuBtnStatus(false);
	}

	private void updateMenuBtnStatus(boolean expanded) {
		if (getGuiManager() != null) {
			ToolbarPanel toolbarPanel = getGuiManager()
					.getUnbundledToolbar();
			if (toolbarPanel != null) {
				toolbarPanel.markMenuAsExpanded(expanded);
			}
		}
	}

	@Override
	public void hideMenu() {
		if (!isMenuInited || !menuShowing) {
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

		if (getGuiManager() != null && getGuiManager().getLayout() != null) {
			getGuiManager().getLayout().getDockManager().resizePanels();
		}

		if (getGuiManager() != null) {
			getGuiManager().setDraggingViews(false, true);
		}
	}

	@Override
	public boolean isMenuShowing() {
		return this.menuShowing;
	}

	@Override
	public void addToHeight(int i) {
		this.setSpHeight(this.getSpHeight() + i);
	}

	/**
	 * Updates height of split panel accordingly if there is algebra input
	 * and/or toolbar or not.
	 */
	@Override
	public void updateSplitPanelHeight() {
		int newHeight = frame.computeHeight();
		if (showAlgebraInput()
				&& getInputPosition() != InputPosition.algebraView
				&& getGuiManager().getAlgebraInput() != null) {
			newHeight -= getGuiManager().getAlgebraInput()
					.getOffsetHeight();
		}
		if (getToolbar() != null && getToolbar().isShown()) {
			newHeight -= ((GGWToolBar) getToolbar()).getOffsetHeight();
		}

		if (frame.isKeyboardShowing()) {
			newHeight -= frame.getKeyboardHeight();

		}
		if (newHeight >= 0) {
			this.setSpHeight(newHeight);
			if (oldSplitLayoutPanel != null) {
				oldSplitLayoutPanel.setHeight(getSpHeight() + "px");
				getGuiManager().updateUnbundledToolbar();
			}
		}
	}

	@Override
	public Panel getPanel() {
		return frame;
	}

	@Override
	public double getInnerWidth() {
		return getKeyboardManager().getKeyboarWidth();
	}

	@Override
	public void centerAndResizeViews() {
		centerAndResizePopups();
		resizePropertiesView();
		updateFloatingButtonsPosition();
	}

	private void centerAndResizePopups() {
		for (Widget w : popups) {
			if (w instanceof HasKeyboardPopup) {
				if (w instanceof DialogBoxW) {
					((DialogBoxW) w).centerAndResize(
							this.getAppletFrame().getKeyboardHeight());
				}
			}
		}
	}

	private void resizePropertiesView() {
		if (getGuiManager().hasPropertiesView()
				&& isUnbundledOrWhiteboard()) {
			((PropertiesViewW) getGuiManager().getPropertiesView()).resize(
					getWidth(), getHeight() - frame.getKeyboardHeight());
		}
	}

	private void updateFloatingButtonsPosition() {
		ToolbarMow toolbarMow = frame.getToolbarMow();
		if (toolbarMow != null) {
			toolbarMow.updateFloatingButtonsPosition();
		}
	}

	@Override
	public void share() {
		FileMenuW.share(this, null);
	}

	@Override
	public void setFileVersion(String version, String appName) {
		super.setFileVersion(version, appName);
		
		if (!"auto".equals(appName)
				&& "auto".equals(getArticleElement().getDataParamAppName())) {
			getArticleElement().attr("appName",
					appName == null ? "" : appName);
			String appCode = getConfig().getAppCode();

			boolean isClassic = "classic".equals(appName) || StringUtil.empty(appName);
			if (isClassic && !isApplet()) {
				removeHeader();
			}

			if (!appCode.equals(appName)) {
				this.activity = null;
				initActivity();
				getGuiManager().resetPanels();
			}
		}
	}

	@Override
	public void showCustomizeToolbarGUI() {
		showBrowser(getCustomizeToolbarGUI());
	}

	@Override
	public void exportCollada(boolean html) {
		if (html) {
			setExport3D(new FormatColladaHTML());
		} else {
			setExport3D(new FormatCollada());
		}
	}

	@Override
	public EmbedManager getEmbedManager() {
		if (embedManager == null && isWhiteboardActive()) {
			embedManager = new EmbedManagerW(this);
		}
		return embedManager;
	}

	@Override
	public final @Nonnull VideoManagerW getVideoManager() {
		if (videoManager == null) {
			videoManager = new VideoManagerW(this);
		}
		return videoManager;
	}

	@Override
	public MaskWidgetList getMaskWidgets() {
		if (maskWidgets == null) {
			maskWidgets = new MaskWidgetListW(this);
		}
		return maskWidgets;
	}

	/**
	 * Remove all widgets for videos and embeds.
	 */
	@Override
	public void clearMedia() {
		if (videoManager != null) {
			videoManager.removePlayers();
		}
		if (embedManager != null) {
			embedManager.removeAll();
		}
	}

	private int getSpHeight() {
		return spHeight;
	}

	private void setSpHeight(int spHeight) {
		this.spHeight = spHeight;
	}

	@Override
	public void openPDF(JavaScriptObject file) {
		this.getDialogManager().showPDFInputDialog(file);
	}

	@Override
	public SaveController getSaveController() {
		if (saveController == null) {
			saveController = new SaveControllerW(this);
		}
		return saveController;
	}

	@Override
	public ShareController getShareController() {
		if (shareController == null) {
			shareController = new ShareControllerW(this);
		}
		return shareController;
	}

	/**
	 * @param mowZoomPanel
	 *            zoom panel
	 */
	public void setMowZoomPanel(ZoomPanelMow mowZoomPanel) {
		this.mowZoomPanel = mowZoomPanel;
	}

	/**
	 * @return zoom panel
	 */
	public ZoomPanelMow getZoomPanelMow() {
		return mowZoomPanel;
	}

	@Override
	public AlgebraViewW getAlgebraView() {
		if (getGuiManager() == null) {
			return null;
		}
		return getGuiManager().getAlgebraView();
	}

	@Override
	public JavaScriptObject getEmbeddedCalculators() {
		getEmbedManager();
		return embedManager != null ? embedManager.getEmbeddedCalculators() : null;
	}

	@Override
	public @Nonnull KeyboardManager getKeyboardManager() {
		if (keyboardManager == null) {
			keyboardManager = new KeyboardManager(this);
		}
		return keyboardManager;
	}

	@Override
	public ScriptManager newScriptManager() {
		return new ScriptManagerW(this, getActivity().getApiExporter());
	}

	@Override
	protected SettingsUpdaterBuilder newSettingsUpdaterBuilder() {
		return new SettingsUpdaterBuilder(this)
				.withFontSettingsUpdater(new FontSettingsUpdaterW(this));
	}

	@Override
	public HasLastItem getLastItemProvider() {
		if (!getConfig().hasAnsButtonInAv()
				|| getActiveEuclidianView().getEuclidianController()
				.isSymbolicEditorSelected()) {
			return null;
		}
		return new ConstructionItemProvider(getKernel().getConstruction(), getAlgebraView(),
				createGeoElementValueConverter());
	}

	@Override
	public void startExam() {
		super.startExam();

		// ensure fullscreen: we may have lost it when handling unsaved
		// changes
		getLAF().toggleFullscreen(true);
		if (guiManager != null && guiManager.getUnbundledToolbar() != null) {
			guiManager.setUnbundledHeaderStyle("examOk");
			menuViewController.setExamMenu();
			guiManager.resetMenu();
			GlobalHeader.INSTANCE.addExamTimer();
			new ExamUtil(this).visibilityEventMain();
			guiManager.initInfoBtnAction();
		}
	}

	/**
	 * Ends the exam mode, exits the exam view.
	 */
	public void endExam() {
		setExam(null);
		resetViewsEnabled();
		LayoutW.resetPerspectives(this);
		getLAF().addWindowClosingHandler(this);
		fireViewsChangedEvent();
		guiManager.updateToolbarActions();
		guiManager.setGeneralToolBarDefinition(ToolBar.getAllToolsNoMacros(true, false, this));
		menuViewController.setDefaultMenu();
		guiManager.resetMenu();
		setActivePerspective(0);
	}

	@Override
	public InlineTextController createInlineTextController(EuclidianView view, GeoInlineText geo) {
		Element parentElement = ((EuclidianViewW) view).getAbsolutePanel().getParent().getElement();
		return new InlineTextControllerW(geo, view, parentElement);
	}

	@Override
	public InlineFormulaController createInlineFormulaController(EuclidianView view,
			GeoFormula geo) {
		EuclidianDockPanelW panel = (EuclidianDockPanelW) getGuiManager().getLayout()
				.getDockManager().getPanel(VIEW_EUCLIDIAN);
		return new InlineFormulaControllerW(geo, this, panel.getEuclidianPanel());
	}

	@Override
	public InlineTableController createTableController(EuclidianView view, GeoInlineTable geo) {
		Element parentElement = ((EuclidianViewW) view).getAbsolutePanel().getParent().getElement();
		return new InlineTableControllerW(geo, view, parentElement);
	}
}
