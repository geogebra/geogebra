/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.properties.SliderPanelW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Web dialog for slider creation
 */
public class SliderDialogW extends DialogBoxW
implements ClickHandler, ChangeHandler, ValueChangeHandler<Boolean>
{
	private Button btOK, btCancel;
	private Label nameLabel;
	private AutoCompleteTextFieldW tfLabel;
	private RadioButton rbNumber, rbAngle, rbInteger;
	private SliderPanelW sliderPanel;
	
	private VerticalPanel mainWidget;
	private VerticalPanel contentWidget;
	private FlowPanel bottomWidget;
	private HorizontalPanel radioButtonWidget;
	private VerticalPanel nameWidget;
	
	private AppW app;
	//private SliderPanel sliderPanel;
	
	private GeoElement geoResult;
	private GeoNumeric number;
	private GeoAngle angle;
			
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
	public SliderDialogW(AppW app, int x, int y) {
		super(false, true, null, app.getPanel());
		//super(app.getFrame(), false);
		this.app = app;
		this.addStyleName("sliderDialog");
		this.addStyleName("GeoGebraFrame");
		//addWindowListener(this);
		
		// create temp geos that may be returned as result
		Construction cons = app.getKernel().getConstruction();
		
		
		number = new GeoNumeric(cons);
		angle = new GeoAngle(cons);
		
		// allow outside range 0-360
		angle.setAngleStyle(AngleStyle.UNBOUNDED);
		
		GeoNumeric.setSliderFromDefault(number,false);
		GeoNumeric.setSliderFromDefault(angle,true);
		number.setValue(1);
		angle.setValue(45 * Math.PI/180);
			
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
	}

	private void createGUI() {
		//setTitle(app.getPlain("Slider"));
		//setResizable(false);
		this.getCaption().setText(app.getMenu("Slider"));

		//Create components to be displayed
		//mainWidget.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);		
		mainWidget.add(contentWidget = new VerticalPanel());
		mainWidget.add(bottomWidget = new FlowPanel());
		bottomWidget.setStyleName("DialogButtonPanel");

		contentWidget.add(nameWidget = new VerticalPanel());
		contentWidget.add(radioButtonWidget = new HorizontalPanel());
		radioButtonWidget.setStyleName("DialogRbPanel");

		// radio buttons for number or angle
		String id = DOM.createUniqueId();
		rbNumber = new RadioButton(id, app.getPlain("Numeric"));
		rbNumber.addValueChangeHandler(this);
		rbNumber.setValue(true);
		rbAngle = new RadioButton(id, app.getPlain("Angle"));
		rbAngle.addValueChangeHandler(this);
		rbInteger = new RadioButton(id, app.getPlain("Integer"));
		rbInteger.addValueChangeHandler(this);

		radioButtonWidget.add(rbNumber);
		radioButtonWidget.add(rbAngle);
		radioButtonWidget.add(rbInteger);			

		sliderPanel = new SliderPanelW(app, true, true);
		sliderPanel.getWidget().setStyleName("sliderPanelWidget");
		//nameWidget.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		
//		FlowPanel namePanel = new FlowPanel();
		nameLabel = new Label(app.getPlain("Name"));
		nameWidget.add(nameLabel);
		
		tfLabel = new AutoCompleteTextFieldW(-1, app);
		updateLabelField(number, false);
		tfLabel.addFocusListener(new FocusListenerW(tfLabel));
		tfLabel.requestToShowSymbolButton();
		nameWidget.add(tfLabel);
		
		contentWidget.add(sliderPanel.getWidget());


		// buttons
		btOK = new Button(app.getPlain("OK"));
		btOK.addClickHandler(this);
		// btApply.getElement().getStyle().setMargin(3, Style.Unit.PX);

		btCancel = new Button(app.getPlain("Cancel"));
		btCancel.addStyleName("cancelBtn");
		btCancel.addClickHandler(this);
		// btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);

		bottomWidget.add(btOK);
		bottomWidget.add(btCancel);
	}

	private void updateLabelField(GeoElement geo, boolean isInteger) {
		String def = geo.isAngle() ? " = " + Unicode.FORTY_FIVE_DEGREES
				: " = 1";
		tfLabel.setText(geo.getDefaultLabel(isInteger) + def); // =45degrees
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
				strLabel = app.getKernel().getAlgebraProcessor().
								parseLabel(text);
			} catch (Exception e) {
				strLabel = null;
			}			
			geoResult.setLabel(strLabel);
			
			// allow eg a=2 in the Name dialog to set the initial value
			if (strLabel != null && text.indexOf('=') > -1 && text.indexOf('=') == text.lastIndexOf('=')) {
				
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

					GeoNumeric geoNum = ((GeoNumeric)geoResult);
					
					if (val > geoNum.getIntervalMax()) geoNum.setIntervalMax(val);
					else if (val < geoNum.getIntervalMin()) geoNum.setIntervalMin(val);
					
					geoNum.setValue(val);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return geoResult;
	}


	public void onClick(ClickEvent e) {
		Element target = e.getNativeEvent().getEventTarget().cast();
		if (target == btOK.getElement()) {
			geoResult = rbAngle.getValue() ? angle : number; 		
			getResult();
			geoResult.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			geoResult.setLabelVisible(true);
			sliderPanel.applyAll(geoResult);
			geoResult.update();
			//((GeoNumeric)geoResult).setRandom(cbRandom.isSelected());

			hide();
			app.getActiveEuclidianView().requestFocusInWindow();

			app.storeUndoInfo();
			app.getKernel().notifyRepaint();
		} else if (target == btCancel.getElement()) {
			hide();
			app.getActiveEuclidianView().requestFocusInWindow();
		}
	}

	public void onValueChange(ValueChangeEvent<Boolean> vc) {
		GeoElement selGeo = rbAngle.getValue() ? angle : number;			
		if (vc.getSource() == rbInteger) {
			number.setAutoStep(false);
			number.setAnimationStep(1);
			number.setIntervalMin(1);
			number.setIntervalMax(30);
			updateLabelField(number, true);
		} else if (vc.getSource() == rbNumber) {
			GeoNumeric num = app.getKernel().getAlgoDispatcher().getDefaultNumber(false);
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


	public void onChange(ChangeEvent event) {
	    // TODO Auto-generated method stub
	    
    }
	
}
