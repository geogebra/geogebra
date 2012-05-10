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
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.web.gui.DialogManagerWeb;
import geogebra.web.main.Application;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.DOM;


public class SliderDialog extends PopupPanel
implements ClickHandler, ChangeHandler
{
	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	private Button btApply, btCancel;
	private RadioButton rbNumber, rbAngle, rbInteger;
	private TextBox min, max, inc;
	private InlineLabel minLabel, maxLabel, incLabel;
	private HorizontalPanel minPanel, maxPanel, incPanel;

	private VerticalPanel mainWidget;
	private HorizontalPanel topWidget;
	private HorizontalPanel bottomWidget;
	private VerticalPanel leftWidget, rightWidget;
	//private InputPanel tfLabel;
	//private JPanel optionPane;
	//private JCheckBox cbRandom;
	
	private Application app;
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
	public SliderDialog(Application app, int x, int y) {
		super(false, true);
		//super(app.getFrame(), false);
		this.app = app;		
		//addWindowListener(this);
		
		// create temp geos that may be returned as result
		Construction cons = app.getKernel().getConstruction();
		
		
		number = new GeoNumeric(cons);
		angle = new GeoAngle(cons);
		DialogManagerWeb.setSliderFromDefault(number,false);
		DialogManagerWeb.setSliderFromDefault(angle,true);
		number.setValue(1);
		angle.setValue(45 * Math.PI/180);
			
		number.setSliderLocation(x, y, true);
		angle.setSliderLocation(x, y, true);
		
		
				
		geoResult = null;

		setWidget(mainWidget = new VerticalPanel());
		createGUI();
	}

	private void createGUI() {
		//setTitle(app.getPlain("Slider"));
		//setResizable(false);

		//Create components to be displayed
		mainWidget.add(topWidget = new HorizontalPanel());
		mainWidget.add(bottomWidget = new HorizontalPanel());

		topWidget.add(leftWidget = new VerticalPanel());
		topWidget.add(rightWidget = new VerticalPanel());

		// radio buttons for number or angle
		String id = DOM.createUniqueId();
		rbNumber = new RadioButton(id, app.getPlain("Numeric"));
		rbNumber.addClickHandler(this);
		rbAngle = new RadioButton(id, app.getPlain("Angle"));
		rbAngle.addClickHandler(this);
		rbInteger = new RadioButton(id, app.getPlain("Integer"));
		rbInteger.addClickHandler(this);

		leftWidget.add(rbNumber);
		leftWidget.add(rbAngle);			
		leftWidget.add(rbInteger);			

		rightWidget.add(minPanel = new HorizontalPanel());
		rightWidget.add(maxPanel = new HorizontalPanel());
		rightWidget.add(incPanel = new HorizontalPanel());

		minPanel.add(minLabel = new InlineLabel(app.getPlain("min")+":"));
		minPanel.add(min = new TextBox());
		min.setVisibleLength(6);
		min.addChangeHandler(this);

		maxPanel.add(maxLabel = new InlineLabel(app.getPlain("max")+":"));
		maxPanel.add(max = new TextBox());
		max.setVisibleLength(6);

		incPanel.add(incLabel = new InlineLabel(app.getPlain("Width")+":"));
		incPanel.add(inc = new TextBox());
		inc.setVisibleLength(6);

		// buttons
		btApply = new Button(app.getPlain("Apply"));
		btApply.addClickHandler(this);
		//btApply.setActionCommand("Apply");
		//btApply.addActionListener(this);
		btCancel = new Button(app.getPlain("Cancel"));
		btCancel.addClickHandler(this);
		//btCancel.setActionCommand("Cancel");
		//btCancel.addActionListener(this);

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
		if (e.getSource() == btApply.getElement().getFirstChild()) {
			geoResult = rbAngle.getValue() ? angle : number; 		
			getResult();
			geoResult.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			geoResult.setLabelVisible(true);
			geoResult.update();
			//((GeoNumeric)geoResult).setRandom(cbRandom.isSelected());

			hide();

			app.storeUndoInfo();
		} else if (e.getSource() == btCancel.getElement().getFirstChild()) {
			hide();
		} else if (e.getSource() == rbNumber.getElement() ||
				   e.getSource() == rbAngle.getElement() ||
				   e.getSource() == rbInteger.getElement()) {
			GeoElement selGeo = rbAngle.getValue() ? angle : number;			
			if (e.getSource() == rbInteger) {
				number.setAnimationStep(1);
				number.setIntervalMin(1);
				number.setIntervalMax(30);
			} else if (e.getSource() == rbNumber) {
				GeoNumeric num = app.getKernel().getDefaultNumber(false);
				number.setAnimationStep(num.getAnimationStep());
				number.setIntervalMin(num.getIntervalMin());
				number.setIntervalMax(num.getIntervalMax());
			}
			GeoElement [] geos = { selGeo };

			sliderPanelUpdate(geos);
		}
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

		if (equalWidth){
			inc.setText(app.getKernel().format(num0.getSliderWidth(),highPrecision));
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

	public void onChange(ChangeEvent ce) {
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
					app.showErrorDialog(app.getError("CircularDefinition"));
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
		else if (source == inc) {
			for (int i = 0; i < geos.length; i++) {
				GeoNumeric num = (GeoNumeric) geos[i];
				num.setSliderWidth(value.getDouble());
				num.updateRepaint();
			}
		} 

		sliderPanelUpdate(geos);
		//actionPerforming = false;
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
