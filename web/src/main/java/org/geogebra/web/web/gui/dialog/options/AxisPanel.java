package org.geogebra.web.web.gui.dialog.options;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.AxisModel;
import org.geogebra.common.gui.dialog.options.model.AxisModel.IAxisModelListener;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.LayoutUtil;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.util.ComboBoxW;
import org.geogebra.web.web.gui.util.NumberListBox;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class AxisPanel extends FlowPanel implements SetLabels, IAxisModelListener {

	private static final long serialVersionUID = 1L;

	private AxisModel model;

	protected CheckBox cbShowAxis, cbAxisNumber, cbManualTicks,
	cbPositiveAxis, cbDrawAtBorder;

	protected NumberListBox ncbTickDist;
	protected ListBox lbTickStyle;
	private ComboBoxW comboAxisLabel, comboUnitLabel;
	protected AutoCompleteTextFieldW tfCross;

	private Label crossAt;

	private Label axisTicks;

	private Label axisLabel;

	private Label axisUnitLabel;

	private AppW app;
	protected EuclidianView view;
	
	
	/******************************************************
	 * @param app
	 * @param view
	 * @param axis
	 */
	public AxisPanel(AppW app, EuclidianView view, int axis, boolean view3D) {

		this.app = app;
		this.view = view;
		model = new AxisModel(app, view, axis, this);

		this.addStyleName("axisPanel");
		
		String strAxisEn = model.getAxisName();
		//		this.setBorder(LayoutUtil.titleBorder(app.getPlain(strAxisEn)));

		// show axis
		cbShowAxis = new CheckBox(app.getPlain("Show" + strAxisEn));
		cbShowAxis.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent event) {
				model.showAxis(cbShowAxis.getValue());
			}});

		// show numbers
		cbAxisNumber = new CheckBox(app.getPlain("ShowAxisNumbers"));
		cbAxisNumber.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent event) {
				model.showAxisNumbers(cbAxisNumber.getValue());
			}});

		// show positive axis only
		cbPositiveAxis = new CheckBox(app.getPlain("PositiveDirectionOnly"));
		cbPositiveAxis.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent event) {
				model.applyPositiveAxis(cbPositiveAxis.getValue());

			}});


		// ticks
		axisTicks = new Label(app.getPlain("AxisTicks") + ":");
		lbTickStyle = new ListBox();

		model.fillTicksCombo();

		lbTickStyle.addChangeHandler(new ChangeHandler(){

			public void onChange(ChangeEvent event) {
				int type = lbTickStyle.getSelectedIndex();
				model.applyTickStyle(type);
				//				view.updateBackground();
				//				updatePanel();
			}});

		FlowPanel showTicksPanel = new FlowPanel();
		showTicksPanel.add(axisTicks);
		showTicksPanel.add(lbTickStyle);

		// distance
		cbManualTicks = new CheckBox(app.getPlain("TickDistance") + ":");
		cbManualTicks.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent event) {
				boolean isTickDistanceOn = cbManualTicks.getValue(); 
				model.applyTickDistance(isTickDistanceOn);
				ncbTickDist.setEnabled(isTickDistanceOn);
				if (isTickDistanceOn) {
					model.applyTickDistance(ncbTickDist.getDoubleValue());
				}

			}});


		ncbTickDist = new NumberListBox(app){

			@Override
            protected void onValueChange(String value) {
				model.applyTickDistance(ncbTickDist.getDoubleValue());
           
            }};

		FlowPanel distancePanel = new FlowPanel();
		distancePanel.add(cbManualTicks);
		distancePanel.add(ncbTickDist);

		// axis and unit label
		comboAxisLabel = new ComboBoxW(app) {

			@Override
            protected void onValueChange(String value) {
				String text = comboAxisLabel.getValue().trim();
				model.applyAxisLabel(text);
            
            }};
		comboAxisLabel.setEnabled(true);
        model.fillAxisCombo();
		
        axisLabel = new Label(app.getPlain("AxisLabel") + ":");
		axisUnitLabel = new Label(app.getPlain("AxisUnitLabel") + ":");
		comboUnitLabel = new ComboBoxW(app) {

			@Override
            protected void onValueChange(String value) {
				String text = comboUnitLabel.getValue().trim();
				model.applyUnitLabel(text);
          
            }};

            model.fillUnitLabel();

		comboUnitLabel.setEnabled(true);
		FlowPanel labelPanel = new FlowPanel();
		labelPanel.add(axisLabel);
		labelPanel.add(comboAxisLabel);
		
		FlowPanel unitPanel = new FlowPanel();
		unitPanel.add(axisUnitLabel);
		unitPanel.add(comboUnitLabel);

		// cross at and stick to edge

		InputPanelW input = new InputPanelW(null, (AppW) app, 1, -1, true);
		tfCross = (AutoCompleteTextFieldW) input.getTextComponent();
		tfCross.setAutoComplete(false);
		tfCross.removeSymbolTable();

		tfCross.addKeyHandler(new KeyHandler() {

			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					model.applyCrossing(tfCross.getText());
				}
			}});

		tfCross.addFocusListener(new FocusListenerW(this){
			@Override
			protected void wrapFocusLost(){
				model.applyCrossing(tfCross.getText());
			}	
		});

		crossAt = new Label(app.getPlain("CrossAt") + ":");
		cbDrawAtBorder = new CheckBox(app.getPlain("StickToEdge"));
		cbDrawAtBorder.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent event) {
				model.applyDrawAtBorder(cbDrawAtBorder.getValue());
			}});


		FlowPanel crossPanel = LayoutUtil.panelRow(crossAt, tfCross, cbDrawAtBorder);

		cbShowAxis.setStyleName("checkBoxPanel");
		cbAxisNumber.setStyleName("checkBoxPanel");
		cbPositiveAxis.setStyleName("checkBoxPanel");
		distancePanel.setStyleName("listBoxPanel");
		showTicksPanel.setStyleName("listBoxPanel");
		labelPanel.setStyleName("listBoxPanel");
		unitPanel.setStyleName("listBoxPanel");
		tfCross.setStyleName("numberInput");
		
		// add all panels
		add(cbShowAxis);
		add(cbAxisNumber);
		add(cbPositiveAxis);
		add(distancePanel);
		add(showTicksPanel);
		add(labelPanel);
		add(unitPanel);
		if (!view3D){
			add(crossPanel);
		}
		updatePanel();
	}
	

	public void updatePanel() {
		int axis = model.getAxis();
		cbAxisNumber.setValue(view.getShowAxesNumbers()[axis]);


		cbManualTicks
		.setValue(!view.isAutomaticAxesNumberingDistance()[axis]);
		ncbTickDist.setSelectedId(view.getAxesNumberingDistances()[axis]+"");
		ncbTickDist.setEnabled(cbManualTicks.getValue());


		comboAxisLabel.setSelectedId(view.getAxesLabels(true)[axis]);
		comboUnitLabel.setSelectedId(view.getAxesUnitLabels()[axis]);

		int type = view.getAxesTickStyles()[axis];
		lbTickStyle.setSelectedIndex(type);

		// cbShowAxis.setSelected(axis == 0 ? view.getShowXaxis() :
		// view.getShowYaxis());
		setShowAxis(view.getShowAxis(axis));

		if (view.getDrawBorderAxes()[axis])
			tfCross.setText("");
		else
			tfCross.setText("" + view.getAxesCross()[axis]);
	
		tfCross.setVisible(!view.getDrawBorderAxes()[axis]);
		cbPositiveAxis.setValue(view.getPositiveAxes()[axis]);

		cbDrawAtBorder.setValue(view.getDrawBorderAxes()[axis]);

	}

	public void setShowAxis(boolean value) {
		cbShowAxis.setValue(value);
	}

	public void setLabels() {
		String strAxisEn = model.getAxisName();
		//		this.setBorder(LayoutUtil.titleBorder(app.getPlain(strAxisEn)));
		//		this.setBorder(LayoutUtil.titleBorder(null));
		cbShowAxis.setText(app.getPlain("Show" + strAxisEn));
		cbAxisNumber.setText(app.getPlain("ShowAxisNumbers"));
		cbManualTicks.setText(app.getPlain("TickDistance") + ":");
		axisTicks.setText(app.getPlain("AxisTicks") + ":");
		cbPositiveAxis.setText(app.getPlain("PositiveDirectionOnly"));
		axisLabel.setText(app.getPlain("AxisLabel") + ":");
		axisUnitLabel.setText(app.getPlain("AxisUnitLabel") + ":");
		crossAt.setText(app.getPlain("CrossAt") + ":");
		cbDrawAtBorder.setText(app.getPlain("StickToEdge"));

	}

	protected double parseDouble(String text) {
		if (text == null || text.equals(""))
			return Double.NaN;
		return app.getKernel().getAlgebraProcessor().evaluateToDouble(text);
	}

	protected String getString() {
		return model.getAxisName();
	}
	//
	//	public void updateFont() {
	//		Font font = app.getPlainFont();
	//
	//		setFont(font);
	//
	//		cbShowAxis.setFont(font);
	//		cbAxisNumber.setFont(font);
	//		cbManualTicks.setFont(font);
	//		axisTicks.setFont(font);
	//		cbPositiveAxis.setFont(font);
	//		axisLabel.setFont(font);
	//		axisUnitLabel.setFont(font);
	//		crossAt.setFont(font);
	//		stickToEdge.setFont(font);
	//
	//		ncbTickDist.setFont(font);
	//
	//		lbTickStyle.setFont(font);
	//		lbAxisLabel.setFont(font);
	//		lbUnitLabel.setFont(font);
	//
	//		tfCross.setFont(font);
	//	}

	public void addTickItem(String item) {
		lbTickStyle.addItem(item);
	}

	public void addAxisLabelItem(String item) {
		comboAxisLabel.addItem(item == null ? "": item);
	} 

	public void addUnitLabelItem(String item) {
		comboUnitLabel.addItem(item == null ? "": item);
	} 

	public void setCrossText(String text) {
		tfCross.setText(text);
	}

	public AxisModel getModel() {
		return model;
	}
}
