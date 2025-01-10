package org.geogebra.web.full.gui.dialog.options;

import java.util.Collection;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel;
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel.MinMaxType;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.impl.graphics.RightAngleProperty;
import org.geogebra.common.properties.impl.graphics.TooltipProperty;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.CompDropDown;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.util.GeoGebraIconW;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class BasicTab extends OptionsEuclidianW.EuclidianTab implements
		EuclidianOptionsModel.IBasicTab {

	/**
	 * 
	 */
	private final OptionsEuclidianW optionsEuclidianW;
	private Label dimTitle;
	private FormLabel[] dimLabel;
	private AutoCompleteTextFieldW[] dimField;
	AutoCompleteTextFieldW tfAxesRatioX;
	AutoCompleteTextFieldW tfAxesRatioY;
	private Label axesRatioLabel;
	ToggleButton tbLockRatio;

	protected ComponentCheckbox cbShowAxes;
	ComponentCheckbox cbBoldAxes;
	private Label colorLabel;
	private StandardButton btAxesColor;
	private Label lineStyle;
	protected FlowPanel axesOptionsPanel;
	private Label axesOptionsTitle;
	private PopupMenuButtonW axesStylePopup;
	protected Label backgroundColorLabel;
	protected StandardButton btBackgroundColor;
	ComponentCheckbox cbShowMouseCoords;
	private FormLabel tooltips;
	private FormLabel rightAngleStyleLabel;
	protected Label miscTitle;
	private Label consProtocolTitle;
	ComponentCheckbox cbShowNavbar;
	ComponentCheckbox cbNavPlay;
	ComponentCheckbox cbOpenConsProtocol;
	protected Label lblAxisLabelStyle;
	private ComponentCheckbox cbAxisLabelSerif;
	private ComponentCheckbox cbAxisLabelBold;
	private ComponentCheckbox cbAxisLabelItalic;
	protected FlowPanel miscPanel;
	private EuclidianOptionsModel model;
	CompDropDown rightAngleStyleListBox;
	CompDropDown lbTooltips;

	/**
	 * @param optionsEuclidianW
	 *            euclidian options panel
	 */
	public BasicTab(OptionsEuclidianW optionsEuclidianW) {
		super();
		this.optionsEuclidianW = optionsEuclidianW;
		this.model = optionsEuclidianW.model;
		addDimensionPanel();
		addAxesOptionsPanel();
		addConsProtocolPanel();
		addMiscPanel();
	}

	private void addMinMaxHandler(final AutoCompleteTextFieldW tf,
			final MinMaxType type) {

		tf.addKeyHandler(e -> {
			if (e.isEnterKey()) {
				model.applyMinMax(tf.getText(), type, this);
				optionsEuclidianW.updateView();
			}
		});

		tf.addBlurHandler(event -> {
			model.applyMinMax(tf.getText(), type, this);
			optionsEuclidianW.updateView();
		});
	}

	protected double parseDouble(String text) {
		if (StringUtil.empty(text)) {
			return Double.NaN;
		}
		return optionsEuclidianW.app.getKernel().getAlgebraProcessor()
				.evaluateToDouble(text);
	}

	void applyAxesRatio() {
		model.applyAxesRatio(
				parseDouble(tfAxesRatioX.getText()),
				parseDouble(tfAxesRatioY.getText()));
		optionsEuclidianW.updateView();
	}

	private void addAxesRatioHandler(final AutoCompleteTextFieldW tf) {
		tf.addKeyHandler(e -> {
			if (e.isEnterKey()) {
				applyAxesRatio();
			}
		});

		tf.addBlurHandler(event -> applyAxesRatio());
	}

	private void addDimensionPanel() {
		dimTitle = new Label("");
		dimTitle.setStyleName("panelTitle");
		int dimension = optionsEuclidianW.view.getDimension();
		dimLabel = new FormLabel[dimension * 2]; // "Xmin", "Xmax" etc.
		dimField = new AutoCompleteTextFieldW[dimension * 2];

		tfAxesRatioX = optionsEuclidianW.getTextField();
		tfAxesRatioY = optionsEuclidianW.getTextField();

		enableAxesRatio(optionsEuclidianW.view.isZoomable()
				&& !optionsEuclidianW.view.isLockedAxesRatio());

		tbLockRatio = new ToggleButton(MaterialDesignResources.INSTANCE.lock_open_black(),
				MaterialDesignResources.INSTANCE.lock_black());
		tbLockRatio.removeStyleName("ToggleButton");
		tbLockRatio.setSelected(optionsEuclidianW.view.isLockedAxesRatio());
		tbLockRatio.setEnabled(optionsEuclidianW.view.isZoomable());

		axesRatioLabel = new Label("");

		FlowPanel dimPanel = new FlowPanel();
		add(dimTitle);
		FlowPanel[] axisRangePanel = new FlowPanel[dimension * 2];
		MinMaxType[] fields;
		if (dimension == 2) {
			fields = new MinMaxType[]{MinMaxType.minX,
					MinMaxType.maxX, MinMaxType.minY, MinMaxType.maxY};
		} else {
			fields = new MinMaxType[]{MinMaxType.minX,
					MinMaxType.maxX, MinMaxType.minY, MinMaxType.maxY, MinMaxType.minZ,
					MinMaxType.maxZ};
		}
		for (int i = 0; i < fields.length; i++) {
			dimLabel[i] = new FormLabel();
			dimField[i] = optionsEuclidianW.getTextField();
			dimField[i].prepareShowSymbolButton(false);
			axisRangePanel[i] = new FlowPanel();
			axisRangePanel[i].setStyleName("panelRowCell");
			axisRangePanel[i].add(dimLabel[i].setFor(dimField[i]));
			axisRangePanel[i].add(dimField[i]);
			addMinMaxHandler(dimField[i], fields[i]);
		}

		dimPanel.add(
				LayoutUtilW.panelRow(axisRangePanel[0], axisRangePanel[1]));
		dimPanel.add(
				LayoutUtilW.panelRow(axisRangePanel[2], axisRangePanel[3]));
		if (dimension == 3) {
			dimPanel.add(
					LayoutUtilW.panelRow(axisRangePanel[4], axisRangePanel[5]));
		}

		if (dimension == 2) {
			dimPanel.add(LayoutUtilW.panelRow(axesRatioLabel));
			dimPanel.add(LayoutUtilW.panelRow(tfAxesRatioX, new Label(" : "),
					tfAxesRatioY, tbLockRatio));
		}

		indent(dimPanel);
		addAxesRatioHandler(tfAxesRatioX);
		addAxesRatioHandler(tfAxesRatioY);

		tbLockRatio.addFastClickHandler(event -> {
			if (tbLockRatio.isSelected()) {
				model.applyLockRatio(parseDouble(tfAxesRatioX.getText())
						/ parseDouble(tfAxesRatioY.getText()), this);
			} else {
				model.applyLockRatio(-1, this);
			}
		});
	}

	protected void indent(FlowPanel panel) {
		FlowPanel indent = new FlowPanel();
		indent.setStyleName("panelIndent");
		indent.add(panel);
		add(indent);
	}

	protected void addAxesOptionsPanel() {
		axesOptionsTitle = new Label();
		axesOptionsTitle.setStyleName("panelTitle");
		// show axes checkbox
		cbShowAxes = new ComponentCheckbox(optionsEuclidianW.loc, true, "ShowAxes",
				this::onShowAxes);

		// show bold checkbox
		cbBoldAxes = new ComponentCheckbox(optionsEuclidianW.loc, false, "Bold",
				this::onBoldAxes);

		// axes color
		colorLabel = new Label(
				optionsEuclidianW.loc.getMenu("Color") + ":");

		lblAxisLabelStyle = new Label(
				optionsEuclidianW.loc.getMenu("LabelStyle") + ":");
		// show axis label bold checkbox
		cbAxisLabelBold = new ComponentCheckbox(optionsEuclidianW.loc, false, "Bold",
				model::setAxisFontBold);

		cbAxisLabelSerif = new ComponentCheckbox(optionsEuclidianW.loc, false, "Serif",
				model::setAxesLabelsSerif);

		// show axis label italic checkbox
		cbAxisLabelItalic = new ComponentCheckbox(optionsEuclidianW.loc, false, "Italic",
				model::setAxisFontItalic);

		btAxesColor = new StandardButton(24);

		btAxesColor.addFastClickHandler(event -> optionsEuclidianW.getDialogManager()
				.showColorChooserDialog(model.getAxesColor(),
						new ColorChangeHandler() {

							@Override
							public void onForegroundSelected() {
								// TODO Auto-generated method stub

							}

							@Override
							public void onColorChange(GColor color) {
								model.applyAxesColor(color);
								updateAxesColorButton(color);
							}

							@Override
							public void onClearBackground() {
								// TODO Auto-generated method stub

							}

							@Override
							public void onBackgroundSelected() {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAlphaChange() {
								// TODO Auto-generated method stub

							}

							@Override
							public void onBarSelected() {
								// TODO Auto-generated method stub

							}
						}));

		// axes style
		lineStyle = new Label(
				optionsEuclidianW.loc.getMenu("LineStyle") + ":");
		final ImageOrText[] iconArray = new ImageOrText[EuclidianOptionsModel
				.getAxesStyleLength()];
		for (int i = 0; i < iconArray.length; i++) {
			iconArray[i] = GeoGebraIconW.createAxesStyleIcon(
					EuclidianStyleConstants.getLineStyleOptions(i));
		}

		axesStylePopup = new PopupMenuButtonW(optionsEuclidianW.app,
				iconArray, -1, 1,
				org.geogebra.common.gui.util.SelectionTable.MODE_ICON) {
			@Override
			public void handlePopupActionEvent() {
				int idx = getSelectedIndex();

				model.applyAxesStyle(EuclidianStyleConstants
						.getLineStyleOptions(idx)
						// make sure bold checkbox doesn't change
						+ (cbBoldAxes.isSelected()
								? EuclidianStyleConstants.AXES_BOLD : 0));
				optionsEuclidianW.updateView();
				super.handlePopupActionEvent();

			}
		};
		axesStylePopup.setKeepVisible(false);

		// axes options panel
		axesOptionsPanel = new FlowPanel();
		add(axesOptionsTitle);
		fillAxesOptionsPanel();

		indent(axesOptionsPanel);
	}

	private void onShowAxes(boolean selected) {
		setShowAxes(selected);
		optionsEuclidianW.updateView();
		optionsEuclidianW.app.storeUndoInfo();
	}

	private void onBoldAxes(boolean selected) {
		model.applyBoldAxes(selected);
		optionsEuclidianW.updateView();
	}

	protected void setShowAxes(Boolean value) {
		model.showAxes(value);
		optionsEuclidianW.xAxisTab.setShowAxis(value);
		optionsEuclidianW.yAxisTab.setShowAxis(value);
	}

	protected void fillAxesOptionsPanel() {
		axesOptionsPanel.add(LayoutUtilW.panelRow(cbShowAxes, cbBoldAxes));
		axesOptionsPanel.add(LayoutUtilW.panelRow(colorLabel, btAxesColor,
				lineStyle, axesStylePopup));
		addFontStyleRow();
	}

	protected void addFontStyleRow() {
		axesOptionsPanel.add(LayoutUtilW.panelRow(lblAxisLabelStyle,
				cbAxisLabelSerif, cbAxisLabelBold, cbAxisLabelItalic));
	}

	void togglePlayButton() {
		Collection<ConstructionProtocolNavigation> cpns = optionsEuclidianW.app
				.getGuiManager().getAllCPNavigations();
		for (ConstructionProtocolNavigation cpn : cpns) {
			cpn.setPlayButtonVisible(!cpn.isPlayButtonVisible());
		}
		optionsEuclidianW.app.setUnsaved();
		optionsEuclidianW.updateGUI();
	}

	void toggleConsProtButton() {
		Collection<ConstructionProtocolNavigation> cpns = optionsEuclidianW.app
				.getGuiManager().getAllCPNavigations();
		for (ConstructionProtocolNavigation cpn : cpns) {
			cpn.setConsProtButtonVisible(!cpn.isConsProtButtonVisible());
		}
		optionsEuclidianW.app.setUnsaved();
		optionsEuclidianW.updateGUI();
	}

	private void addConsProtocolPanel() {
		consProtocolTitle = new Label();
		consProtocolTitle.setStyleName("panelTitle");
		FlowPanel consProtocolPanel = new FlowPanel();

		cbShowNavbar = new ComponentCheckbox(optionsEuclidianW.loc, false, "Show",
				this::onShowNavBar);

		consProtocolPanel.add(cbShowNavbar);

		cbNavPlay = new ComponentCheckbox(optionsEuclidianW.loc, true, "PlayButton",
				selected -> togglePlayButton());

		cbOpenConsProtocol = new ComponentCheckbox(optionsEuclidianW.loc, true,
				"ConstructionProtocolButton",
				selected -> toggleConsProtButton());

		ConstructionProtocolNavigation cpn = optionsEuclidianW.app
				.getGuiManager().getCPNavigationIfExists();
		boolean selectNavPlay = cpn == null || cpn.isPlayButtonVisible();
		if (selectNavPlay != cbNavPlay.isSelected()) {
			cbNavPlay.setSelected(selectNavPlay);
		}
		boolean selectConsProtocol = cpn == null || cpn.isConsProtButtonVisible();
		if (selectConsProtocol != cbOpenConsProtocol.isSelected()) {
			cbOpenConsProtocol.setSelected(selectConsProtocol);
		}

		FlowPanel buttons = new FlowPanel();
		buttons.setStyleName("panelIndent");
		buttons.add(cbNavPlay);
		buttons.add(cbOpenConsProtocol);
		consProtocolPanel.add(buttons);

		add(consProtocolTitle);
		indent(consProtocolPanel);
	}

	private void onShowNavBar(boolean selected) {
		optionsEuclidianW.app
				.toggleShowConstructionProtocolNavigation(
						optionsEuclidianW.view
								.getViewID());
		cbNavPlay.setDisabled(!selected);
		cbOpenConsProtocol.setDisabled(!selected);
	}

	protected void applyBackgroundColor(GColor color) {
		int viewIdx = 0;
		if (optionsEuclidianW.view == optionsEuclidianW.app
				.getEuclidianView1()) {
			viewIdx = 1;
		} else if (optionsEuclidianW.app
				.hasEuclidianView2EitherShowingOrNot(1)
				&& optionsEuclidianW.view == optionsEuclidianW.app
						.getEuclidianView2(1)) {
			viewIdx = 2;
		} else if (optionsEuclidianW.app
				.isEuclidianView3D(optionsEuclidianW.view)) {
			viewIdx = 3;
		}
		model.applyBackgroundColor(viewIdx, color);
	}

	protected void addMiscPanel() {
		miscTitle = new Label();
		miscTitle.setStyleName("panelTitle");
		// background color panel
		backgroundColorLabel = new Label(
				optionsEuclidianW.loc.getMenu("BackgroundColor") + ":");

		btBackgroundColor = new StandardButton(24);

		// show mouse coords
		cbShowMouseCoords = new ComponentCheckbox(optionsEuclidianW.loc, false,
				"ShowMouseCoordinates", model::applyMouseCoords);
		// show tooltips
		TooltipProperty tooltipProperty = new TooltipProperty(optionsEuclidianW.loc,
				model.getSettings(), optionsEuclidianW.view);
		lbTooltips = new CompDropDown(optionsEuclidianW.app, tooltipProperty);
		tooltips = new FormLabel(
				optionsEuclidianW.loc.getMenu("Tooltips") + ":")
						.setFor(lbTooltips);
		tooltips.addStyleName("dropDownLabel");

		RightAngleProperty angleProperty = new RightAngleProperty(optionsEuclidianW.loc,
				optionsEuclidianW.app);
		rightAngleStyleListBox = new CompDropDown(optionsEuclidianW.app, angleProperty);
		rightAngleStyleLabel = new FormLabel(
				optionsEuclidianW.loc.getMenu("RightAngleStyle") + ":")
						.setFor(rightAngleStyleListBox);
		rightAngleStyleLabel.addStyleName("dropDownLabel");

		miscPanel = new FlowPanel();
		add(miscTitle);

		fillMiscPanel();

		indent(miscPanel);

		btBackgroundColor.addFastClickHandler(event -> {
			optionsEuclidianW.getDialogManager()
					.showColorChooserDialog(model.getBackgroundColor(),
							new ColorChangeHandler() {

								@Override
								public void onForegroundSelected() {
									// TODO Auto-generated method stub
								}

								@Override
								public void onColorChange(GColor color) {
									applyBackgroundColor(color);
									updateBackgroundColor(color);
								}

								@Override
								public void onClearBackground() {
									// TODO Auto-generated method stub
								}

								@Override
								public void onBackgroundSelected() {
									// TODO Auto-generated method stub
								}

								@Override
								public void onAlphaChange() {
									// TODO Auto-generated method stub
								}

								@Override
								public void onBarSelected() {
									// TODO Auto-generated method stub
								}
							});
		});
	}

	protected void fillMiscPanel() {
		miscPanel.add(
				LayoutUtilW.panelRow(backgroundColorLabel, btBackgroundColor));
		miscPanel.add(LayoutUtilW.panelRow(tooltips, lbTooltips));
		miscPanel.add(LayoutUtilW.panelRow(cbShowMouseCoords));
		miscPanel.add(LayoutUtilW.panelRow(rightAngleStyleLabel,
				rightAngleStyleListBox));
	}

	@Override
	public void setLabels() {
		dimTitle.setText(optionsEuclidianW.loc.getMenu("Dimensions"));

		dimLabel[0].setText(optionsEuclidianW.loc.getMenu("xmin") + ":");
		dimLabel[1].setText(optionsEuclidianW.loc.getMenu("xmax") + ":");
		dimLabel[2].setText(optionsEuclidianW.loc.getMenu("ymin") + ":");
		dimLabel[3].setText(optionsEuclidianW.loc.getMenu("ymax") + ":");
		axesRatioLabel.setText(optionsEuclidianW.loc.getMenu("xAxis")
				+ " : " + optionsEuclidianW.loc.getMenu("yAxis"));

		axesOptionsTitle.setText(optionsEuclidianW.loc.getMenu("Axes"));
		cbShowAxes.setLabels();
		cbBoldAxes.setLabels();
		colorLabel.setText(optionsEuclidianW.loc.getMenu("Color") + ":");
		lineStyle
				.setText(optionsEuclidianW.loc.getMenu("LineStyle") + ":");

		miscTitle.setText(optionsEuclidianW.loc.getMenu("Miscellaneous"));
		backgroundColorLabel.setText(
				optionsEuclidianW.loc.getMenu("BackgroundColor") + ":");

		lbTooltips.setLabels();
		cbShowMouseCoords.setLabels();

		rightAngleStyleListBox.setLabels();

		consProtocolTitle.setText(optionsEuclidianW.loc
				.getMenu("ConstructionProtocolNavigation"));

		cbShowNavbar.setLabels();
		cbNavPlay.setLabels();
		cbOpenConsProtocol.setLabels();

		lblAxisLabelStyle.setText(
				optionsEuclidianW.loc.getMenu("LabelStyle") + ":");
		cbAxisLabelSerif.setLabels();
		cbAxisLabelBold.setLabels();
		cbAxisLabelItalic.setLabels();
		tooltips.setText(optionsEuclidianW.loc.getMenu("Tooltips") + ":");

		rightAngleStyleLabel.setText(
				optionsEuclidianW.loc.getMenu("RightAngleStyle") + ":");
	}

	@Override
	public void enableAxesRatio(boolean value) {
		tfAxesRatioX.getTextBox().setEnabled(value);
		tfAxesRatioY.getTextBox().setEnabled(value);
		if (tbLockRatio != null) {
			tbLockRatio.setEnabled(value);
		}
	}

	/**
	 * @param minX
	 *            min x
	 * @param maxX
	 *            max x
	 * @param minY
	 *            min y
	 * @param maxY
	 *            max y
	 */
	public void setMinMaxText(String minX, String maxX, String minY,
			String maxY) {
		dimField[0].setText(minX);
		dimField[1].setText(maxX);
		dimField[2].setText(minY);
		dimField[3].setText(maxY);
	}

	@Override
	public void setMinMaxText(String minX, String maxX, String minY,
			String maxY, String minZ, String maxZ) {
		dimField[0].setText(minX);
		dimField[1].setText(maxX);
		dimField[2].setText(minY);
		dimField[3].setText(maxY);
		dimField[4].setText(minZ);
		dimField[5].setText(maxZ);
	}

	@Override
	public final void updateAxes(GColor color, boolean isShown, boolean isBold) {
		cbShowAxes.setSelected(isShown);
		cbBoldAxes.setSelected(isBold);
		updateAxesColorButton(color);
	}

	/**
	 * @param color
	 *            axes color
	 */
	public void updateAxesColorButton(GColor color) {
		ImageOrText content = new ImageOrText();
		content.setBgColor(color);
		btAxesColor.setIcon(content);
	}

	@Override
	public void updateBackgroundColor(GColor color) {
		ImageOrText content = new ImageOrText();
		content.setBgColor(color);
		btBackgroundColor.setIcon(content);
	}

	@Override
	public void updateConsProtocolPanel(boolean isVisible) {
		// cons protocol panel
		cbShowNavbar.setSelected(isVisible);
		cbNavPlay.setDisabled(!isVisible);
		cbOpenConsProtocol.setDisabled(!isVisible);
	}

	@Override
	public void showMouseCoords(boolean value) {
		cbShowMouseCoords.setSelected(value);
	}

	@Override
	public void selectAxesStyle(int index) {
		axesStylePopup.setSelectedIndex(index);
	}

	@Override
	public void enableLock(boolean value) {
		tbLockRatio.setEnabled(value);
	}

	protected void updateMinMax() {
		optionsEuclidianW.view.updateBoundObjects();
		setMinMaxText(
				optionsEuclidianW.view.getXminObject()
						.getLabel(StringTemplate.editTemplate),
				optionsEuclidianW.view.getXmaxObject()
						.getLabel(StringTemplate.editTemplate),
				optionsEuclidianW.view.getYminObject()
						.getLabel(StringTemplate.editTemplate),
				optionsEuclidianW.view.getYmaxObject()
						.getLabel(StringTemplate.editTemplate));
	}

	@Override
	public void updateBounds() {
		updateMinMax();

		double xscale = optionsEuclidianW.view.getXscale();
		double yscale = optionsEuclidianW.view.getYscale();
		if (xscale >= yscale) {
			tfAxesRatioX.setText("1");
			tfAxesRatioY.setText(optionsEuclidianW.app.getKernel()
					.format(xscale / yscale, StringTemplate.editTemplate));
		} else {
			tfAxesRatioX.setText(optionsEuclidianW.app.getKernel()
					.format(yscale / xscale, StringTemplate.editTemplate));
			tfAxesRatioY.setText("1");
		}
	}

	public FormLabel[] getDimLabel() {
		return dimLabel;
	}

	public OptionsEuclidianW getOptionsEuclidianW() {
		return optionsEuclidianW;
	}

	@Override
	public void selectTooltipType(int index) {
		lbTooltips.setSelectedIndex(index);
	}

	@Override
	public void updateAxisFontStyle(boolean serif, boolean isBold,
			boolean isItalic) {
		cbAxisLabelSerif.setSelected(serif);
		cbAxisLabelBold.setSelected(isBold);
		cbAxisLabelItalic.setSelected(isItalic);
	}
}