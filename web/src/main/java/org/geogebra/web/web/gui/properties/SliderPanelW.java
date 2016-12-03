package org.geogebra.web.web.gui.properties;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.dialog.options.model.SliderModel;
import org.geogebra.common.gui.dialog.options.model.SliderModel.ISliderOptionsListener;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.inputfield.FieldHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.AngleTextFieldW;
import org.geogebra.web.web.gui.dialog.options.CheckboxPanel;
import org.geogebra.web.web.gui.dialog.options.model.ExtendedAVModel;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * panel for numeric slider
 * 
 * @author Markus Hohenwarter
 */
public class SliderPanelW extends OptionPanel implements ISliderOptionsListener {

	private SliderModel model;
	private AngleTextFieldW tfMin, tfMax;
	private AutoCompleteTextFieldW tfWidth;
	private Label minLabel;
	private Label maxLabel;
	private Label widthLabel;
	private Label widthUnitLabel;
	private CheckBox cbSliderFixed, cbRandom;
	private ListBox lbSliderHorizontal;

	private AppW app;
	private AnimationStepPanelW stepPanel;
	private AnimationSpeedPanelW speedPanel;
	private Kernel kernel;
	private FlowPanel intervalPanel, sliderPanel, animationPanel;
	private boolean useTabbedPane;
	private boolean actionPerforming;

	private boolean widthUnit = false;

	private CheckboxPanel avPanel;
	private Localization loc;

	

	public SliderPanelW(final AppW app,
			boolean useTabbedPane, boolean includeRandom) {
		this.app = app;
		this.loc = app.getLocalization();
		kernel = app.getKernel();
		model = new SliderModel(app, this);
		setModel(model);

		this.useTabbedPane = useTabbedPane;
		model.setIncludeRandom(includeRandom); 

		intervalPanel = new FlowPanel();
		intervalPanel.setStyleName("sliderIntervalPanel");
		sliderPanel = new FlowPanel();
		FlowPanel positionPanel = new FlowPanel();
		positionPanel.setStyleName("optionsPanel");
		sliderPanel.add(positionPanel);
		animationPanel = new FlowPanel();

		avPanel = new CheckboxPanel("ShowSliderInAlgebraView",
				app.getLocalization(), new ExtendedAVModel(null, app));


		cbSliderFixed = new CheckBox();
		cbSliderFixed.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent event) {
				model.applyFixed(cbSliderFixed.getValue());

			}});
		positionPanel.add(cbSliderFixed);

		cbRandom = new CheckBox();
		cbRandom.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent event) {
				model.applyRandom(cbRandom.getValue());

			}});
		;
		positionPanel.add(cbRandom);

		lbSliderHorizontal = new ListBox();
		lbSliderHorizontal.addChangeHandler(new ChangeHandler(){

			public void onChange(ChangeEvent event) {
				model.applyDirection(lbSliderHorizontal.getSelectedIndex());

			}});

		positionPanel.add(lbSliderHorizontal);

		tfMin = new AngleTextFieldW(6, app);
		tfMin.addKeyDownHandler(new KeyDownHandler(){

			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeEvent().getKeyCode() == 13) {
					applyMin();
				}

			}});
		tfMin.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				FieldHandler.focusGained(tfMin, app);
			}
		});
		
		tfMin.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				applyMin();
				FieldHandler.focusLost(tfMin, app);
			}
		});

		tfMax = new AngleTextFieldW(6, app);
		tfMax.addKeyDownHandler(new KeyDownHandler(){

			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeEvent().getKeyCode() == 13) {
					applyMax();
				}
			}});

		tfMax.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				FieldHandler.focusGained(tfMax, app);
			}
		});

		tfMax.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				applyMax();
				FieldHandler.focusLost(tfMax, app);
			}
		});


		tfWidth = new AutoCompleteTextFieldW(8, app);
		tfWidth.removeSymbolTable();
		tfWidth.addKeyHandler(new KeyHandler(){

			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					applyWidth();
				}
			}});
		
		tfWidth.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				FieldHandler.focusGained(tfWidth, app);
			}
		});

		tfWidth.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				applyWidth();
				FieldHandler.focusLost(tfWidth, app);
			}
		});


		maxLabel = new Label();
		minLabel = new Label();
		widthLabel = new Label();
		widthUnitLabel = new Label();

		FlowPanel minPanel = new FlowPanel();
		minPanel.add(minLabel);
		minPanel.add(tfMin);
		intervalPanel.add(minPanel);

		FlowPanel maxPanel = new FlowPanel();
		maxPanel.add(maxLabel);
		maxPanel.add(tfMax);
		intervalPanel.add(maxPanel);

		FlowPanel widthPanel = new FlowPanel();
		widthPanel.setStyleName("optionsPanel");
		widthPanel.setStyleName("sliderWidthPanel");
		widthPanel.add(widthLabel);
		widthPanel.add(tfWidth);
		widthPanel.add(widthUnitLabel);

		sliderPanel.add(widthPanel);

		// add increment to intervalPanel
		stepPanel = new AnimationStepPanelW(app);
		stepPanel.setPartOfSliderPanel();
		intervalPanel.add(stepPanel.getWidget());

		speedPanel = new AnimationSpeedPanelW(app);
		speedPanel.setPartOfSliderPanel();
		animationPanel.add(speedPanel.getWidget());
		initPanels();
		setLabels();
	}

	@Override
	public OptionPanel updatePanel(Object[] geos) {
		stepPanel.updatePanel(geos);
		speedPanel.updatePanel(geos);
		avPanel.updatePanel(geos);
		return super.updatePanel(geos);
	}
	protected void applyMin() {
		model.applyMin(getNumberFromInput(tfMin.getText().trim()));

	}

	protected void applyMax() {
		model.applyMax(getNumberFromInput(tfMax.getText().trim()));

	}

	protected void applyWidth() {
		model.applyWidth(getNumberFromInput(tfWidth.getText().trim()).getDouble());

	}

	private void initPanels() {
		FlowPanel mainPanel = new FlowPanel();

		// put together interval, slider options, animation panels
		if (useTabbedPane) {
			TabPanel tabPanel = new TabPanel();
			tabPanel.add(intervalPanel, loc.getMenu("Interval"));
			tabPanel.add(sliderPanel, loc.getMenu("Slider"));
			tabPanel.add(animationPanel, loc.getMenu("Animation"));

			mainPanel.add(tabPanel);
			tabPanel.selectTab(0);
		} else { // no tabs
			//			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			//			intervalPanel.setBorder(BorderFactory.createTitledBorder(app
			//					.getPlain("Interval")));
			//			sliderPanel.setBorder(BorderFactory.createTitledBorder(app
			//					.getPlain("Slider")));
			//			animationPanel.setBorder(BorderFactory.createTitledBorder(app
			//					.getPlain("Animation")));
			mainPanel.add(intervalPanel);
			mainPanel.add(sliderPanel);
			mainPanel.add(animationPanel);
		}

		mainPanel.add(avPanel.getWidget());

		setWidget(mainPanel);
	}

	public void setLabels() {
		cbSliderFixed.setText(loc.getMenu("fixed"));
		cbRandom.setText(loc.getMenu("Random"));

		String[] comboStr = { loc.getMenu("horizontal"),
				loc.getMenu("vertical") };

		int selectedIndex = lbSliderHorizontal.getSelectedIndex();
		lbSliderHorizontal.clear();

		for (int i = 0; i < comboStr.length; ++i) {
			lbSliderHorizontal.addItem(comboStr[i]);
		}

		lbSliderHorizontal.setSelectedIndex(selectedIndex);


		minLabel.setText(loc.getMenu("min") + ":");
		maxLabel.setText(loc.getMenu("max") + ":");
		widthLabel.setText(loc.getMenu("Width") + ":");

		model.setLabelForWidthUnit();


		stepPanel.setLabels();
		speedPanel.setLabels();

		avPanel.setLabels();

	}

	private NumberValue getNumberFromInput(final String inputText) {
		boolean emptyString = inputText.equals("");
		NumberValue value = new MyDouble(kernel, Double.NaN);
		if (!emptyString) {
			value = kernel.getAlgebraProcessor().evaluateToNumeric(inputText,
					false);
		}

		return value;
	}

	public void setMinText(String text) {
		tfMin.setText(text);
	}

	public void setMaxText(String text) {
		tfMax.setText(text);
	}

	public void setWidthText(String text) {
		tfWidth.setText(text);
	}

	public void selectFixed(boolean value) {
		cbSliderFixed.setValue(value);
	}

	public void selectRandom(boolean value) {
		cbRandom.setValue(value);
	}

	public void setRandomVisible(boolean value) {
		cbRandom.setVisible(value);
	}

	public void setSliderDirection(int index) {
		lbSliderHorizontal.setSelectedIndex(index);
	}

	public void setWidthUnitText(String text) {
		widthUnitLabel.setText(text);
	}

	public void applyAll(GeoElement geoResult) {
		Object geos[] =  {geoResult};
		model.setGeos(geos);
		model.applyFixed(cbSliderFixed.getValue());
		model.applyRandom(cbRandom.getValue());
		model.applyDirection(lbSliderHorizontal.getSelectedIndex());
		applyMin();
		applyMax();
		applyWidth();
	}
}