package geogebra.web.gui.dialog.options;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.SetLabels;
import geogebra.common.gui.dialog.options.model.AxisModel;
import geogebra.common.gui.dialog.options.model.AxisModel.IAxisModelListener;
import geogebra.common.gui.util.TableSymbols;
import geogebra.html5.event.FocusListener;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.util.LayoutUtil;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.main.AppW;

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

	protected NumberComboBox ncbTickDist;
	protected ListBox lbTickStyle, lbAxisLabel, lbUnitLabel;
	protected AutoCompleteTextFieldW tfCross;

	private Label crossAt;

	private Label axisTicks;

	private Label axisLabel;

	private Label axisUnitLabel;

	private AppW app;
	protected EuclidianView view;
	private class NumberComboBox extends ListBox {
		private static final String PI_STRING = "\u03c0";

		public NumberComboBox() {		
			addItem("1"); //pi
			addItem(PI_STRING); //pi
			addItem(PI_STRING + "/2"); //pi/2
		}
		
		public double getValue() {
			final String text = getItemText(getSelectedIndex()).toString().trim();
			if (text.equals("")) return Double.NaN;
			return app.getKernel().getAlgebraProcessor().evaluateToDouble(text);			
		}

	};
	
	/******************************************************
	 * @param app
	 * @param view
	 * @param axis
	 */
	public AxisPanel(AppW app, EuclidianView view, int axis) {

		this.app = app;
		this.view = view;
		model = new AxisModel(app, view, axis, this);

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
					model.applyTickDistance(ncbTickDist.getValue());
				}

			}});


		ncbTickDist = new NumberComboBox();

		ncbTickDist.addChangeHandler(new ChangeHandler(){

			public void onChange(ChangeEvent event) {
				model.applyTickDistance(ncbTickDist.getValue());

            }});
		FlowPanel distancePanel = new FlowPanel();
		distancePanel.add(cbManualTicks);
		distancePanel.add(ncbTickDist);

		// axis and unit label
		lbAxisLabel = new ListBox();
		lbAxisLabel.addItem("");
		lbAxisLabel.addItem(axis == 0 ? "x" : "y");
		String[] greeks = TableSymbols.greekLowerCase;
		for (int i = 0; i < greeks.length; i++) {
			lbAxisLabel.addItem(greeks[i]);
		}
		lbAxisLabel.addChangeHandler(new ChangeHandler(){

			public void onChange(ChangeEvent event) {
				int idx = lbAxisLabel.getSelectedIndex();
				String text = lbAxisLabel.getItemText(idx).toString().trim();
				model.applyAxisLabel(text);

			}});

		axisLabel = new Label(app.getPlain("AxisLabel") + ":");
		axisUnitLabel = new Label(app.getPlain("AxisUnitLabel") + ":");
		lbUnitLabel = new ListBox();
		//	lbUnitLabel.setEditable(true);
		lbUnitLabel.addChangeHandler(new ChangeHandler(){

			public void onChange(ChangeEvent event) {
				int idx = lbUnitLabel.getSelectedIndex();
				String text = lbUnitLabel.getItemText(idx).toString().trim();
				model.applyUnitLabel(text);

			}});
		model.fillUnitLabel();

		FlowPanel labelPanel = new FlowPanel();
		labelPanel.add(axisLabel);
		labelPanel.add(lbAxisLabel);
		labelPanel.add(axisUnitLabel);
		labelPanel.add(lbUnitLabel);

		// cross at and stick to edge

		InputPanelW input = new InputPanelW(null, (AppW) app, 1, -1, true);
		tfCross = (AutoCompleteTextFieldW) input.getTextComponent();
		tfCross.setAutoComplete(false);
		tfCross.setShowSymbolTableIcon(false);

		tfCross.addKeyHandler(new KeyHandler() {

			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					model.applyCrossing(tfCross.getText());
				}
			}});

		tfCross.addFocusListener(new FocusListener(this){
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
		tfCross.setStyleName("numberInput");
		
		// add all panels
		add(cbShowAxis);
		add(cbAxisNumber);
		add(cbPositiveAxis);
		add(distancePanel);
		add(showTicksPanel);
		add(labelPanel);
		add(crossPanel);

		updatePanel();
	}


	public void updatePanel() {
		int axis = model.getAxis();
		cbAxisNumber.setValue(view.getShowAxesNumbers()[axis]);


		cbManualTicks
		.setValue(!view.isAutomaticAxesNumberingDistance()[axis]);
//		ncbTickDist.setSelectedIndex(axis);
		ncbTickDist.setEnabled(cbManualTicks.getValue());


		lbAxisLabel.setSelectedIndex(axis);
		lbUnitLabel.setSelectedIndex(axis);

		int type = view.getAxesTickStyles()[axis];
		lbTickStyle.setSelectedIndex(type);

		// cbShowAxis.setSelected(axis == 0 ? view.getShowXaxis() :
		// view.getShowYaxis());
		cbShowAxis.setValue(view.getShowAxis(axis));

		if (view.getDrawBorderAxes()[axis])
			tfCross.setText("");
		else
			tfCross.setText("" + view.getAxesCross()[axis]);
	
		tfCross.setVisible(!view.getDrawBorderAxes()[axis]);
		cbPositiveAxis.setValue(view.getPositiveAxes()[axis]);

		cbDrawAtBorder.setValue(view.getDrawBorderAxes()[axis]);

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

	public void addUnitLabelItem(String item) {
		lbUnitLabel.addItem(item == null ? "": item);
	} 

	public void setCrossText(String text) {
		tfCross.setText(text);
	}

	public AxisModel getModel() {
		return model;
	}
}
