package org.geogebra.web.full.gui.properties;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.options.model.SliderModel;
import org.geogebra.common.gui.dialog.options.model.SliderModel.ISliderOptionsListener;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.web.full.gui.AngleTextFieldW;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.dialog.options.CheckboxPanel;
import org.geogebra.web.full.gui.dialog.options.model.ExtendedAVModel;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.SliderPanel;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.ListBox;

/**
 * panel for numeric slider
 * 
 * @author Markus Hohenwarter
 */
public class SliderPropertiesPanelW extends OptionPanel implements ISliderOptionsListener {

	private SliderModel model;
	private AngleTextFieldW tfMin;
	private AngleTextFieldW tfMax;
	private AutoCompleteTextFieldW tfWidth;
	private AutoCompleteTextFieldW tfBlobSize;
	private AutoCompleteTextFieldW tfLineThickness;
	private SliderPanel sliderTransparency;
	private Label transparencyLabel;
	private Label blobSizeUnitLabel;
	private Label lineThicknessUnitLabel;
	private StandardButton blobColorChooserBtn;
	private Label blobColorLbl;
	private StandardButton lineColorChooserBtn;
	private Label pointStyleTitleLbl;
	private Label lineStyleTitleLbl;
	private Label lineColorLbl;
	private Label blobSizeLabel;
	private Label lineThicknessLabel;
	private Label minLabel;
	private Label maxLabel;
	private Label widthLabel;
	private Label widthUnitLabel;
	private ComponentCheckbox cbSliderFixed;
	private ComponentCheckbox cbRandom;
	private ListBox lbSliderHorizontal;

	private AnimationStepPanelW stepPanel;
	private AnimationSpeedPanelW speedPanel;
	private Kernel kernel;
	private FlowPanel intervalPanel;
	private FlowPanel sliderPanel;
	private FlowPanel animationPanel;
	private FlowPanel widthPanel;
	private FlowPanel sliderStylePanel;
	private boolean useTabbedPane;

	private CheckboxPanel avPanel;
	private Localization loc;

	/**
	 * @param app
	 *            application
	 * @param useTabbedPane
	 *            true if used tabbed pane
	 * @param includeRandom
	 *            true if include random
	 */
	public SliderPropertiesPanelW(final AppW app,
			boolean useTabbedPane, boolean includeRandom) {
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
		sliderStylePanel = new FlowPanel();

		avPanel = new CheckboxPanel(app.getLocalization(), new ExtendedAVModel(null, app));

		cbSliderFixed = new ComponentCheckbox(loc, false, "fixed",
				(selected) -> getModel().applyFixed(selected));
		positionPanel.add(cbSliderFixed);

		cbRandom = new ComponentCheckbox(loc, false, "Random",
				(selected) -> getModel().applyRandom(selected));
		positionPanel.add(cbRandom);

		lbSliderHorizontal = new ListBox();
		lbSliderHorizontal.addChangeHandler(event -> getModel()
				.applyDirection(
						getLbSliderHorizontal().getSelectedIndex()));

		positionPanel.add(lbSliderHorizontal);

		tfMin = new AngleTextFieldW(6, app);
		tfMin.addKeyDownHandler(event -> {
			if (event.getNativeEvent().getKeyCode() == 13) {
				applyMin();
			}
		});
		tfMin.enableGGBKeyboard();
		
		tfMin.addBlurHandler(event -> applyMin());

		tfMax = new AngleTextFieldW(6, app);
		tfMax.addKeyDownHandler(event -> {
			if (event.getNativeEvent().getKeyCode() == 13) {
				applyMax();
			}
		});

		tfMax.enableGGBKeyboard();

		tfMax.addBlurHandler(event -> applyMax());

		tfWidth = new AutoCompleteTextFieldW(8, app);
		tfWidth.removeSymbolTable();
		tfWidth.addKeyHandler(e -> {
			if (e.isEnterKey()) {
				applyWidth();
			}
		});
		
		tfWidth.enableGGBKeyboard();
		
		tfWidth.addBlurHandler(event -> applyWidth());

		createBlobSizeTextField(app);
		createBlobColorChooserBtn(app);
		createTransparencySlider();
		createLineColorChooserBtn(app);
		createLineThicknessTextField(app);

		maxLabel = new Label();
		minLabel = new Label();
		widthLabel = new Label();
		widthUnitLabel = new Label();
		blobSizeLabel = new Label();
		blobColorLbl = new Label();
		lineColorLbl = new Label();
		lineThicknessLabel = new Label();
		transparencyLabel = new Label();
		lineThicknessUnitLabel = new Label("px");
		blobSizeUnitLabel = new Label("px");
		pointStyleTitleLbl = new Label();
		lineStyleTitleLbl = new Label();

		FlowPanel minPanel = new FlowPanel();
		minPanel.add(minLabel);
		minPanel.add(tfMin);
		intervalPanel.add(minPanel);

		FlowPanel maxPanel = new FlowPanel();
		maxPanel.add(maxLabel);
		maxPanel.add(tfMax);
		intervalPanel.add(maxPanel);

		widthPanel = new FlowPanel();
		widthPanel.setStyleName("optionsPanel");
		widthPanel.setStyleName("sliderWidthPanel");
		widthPanel.add(widthLabel);
		tfWidth.add(widthUnitLabel);
		widthUnitLabel.setStyleName("unitLabel");
		widthPanel.add(tfWidth);

		createSliderStylePanel();

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
	public SliderModel getModel() {
		return model;
	}

	private void createPointStylePanel() {
		FlowPanel blobSizePanel = new FlowPanel();
		blobSizePanel.setStyleName("sliderWidthPanel");
		blobSizePanel.add(blobSizeLabel);
		tfBlobSize.add(blobSizeUnitLabel);
		blobSizeUnitLabel.setStyleName("unitLabel");
		blobSizePanel.add(tfBlobSize);

		sliderStylePanel.add(blobSizePanel);
		sliderStylePanel
				.add(LayoutUtilW.panelRow(blobColorLbl, blobColorChooserBtn));
	}

	private void createLineStylePanel() {
		sliderStylePanel.add(widthPanel);
		FlowPanel lineThicknessPanel = new FlowPanel();
		lineThicknessPanel.setStyleName("sliderWidthPanel");
		lineThicknessPanel.add(lineThicknessLabel);
		tfLineThickness.add(lineThicknessUnitLabel);
		lineThicknessUnitLabel.setStyleName("unitLabel");
		lineThicknessPanel.add(tfLineThickness);

		sliderStylePanel.add(lineThicknessPanel);
		sliderStylePanel
				.add(LayoutUtilW.panelRow(lineColorLbl, lineColorChooserBtn));
		FlowPanel transparencySliderPanel = new FlowPanel();
		transparencySliderPanel.setStyleName("optionsPanel");
		transparencySliderPanel.add(transparencyLabel);
		transparencySliderPanel.add(sliderTransparency);
		sliderStylePanel.add(transparencySliderPanel);
	}

	private void createSliderStylePanel() {
		pointStyleTitleLbl.addStyleName("panelTitle");
		sliderStylePanel.add(pointStyleTitleLbl);
		createPointStylePanel();
		lineStyleTitleLbl.addStyleName("panelTitle");
		sliderStylePanel.add(lineStyleTitleLbl);
		createLineStylePanel();
	}

	private void createTransparencySlider() {
		sliderTransparency = new SliderPanel(0, 100);
		sliderTransparency.setTickSpacing(5);
		sliderTransparency.setValue(40);
		sliderTransparency.addInputHandler(event -> applyTransparency());
		sliderTransparency.addValueChangeHandler(val -> model.storeUndoInfo());
	}

	private void createLineThicknessTextField(AppW app) {
		tfLineThickness = new AutoCompleteTextFieldW(8, app);
		tfLineThickness.removeSymbolTable();
		tfLineThickness.addKeyHandler(e -> {
			if (e.isEnterKey()) {
				applyLineThickness();
			}
		});
		tfLineThickness.enableGGBKeyboard();
		tfLineThickness.addBlurHandler(event -> applyLineThickness());
	}

	private void createBlobColorChooserBtn(final AppW app) {
		blobColorChooserBtn = new StandardButton(24);
		updateBlobOrLineColorButton(model.getBlobColor(), true);
		blobColorChooserBtn.addFastClickHandler(event -> ((DialogManagerW) app.getDialogManager())
				.showColorChooserDialog(getModel().getBlobColor(),
						getBlobColorHandler()));
	}

	protected ColorChangeHandler getBlobColorHandler() {
		return new ColorChangeHandler() {

			@Override
			public void onForegroundSelected() {
				// do nothing
			}

			@Override
			public void onColorChange(GColor color) {
				getModel().applyBlobColor(color);
				updateBlobOrLineColorButton(color, true);
			}

			@Override
			public void onClearBackground() {
				// do nothing
			}

			@Override
			public void onBackgroundSelected() {
				// do nothing
			}

			@Override
			public void onAlphaChange() {
				// do nothing
			}

			@Override
			public void onBarSelected() {
				// do nothing
			}
		};
	}

	/**
	 * @param color
	 *            basic color
	 * @return basic color with opacity
	 */
	public GColor getColorWithOpacity(GColor color) {
		GColor lineCol = color == null ? GColor.BLACK : color;
		return GColor.newColor(lineCol.getRed(),
				lineCol.getGreen(), lineCol.getBlue(),
				getSliderTransparency().getValue() * 255 / 100);
	}

	private void createLineColorChooserBtn(final AppW app) {
		lineColorChooserBtn = new StandardButton(24);
		updateBlobOrLineColorButton(getColorWithOpacity(model.getLineColor()),
				false);
		lineColorChooserBtn.addFastClickHandler(event -> ((DialogManagerW) app.getDialogManager())
				.showColorChooserDialog(getModel().getLineColor(),
						getLineColorHandler()));
	}

	protected ColorChangeHandler getLineColorHandler() {
		return new ColorChangeHandler() {

			@Override
			public void onForegroundSelected() {
				// do nothing
			}

			@Override
			public void onColorChange(GColor color) {
				getModel().applyLineColor(getColorWithOpacity(color));
				updateBlobOrLineColorButton(getColorWithOpacity(color), false);
			}

			@Override
			public void onClearBackground() {
				// do nothing
			}

			@Override
			public void onBackgroundSelected() {
				// do nothing
			}

			@Override
			public void onAlphaChange() {
				// do nothing
			}

			@Override
			public void onBarSelected() {
				// do nothing
			}
		};
	}

	/**
	 * @param color
	 *            of blob shown as selected
	 * @param isBlob
	 *            true if blob (false for line)
	 */
	public void updateBlobOrLineColorButton(GColor color, boolean isBlob) {
		ImageOrText content = new ImageOrText();
		content.setBgColor(color == null ? GColor.BLACK : color);
		if (isBlob) {
			blobColorChooserBtn.setIcon(content);
		} else {
			if (color == null) {
				content.setBgColor(getColorWithOpacity(null));
			}
			lineColorChooserBtn.setIcon(content);
		}
	}

	private void createBlobSizeTextField(AppW app) {
		tfBlobSize = new AutoCompleteTextFieldW(8, app);
		tfBlobSize.removeSymbolTable();
		tfBlobSize.addKeyHandler(e -> {
			if (e.isEnterKey()) {
				applyBlobSize();
			}
		});
		tfBlobSize.enableGGBKeyboard();
		tfBlobSize.addBlurHandler(event -> applyBlobSize());
	}

	@Override
	public OptionPanel updatePanel(Object[] geos) {
		stepPanel.updatePanel(geos);
		speedPanel.updatePanel(geos);
		avPanel.updatePanel(geos);
		return super.updatePanel(geos);
	}

	/**
	 * apply min value
	 */
	protected void applyMin() {
		model.applyMin(getNumberFromInput(tfMin.getText().trim()));
	}

	/**
	 * apply max value
	 */
	protected void applyMax() {
		model.applyMax(getNumberFromInput(tfMax.getText().trim()));
	}

	/**
	 * apply width of slider (line length)
	 */
	protected void applyWidth() {
		model.applyWidth(getNumberFromInput(tfWidth.getText().trim()).getDouble());
	}

	/**
	 * apply width of slider (line length)
	 */
	protected void applyTransparency() {
		model.applyTransparency(sliderTransparency.getValue());
		model.updateProperties();
	}

	/**
	 * apply blob size
	 */
	protected void applyBlobSize() {
		double blob = getNumberFromInput(tfBlobSize.getText().trim())
				.getDouble() <= 0 ? 1
						: getNumberFromInput(tfBlobSize.getText().trim())
								.getDouble();
		model.applyBlobSize(blob);
		if (DoubleUtil.isEqual(blob, 1)) {
			tfBlobSize.setText(String.valueOf(1));
		}
	}

	/**
	 * apply line thickness
	 */
	protected void applyLineThickness() {
		double thickness = getNumberFromInput(tfLineThickness.getText().trim())
				.getDouble() * 2 <= 0 ? 2
						: getNumberFromInput(tfLineThickness.getText().trim())
								.getDouble() * 2;
		model.applyLineThickness(thickness);
		if (DoubleUtil.isEqual(thickness, 2)) {
			tfLineThickness.setText(String.valueOf(1));
		}
	}

	private void initPanels() {
		FlowPanel mainPanel = new FlowPanel();

		// put together interval, slider options, animation panels
		if (useTabbedPane) {
			MultiRowsTabPanel tabPanel = new MultiRowsTabPanel("dialogThreeTabs");
			tabPanel.add(intervalPanel, loc.getMenu("Interval"));
			tabPanel.add(sliderPanel, loc.getMenu("Slider"));
			tabPanel.add(animationPanel, loc.getMenu("Animation"));
			mainPanel.add(tabPanel);
			tabPanel.selectTab(0);
		} else { // no tabs
			mainPanel.add(intervalPanel);
			mainPanel.add(sliderPanel);
			mainPanel.add(animationPanel);

			mainPanel.add(avPanel.getWidget());
			mainPanel.add(sliderStylePanel);
		}

		setWidget(mainPanel);
	}

	/**
	 * @return list of slider labels
	 */
	public ListBox getLbSliderHorizontal() {
		return lbSliderHorizontal;
	}

	/**
	 * @return slider for transparency
	 */
	public SliderPanel getSliderTransparency() {
		return sliderTransparency;
	}

	@Override
	public void setLabels() {
		pointStyleTitleLbl.setText(loc.getMenu("PointStyle"));
		lineStyleTitleLbl.setText(loc.getMenu("LineStyle"));
		cbSliderFixed.setLabels();
		cbRandom.setLabels();
		String[] comboStr = { loc.getMenu("horizontal"),
				loc.getMenu("vertical") };
		int selectedIndex = lbSliderHorizontal.getSelectedIndex();
		lbSliderHorizontal.clear();
		for (int i = 0; i < comboStr.length; ++i) {
			lbSliderHorizontal.addItem(comboStr[i]);
		}
		lbSliderHorizontal.setSelectedIndex(selectedIndex);
		minLabel.setText(loc.getMenu("min"));
		maxLabel.setText(loc.getMenu("max"));
		widthLabel.setText(loc.getMenu("Width"));
		blobSizeLabel.setText(loc.getMenu("Size"));
		blobColorLbl.setText(loc.getMenu("Color") + ":");
		lineColorLbl.setText(loc.getMenu("Color") + ":");
		lineThicknessLabel
				.setText(loc.getMenu("Thickness"));
		transparencyLabel
				.setText(loc.getMenu("LineOpacity") + ":");
		model.setLabelForWidthUnit();
		stepPanel.setLabels();
		speedPanel.setLabels();
		avPanel.setLabels();
	}

	private NumberValue getNumberFromInput(final String inputText) {
		boolean emptyString = "".equals(inputText);
		NumberValue value = new MyDouble(kernel, Double.NaN);
		if (!emptyString) {
			value = kernel.getAlgebraProcessor().evaluateToNumeric(inputText,
					false);
		}
		return value;
	}

	@Override
	public void setMinText(String text) {
		tfMin.setText(text);
	}

	@Override
	public void setMaxText(String text) {
		tfMax.setText(text);
	}

	@Override
	public void setWidthText(String text) {
		tfWidth.setText(text);
	}

	@Override
	public void setBlobSizeText(String text) {
		tfBlobSize.setText(text);
	}

	@Override
	public void setLineThicknessSizeText(String text) {
		tfLineThickness.setText(text);
	}

	@Override
	public void selectFixed(boolean value) {
		cbSliderFixed.setSelected(value);
	}

	@Override
	public void selectRandom(boolean value) {
		cbRandom.setSelected(value);
	}

	@Override
	public void setRandomVisible(boolean value) {
		cbRandom.setVisible(value);
	}

	@Override
	public void setSliderDirection(int index) {
		lbSliderHorizontal.setSelectedIndex(index);
	}

	@Override
	public void setWidthUnitText(String text) {
		widthUnitLabel.setText(text);
	}

	/**
	 * @param geoResult
	 *            result geoElement
	 */
	public void applyAll(GeoElement geoResult) {
		Object[] geos = { geoResult };
		model.setGeos(geos);
		model.applyFixed(cbSliderFixed.isSelected());
		model.applyRandom(cbRandom.isSelected());
		model.applyDirection(lbSliderHorizontal.getSelectedIndex());
		applyMin();
		applyMax();
		applyWidth();
		applyBlobSize();
		applyLineThickness();
	}

	@Override
	public void setBlobColor(GColor color) {
		updateBlobOrLineColorButton(color, true);
	}

	@Override
	public void setLineColor(GColor color) {
		updateBlobOrLineColorButton(color, false);
	}
}