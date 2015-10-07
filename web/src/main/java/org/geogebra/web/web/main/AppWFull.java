package org.geogebra.web.web.main;

import java.util.Date;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.spreadsheet.CopyPasteCut;
import org.geogebra.common.gui.view.spreadsheet.DataImport;
import org.geogebra.common.io.OFFHandler;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.kernel.View;
import org.geogebra.common.main.App;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.opencsv.CSVException;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.view.algebra.GeoContainer;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.StringHandler;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.gui.layout.DockManagerW;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.layout.panels.AlgebraStyleBarW;
import org.geogebra.web.web.gui.util.PopupBlockAvoider;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.web.gui.view.dataCollection.DataCollection;
import org.geogebra.web.web.gui.view.spreadsheet.MyTableW;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;

public abstract class AppWFull extends AppW {

	private DataCollection dataCollection;

	protected AppWFull(ArticleElement ae, int dimension, GLookAndFeelI laf) {
		super(ae, dimension, laf);
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
					if (AppWFull.this.isKeyboardNeeded()) {
						getAppletFrame().showKeyBoard(true,
								dp.getKeyboardListener(), true);
					}
				}
				if (!AppWFull.this.isKeyboardNeeded()) {
					getAppletFrame().showKeyBoard(false, null, true);
				}

			}
		});

	}

	@Override
	public void showStartTooltip() {
		if (articleElement.getDataParamShowStartTooltip()) {
			ToolTipManagerW.sharedInstance().setBlockToolTip(false);
			ToolTipManagerW.sharedInstance().showBottomInfoToolTip(
			        getPlain("NewToGeoGebra") + "<br/>"
			                + getPlain("ClickHereToGetHelp"),
			        GeoGebraConstants.QUICKSTART_URL, ToolTipLinkType.Help,
			        this);
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

	// maybe this is unnecessary, just I did not want to make error here
	boolean infiniteLoopPreventer = false;

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
		final GeoGebraTubeExportWeb ggbtube = new GeoGebraTubeExportWeb(this);
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

	public void updateAVStylebar() {
		if (getGuiManager() != null && getGuiManager().hasAlgebraView()) {
			AlgebraStyleBarW styleBar = ((AlgebraViewW) getView(App.VIEW_ALGEBRA))
					.getStyleBar(false);
			if (styleBar != null) {
				styleBar.update(null);
			}
		}
	}
	
	@Override
	public void examWelcome(){

		if (isExam()) {
			String[] optionNames = { getMenu("StartExam") };
			getOptionPane()
					.showOptionDialog(this,
			        getMenu("WelcomeExam"), getMenu("GeoGebraExam"),
			        GOptionPane.CUSTOM_OPTION, GOptionPane.INFORMATION_MESSAGE,
			        null, optionNames, new AsyncOperation() {
				        @Override
				        public void callback(Object obj) {
					        DivElement divID = (DivElement) Document.get()
					                .getElementById("timer");
					        divID.setPropertyBoolean("started", true);
					        Date date = new Date();
					        final long start = date.getTime();
					        // We need to set seconds, otherwise it does not fit
					        // into int.
					        divID.setPropertyInt("start", (int) (start / 1000));

							DockPanelW dp = ((DockManagerW) getGuiManager()
									.getLayout().getDockManager())
									.getPanelForKeyboard();
							if (dp != null
									&& dp.getKeyboardListener() instanceof GeoContainer) { // dp.getKeyboardListener().setFocus(true);
								((GeoContainer) dp.getKeyboardListener())
										.stopEditing(null);
								dp.getKeyboardListener().ensureEditing();


							}

				        }
			        });
		}
	}

}
