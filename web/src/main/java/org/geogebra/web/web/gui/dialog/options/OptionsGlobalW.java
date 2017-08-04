package org.geogebra.web.web.gui.dialog.options;

import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;
import org.geogebra.web.web.main.GeoGebraPreferencesW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * global settings tab
 * 
 * @author csilla
 *
 */
public class OptionsGlobalW implements OptionPanelW {
	/**
	 * application
	 */
	AppW app;
	private FlowPanel optionsPanel;
	private Label lblGlobal;
	private Label lblRounding;
	/**
	 * rounding combo box
	 */
	ListBox roundingList;
	private Label lblLabeling;
	/**
	 * labeling combo box
	 */
	ListBox labelingList;
	private Label lblFontSize;
	/**
	 * font size combo box
	 */
	ListBox fontSizeList;
	private StandardButton saveSettingsBtn;
	private StandardButton restoreSettingsBtn;
	


	/**
	 * @param app
	 *            - application
	 */
	public OptionsGlobalW(AppW app) {
		this.app = app;
		createGUI();
		updateGUI();
		// app.getSettings().getAlgebra().addListener(this);
	}
	
	private void createGUI() {
		optionsPanel = new FlowPanel();
		optionsPanel.setStyleName("algebraOptions");
		lblGlobal = new Label(app.getLocalization().getMenu("Global"));
		lblGlobal.addStyleName("panelTitle");
		optionsPanel.add(lblGlobal);
		addLabelsWithComboBox();
		addSaveSettingBtn();
		addRestoreSettingsBtn();
	}

	private void addLabelsWithComboBox() {
		addRoundingItem();
		addLabelingItem();
		addFontItem();
	}

	private void addRoundingItem() {
		lblRounding = new Label(
				app.getLocalization().getMenu("Rounding") + ":");
		roundingList = new ListBox();
		optionsPanel.add(LayoutUtilW.panelRowIndent(lblRounding, roundingList));
		roundingList.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				try {
					String decStr = roundingList.getValue(roundingList.getSelectedIndex()).substring(0, 2).trim();
					int decimals = Integer.parseInt(decStr);
					// Application.debug("decimals " + decimals);

					app.getKernel().setPrintDecimals(decimals);
					app.getKernel().updateConstruction();
					app.refreshViews();

					// see ticket 79
					app.getKernel().updateConstruction();

					app.setUnsaved();
				} catch (Exception e) {
					e.printStackTrace();
					app.showError(e.toString());
				}
			}
		});
	}

	private void addLabelingItem() {
		lblLabeling = new Label(
				app.getLocalization().getMenu("Labeling") + ":");
		labelingList = new ListBox();
		optionsPanel.add(LayoutUtilW.panelRowIndent(lblLabeling, labelingList));
		labelingList.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int index = labelingList.getSelectedIndex();
				if (app.isUnbundledGraphing()) {
					index++;
				}
				app.setLabelingStyle(index);
				app.setUnsaved();
			}
		});
	}

	private void addFontItem() {
		lblFontSize = new Label(
				app.getLocalization().getMenu("FontSize") + ":");
		fontSizeList = new ListBox();
		optionsPanel.add(LayoutUtilW.panelRowIndent(lblFontSize, fontSizeList));
		fontSizeList.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				String fontStr = fontSizeList
						.getValue(fontSizeList.getSelectedIndex());
				try {
					app.setFontSize(Integer.parseInt(fontStr.substring(0, 2)),
							true);
					app.setUnsaved();
				} catch (Exception e) {
					app.showError(e.toString());
				}
			}
		});
	}
	
	/**
	 * select the font size stored in app
	 */
	void setFontSizeInComboBox() {
		int font = app.getFontSize();
		for (int i = 0; i < fontSizeList.getItemCount(); i++) {
			if (fontSizeList.getValue(i).startsWith(String.valueOf(font))) {
				fontSizeList.setSelectedIndex(i);
				return;
			}
		}
	}

	/**
	 * select labeling style stored in app
	 */
	void setLabelingInComboBox() {
		int labeling = app.getLabelingStyle();
		if (app.isUnbundledGraphing()) {
			switch (labeling) {
			case 1:
				labelingList.setSelectedIndex(0);
				break;
			case 2: 
				labelingList.setSelectedIndex(1);
				break;
			case 3:
				labelingList.setSelectedIndex(2);
				break;
			default:
				labelingList.setSelectedIndex(0);
			}
		} else {
			labelingList.setSelectedIndex(labeling);
		}
	}

	/**
	 * select decimal places stored in app
	 */
	void setRoundingInComboBox() {
		int decimals = app.getKernel().getPrintDecimals();
		for (int i = 0; i < roundingList.getItemCount(); i++) {
			if (roundingList.getValue(i).startsWith(String.valueOf(decimals))) {
				roundingList.setSelectedIndex(i);
				return;
			}
		}
	}

	private void addRestoreSettingsBtn() {
		restoreSettingsBtn = new StandardButton(
				app.getLocalization().getMenu("Settings.ResetDefault"));
		restoreSettingsBtn.setStyleName("MyCanvasButton");
		restoreSettingsBtn.addStyleName("settingsBtn");
		restoreSettingsBtn.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				resetDefault();
				setFontSizeInComboBox();
				setLabelingInComboBox();
				setRoundingInComboBox();
			}
		});
		optionsPanel.add(LayoutUtilW.panelRowIndent(saveSettingsBtn,
				restoreSettingsBtn));
	}

	private void addSaveSettingBtn() {
		saveSettingsBtn = new StandardButton(
				app.getLocalization().getMenu("Settings.Save"));
		saveSettingsBtn.setStyleName("MyCanvasButton");
		saveSettingsBtn.addStyleName("settingsBtn");
		saveSettingsBtn.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				GeoGebraPreferencesW.getPref().saveXMLPreferences(app);
			}
		});
		optionsPanel.add(saveSettingsBtn);
	}

	@Override
	public void updateGUI() {
		updateRoundingList();
		setRoundingInComboBox();
		updateLabelingList();
		setLabelingInComboBox();
		updateFontSizeList();
		setFontSizeInComboBox();
	}

	private void updateFontSizeList() {
		for (int i = 0; i < org.geogebra.common.util.Util
				.menuFontSizesLength(); i++) {
			fontSizeList.addItem(
					org.geogebra.common.util.Util.menuFontSizes(i) + " pt");
		}
	}

	private void updateLabelingList() {
		String[] labelingStrs = { "Labeling.automatic", "Labeling.on",
				"Labeling.off", "Labeling.pointsOnly" };
		for (String str : labelingStrs) {
			if (str.equals("Labeling.automatic") && app.isUnbundledGraphing()) {
				continue;
			}
			labelingList.addItem(app.getLocalization().getMenu(str));
		}
	}

	private void updateRoundingList() {
		String[] strDecimalSpaces = app.getLocalization().getRoundingMenuWithoutSeparator();
		for (String str : strDecimalSpaces) {
			roundingList.addItem(str);
		}
	}

	@Override
	public Widget getWrappedPanel() {
		return optionsPanel;
	}

	@Override
	public void onResize(int height, int width) {
		// TODO Auto-generated method stub

	}

	@Override
	public MultiRowsTabPanel getTabPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Reset defaults
	 */
	protected void resetDefault() {
		GeoGebraPreferencesW.getPref().clearPreferences();

		// reset defaults for GUI, views etc
		// this has to be called before load XML preferences,
		// in order to avoid overwrite
		app.getSettings().resetSettings(app);

		// for geoelement defaults, this will do nothing, so it is
		// OK here
		GeoGebraPreferencesW.getPref().resetPreferences(app);

		// reset default line thickness etc
		app.getKernel().getConstruction().getConstructionDefaults()
				.resetDefaults();

		// reset defaults for geoelements; this will create brand
		// new objects
		// so the options defaults dialog should be reset later
		app.getKernel().getConstruction().getConstructionDefaults()
				.createDefaultGeoElements();

		// reset the stylebar defaultGeo
		if (app.getEuclidianView1().hasStyleBar()) {
			app.getEuclidianView1().getStyleBar().restoreDefaultGeo();
		}
		if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1).hasStyleBar()) {
			app.getEuclidianView2(1).getStyleBar().restoreDefaultGeo();
		}
		// TODO needed to eg. update rounding, possibly too heavy
		app.getKernel().updateConstruction();
	}

}
