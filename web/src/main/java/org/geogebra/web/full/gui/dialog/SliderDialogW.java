/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonData;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonPanel;
import org.geogebra.web.full.gui.properties.SliderPanelW;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Web dialog for slider creation
 */
public class SliderDialogW extends ComponentDialog implements HasKeyboardPopup {
	private AutoCompleteTextFieldW tfLabel;
	private RadioButtonPanel angleRadioButtonPanel;
	private SliderPanelW sliderPanel;

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
		angle.setSliderLocation(x, y, true);

		geoResult = null;

		Object [] geos = { getSelGeo() };
		sliderPanelUpdate(geos);
		updateLabelField(number, false);
	}

	private GeoElement getSelGeo() {
		return angleRadioButtonPanel.isNthRadioButtonSelected(1)
				? angle : number;
	}

	private void buildContent() {
		Localization loc = app.getLocalization();
		VerticalPanel mainWidget = new VerticalPanel();
		VerticalPanel contentWidget = new VerticalPanel();
		mainWidget.add(contentWidget);

		VerticalPanel nameWidget = new VerticalPanel();
		contentWidget.add(nameWidget);

		RadioButtonData numberData = new RadioButtonData("Numeric", true,
				() -> {
					GeoNumeric num = app.getKernel().getAlgoDispatcher().getDefaultNumber(false);
					number.setAutoStep(num.isAutoStep());
					number.setAnimationStep(num.getAnimationStep());
					number.setIntervalMin(num.getIntervalMin());
					number.setIntervalMax(num.getIntervalMax());
					updateLabelField(number, false);
					sliderPanelUpdate(new GeoElement[] { number });
				});
		RadioButtonData angleData = new RadioButtonData("Angle", false,
				() -> {
					updateLabelField(angle, false);
					sliderPanelUpdate(new GeoElement[] { angle });
				});
		RadioButtonData integerData = new RadioButtonData("Integer", false,
				() -> {
					number.setAutoStep(false);
					number.setAnimationStep(1);
					number.setIntervalMin(1);
					number.setIntervalMax(30);
					updateLabelField(number, true);
					sliderPanelUpdate(new GeoElement[] { number });
				});
		angleRadioButtonPanel = new RadioButtonPanel(loc,
				Arrays.asList(numberData, angleData, integerData));
		contentWidget.add(angleRadioButtonPanel);

		sliderPanel = new SliderPanelW((AppW) app, true, true);
		sliderPanel.getWidget().setStyleName("sliderPanelWidget");

		Label nameLabel = new Label(loc.getMenu("Name"));
		nameLabel.setStyleName("coloredLabel");
		nameWidget.add(nameLabel);
		
		tfLabel = new AutoCompleteTextFieldW(-1, app);
		tfLabel.enableGGBKeyboard();
		tfLabel.requestToShowSymbolButton();
		nameWidget.add(tfLabel);
		
		contentWidget.add(sliderPanel.getWidget());

		addDialogContent(mainWidget);
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
					double val = 0;
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
		if (!angleRadioButtonPanel.isNthRadioButtonSelected(1)) {
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