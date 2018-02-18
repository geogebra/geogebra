package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.lang.Language;
import org.geogebra.web.full.main.GeoGebraPreferencesW;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.StandardButton;
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
		ListBox languageList;
		private StandardButton saveSettingsBtn;
		private StandardButton restoreSettingsBtn;

		/**
		 * constructor
		 */
		protected GlobalTab() {
			createGUI();
			updateGUI();
			if (app.isUnbundledOrWhiteboard()) {
				setStyleName("propMaterialTab");
			} else {
				setStyleName("propertiesTab");
			}
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
					.add(LayoutUtilW.panelRowIndent(lblRounding, roundingList));
			roundingList.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					try {
						String decStr = roundingList
								.getValue(roundingList.getSelectedIndex())
								.substring(0, 2).trim();
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
			labelingList = new ListBox();
			lblLabeling = new FormLabel(
					app.getLocalization().getMenu("Labeling") + ":")
							.setFor(labelingList);

			optionsPanel
					.add(LayoutUtilW.panelRowIndent(lblLabeling, labelingList));
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
					.add(LayoutUtilW.panelRowIndent(lblFontSize, fontSizeList));
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

		private void addLanguageItem() {
			languageList = new ListBox();
			lblLanguage = new FormLabel(
					app.getLocalization().getMenu("Language") + ":")
							.setFor(languageList);
			optionsPanel
					.add(LayoutUtilW.panelRowIndent(lblLanguage, languageList));
			languageList.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					String fontStr = languageList
							.getValue(languageList.getSelectedIndex());
					switchLanguage(fontStr, app);
				}
			});
		}


		void setLanguageInComboBox() {
			String font = app.getLocalization().getLocaleStr();
			for (int i = 0; i < languageList.getItemCount(); i++) {
				if (languageList.getValue(i).startsWith(String.valueOf(font))) {
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
			int decimals = app.getKernel().getPrintDecimals();
			for (int i = 0; i < roundingList.getItemCount(); i++) {
				if (roundingList.getValue(i)
						.startsWith(String.valueOf(decimals))) {
					roundingList.setSelectedIndex(i);
					return;
				}
			}
		}

		private void addRestoreSettingsBtn() {
			restoreSettingsBtn = new StandardButton(
					app.getLocalization().getMenu("RestoreSettings"),
					app);
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
					app.getLocalization().getMenu("Settings.Save"), app);
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
			setRoundingInComboBox();
			updateLabelingList();
			setLabelingInComboBox();
			updateFontSizeList();
			setFontSizeInComboBox();
			updateLanguageList();
			setLanguageInComboBox();
		}

		private void updateFontSizeList() {
			fontSizeList.clear();
			for (int i = 0; i < org.geogebra.common.util.Util
					.menuFontSizesLength(); i++) {
				fontSizeList.addItem(
						org.geogebra.common.util.Util.menuFontSizes(i) + " pt");
			}
		}

		private void updateLanguageList() {
			languageList.clear();
			for (Language l : Language.values()) {
				if (!l.fullyTranslated && app.has(Feature.ALL_LANGUAGES)) {
					continue;
				}
				languageList.addItem(l.name, l.getLocaleGWT());
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
					.getRoundingMenuWithoutSeparator();
			for (String str : strDecimalSpaces) {
				roundingList.addItem(str);
			}
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

	public static void switchLanguage(String fontStr, AppW app) {
		app.getLAF().storeLanguage(fontStr, app);
		if (app.getLoginOperation().isLoggedIn()) {
			app.getLoginOperation().getGeoGebraTubeAPI().setUserLanguage(
					fontStr,
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

		app.setLanguage(fontStr);
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
