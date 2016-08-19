package org.geogebra.web.web.main;

import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.MenuBar;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.menubar.MenuInterface;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.spreadsheet.CopyPasteCut;
import org.geogebra.common.gui.view.spreadsheet.DataImport;
import org.geogebra.common.io.OFFHandler;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.kernel.View;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.events.StayLoggedOutEvent;
import org.geogebra.common.move.ggtapi.TubeAvailabilityCheckEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.opencsv.CSVException;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.FileManagerI;
import org.geogebra.web.html5.main.StringHandler;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.URL;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.CustomizeToolbarGUI;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.HeaderPanelDeck;
import org.geogebra.web.web.gui.LanguageGUI;
import org.geogebra.web.web.gui.MyHeaderPanel;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.gui.exam.ExamDialog;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.layout.DockGlassPaneW;
import org.geogebra.web.web.gui.layout.DockManagerW;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.layout.LayoutW;
import org.geogebra.web.web.gui.layout.ZoomSplitLayoutPanel;
import org.geogebra.web.web.gui.layout.panels.AlgebraStyleBarW;
import org.geogebra.web.web.gui.menubar.PerspectivesPopup;
import org.geogebra.web.web.gui.util.PopupBlockAvoider;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.web.gui.view.dataCollection.DataCollection;
import org.geogebra.web.web.gui.view.spreadsheet.MyTableW;
import org.geogebra.web.web.move.ggtapi.models.GeoGebraTubeAPIW;
import org.geogebra.web.web.move.ggtapi.models.MaterialCallback;
import org.geogebra.web.web.move.googledrive.operations.GoogleDriveOperationW;

import java.util.ArrayList;
import java.util.List;

/**
 * App with all the GUI
 *
 */
public abstract class AppWFull extends AppW {

	private DataCollection dataCollection;
	private GuiManagerInterfaceW guiManager = null;
	private LanguageGUI lg;

	private CustomizeToolbarGUI ct;
	/** flag to prevent infinite recursion in focusGained */
	boolean focusGainedRunning = false;
	private ArrayList<Runnable> waitingForLocalization;
	private boolean localizationLoaded;

	/**
	 * 
	 * @param ae
	 *            article element
	 * @param dimension
	 *            2 or 3 (for 2D or 3D app)
	 * @param laf
	 *            look and feel
	 */
	protected AppWFull(ArticleElement ae, int dimension, GLookAndFeelI laf) {
		super(ae, dimension, laf);
		

		if (this.isExam()) {
			afterLocalizationLoaded(new Runnable() {

				@Override
				public void run() {
					examWelcome();
				}
			});
		}
	}

	@Override
	public void showKeyboard(MathKeyboardListener textField) {
		showKeyboard(textField, false);
	}

	/**
	 * @return data collection view
	 */
	public DataCollection getDataCollection() {
		if (this.dataCollection == null) {
			this.dataCollection = new DataCollection(this);
		}
		return this.dataCollection;
	}

	@Override
	public void showKeyboard(MathKeyboardListener textField, boolean forceShow) {
		getAppletFrame().showKeyBoard(true, textField, forceShow);
		if (textField != null) {
			CancelEventTimer.keyboardSetVisible();
		}
	}

	@Override
	public void updateKeyboardHeight() {
		getAppletFrame().updateKeyboardHeight();
	}
	
	@Override
	public void updateKeyBoardField(MathKeyboardListener field) {
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
		return new GuiManagerW(AppWFull.this, getDevice());
	}

	/**
	 * @return device (tablet / Win store device / browser)
	 */
	protected abstract GDevice getDevice();

	@Override
	public void hideKeyboard() {
		getAppletFrame().showKeyBoard(false, null, false);
	}

	@Override
	public final boolean letShowPropertiesDialog() {
		return rightClickEnabled
				|| getArticleElement().getDataParamShowMenuBar(false)
				|| getArticleElement().getDataParamApp();
	}

	@Override
	public void updateKeyboard() {

		getGuiManager().focusScheduled(false, false, false);
		getGuiManager().invokeLater(new Runnable() {

			@Override
			public void run() {
				DockPanelW dp = ((DockManagerW) getGuiManager().getLayout().getDockManager()).getPanelForKeyboard();
				if (dp != null && dp.getKeyboardListener() != null) {
					// dp.getKeyboardListener().setFocus(true);
					dp.getKeyboardListener().ensureEditing();
					dp.getKeyboardListener().setFocus(true, true);
					if (isKeyboardNeeded()
							&& (getExam() == null || getExam().getStart() > 0)) {
						getAppletFrame().showKeyBoard(true,
								dp.getKeyboardListener(), true);
					}
				}
				if (!isKeyboardNeeded()) {
					getAppletFrame().showKeyBoard(false, null, true);
				}

			}
		});

	}

	@Override
	public void notifyLocalizationLoaded() {
		if(waitingForLocalization == null){
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
	public void afterLocalizationLoaded(Runnable run) {
		if (localizationLoaded) {
			run.run();
		} else {
			if (waitingForLocalization == null) {
				waitingForLocalization = new ArrayList<Runnable>();
			}
			waitingForLocalization.add(run);
		}
	}

	@Override
	public void showStartTooltip(final int perspID) {
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
		String[] tutorials = new String[] { "graphing", "graphing", "geometry",
				"spreadsheet",
				"cas", "3d", "probability" };
		if (articleElement.getDataParamShowStartTooltip(perspID > 0)) {
			ToolTipManagerW.sharedInstance().setBlockToolTip(false);
			String tooltipText = getPlain("NewToGeoGebra")
					+ "<br/>"
					+ getLocalization()
					.getPlain(
							"CheckOutTutorial",
							getLocalization().getMenu(
									Perspective.perspectiveNames[perspID]));
			String tooltipURL = GeoGebraConstants.QUICKSTART_URL
					+ tutorials[perspID] + "/"
					+ getLocalization().getLocaleStr() + "/";
			DockPanelW focused = ((DockManagerW) getGuiManager().getLayout()
					.getDockManager()).getPanelForKeyboard();
			ToolTipManagerW.sharedInstance().showBottomInfoToolTip(
					tooltipText,
					tooltipURL, ToolTipLinkType.Help,
 this,
					focused != null && focused.isVisible());
			ToolTipManagerW.sharedInstance().setBlockToolTip(true);
		}
	}

	@Override
	public void checkSaved(Runnable runnable) {
		((DialogManagerW) getDialogManager()).getSaveDialog().showIfNeeded(
				runnable);
	}

	@Override
	public void openCSV(String csv) {
			String[][] data = DataImport.parseExternalData(this, csv, true);
		CopyPasteCut cpc = ((MyTableW) getGuiManager().getSpreadsheetView()
				.getSpreadsheetTable()).getCopyPasteCut();
		cpc.pasteExternal(
					data, 0, 0, data.length > 0 ? data[0].length - 1 : 0,
					data.length);
			onOpenFile();
	}



	@Override
	public void focusGained(View v, Element el) {
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
	}

	@Override
	public final void uploadToGeoGebraTube() {

		final PopupBlockAvoider popupBlockAvoider = new PopupBlockAvoider();
		final GeoGebraTubeExportW ggbtube = new GeoGebraTubeExportW(this);
		getGgbApi().getBase64(true, new StringHandler() {

			@Override
			public void handle(String s) {
				ggbtube.uploadWorksheetSimple(s, popupBlockAvoider);

			}
		});
	}

	@Override
	public void fileNew() {
		super.fileNew();
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
		GeoGebraPreferencesW.getPref().loadXMLPreferences(this);

		resetAllToolbars();
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
	
	@Override
	public void openOFF(String content){
		OFFHandler h = new OFFHandler(getKernel(), 
				getKernel().getConstruction());
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
		afterLoadFileAppOrNot();

	}

	@Override
	public void showConfirmDialog(String title, String mess) {
		getOptionPane().showInputDialog(this, "", title, mess,
				GOptionPane.OK_CANCEL_OPTION, GOptionPane.PLAIN_MESSAGE, null,
				null, null);
	}

	/**
	 * Updates the stylebar in Algebra View
	 */
	public void updateAVStylebar() {
		if (getGuiManager() != null && getGuiManager().hasAlgebraView()) {
			AlgebraStyleBarW styleBar = ((AlgebraViewW) getView(App.VIEW_ALGEBRA))
					.getStyleBar(false);
			if (styleBar != null) {
				styleBar.update(null);
			}
		}
	}


	/**
	 * Popup exam welcome message
	 */
	@Override
	public void examWelcome(){
		if (isExam() && getExam().getStart() < 0) {
			this.closePerspectivesPopup();

			new ExamDialog(this).show();

			if (Location.getHost() != null) {
				return;
			}

		}
	}

	@Override
	public abstract HeaderPanelDeck getAppletFrame();
	@Override
	public ToolBarInterface getToolbar() {
		return getAppletFrame().getToolbar();
	}

	@Override
	protected CustomizeToolbarGUI getCustomizeToolbarGUI() {
		if (this.ct == null) {
			this.ct = new CustomizeToolbarGUI(this);
		}
		int toolbarId = getGuiManager().getActiveToolbarId();
		Log.debug("[CT] toolbarId: " + toolbarId);
		ct.setToolbarId(toolbarId);
		return this.ct;
	}

	@Override
	public final LanguageGUI getLanguageGUI() {
		if (this.lg == null) {
			this.lg = new LanguageGUI(this);
		}
		return this.lg;
	}

	@Override
	public final void set1rstMode() {
		GGWToolBar.set1rstMode(this);
	}

	@Override
	public final void setLabels() {
		super.setLabels();
		if (this.lg != null) {
			lg.setLabels();
		}
		if (this.ct != null) {
			ct.setLabels();
		}
	}

	@Override
	public final native void copyBase64ToClipboardChromeWebAppCase(String str) /*-{
		// solution copied from geogebra.web.gui.view.spreadsheet.CopyPasteCutW.copyToSystemClipboardChromeWebapp
		// although it's strange that .contentEditable is not set to true
		var copyFrom = @org.geogebra.web.web.gui.view.spreadsheet.CopyPasteCutW::getHiddenTextArea()();
		copyFrom.value = str;
		copyFrom.select();
		$doc.execCommand('copy');
	}-*/;

	@Override
	public final boolean isSelectionRectangleAllowed() {
		return getToolbar() != null;
	}

	@Override
	public void toggleShowConstructionProtocolNavigation(int id) {
		super.toggleShowConstructionProtocolNavigation(id);
		if (getGuiManager() != null) {
			getGuiManager().updateMenubar();
		}
	}

	@Override
	protected final void updateTreeUI() {
		if (getSplitLayoutPanel() instanceof ZoomSplitLayoutPanel) {
			((ZoomSplitLayoutPanel) getSplitLayoutPanel()).forceLayout();
		}
		// updateComponentTreeUI();

	}

	@Override
	public final FileManagerI getFileManager() {
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
	public void openSearch(String query) {
		showBrowser((MyHeaderPanel) getGuiManager().getBrowseView(query));
	}

	@Override
	protected final void initGoogleDriveEventFlow() {

		googleDriveOperation = new GoogleDriveOperationW(this);
		String state = URL.getQueryParameterAsString("state");
		if (getNetworkOperation().isOnline() && state != null
				&& !"".equals(state)) {
			googleDriveOperation.initGoogleDriveApi();
		}

	}

	@Override
	public final Element getFrameElement() {
		return getAppletFrame().getElement();
	}

	/** material ID waiting for login */
	String toOpen = "";
	private PerspectivesPopup perspectivesPopup;
	@Override
	public final void openMaterial(final String id, final Runnable onError) {
		if (((GeoGebraTubeAPIW) getLoginOperation().getGeoGebraTubeAPI())
				.isCheckDone()) {
			doOpenMaterial(id, onError);
		} else {
			toOpen = id;
			getLoginOperation().getView().add(new EventRenderable() {

				@Override
			public void renderEvent(BaseEvent event) {
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
		}

	}

	/**
	 * @param id
	 *            material ID
	 * @param onError
	 *            error callback
	 */
	public void doOpenMaterial(String id, final Runnable onError) {
		((GeoGebraTubeAPIW) getLoginOperation().getGeoGebraTubeAPI()).getItem(
				id, new MaterialCallback() {

					@Override
					public void onLoaded(final List<Material> parseResponse,
							ArrayList<Chapter> meta) {
						if (parseResponse.size() == 1) {
							Material material = parseResponse.get(0);
							material.setSyncStamp(parseResponse.get(0)
									.getModified());
							getGgbApi().setBase64(material.getBase64());
							setActiveMaterial(material);
							AppWFull.this.updateMaterialURL(material.getId(),
									material.getSharingKey());
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
	public final boolean isOffline() {
		return getDevice().isOffline(this);
	}

	/**
	 * @return glass pane
	 */
	public DockGlassPaneW getGlassPane() {
		// TODO Auto-generated method stub
		return null;
	}


	private void addMenuItem(MenuBar parentMenu, String key,
			MenuInterface subMenu) {

		if (subMenu instanceof MenuBar) {
			((MenuBar) subMenu).addStyleName("GeoGebraMenuBar");
		}
		ImageResource imgRes = AppResources.INSTANCE.empty();

		if ("Labeling".equals(key)) {
			imgRes = AppResources.INSTANCE.mode_showhidelabel_16();
		}
		if ("FontSize".equals(key)) {
			imgRes = GuiResources.INSTANCE.menu_icon_options_font_size();
		}
		parentMenu.addItem(
				getGuiManager().getMenuBarHtml(imgRes,
						getLocalization().getMenu(key), true),
				true, (MenuBar) subMenu);
	}

	@Override
	public void showPerspectivesPopup() {

		getPerspectivesPopup().showPerspectivesPopup();
	}

	@Override
	public void closePerspectivesPopup() {

		getPerspectivesPopup().closePerspectivesPopup();
	}

	@Override
	public void setActivePerspective(int index) {
		getPerspectivesPopup().setActivePerspective(index);
	}

	private PerspectivesPopup getPerspectivesPopup() {
		if (this.perspectivesPopup == null) {
			this.perspectivesPopup = new PerspectivesPopup(this);
		}
		return perspectivesPopup;
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
				getInputPosition() == InputPosition.bottom);
	}

}
