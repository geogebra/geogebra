/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.dialog;

import java.util.Arrays;

import org.geogebra.common.euclidian.smallscreen.AdjustSlider;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonData;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonPanel;
import org.geogebra.web.full.gui.properties.SliderPropertiesPanelW;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

/**
 * Web dialog for slider creation
 */
public class SliderDialogW extends ComponentDialog implements HasKeyboardPopup {
	final static public int NUMERIC = 0;
	final static public int ANGLE = 1;
	final static public int INTEGER = 2;
	private AutoCompleteTextFieldW tfLabel;
	private RadioButtonPanel<Integer> angleRadioButtonPanel;
	private SliderPropertiesPanelW sliderPanel;

	private GeoElement geoResult;
	private GeoNumeric number;
	private GeoAngle angle;

	/**
	 * Creates a dialog to create a new GeoNumeric for a slider.
	 *
	 *  @param app
	 *            application
	 * @param data
	 *            dialog translation keys
	 * @param x
	 *            x-coordinate of slider in screen coords
	 * @param y
	 *            x-coordinate of slider in screen coords
	 */
	public SliderDialogW(final AppW app, DialogData data, int x, int y) {
		super(app, data, false, true);
		this.addStyleName("sliderDialog");
		buildContent();
		setOnPositiveAction(this::createSlider);
		initResultGeo(x, y);

		if (!app.isWhiteboardActive()) {
			app.registerPopup(this);
		}
		this.addCloseHandler(event -> {
			app.unregisterPopup(this);
			app.hideKeyboard();
		});
	}

	private void initResultGeo(int x, int y) {
		Construction cons = app.getKernel().getConstruction();

		number = new GeoNumeric(cons);
		angle = new GeoAngle(cons);

		// allow outside range 0-360
		angle.setAngleStyle(AngleStyle.UNBOUNDED);

		GeoNumeric.setSliderFromDefault(number, false);
		GeoNumeric.setSliderFromDefault(angle, true);
		number.setValue(1);
		angle.setValue(45 * Math.PI / 180);

		number.setSliderLocation(x, y, true);
		number.setAVSliderOrCheckboxVisible(true);
		angle.setSliderLocation(x, y, true);
		angle.setAVSliderOrCheckboxVisible(true);
		geoResult = null;

		Object [] geos = { getSelGeo() };
		sliderPanelUpdate(geos);
		updateLabelField(number, false);
	}

	private GeoElement getSelGeo() {
		return angleRadioButtonPanel.getValue() == ANGLE
				? angle : number;
	}

	private void buildContent() {
		Localization loc = app.getLocalization();
		FlowPanel mainWidget = new FlowPanel();
		FlowPanel contentWidget = new FlowPanel();
		mainWidget.add(contentWidget);

		FlowPanel nameWidget = new FlowPanel();
		contentWidget.add(nameWidget);

		RadioButtonData<Integer> numberData = new RadioButtonData<>("Numeric", NUMERIC);
		RadioButtonData<Integer> angleData = new RadioButtonData<>("Angle", ANGLE);
		RadioButtonData<Integer> integerData = new RadioButtonData<>("Integer", INTEGER);

		angleRadioButtonPanel = new RadioButtonPanel<>(loc,
				Arrays.asList(numberData, angleData, integerData), NUMERIC,
				this::onSliderTypeChange);
		contentWidget.add(angleRadioButtonPanel);

		sliderPanel = new SliderPropertiesPanelW((AppW) app, true, true);
		sliderPanel.getWidget().setStyleName("sliderPanelWidget");

		Label nameLabel = new Label(loc.getMenu("Name"));
		nameLabel.setStyleName("coloredLabel");
		nameWidget.add(nameLabel);
		
		tfLabel = new AutoCompleteTextFieldW(-1, app);
		tfLabel.enableGGBKeyboard();
		nameWidget.add(tfLabel);
		
		contentWidget.add(sliderPanel.getWidget());

		addDialogContent(mainWidget);
	}

	private void onSliderTypeChange(int value) {
		if (value == NUMERIC) {
			GeoNumeric num = app.getKernel().getAlgoDispatcher().getDefaultNumber(false);
			number.setAutoStep(num.isAutoStep());
			number.setAnimationStep(num.getAnimationStep());
			number.setIntervalMin(num.getIntervalMin());
			number.setIntervalMax(num.getIntervalMax());
			updateLabelField(number, false);
			sliderPanelUpdate(new GeoElement[] { number });
		} else if (value == ANGLE) {
			updateLabelField(angle, false);
			sliderPanelUpdate(new GeoElement[] { angle });
		} else {
			number.setAutoStep(false);
			number.setAnimationStep(1);
			number.setIntervalMin(1);
			number.setIntervalMax(30);
			updateLabelField(number, true);
			sliderPanelUpdate(new GeoElement[] { number });
		}
	}

	private void updateLabelField(GeoNumeric geo, boolean isInteger) {
		String def = geo.isAngle() ? " = " + Unicode.FORTY_FIVE_DEGREES_STRING
				: " = 1";
		String label = isInteger ? geo.getLabelManager().getNextIntegerLabel()
				: geo.getDefaultLabel();
		tfLabel.setText(label + def); // =45degrees
	}

	/**
	 * Sets the geoResult name and value: this is temporarily just a default label
	 *
	 * @return GeoElement: the geoResult itself
	 */
	public GeoElement getResult() {
		if (geoResult != null) {
			// set label of geoResult
			String strLabel;
			String text = tfLabel.getText();
			try {
				strLabel = app.getKernel().getAlgebraProcessor()
						.parseLabel(text);
			} catch (Exception e) {
				strLabel = null;
			}
			geoResult.setLabel(strLabel);

			// allow eg a=2 in the Name dialog to set the initial value
			if (strLabel != null && text.indexOf('=') > -1
					&& text.indexOf('=') == text.lastIndexOf('=')) {
				try {
					double val;
					if (text.indexOf(Unicode.DEGREE_CHAR) > text.indexOf('=')) {
						val = Double.parseDouble(text.substring(
								text.indexOf('=') + 1,
								text.indexOf(Unicode.DEGREE_CHAR)));
						val *= Math.PI / 180;
					} else {
						val = Double.parseDouble(text.substring(text
								.indexOf('=') + 1));
					}

					GeoNumeric geoNum = (GeoNumeric) geoResult;
					
					if (val > geoNum.getIntervalMax()) {
						geoNum.setIntervalMax(val);
					} else if (val < geoNum.getIntervalMin()) {
						geoNum.setIntervalMin(val);
					}
					
					geoNum.setValue(val);
				} catch (Exception e) {
					Log.debug(e);
				}
			}
		}
		return geoResult;
	}

	private void createSlider() {
		geoResult = getSelGeo();
		getResult();
		geoResult.setLabelMode(GeoElementND.LABEL_NAME_VALUE);
		geoResult.setLabelVisible(true);
		sliderPanel.applyAll(geoResult);
		geoResult.update();
		if (angleRadioButtonPanel.getValue() != ANGLE) {
			AdjustSlider.ensureOnScreen((GeoNumeric) geoResult,
					app.getActiveEuclidianView());
		}

		app.getActiveEuclidianView().requestFocusInWindow();

		app.storeUndoInfo();
		app.getKernel().notifyRepaint();
	}

	private void sliderPanelUpdate(Object[] geos) {
		sliderPanel.updatePanel(geos);
	}
}