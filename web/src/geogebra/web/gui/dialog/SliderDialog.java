/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.web.gui.dialog;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.web.gui.AngleTextFieldW;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


public class SliderDialog extends PopupPanel
implements ClickHandler, ChangeHandler, ValueChangeHandler<Boolean>
{
	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	private Button btApply, btCancel;
	private RadioButton rbNumber, rbAngle, rbInteger;
	private AngleTextFieldW min, max, inc;
	private InlineLabel minLabel, maxLabel, incLabel;
	private HorizontalPanel minPanel, maxPanel, incPanel;

	private VerticalPanel mainWidget;
	private HorizontalPanel topWidget;
	private HorizontalPanel bottomWidget;
	private VerticalPanel leftWidget, rightWidget;
	//private InputPanel tfLabel;
	//private JPanel optionPane;
	//private JCheckBox cbRandom;
	
	private AppW app;
	//private SliderPanel sliderPanel;
	
	private GeoElement geoResult;
	private GeoNumeric number;
	private GeoAngle angle;
			
	/**
	 * Creates a dialog to create a new GeoNumeric for a slider.
	 * @param x x-coordinate of slider in screen coords
	 * @param y x-coordinate of slider in screen coords
	 * @param app
	 */
	public SliderDialog(AppW app, int x, int y) {
		super(false, true);
		//super(app.getFrame(), false);
		this.app = app;		
		//addWindowListener(this);
		
		// create temp geos that may be returned as result
		Construction cons = app.getKernel().getConstruction();
		
		
		number = new GeoNumeric(cons);
		angle = new GeoAngle(cons);
		DialogManagerW.setSliderFromDefault(number,false);
		DialogManagerW.setSliderFromDefault(angle,true);
		number.setValue(1);
		angle.setValue(45 * Math.PI/180);
			
		number.setSliderLocation(x, y, true);
		angle.setSliderLocation(x, y, true);
		
		
				
		geoResult = null;

		setWidget(mainWidget = new VerticalPanel());
		createGUI();

		GeoElement selGeo = rbAngle.getValue() ? angle : number;
		Object [] geos = { selGeo };
		sliderPanelUpdate(geos);
		animationStepUpdate(geos);
	}

	private void createGUI() {
		//setTitle(app.getPlain("Slider"));
		//setResizable(false);

		//Create components to be displayed
		mainWidget.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);		
		mainWidget.add(topWidget = new HorizontalPanel());
		mainWidget.add(bottomWidget = new HorizontalPanel());

		topWidget.add(leftWidget = new VerticalPanel());
		topWidget.add(rightWidget = new VerticalPanel());

		// radio buttons for number or angle
		String id = DOM.createUniqueId();
		rbNumber = new RadioButton(id, app.getPlain("Numeric"));
		rbNumber.addValueChangeHandler(this);
		rbNumber.setChecked(true);
		rbAngle = new RadioButton(id, app.getPlain("Angle"));
		rbAngle.addValueChangeHandler(this);
		rbInteger = new RadioButton(id, app.getPlain("Integer"));
		rbInteger.addValueChangeHandler(this);

		leftWidget.add(rbNumber);
		leftWidget.add(rbAngle);
		leftWidget.add(rbInteger);			

		rightWidget.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		rightWidget.add(minPanel = new HorizontalPanel());
		rightWidget.add(maxPanel = new HorizontalPanel());
		rightWidget.add(incPanel = new HorizontalPanel());

		minPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		minPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		minPanel.add(minLabel = new InlineLabel(app.getPlain("min")+": "));
		minPanel.add(min = new AngleTextFieldW(6, app));
		minPanel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		min.addChangeHandler(this);

		maxPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		maxPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		maxPanel.add(maxLabel = new InlineLabel(app.getPlain("max")+": "));
		maxPanel.add(max = new AngleTextFieldW(6, app));
		maxPanel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		max.addChangeHandler(this);

		incPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		incPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		incPanel.add(incLabel = new InlineLabel(app.getPlain("AnimationStep") + ": "));
		incPanel.add(inc = new AngleTextFieldW(6, app));
		incPanel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		inc.addChangeHandler(this);

		// buttons
		btApply = new Button(app.getPlain("Apply"));
		btApply.addClickHandler(this);
		btApply.getElement().getStyle().setMargin(3, Style.Unit.PX);
		//btApply.setActionCommand("Apply");
		//btApply.addActionListener(this);
		btCancel = new Button(app.getPlain("Cancel"));
		btCancel.addClickHandler(this);
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		//btCancel.setActionCommand("Cancel");
		//btCancel.addActionListener(this);

		bottomWidget.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		bottomWidget.add(btApply);
		bottomWidget.add(btCancel);

		//JPanel btPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		//btPanel.add(btApply);
		//btPanel.add(btCancel);
	}

	/**
	 * Sets the geoResult name and value: this is temporarily just a default label
	 *
	 * @return GeoElement: the geoResult itself
	 */
	public GeoElement getResult() {
		if (geoResult != null) {
			geoResult.setLabel(geoResult.getDefaultLabel(false));
		}
		return geoResult;
	}

	public void onClick(ClickEvent e) {
		Element target = e.getNativeEvent().getEventTarget().cast();
		if (target == btApply.getElement()) {
			geoResult = rbAngle.getValue() ? angle : number; 		
			getResult();
			geoResult.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			geoResult.setLabelVisible(true);
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
			number.setAnimationStep(1);
			number.setIntervalMin(1);
			number.setIntervalMax(30);
		} else if (vc.getSource() == rbNumber) {
			GeoNumeric num = app.getKernel().getAlgoDispatcher().getDefaultNumber(false);
			number.setAnimationStep(num.getAnimationStep());
			number.setIntervalMin(num.getIntervalMin());
			number.setIntervalMax(num.getIntervalMax());
		}
		GeoElement [] geos = { selGeo };

		sliderPanelUpdate(geos);
		animationStepUpdate(geos);
	}

	public final static int TEXT_FIELD_FRACTION_DIGITS = 8;

	public void sliderPanelUpdate(Object[] geos) {

		if (!sliderPanelCheckGeos(geos))
			return;

		// check if properties have same values
		GeoNumeric temp, num0 = (GeoNumeric) geos[0];
		boolean equalMax = true;
		boolean equalMin = true;
		boolean equalWidth = true;
		boolean equalSliderFixed = true;
		boolean random = true;
		boolean equalSliderHorizontal = true;
		boolean onlyAngles = true;

		for (int i = 0; i < geos.length; i++) {
			temp = (GeoNumeric) geos[i];

			// we don't check isIntervalMinActive, because we want to display the interval even if it's empty
			if (num0.getIntervalMinObject() == null || temp.getIntervalMinObject() == null || !Kernel.isEqual(num0.getIntervalMin(), temp.getIntervalMin()))
				equalMin = false;
			if (num0.getIntervalMaxObject() == null || temp.getIntervalMaxObject() == null || !Kernel.isEqual(num0.getIntervalMax(), temp.getIntervalMax()))
				equalMax = false;
			if (!Kernel.isEqual(num0.getSliderWidth(), temp.getSliderWidth()))
				equalWidth = false;
			if (num0.isSliderFixed() != temp.isSliderFixed())
				equalSliderFixed = false;
			if (num0.isRandom() != temp.isRandom())
				random = false;
			if (num0.isSliderHorizontal() != temp.isSliderHorizontal())
				equalSliderHorizontal = false;

			if (!(temp instanceof GeoAngle))
				onlyAngles = false;
		}

		StringTemplate highPrecision = StringTemplate.printDecimals(StringType.GEOGEBRA, TEXT_FIELD_FRACTION_DIGITS,false);
		if (equalMin){
			GeoElement min0 = num0.getIntervalMinObject();
			if (onlyAngles && (min0 == null ||(!min0.isLabelSet() && min0.isIndependent()))){				
				min.setText(app.getKernel().formatAngle(num0.getIntervalMin(),highPrecision).toString());			
			}else
				min.setText(min0.getLabel(highPrecision));
		} else {
			min.setText("");
		}
		
		if (equalMax){
			GeoElement max0 = num0.getIntervalMaxObject();
			if (onlyAngles &&  (max0 == null ||(!max0.isLabelSet() && max0.isIndependent()) ))
				max.setText(app.getKernel().formatAngle(num0.getIntervalMax(),highPrecision).toString());
			else
				max.setText(max0.getLabel(highPrecision));
		} else {
			max.setText("");
		}
	}

	private static boolean sliderPanelCheckGeos(Object[] geos) {
		boolean geosOK = true;
		for (int i = 0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
 			if (!(geo.isIndependent() && geo.isGeoNumeric())) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	private boolean animationStepPanelCheckGeos(Object[] geos) {
		boolean geosOK = true;
		for (int i = 0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (!geo.isChangeable() 
					|| geo.isGeoText() 
					|| geo.isGeoImage()
					|| geo.isGeoList()
					|| geo.isGeoBoolean()
					|| geo.isGeoButton()
					|| !true && geo.isGeoNumeric() && geo.isIndependent() // slider						
			)  
			{				
				geosOK = false;
				break;
			}
		}
		
		
		return geosOK;
	}

	public void onChange(ChangeEvent ce) {
		if (ce.getSource() == inc)
			doAnimationStepActionPerformed();
		if (ce.getSource() instanceof TextBox)
			doTextFieldActionPerformed((TextBox)ce.getSource());
	}

	private void doTextFieldActionPerformed(TextBox source) {

		GeoElement selGeo = rbAngle.getValue() ? angle : number;
		Object [] geos = { selGeo };

		//actionPerforming = true;
		String inputText = source.getText().trim();
		boolean emptyString = inputText.equals("");
		NumberValue value = new MyDouble(app.getKernel(),Double.NaN);
		if (!emptyString) {
			value = app.getKernel().getAlgebraProcessor().evaluateToNumeric(inputText,false);					
		}			
		
		if (source == min || source == max) {
			for (int i = 0; i < geos.length; i++) {
				GeoNumeric num = (GeoNumeric) geos[i];
				boolean dependsOnListener = false;
				GeoElement geoValue = (GeoElement)value.toGeoElement();
				if(num.getMinMaxListeners()!=null)
					for(GeoNumeric listener : num.getMinMaxListeners()){
						if(geoValue.isChildOrEqual(listener)) 
							dependsOnListener = true;
					}
				if(dependsOnListener || geoValue.isChildOrEqual(num)){
					app.showErrorDialog(app.getLocalization().getError("CircularDefinition"));
				}
				else{ 
					if(source == min)
						num.setIntervalMin(value);
					else
						num.setIntervalMax(value);
				}
				num.updateRepaint();
			
			}
		}

		sliderPanelUpdate(geos);
		//actionPerforming = false;
	}

	private void doAnimationStepActionPerformed() {

		GeoElement selGeo = rbAngle.getValue() ? angle : number;
		Object [] geos = { selGeo };

		NumberValue newVal =
			app.getKernel().getAlgebraProcessor().evaluateToNumeric(
				inc.getText(),true);
		if (newVal != null && !Double.isNaN(newVal.getDouble())) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				geo.setAnimationStep(newVal);
				geo.updateRepaint();
			}
		}
		animationStepUpdate(geos);
	}

	public void animationStepUpdate(Object[] geos) {

		if (!animationStepPanelCheckGeos(geos))
			return;

		//inc.removeChangeHandler(this);

		// check if properties have same values
		GeoElement temp, geo0 = (GeoElement) geos[0];
		boolean equalStep = true;
		boolean onlyAngles = true;

		for (int i = 0; i < geos.length; i++) {
			temp = (GeoElement) geos[i];
			// same object visible value
			if (!Kernel.isEqual(geo0.getAnimationStep(), temp.getAnimationStep()))
				equalStep = false;
			if (!(temp.isGeoAngle()))
				onlyAngles = false;
		}

		// set trace visible checkbox
		//int oldDigits = kernel.getMaximumFractionDigits();
		//kernel.setMaximumFractionDigits(PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS);
		StringTemplate highPrecision = StringTemplate.printDecimals(StringType.GEOGEBRA, TEXT_FIELD_FRACTION_DIGITS,false);

        if (equalStep){
        	GeoElement stepGeo = geo0.getAnimationStepObject();
			if (onlyAngles && (stepGeo == null ||(!stepGeo.isLabelSet() && stepGeo.isIndependent())))
				inc.setText(
					app.getKernel().formatAngle(geo0.getAnimationStep(),highPrecision).toString());
			else
				inc.setText(stepGeo.getLabel(highPrecision));
        }
		else
			inc.setText("");

		//tfAnimStep.addActionListener(this);
	}

/*
	private void setLabelFieldFocus() {	
		tfLabel.getTextComponent().requestFocus();
		tfLabel.selectText();	
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_ENTER:		
				btApply.doClick();
				break;
				
			case KeyEvent.VK_ESCAPE:
				btCancel.doClick();
				e.consume();
				break;				
		}					
	}

	public void keyReleased(KeyEvent arg0) {		
	}

	public void keyTyped(KeyEvent arg0) {		
	}

	public void windowActivated(WindowEvent arg0) {		
	}

	public void windowClosed(WindowEvent arg0) {		
	}

	public void windowClosing(WindowEvent arg0) {		
	}

	public void windowDeactivated(WindowEvent arg0) {
	}

	public void windowDeiconified(WindowEvent arg0) {
	}

	public void windowIconified(WindowEvent arg0) {
	}

	public void windowOpened(WindowEvent arg0) {		
		setLabelFieldFocus();
	}*/

	
			
}
