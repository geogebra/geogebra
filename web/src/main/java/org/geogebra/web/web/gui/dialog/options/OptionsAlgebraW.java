package org.geogebra.web.web.gui.dialog.options;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.OptionsAdvanced;
import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class OptionsAlgebraW extends OptionsAdvanced
 implements OptionPanelW,
		SetLabels, ClickHandler, ChangeHandler, SettingListener {
	private AppW app;
	private FlowPanel optionsPanel;
	private Label lblShow;
	private CheckBox showAuxiliaryObjects;
	private ListBox sortMode;
	private AlgebraStyleListBox description;
	private Label lblCoordStyle;
	private ListBox coordStyle;
	private Label lblAngleUnit;
	private ListBox angleUnit;
	private Label lblSortMode;
	private Label lblDescriptionMode;
	private List<SortMode> supportedModes = Arrays.asList(SortMode.DEPENDENCY,
			SortMode.TYPE, SortMode.ORDER, SortMode.LAYER);

	public OptionsAlgebraW(AppW app) {
		this.app = app;
		createGUI();
		app.getSettings().getAlgebra().addListener(this);
	}

	private void createGUI() {
		optionsPanel = new FlowPanel();
		optionsPanel.setStyleName("algebraOptions");
		lblShow = new Label();
		lblShow.addStyleName("panelTitle");
		showAuxiliaryObjects = new CheckBox();
		showAuxiliaryObjects.addClickHandler(this);
		lblSortMode = new Label();
		lblSortMode.addStyleName("panelTitle");
		lblDescriptionMode = new Label();
		lblDescriptionMode.addStyleName("panelTitle");
		sortMode = new ListBox();
		description = new AlgebraStyleListBox(app);
		if (app.has(Feature.ADVANCED_OPTIONS)) {
			lblCoordStyle = new Label(
					app.getLocalization().getMenu("Coordinates") + ":");
			coordStyle = new ListBox();
			lblAngleUnit = new Label(
					app.getLocalization().getMenu("AngleUnit") + ":");
			angleUnit = new ListBox();
		}

		optionsPanel.add(lblShow);
		optionsPanel.add(LayoutUtilW.panelRowIndent(showAuxiliaryObjects));
		optionsPanel.add(lblSortMode);
		optionsPanel.add(LayoutUtilW.panelRowIndent(sortMode));
		optionsPanel.add(lblDescriptionMode);
		optionsPanel.add(LayoutUtilW.panelRowIndent(description));
		if (app.has(Feature.ADVANCED_OPTIONS)) {
			optionsPanel
					.add(LayoutUtilW.panelRowIndent(lblCoordStyle, coordStyle));
			coordStyle.addChangeHandler(this);
			optionsPanel
					.add(LayoutUtilW.panelRowIndent(lblAngleUnit, angleUnit));
			angleUnit.addChangeHandler(this);
		}
		sortMode.addChangeHandler(this);
		description.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int idx = description.getSelectedIndex();
				if (app.has(Feature.AV_DEFINITION_AND_VALUE)) {
					app.getKernel()
.setAlgebraStyle(
							AlgebraSettings.getStyleModeAt(idx));
				} else {
					app.getKernel().setAlgebraStyle(idx);

				}
				app.getKernel().updateConstruction();
			}
		});
		// updateSortMode(); done by setLabels
		// updateDescription(); done by set labels
		setLabels();
	}

	private void updateSortMode() {
		sortMode.clear();
		for (SortMode mode : supportedModes) {
			sortMode.addItem(app.getLocalization().getMenu(mode.toString()));
		}

		SortMode selectedMode = app.getAlgebraView().getTreeMode();
		sortMode.setSelectedIndex(supportedModes.indexOf(selectedMode));
	}

	private void updateCoordStyle() {
		if (app.has(Feature.ADVANCED_OPTIONS)) {
			lblCoordStyle.setText(
					app.getLocalization().getMenu("Coordinates") + ":");
			coordStyle.clear();
			coordStyle.addItem(app.getLocalization().getMenu("A = (x, y)"));
			coordStyle.addItem(app.getLocalization().getMenu("A(x | y)"));
			coordStyle.addItem(app.getLocalization().getMenu("A: (x, y)"));
			coordStyle.setSelectedIndex(app.getKernel().getCoordStyle());
			app.getKernel().updateConstruction();
		}
	}

	private void updateAngleUnit() {
		if (app.has(Feature.ADVANCED_OPTIONS)) {
			lblAngleUnit
					.setText(app.getLocalization().getMenu("AngleUnit") + ":");
			angleUnit.clear();
			angleUnit.addItem(app.getLocalization().getMenu("Degree"));
			angleUnit.addItem(app.getLocalization().getMenu("Radiant"));
			angleUnit.setSelectedIndex(
					app.getKernel().getAngleUnit() == Kernel.ANGLE_RADIANT ? 1
							: 0);
			app.getKernel().updateConstruction();
			app.setUnsaved();
		}
	}

	@Override
	public void updateGUI() {
		showAuxiliaryObjects.setValue(app.showAuxiliaryObjects);
		updateSortMode();
		description.update();
		if (app.has(Feature.ADVANCED_OPTIONS)) {
			updateCoordStyle();
			updateAngleUnit();
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
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source == showAuxiliaryObjects) {
			app.setShowAuxiliaryObjects(showAuxiliaryObjects.getValue());
		}
	}

	@Override
	public void setLabels() {
		lblShow.setText(app.getLocalization().getPlain("Show"));
		showAuxiliaryObjects
				.setText(app.getLocalization().getPlain("AuxiliaryObjects"));

		lblSortMode.setText(app.getLocalization().getPlain("SortBy"));
		lblDescriptionMode.setText(app.getLocalization().getMenu(
				"AlgebraDescriptions"));
		updateSortMode();
		description.update();
		if (app.has(Feature.ADVANCED_OPTIONS)) {
			updateCoordStyle();
			updateAngleUnit();
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		Object source = event.getSource();
		if (source == sortMode) {
			int i = sortMode.getSelectedIndex();
			app.getSettings().getAlgebra()
.setTreeMode(supportedModes.get(i));

		} else if (app.has(Feature.ADVANCED_OPTIONS)) {
			if (source == coordStyle) {
				int i = coordStyle.getSelectedIndex();
				app.getKernel().setCoordStyle(i);
				app.getKernel().updateConstruction();
			} else if (source == angleUnit) {
				int i = angleUnit.getSelectedIndex();
				app.getKernel().setAngleUnit(
						i == 0 ? Kernel.ANGLE_DEGREE : Kernel.ANGLE_RADIANT);
				app.getKernel().updateConstruction();
				app.setUnsaved();
			}
		}
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		updateGUI();

	}

	@Override
	public MultiRowsTabPanel getTabPanel() {
		return null;
	}
}
