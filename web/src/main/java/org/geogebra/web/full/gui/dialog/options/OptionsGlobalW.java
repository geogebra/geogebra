package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.settings.LabelVisibility;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.ValuedProperty;
import org.geogebra.common.properties.impl.general.FontSizeProperty;
import org.geogebra.common.properties.impl.general.LabelingProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.general.RoundingIndexProperty;
import org.geogebra.web.full.gui.components.CompDropDown;
import org.geogebra.web.full.main.GeoGebraPreferencesW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * global settings tab
 */
public class OptionsGlobalW implements OptionPanelW, SetLabels {

	/**
	 * application
	 */
	AppW app;
	private final GlobalTab globalTab;
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
	protected class GlobalTab extends FlowPanel implements SetLabels, PropertyValueObserver {
		private FlowPanel optionsPanel;
		private FormLabel lblRounding;
		private CompDropDown roundingDropDown;
		private FormLabel lblLabeling;
		private CompDropDown labelingDropDown;
		private FormLabel lblFontSize;
		private CompDropDown fontSizeDropDown;
		private FormLabel lblLanguage;
		private CompDropDown languageDropDown;
		private StandardButton saveSettingsBtn;
		private StandardButton restoreSettingsBtn;
		private FlowPanel saveRestoreRow;
		private final ExamController examController = GlobalScope.examController;

		/**
		 * constructor
		 */
		protected GlobalTab() {
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
			NamedEnumeratedProperty<?> roundingProp =
					new RoundingIndexProperty(app, app.getLocalization());
			roundingDropDown = new CompDropDown(app, roundingProp);
			lblRounding = new FormLabel(
					app.getLocalization().getMenu("Rounding") + ":")
							.setFor(roundingDropDown);
			lblRounding.addStyleName("dropDownLabel");
			optionsPanel
					.add(LayoutUtilW.panelRow(lblRounding, roundingDropDown));
		}

		private void addLabelingItem() {
			LabelingProperty property;
			if (app.isUnbundledOrWhiteboard()) {
				property = new LabelingProperty(app.getLocalization(),
						app.getSettings().getLabelSettings());
			} else {
				property = new LabelingProperty(app.getLocalization(),
						app.getSettings().getLabelSettings(), LabelVisibility.Automatic,
						LabelVisibility.AlwaysOn, LabelVisibility.AlwaysOff,
						LabelVisibility.PointsOnly);
			}
			labelingDropDown = new CompDropDown(app, property);
			lblLabeling = new FormLabel(
					app.getLocalization().getMenu("Labeling") + ":")
							.setFor(labelingDropDown);
			lblLabeling.addStyleName("dropDownLabel");
			optionsPanel
					.add(LayoutUtilW.panelRow(lblLabeling, labelingDropDown));
		}

		private void addFontItem() {
			NamedEnumeratedProperty<?> fontSizeProperty = new FontSizeProperty(
					app.getLocalization(),
					app.getSettings().getFontSettings(),
					app.getSettingsUpdater().getFontSettingsUpdater());
			fontSizeDropDown = new CompDropDown(app, fontSizeProperty);
			lblFontSize = new FormLabel(
					app.getLocalization().getMenu("FontSize") + ":")
							.setFor(fontSizeDropDown);
			lblFontSize.addStyleName("dropDownLabel");
			optionsPanel
					.add(LayoutUtilW.panelRow(lblFontSize, fontSizeDropDown));
		}

		private void addLanguageItem() {
			NamedEnumeratedProperty<?> languageProperty = new LanguageProperty(app,
					app.getLocalization());
			languageProperty.addValueObserver(this);
			//GlobalScope.propertiesRegistry.register(languageProperty);
			languageDropDown = new CompDropDown(app, languageProperty);
			lblLanguage = new FormLabel(
					app.getLocalization().getMenu("Language") + ":")
							.setFor(languageDropDown);
			lblLanguage.addStyleName("dropDownLabel");
			optionsPanel
					.add(LayoutUtilW.panelRow(lblLanguage, languageDropDown));
		}

		private void storeLanguage(String lang) {
			if (app.getLoginOperation() != null) {
				app.getLoginOperation().setUserLanguage(lang);
			}
			app.getLAF().storeLanguage(lang);
		}

		private void addRestoreSettingsBtn() {
			restoreSettingsBtn = new StandardButton(
					app.getLocalization().getMenu("RestoreSettings"));
			restoreSettingsBtn.setStyleName("settingsBtn");
			restoreSettingsBtn.addFastClickHandler(source -> {
				resetDefault();
				fontSizeDropDown.resetFromModel();
				labelingDropDown.resetFromModel();
				roundingDropDown.resetFromModel();
			});
			saveRestoreRow = LayoutUtilW
					.panelRow(saveSettingsBtn, restoreSettingsBtn);
			saveRestoreRow.setVisible(examController.isIdle());
			optionsPanel.add(saveRestoreRow);
		}

		private void addSaveSettingBtn() {
			saveSettingsBtn = new StandardButton(
					app.getLocalization().getMenu("Settings.Save"));
			saveSettingsBtn.setStyleName("settingsBtn");
			saveSettingsBtn.addFastClickHandler(
					source -> GeoGebraPreferencesW.saveXMLPreferences(app));
			optionsPanel.add(saveSettingsBtn);
		}

		/**
		 * update gui
		 */
		public void updateGUI() {
			labelingDropDown.resetFromModel();
			fontSizeDropDown.resetFromModel();
			languageDropDown.resetFromModel();
			lblLanguage.setVisible(examController.isIdle());
			languageDropDown.setVisible(examController.isIdle());
			saveRestoreRow.setVisible(examController.isIdle());
		}

		/**
		 * Reset defaults
		 */
		protected void resetDefault() {
			GeoGebraPreferencesW.clearPreferences(app);

			// reset defaults for GUI, views etc
			// this has to be called before load XML preferences,
			// in order to avoid overwrite
			app.getSettings().resetSettings(app);

			// for geoelement defaults, this will do nothing, so it is
			// OK here
			GeoGebraPreferencesW.resetPreferences(app);

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
			roundingDropDown.setLabels();
			lblLabeling.setText(app.getLocalization().getMenu("Labeling") + ":");
			labelingDropDown.setLabels();
			lblFontSize.setText(app.getLocalization().getMenu("FontSize") + ":");
			fontSizeDropDown.setLabels();
			lblLanguage
					.setText(app.getLocalization().getMenu("Language") + ":");
			languageDropDown.setLabels();
			saveSettingsBtn
					.setText(app.getLocalization().getMenu("Settings.Save"));
			restoreSettingsBtn
					.setText(app.getLocalization().getMenu("RestoreSettings"));
		}

		// PropertyValueObserver

		@Override
		public void onDidSetValue(ValuedProperty property) {
			if (property instanceof LanguageProperty) {
				storeLanguage(((LanguageProperty) property).getValue());
			}
		}
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
