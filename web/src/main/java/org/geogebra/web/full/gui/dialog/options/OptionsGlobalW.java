package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.menubar.OptionsMenu;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.lang.Language;
import org.geogebra.web.full.main.GeoGebraPreferencesW;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * global settings tab
 *
 * @author csilla
 *
 */
public class OptionsGlobalW implements OptionPanelW, SetLabels {

	/**
	 * application
	 */
	AppW app;
	private GlobalTab globalTab;
	/**
	 * tabs (for now only global)
	 */
	protected MultiRowsTabPanel tabPanel;

	/**
	 * tab for global settings
	 *
	 * @author csilla
	 *
	 */
	protected class GlobalTab extends FlowPanel implements SetLabels {
		private FlowPanel optionsPanel;
		private FormLabel lblRounding;
		/**
		 * rounding combo box
		 */
		ListBox roundingList;
		private FormLabel lblLabeling;
		/**
		 * labeling combo box
		 */
		ListBox labelingList;
		private FormLabel lblFontSize;
		/**
		 * font size combo box
		 */
		ListBox fontSizeList;
		private FormLabel lblLanguage;
		/**
		 * language setting
		 */
		ListBox languageList;
		private StandardButton saveSettingsBtn;
		private StandardButton restoreSettingsBtn;
		private FlowPanel saveRestoreRow;
		private OptionsMenu optionsMenu;

		/**
		 * constructor
		 */
		protected GlobalTab() {
			optionsMenu = new OptionsMenu(app.getLocalization());
			createGUI();
			updateGUI();
			setStyleName("propertiesTab");
			add(optionsPanel);
		}

		private void createGUI() {
			optionsPanel = new FlowPanel();
			addLabelsWithComboBox();
			addSaveSettingBtn();
			addRestoreSettingsBtn();
		}

		private void addLabelsWithComboBox() {
			addLanguageItem();
			addRoundingItem();
			addLabelingItem();
			addFontItem();
		}

		private void addRoundingItem() {
			roundingList = new ListBox();
			lblRounding = new FormLabel(
					app.getLocalization().getMenu("Rounding") + ":")
							.setFor(roundingList);
			optionsPanel
					.add(LayoutUtilW.panelRow(lblRounding, roundingList));
			roundingList.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					try {
						// TODO copypasted from RoundingProperty
						int index = roundingList.getSelectedIndex();
						boolean figures = index >= app.getLocalization()
								.getDecimalPlaces().length;
						optionsMenu.setRounding(app,
								figures ? index + 1 : index, figures);
						app.setUnsaved();
					} catch (Exception e) {
						app.showGenericError(e);
					}
				}
			});
		}

		private void addLabelingItem() {
			labelingList = new ListBox();
			lblLabeling = new FormLabel(
					app.getLocalization().getMenu("Labeling") + ":")
							.setFor(labelingList);

			optionsPanel
					.add(LayoutUtilW.panelRow(lblLabeling, labelingList));
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
			fontSizeList = new ListBox();
			lblFontSize = new FormLabel(
					app.getLocalization().getMenu("FontSize") + ":")
							.setFor(fontSizeList);
			optionsPanel
					.add(LayoutUtilW.panelRow(lblFontSize, fontSizeList));
			fontSizeList.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					String fontStr = fontSizeList
							.getValue(fontSizeList.getSelectedIndex());
					try {
						app.setFontSize(
								Integer.parseInt(fontStr.substring(0, 2)),
								true);
						app.setUnsaved();
					} catch (Exception e) {
						app.showGenericError(e);
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

		private void addLanguageItem() {
			languageList = new ListBox();
			lblLanguage = new FormLabel(
					app.getLocalization().getMenu("Language") + ":")
							.setFor(languageList);
			optionsPanel
					.add(LayoutUtilW.panelRow(lblLanguage, languageList));
			languageList.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					String localeStr = languageList
							.getValue(languageList.getSelectedIndex());
					switchLanguage(localeStr, app);
				}
			});
		}

		/**
		 * Update combobox from language locale
		 */
		void setLanguageInComboBox() {
			String localeStr = app.getLocalization().getLocaleStr();
			for (int i = 0; i < languageList.getItemCount(); i++) {
				if (languageList.getValue(i).equals(localeStr)) {
					languageList.setSelectedIndex(i);
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
			roundingList.setSelectedIndex(
					optionsMenu.getMenuDecimalPosition(app.getKernel(), true));
		}

		private void addRestoreSettingsBtn() {
			restoreSettingsBtn = new StandardButton(
					app.getLocalization().getMenu("RestoreSettings"));
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
			saveRestoreRow = LayoutUtilW
					.panelRow(saveSettingsBtn, restoreSettingsBtn);
			saveRestoreRow.setVisible(!app.isExam());
			optionsPanel.add(saveRestoreRow);
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

		/**
		 * update gui
		 */
		public void updateGUI() {
			updateRoundingList();
			updateLabelingList();
			setLabelingInComboBox();
			updateFontSizeList();
			setFontSizeInComboBox();
			updateLanguageList();
			setLanguageInComboBox();
			saveRestoreRow.setVisible(!app.isExam());
		}

		private void updateFontSizeList() {
			fontSizeList.clear();
			for (int i = 0; i < org.geogebra.common.util.Util
					.menuFontSizesLength(); i++) {
				fontSizeList.addItem(app.getLocalization().getPlain("Apt",
						org.geogebra.common.util.Util.menuFontSizes(i) + ""));
			}
		}

		private void updateLanguageList() {
			languageList.clear();
			for (Language l : Language.values()) {
				if (l.fullyTranslated || app.has(Feature.ALL_LANGUAGES)) {
					languageList.addItem(l.name, l.getLocaleGWT());
				}
			}
		}

		private void updateLabelingList() {
			labelingList.clear();
			String[] labelingStrs = { "Labeling.automatic", "Labeling.on",
					"Labeling.off", "Labeling.pointsOnly" };
			for (String str : labelingStrs) {
				if ("Labeling.automatic".equals(str)
						&& app.isUnbundledGraphing()) {
					continue;
				}
				labelingList.addItem(app.getLocalization().getMenu(str));
			}
		}

		private void updateRoundingList() {
			roundingList.clear();
			String[] strDecimalSpaces = app.getLocalization()
					.getRoundingMenu();
			for (String str : strDecimalSpaces) {
				if (!Localization.ROUNDING_MENU_SEPARATOR.equals(str)) {
					roundingList.addItem(str);
				}
			}
			setRoundingInComboBox();
		}

		/**
		 * Reset defaults
		 */
		protected void resetDefault() {
			GeoGebraPreferencesW.getPref().clearPreferences(app);

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
			app.getKernel().updateConstruction(false);
		}

		/**
		 * @param height
		 *            - height
		 * @param width
		 *            - width
		 */
		public void onResize(int height, int width) {
			this.setHeight(height + "px");
			this.setWidth(width + "px");
		}

		@Override
		public void setLabels() {
			lblRounding
					.setText(app.getLocalization().getMenu("Rounding") + ":");
			updateRoundingList();
			lblLabeling.setText(app.getLocalization().getMenu("Labeling") + ":");
			updateLabelingList();
			lblFontSize.setText(app.getLocalization().getMenu("FontSize") + ":");
			updateFontSizeList();
			lblLanguage
					.setText(app.getLocalization().getMenu("Language") + ":");
			saveSettingsBtn
					.setText(app.getLocalization().getMenu("Settings.Save"));
			restoreSettingsBtn
					.setText(app.getLocalization().getMenu("RestoreSettings"));
		}
	}

	/**
	 * @param localeStr
	 *            selected language
	 * @param app
	 *            see {@link AppW}{
	 */
	public static void switchLanguage(String localeStr, AppW app) {
		app.getLAF().storeLanguage(localeStr);
		if (app.getLoginOperation().isLoggedIn()) {
			app.getLoginOperation().getGeoGebraTubeAPI().setUserLanguage(
					localeStr,
					app.getLoginOperation().getModel().getLoginToken());
		}
		app.setUnsaved();
		// On changing language from LTR/RTL the page will
		// reload.
		// The current workspace will be saved, and load
		// back after page reloading.
		// Otherwise only the language will change, and the
		// setting related with language.

		// TODO change direction if Localization
		// .rightToLeftReadingOrder(current.localeGWT) !=
		// app.getLocalization().rightToLeftReadingOrder
		app.setLanguage(localeStr);
	}

	/**
	 * @param app
	 *            - application
	 */
	public OptionsGlobalW(AppW app) {
		this.app = app;
		tabPanel = new MultiRowsTabPanel();
		globalTab = new GlobalTab();
		tabPanel.add(globalTab, app.getLocalization().getMenu("Global"));
		updateGUI();
		tabPanel.selectTab(0);
		app.setDefaultCursor();
	}

	@Override
	public void updateGUI() {
		globalTab.updateGUI();
	}

	@Override
	public Widget getWrappedPanel() {
		return tabPanel;
	}

	@Override
	public void onResize(int height, int width) {
		globalTab.onResize(height, width);
	}

	@Override
	public MultiRowsTabPanel getTabPanel() {
		return null;
	}

	@Override
	public void setLabels() {
		tabPanel.getTabBar().setTabText(0,
				app.getLocalization().getMenu("Global"));
		globalTab.setLabels();
	}
}
