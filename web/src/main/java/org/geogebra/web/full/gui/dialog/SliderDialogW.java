/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.smallscreen.AdjustSlider;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.properties.SliderPanelW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Web dialog for slider creation
 */
public class SliderDialogW extends DialogBoxW implements ClickHandler,
		ValueChangeHandler<Boolean>, HasKeyboardPopup {
	private Button btOK;
	private Button btCancel;
	private AutoCompleteTextFieldW tfLabel;
	private RadioButton rbNumber;
	private RadioButton rbAngle;
	private RadioButton rbInteger;
	private SliderPanelW sliderPanel;
	
	private VerticalPanel mainWidget;
	private VerticalPanel contentWidget;
	private FlowPanel bottomWidget;
	private HorizontalPanel radioButtonWidget;
	private VerticalPanel nameWidget;
	
	private AppW appw;
	
	private GeoElement geoResult;
	private GeoNumeric number;
	private GeoAngle angle;
	private Localization loc;
			
	/**
	 * Creates a dialog to create a new GeoNumeric for a slider.
	 * 
	 * @param x
	 *            x-coordinate of slider in screen coords
	 * @param y
	 *            x-coordinate of slider in screen coords
	 * @param app
	 *            application
	 */
	public SliderDialogW(final AppW app, int x, int y) {
		super(false, true, null, app.getPanel(), app);

		this.appw = app;
		this.loc = app.getLocalization();
		this.addStyleName("sliderDialog");
		this.addStyleName("GeoGebraFrame");

		// create temp geos that may be returned as result
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

		setWidget(mainWidget = new VerticalPanel());
		addStyleName("GeoGebraPopup");
		createGUI();
		
		this.setGlassEnabled(true);
		this.setVisible(true);

		GeoElement selGeo = rbAngle.getValue() ? angle : number;
		Object [] geos = { selGeo };
		sliderPanelUpdate(geos);
		if (!app.isWhiteboardActive()) {
			app.registerPopup(this);
		}
		this.addCloseHandler(new CloseHandler<GPopupPanel>() {
			@Override
			public void onClose(CloseEvent<GPopupPanel> event) {
				app.unregisterPopup(SliderDialogW.this);
				app.hideKeyboard();
			}
		});
	}

	private void createGUI() {
		getCaption().setText(loc.getMenu("Slider"));

		// Create components to be displayed
		mainWidget.add(contentWidget = new VerticalPanel());
		mainWidget.add(bottomWidget = new FlowPanel());
		bottomWidget.setStyleName("DialogButtonPanel");

		contentWidget.add(nameWidget = new VerticalPanel());
		contentWidget.add(radioButtonWidget = new HorizontalPanel());
		radioButtonWidget.setStyleName("DialogRbPanel");

		// radio buttons for number or angle
		String id = DOM.createUniqueId();
		rbNumber = new RadioButton(id, loc.getMenu("Numeric"));
		rbNumber.addValueChangeHandler(this);
		rbNumber.setValue(true);
		rbAngle = new RadioButton(id, loc.getMenu("Angle"));
		rbAngle.addValueChangeHandler(this);
		rbInteger = new RadioButton(id, loc.getMenu("Integer"));
		rbInteger.addValueChangeHandler(this);

		radioButtonWidget.add(rbNumber);
		radioButtonWidget.add(rbAngle);
		radioButtonWidget.add(rbInteger);			

		sliderPanel = new SliderPanelW(appw, true, true);
		sliderPanel.getWidget().setStyleName("sliderPanelWidget");

		Label nameLabel = new Label(loc.getMenu("Name"));
		if (appw.isUnbundledOrWhiteboard()) {
			nameLabel.setStyleName("coloredLabel");
		}
		nameWidget.add(nameLabel);
		
		tfLabel = new AutoCompleteTextFieldW(-1, appw);
		tfLabel.enableGGBKeyboard();
		updateLabelField(number, false);
		tfLabel.requestToShowSymbolButton();
		nameWidget.add(tfLabel);
		
		contentWidget.add(sliderPanel.getWidget());

		// buttons
		btOK = new Button(loc.getMenu("OK"));
		btOK.addClickHandler(this);

		btCancel = new Button(loc.getMenu("Cancel"));
		btCancel.addStyleName("cancelBtn");
		btCancel.addClickHandler(this);

		bottomWidget.add(btOK);
		bottomWidget.add(btCancel);
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
				strLabel = appw.getKernel().getAlgebraProcessor()
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
					e.printStackTrace();
				}
			}
		}
		return geoResult;
	}

	@Override
	public void onClick(ClickEvent e) {
		Element target = e.getNativeEvent().getEventTarget().cast();
		if (target == btOK.getElement()) {
			geoResult = rbAngle.getValue() ? angle : number;
			getResult();
			geoResult.setLabelMode(GeoElementND.LABEL_NAME_VALUE);
			geoResult.setLabelVisible(true);
			sliderPanel.applyAll(geoResult);
			geoResult.update();
			if (!rbAngle.getValue()) {
				AdjustSlider.ensureOnScreen((GeoNumeric) geoResult,
						appw.getActiveEuclidianView());
			}

			hide();
			appw.getActiveEuclidianView().requestFocusInWindow();

			appw.storeUndoInfo();
			appw.getKernel().notifyRepaint();
		} else if (target == btCancel.getElement()) {
			hide();
			appw.getActiveEuclidianView().requestFocusInWindow();
		}
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> vc) {
		GeoElement selGeo = rbAngle.getValue() ? angle : number;			
		if (vc.getSource() == rbInteger) {
			number.setAutoStep(false);
			number.setAnimationStep(1);
			number.setIntervalMin(1);
			number.setIntervalMax(30);
			updateLabelField(number, true);
		} else if (vc.getSource() == rbNumber) {
			GeoNumeric num = appw.getKernel().getAlgoDispatcher().getDefaultNumber(false);
			number.setAutoStep(num.isAutoStep());
			number.setAnimationStep(num.getAnimationStep());
			number.setIntervalMin(num.getIntervalMin());
			number.setIntervalMax(num.getIntervalMax());
			updateLabelField(number, false);
		} else {
			updateLabelField(angle, false);
		}
		GeoElement [] geos = { selGeo };

		sliderPanelUpdate(geos);
	}

	private void sliderPanelUpdate(Object[] geos) {
		sliderPanel.updatePanel(geos);
	}
	
}
