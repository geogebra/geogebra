package org.geogebra.web.web.gui.dialog.options;

import org.geogebra.common.gui.SetLabels;
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
		 * constructor
		 */
		protected GlobalTab() {
			createGUI();
			updateGUI();
			if (app.isUnbundled() || app.isWhiteboardActive()) {
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
			addRoundingItem();
			addLabelingItem();
			addFontItem();
		}

		private void addRoundingItem() {
			lblRounding = new Label(
					app.getLocalization().getMenu("Rounding") + ":");
			roundingList = new ListBox();
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
			lblLabeling = new Label(
					app.getLocalization().getMenu("Labeling") + ":");
			labelingList = new ListBox();
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
			lblFontSize = new Label(
					app.getLocalization().getMenu("FontSize") + ":");
			fontSizeList = new ListBox();
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
		}

		private void updateFontSizeList() {
			fontSizeList.clear();
			for (int i = 0; i < org.geogebra.common.util.Util
					.menuFontSizesLength(); i++) {
				fontSizeList.addItem(
						org.geogebra.common.util.Util.menuFontSizes(i) + " pt");
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
			saveSettingsBtn
					.setText(app.getLocalization().getMenu("Settings.Save"));
			restoreSettingsBtn
					.setText(app.getLocalization().getMenu("RestoreSettings"));
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

	public void updateGUI() {
		globalTab.updateGUI();
	}

	public Widget getWrappedPanel() {
		return tabPanel;
	}

	public void onResize(int height, int width) {
		globalTab.onResize(height, width);
	}

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
