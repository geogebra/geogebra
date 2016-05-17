package org.geogebra.web.web.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.spreadsheet.CopyPasteCut;
import org.geogebra.common.gui.view.spreadsheet.DataImport;
import org.geogebra.common.io.OFFHandler;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.kernel.View;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.events.StayLoggedOutEvent;
import org.geogebra.common.move.ggtapi.TubeAvailabilityCheckEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.opencsv.CSVException;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.view.algebra.GeoContainer;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ExamUtil;
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
import org.geogebra.web.web.gui.dialog.DialogBoxW;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.gui.layout.DockGlassPaneW;
import org.geogebra.web.web.gui.layout.DockManagerW;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.layout.LayoutW;
import org.geogebra.web.web.gui.layout.ZoomSplitLayoutPanel;
import org.geogebra.web.web.gui.layout.panels.AlgebraStyleBarW;
import org.geogebra.web.web.gui.util.PopupBlockAvoider;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.web.gui.view.dataCollection.DataCollection;
import org.geogebra.web.web.gui.view.spreadsheet.MyTableW;
import org.geogebra.web.web.move.ggtapi.models.GeoGebraTubeAPIW;
import org.geogebra.web.web.move.ggtapi.models.MaterialCallback;
import org.geogebra.web.web.move.googledrive.operations.GoogleDriveOperationW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class AppWFull extends AppW {

	private DataCollection dataCollection;
	private GuiManagerInterfaceW guiManager = null;
	private LanguageGUI lg;

	private CustomizeToolbarGUI ct;
	// maybe this is unnecessary, just I did not want to make error here
	boolean infiniteLoopPreventer = false;
	private ArrayList<Runnable> waitingForLocalization;
	private boolean localizationLoaded;
	protected AppWFull(ArticleElement ae, int dimension, GLookAndFeelI laf) {
		super(ae, dimension, laf);
		if (this.isExam()) {
			afterLocalizationLoaded(new Runnable() {

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

			public void run() {
				doShowStartTooltip(perspID);
			}
		});
	}

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
			if (!infiniteLoopPreventer) {
				infiniteLoopPreventer = true;
				getGuiManager().setActiveView(v.getViewID());
				infiniteLoopPreventer = false;
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
	public void examWelcome(){
		if (isExam() && getExam().getStart() < 0) {
			Localization loc = getLocalization();
			//StyleInjector
			//		.inject(GuiResources.INSTANCE.examStyleLTR().getText());
			final DialogBoxW box = new DialogBoxW(false, true, null, getPanel());
			VerticalPanel mainWidget = new VerticalPanel();
			FlowPanel btnPanel = new FlowPanel();
			FlowPanel cbxPanel = new FlowPanel();

			Button btnOk = new Button();
			Button btnCancel = new Button();
			Button btnHelp = new Button();
			//mainWidget.add(btnPanel);
			
			btnPanel.add(btnOk);
			btnPanel.add(btnCancel);
			btnPanel.add(btnHelp);
			
			btnOk.setText(loc.getMenu("exam_start_button"));
			btnCancel.setText(loc.getMenu("Cancel"));
			btnHelp.setText(loc.getMenu("Help"));

			//description.addStyleName("padding");
			box.addStyleName("boxsize");
			int checkboxes = 0;

			if(getArticleElement().getDataParamEnableCAS(false)
				||!getArticleElement().getDataParamEnableCAS(true)){
				getExam().setCASAllowed(getArticleElement().getDataParamEnableCAS(false));
			}else{
				checkboxes++;
				final CheckBox cas = new CheckBox(loc.getMenu("Perspective.CAS"));
				cas.addStyleName("examCheckbox");
				cas.setValue(true);
				getExam().setCASAllowed(true);
				cbxPanel.add(cas);
				cas.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						getExam().setCASAllowed(cas.getValue());
						getGuiManager().updateToolbarActions();
					}
				});
			}
			if(getArticleElement().getDataParamEnable3D(false)
				||!getArticleElement().getDataParamEnable3D(true)){
				getExam().setCASAllowed(getArticleElement().getDataParamEnable3D(false));
			}else{
				checkboxes++;
				final CheckBox allow3D = new CheckBox(loc.getMenu("Perspective.3DGraphics"));
				allow3D.addStyleName("examCheckbox");
				allow3D.setValue(true);
			
				getExam().set3DAllowed(true);

				cbxPanel.add(allow3D);
				allow3D.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						getExam().set3DAllowed(allow3D.getValue());
						getGuiManager().updateToolbarActions();

					}
				});
			}
			getGuiManager().updateToolbarActions();
			if (checkboxes > 0) {
				Label description = new Label(
						loc.getMenu("exam_custom_description"));
				mainWidget.add(description);
				mainWidget.add(cbxPanel);
				cbxPanel.addStyleName("ExamCheckboxPanel");
				btnPanel.addStyleName("DialogButtonPanel");
			}


			
			
			
			mainWidget.add(btnPanel);
			box.setWidget(mainWidget);
			box.getCaption().setText(getMenu("exam_custom_header"));
			box.center();
			
			// start exam button
			btnOk.addStyleName("examStartButton");
			btnOk.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					ExamUtil.toggleFullscreen(true);
					StyleInjector
					.inject(GuiResources.INSTANCE.examStyleLTR().getText());
					Date date = new Date();	
					getGuiManager().updateToolbarActions();
					getLAF().removeWindowClosingHandler();
					fileNew();
					updateRounding();
					getGgbApi().setPerspective("1");
					getGuiManager().setGeneralToolBarDefinition(
							ToolBar.getAllToolsNoMacros(true, true));
					kernel.getAlgebraProcessor().reinitCommands();
					getExam().setStart(date.getTime());
					fireViewsChangedEvent();
					getGuiManager().updateToolbar();
					getGuiManager().updateToolbarActions();
					Layout.initializeDefaultPerspectives(AppWFull.this, 0.2);
					getGuiManager().updateMenubar();
					getGuiManager().resetMenu();
					DockPanelW dp = ((DockManagerW) getGuiManager().getLayout()
							.getDockManager()).getPanelForKeyboard();
					if (dp != null
							&& dp.getKeyboardListener() instanceof GeoContainer) { // dp.getKeyboardListener().setFocus(true);

						showKeyboard(dp.getKeyboardListener(), true);
					}
					box.hide();

				}
			});
			// Cancel button
			btnCancel.addStyleName("cancelBtn");
			btnCancel.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
					getExam().exit();
					setExam(null);
					ExamUtil.toggleFullscreen(false);
					fireViewsChangedEvent();
					getGuiManager().updateToolbarActions();
					getGuiManager().setGeneralToolBarDefinition(
							ToolBar.getAllToolsNoMacros(true, false));
					getGuiManager().updateToolbar();
					getGuiManager().resetMenu();
					box.hide();
				}
			});
			// Help button
			btnHelp.addStyleName("cancelBtn");
			btnHelp.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
					ToolTipManagerW.openWindow("https://www.geogebra.org/tutorial/exam");
				}
			});

			if (Location.getHost() != null) {
				return;
			}

		}
	}

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
	@Override
	public final void openMaterial(final String id, final Runnable onError) {
		if (((GeoGebraTubeAPIW) getLoginOperation().getGeoGebraTubeAPI())
				.isCheckDone()) {
			doOpenMaterial(id, onError);
		} else {
			toOpen = id;
			getLoginOperation().getView().add(new EventRenderable() {

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

	public DockGlassPaneW getGlassPane() {
		// TODO Auto-generated method stub
		return null;
	}

}
