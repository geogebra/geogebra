package org.geogebra.web.web.main;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.spreadsheet.CopyPasteCut;
import org.geogebra.common.gui.view.spreadsheet.DataImport;
import org.geogebra.common.io.OFFHandler;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.View;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.MaterialsManagerI;
import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.events.StayLoggedOutEvent;
import org.geogebra.common.move.ggtapi.TubeAvailabilityCheckEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.opencsv.CSVException;
import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.javax.swing.GImageIconW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.StringHandler;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.ViewW;
import org.geogebra.web.web.euclidian.EuclidianStyleBarW;
import org.geogebra.web.web.gui.AccessibilityManagerW;
import org.geogebra.web.web.gui.CustomizeToolbarGUI;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.MyHeaderPanel;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.gui.exam.ExamDialog;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.gui.layout.DockGlassPaneW;
import org.geogebra.web.web.gui.layout.DockManagerW;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.layout.DockSplitPaneW;
import org.geogebra.web.web.gui.layout.LayoutW;
import org.geogebra.web.web.gui.layout.panels.AlgebraStyleBarW;
import org.geogebra.web.web.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.web.gui.menubar.PerspectivesPopup;
import org.geogebra.web.web.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.web.gui.util.PopupBlockAvoider;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.web.gui.view.dataCollection.DataCollection;
import org.geogebra.web.web.gui.view.spreadsheet.MyTableW;
import org.geogebra.web.web.move.ggtapi.models.GeoGebraTubeAPIW;
import org.geogebra.web.web.move.ggtapi.models.MaterialCallback;
import org.geogebra.web.web.move.ggtapi.operations.LoginOperationW;
import org.geogebra.web.web.move.googledrive.operations.GoogleDriveOperationW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.HeaderPanel;

/**
 * App with all the GUI
 *
 */
public abstract class AppWFull extends AppW implements HasKeyboard {

	private final static int AUTO_SAVE_PERIOD = 2000;

	private DataCollection dataCollection;
	private GuiManagerInterfaceW guiManager = null;

	private CustomizeToolbarGUI ct;
	/** flag to prevent infinite recursion in focusGained */
	boolean focusGainedRunning = false;
	private ArrayList<Runnable> waitingForLocalization;
	private boolean localizationLoaded;
	/** browser / tablet / win store device */
	protected final GDevice device;
	/** material ID waiting for login */
	String toOpen = "";
	private PerspectivesPopup perspectivesPopup;

	private int activePerspective;

	private AccessibilityManagerInterface accessibilityManager;
	protected boolean menuShowing = false;
	protected final GeoGebraFrameBoth frame;
	protected View focusedView;
	protected DockSplitPaneW oldSplitLayoutPanel = null; // just a
																// technical

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
	 * @param gf
	 *            frame
	 */
	protected AppWFull(ArticleElement ae, int dimension,
			GLookAndFeelI laf,
			GDevice device, GeoGebraFrameBoth gf) {
		super(ae, dimension, laf);
		this.frame = gf;
		this.device = device;

		if (this.isExam()) {
			afterLocalizationLoaded(new Runnable() {

				@Override
				public final void run() {
					examWelcome();
				}
			});
		}
		if (this.getArticleElement().getDataParamShowMenuBar(false)) {
			maybeStartAutosave();
		}

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

	/**
	 * @return data collection view
	 */
	public final DataCollection getDataCollection() {
		if (this.dataCollection == null) {
			this.dataCollection = new DataCollection(this);
		}
		return this.dataCollection;
	}

	@Override
	public final boolean showKeyboard(MathKeyboardListener textField,
			boolean forceShow) {
		boolean ret = getAppletFrame().showKeyBoard(true, textField, forceShow);
		if (textField != null) {
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
		getGuiManager().setOnScreenKeyboardTextField(field);
	}

	@Override
	public final GuiManagerInterfaceW getGuiManager() {
		return guiManager;
	}

	@Override
	public final void initGuiManager() {
		// this should not be called from AppWsimple!
		setWaitCursor();
		guiManager = newGuiManager();
		getGuiManager().setLayout(new LayoutW(this));
		getGuiManager().initialize();
		setDefaultCursor();
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

		getGuiManager().focusScheduled(false, false, false);
		invokeLater(new Runnable() {

			@Override
			public final void run() {
				DockPanelW dp = ((DockManagerW) getGuiManager().getLayout()
						.getDockManager()).getPanelForKeyboard();
				MathKeyboardListener listener = getGuiManager()
						.getKeyboardListener(dp);
				if (listener != null) {
					// dp.getKeyboardListener().setFocus(true);
					listener.ensureEditing();
					listener.setFocus(true, true);
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
	public final void notifyLocalizationLoaded() {
		if (waitingForLocalization == null) {
			return;
		}
		localizationLoaded = true;
		for (Runnable run : waitingForLocalization) {
			run.run();
		}

		waitingForLocalization.clear();
	}

	/**
	 * @param run
	 *            localization callback
	 */
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
			public final void run() {
				doShowStartTooltip(perspID);
			}
		});
	}

	/**
	 * @param perspID
	 *            perspective ID
	 */
	void doShowStartTooltip(int perspID) {
		String[] tutorials = new String[] { "graphing", "graphing", "geometry",
				"spreadsheet", "cas", "3d", "probability" };
		if (articleElement.getDataParamShowStartTooltip(perspID > 0)) {
			ToolTipManagerW.sharedInstance().setBlockToolTip(false);
			String tooltipText = getLocalization().getMenu("NewToGeoGebra")
					+ "<br/>"
					+ getLocalization().getPlain("CheckOutTutorial",
							getLocalization().getMenu(
									Perspective.getPerspectiveName(perspID)));
			String tooltipURL = GeoGebraConstants.QUICKSTART_URL
					+ tutorials[perspID] + "/"
					+ getLocalization().getLocaleStr() + "/";
			DockPanelW focused = ((DockManagerW) getGuiManager().getLayout()
					.getDockManager()).getPanelForKeyboard();
			ToolTipManagerW.sharedInstance().showBottomInfoToolTip(tooltipText,
					tooltipURL, ToolTipLinkType.Help, this,
					focused != null && focused.isVisible());
			ToolTipManagerW.sharedInstance().setBlockToolTip(true);
		}
	}

	@Override
	public final void checkSaved(Runnable runnable) {
		((DialogManagerW) getDialogManager()).getSaveDialog()
				.showIfNeeded(runnable);
	}

	@Override
	public final void openCSV(String csv) {
		String[][] data = DataImport.parseExternalData(this, csv, true);
		CopyPasteCut cpc = ((MyTableW) getGuiManager().getSpreadsheetView()
				.getSpreadsheetTable()).getCopyPasteCut();
		cpc.pasteExternal(data, 0, 0, data.length > 0 ? data[0].length - 1 : 0,
				data.length);
		onOpenFile();
	}

	@Override
	public final void focusGained(View v, Element el) {
		super.focusGained(v, el);
		if (getGuiManager() != null) {
			// somehow the panel was not activated in case focus gain
			// so it is good to do here, unless it makes an
			// infinite loop... my code inspection did not find
			// infinite loop, but it is good to try to exclude that
			// anyway, e.g. for future changes in the code
			if (!focusGainedRunning) {
				focusGainedRunning = true;
				getGuiManager().setActiveView(v.getViewID());
				focusGainedRunning = false;
			}
		}
		focusedView = v;
		GeoGebraFrameW.useFocusedBorder(getArticleElement(), frame);

		// we really need to set it to true
		switch (v.getViewID()) {
		case App.VIEW_ALGEBRA:
		case App.VIEW_EUCLIDIAN:
		case App.VIEW_EUCLIDIAN2:
			this.getGlobalKeyDispatcher().setFocusedIfNotTab();
			break;
		default:
			if (App.isView3D(v.getViewID()) || ((v
					.getViewID() >= App.VIEW_EUCLIDIAN_FOR_PLANE_START)
					&& (v.getViewID() <= App.VIEW_EUCLIDIAN_FOR_PLANE_END))) {
				this.getGlobalKeyDispatcher().setFocusedIfNotTab();
			}
		}
	}

	@Override
	public final void uploadToGeoGebraTube() {

		final PopupBlockAvoider popupBlockAvoider = new PopupBlockAvoider();
		final GeoGebraTubeExportW ggbtube = new GeoGebraTubeExportW(this);
		getGgbApi().getBase64(true, new StringHandler() {

			@Override
			public final void handle(String s) {
				ggbtube.uploadWorksheetSimple(s, popupBlockAvoider);

			}
		});
	}

	@Override
	protected void resetUI() {
		resetEVs();
		// make sure file->new->probability does not clear the prob. calc
		if (this.getGuiManager() != null
				&& this.getGuiManager().hasProbabilityCalculator()) {
			((ProbabilityCalculatorView) this.getGuiManager()
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
			p = Layout.getDefaultPerspectives(Perspective.WHITEBOARD - 1);
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

		resetPenTool();

		resetToolbarPanel();

	}

	private void resetAllToolbars() {

		GuiManagerW gm = (GuiManagerW) getGuiManager();
		DockPanelW[] panels = gm.getLayout().getDockManager().getPanels();
		for (DockPanelW panel : panels) {
			if (panel.canCustomizeToolbar()) {
				panel.setToolbarString(panel.getDefaultToolbarString());
			}
		}

		gm.setToolBarDefinition(gm.getDefaultToolbarString());

	}

	/**
	 * Selects Pen tool in whiteboard
	 */
	protected final void resetPenTool() {
		if (!isWhiteboardActive()) {
			return;
		}

		setMode(EuclidianConstants.MODE_PEN, ModeSetter.TOOLBAR);
		getEuclidianController().getPen().DEFAULT_PEN_LINE
				.setLineThickness(EuclidianConstants.DEFAULT_PEN_SIZE);
		getActiveEuclidianView().getSettings()
				.setDeleteToolSize(EuclidianConstants.DEFAULT_ERASER_SIZE);

	}

	/**
	 * Resets toolbar
	 */
	protected final void resetToolbarPanel() {
		GuiManagerW gm = (GuiManagerW) getGuiManager();
		DockPanel avPanel = gm.getLayout().getDockManager()
				.getPanel(VIEW_ALGEBRA);
		if (avPanel instanceof ToolbarDockPanelW) {
			((ToolbarDockPanelW) avPanel).getToolbar().reset();
		}
	}

	@Override
	public final void openOFF(String content) {
		OFFHandler h = new OFFHandler(getKernel().getConstruction());
		h.reset();
		String[] lines = content.split("\n");
		try {
			for (String line : lines) {

				h.addLine(line);

			}
		} catch (CSVException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		h.updateAfterParsing();
		afterLoadFileAppOrNot(false);

	}

	@Override
	public final void showConfirmDialog(String title, String mess) {
		getOptionPane().showInputDialog(this, "", title, mess,
				GOptionPane.OK_CANCEL_OPTION, GOptionPane.PLAIN_MESSAGE, null,
				null, null);
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
			this.closePerspectivesPopup();

			resetViewsEnabled();

			new ExamDialog(this).show();

			if (Location.getHost() != null) {
				return;
			}

		}
	}

	@Override
	public GeoGebraFrameBoth getAppletFrame() {
		return frame;
	}

	@Override
	public final ToolBarInterface getToolbar() {
		return getAppletFrame().getToolbar();
	}

	@Override
	protected final CustomizeToolbarGUI getCustomizeToolbarGUI() {
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
	public final native void copyBase64ToClipboardChromeWebAppCase(
			String str) /*-{
		// solution copied from geogebra.web.gui.view.spreadsheet.CopyPasteCutW.copyToSystemClipboardChromeWebapp
		// although it's strange that .contentEditable is not set to true
		var copyFrom = @org.geogebra.web.html5.main.AppW::getHiddenTextArea()();
		copyFrom.value = str;
		copyFrom.select();
		$doc.execCommand('copy');
	}-*/;

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
	public final void showBrowser(HeaderPanel bg) {
		getAppletFrame().showBrowser(bg);
	}

	@Override
	public final void openSearch(String query) {
		showBrowser((MyHeaderPanel) getGuiManager().getBrowseView(query));
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
	public final void openMaterial(final String id, final Runnable onError) {
		if (getLoginOperation() != null
				&& ((GeoGebraTubeAPIW) getLoginOperation().getGeoGebraTubeAPI())
						.isCheckDone()) {
			doOpenMaterial(id, onError);
		} else {
			if (getLoginOperation() == null) {
				this.initSignInEventFlow(new LoginOperationW(this),
						ArticleElement.isEnableUsageStats());
			}
			toOpen = id;

			getLoginOperation().getView().add(new EventRenderable() {

				@Override
				public final void renderEvent(BaseEvent event) {
					Log.debug("received:" + event);
					if (event instanceof LoginEvent
							|| event instanceof StayLoggedOutEvent
							|| event instanceof TubeAvailabilityCheckEvent) {
						if (toOpen != null && toOpen.length() > 0) {
							doOpenMaterial(toOpen, onError);
							toOpen = "";
						}
					}
				}
			});
			Log.debug("listening");
		}

	}

	/**
	 * @param id
	 *            material ID
	 * @param onError
	 *            error callback
	 */
	public final void doOpenMaterial(String id, final Runnable onError) {
		((GeoGebraTubeAPIW) getLoginOperation().getGeoGebraTubeAPI())
				.getItem(id, new MaterialCallback() {

					@Override
					public final void onLoaded(
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
							getGgbApi().setBase64(material.getBase64());
							setActiveMaterial(material);
						} else {
							onError.run();
						}
					}

					@Override
					public final void onError(Throwable error) {
						onError.run();
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
			public final void onOpenFile() {
				AppWFull.this.updateMaterialURL(material.getId(),
						material.getSharingKey(), material.getTitle());
				AppWFull.this.unregisterOpenFileListener(this);
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
			public final void run() {
				getPerspectivesPopup().showPerspectivesPopup();
			}
		});

	}

	/**
	 * Removed element called ggbsplash
	 */
	protected final static void removeSplash() {
		Element el = DOM.getElementById("ggbsplash");
		if (el != null) {
			el.removeFromParent();
		}
	}

	@Override
	public final void appSplashCanNowHide() {
		String cmd = Location.getParameter("command");

		if (cmd != null) {

			Log.debug("exectuing commands: " + cmd);

			String[] cmds = cmd.split(";");
			for (int i = 0; i < cmds.length; i++) {
				getKernel().getAlgebraProcessor()
						.processAlgebraCommandNoExceptionsOrErrors(cmds[i],
								false);
			}
		}
		removeSplash();

	}

	@Override
	public final void closePerspectivesPopup() {
		if (this.perspectivesPopup != null) {
			getPerspectivesPopup().closePerspectivesPopup();
		}
	}

	@Override
	public final void setActivePerspective(int index) {
		activePerspective = index;
	}

	@Override
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
		if (getGuiManager().hasCasView()) {
			DockPanel sp = getGuiManager().getLayout().getDockManager()
					.getPanel(App.VIEW_CAS);
			if (sp != null) {
				((DockPanelW) sp).onResize();
			}
		}
		getAppletFrame()
				.setMenuHeight(getInputPosition() == InputPosition.bottom);
	}

	@Override
	public final GImageIconW wrapGetModeIcon(int mode) {
		return new GImageIconW(GGWToolBar.getImageURL(mode, this));
	}

	@Override
	public final void closePopups() {
		super.closePopups();
		EuclidianStyleBarW.setCurrentPopup(null);
		if (getToolbar() != null && getToolbar().isMobileToolbar()) {
			((GGWToolBar) getToolbar()).getToolBar().closeAllSubmenu();
		}
		if (isUnbundledOrWhiteboard()) {
			hideMenu();
			closePageControlPanel();
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

	/**
	 * @param ggburl
	 *            ggb file URL
	 */
	public final static void loadURL_GGB(String ggburl) {
		ViewW.fileLoader.getView().processFileName(ggburl);
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

	private void maybeStartAutosave() {
		if (hasMacroToRestore() || !this.getLAF().autosaveSupported()) {
			return;
		}
		final String materialJSON = getFileManager().getAutosaveJSON();
		if (materialJSON != null && !this.isStartedWithFile()
				&& this.getExam() == null) {

			afterLocalizationLoaded(new Runnable() {

				@Override
				public final void run() {
					((DialogManagerW) getDialogManager())
							.showRecoverAutoSavedDialog(AppWFull.this,
									materialJSON);
				}
			});
		} else {
			this.startAutoSave();
		}

	}

	/**
	 * if there are unsaved changes, the file is saved to the localStorage.
	 */
	public final void startAutoSave() {
		Timer timer = new Timer() {
			private int counter = 0;

			@Override
			public final void run() {
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
		return isUnbundledOrWhiteboard()
				|| has(Feature.WEB_CLASSIC_FLOATING_MENU);
	}

	@Override
	public final boolean isWhiteboardActive() {
		return activePerspective == 6
				|| "7".equals(getArticleElement().getDataParamPerspective());
	}

	@Override
	public final void ensureStandardView() {
		getActiveEuclidianView()
				.setKeepCenter(has(Feature.CENTER_STANDARD_VIEW));
	}

	@Override
	public final void onHeaderVisible() {
		ToolbarPanel toolbar = ((GuiManagerW) getGuiManager())
				.getToolbarPanelV2();
		if (isPortrait() && toolbar.isClosed()) {
			toolbar.doCloseInPortrait();
		}
	}

	@Override
	public final AccessibilityManagerInterface getAccessibilityManager() {
		if (accessibilityManager == null) {
			accessibilityManager = new AccessibilityManagerW(this);
		}
		return accessibilityManager;
	}

	/**
	 * Closes the page control panel
	 */
	public void closePageControlPanel() {
		if (!has(Feature.MOW_MULTI_PAGE)) {
			return;
		}
		frame.getPageControlPanel().close();
	}

	/**
	 * Empty the construction but don't initialize undo
	 */
	public void loadEmptySlide() {
		kernel.clearConstruction(true);
		resetMaxLayerUsed();
		setCurrentFile(null);
		setMoveMode();
		resetUI();
	}
	@Override
	public void executeAction(EventType action, String[] args) {
		getPageController().executeAction(action, args);
	}

}