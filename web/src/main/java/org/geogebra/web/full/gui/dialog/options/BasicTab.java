package org.geogebra.web.full.gui.dialog.options;

import java.util.Collection;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel;
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel.MinMaxType;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.GeoGebraIconW;
import org.geogebra.web.full.gui.util.MyCJButton;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.GToggleButton;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.LayoutUtilW;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class BasicTab extends OptionsEuclidianW.EuclidianTab {

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
	private FlowPanel dimPanel;
	GToggleButton tbLockRatio;
	private Image imgLock;
	private Image imgUnlock;

	protected CheckBox cbShowAxes;
	CheckBox cbBoldAxes;
	private Label colorLabel;
	private MyCJButton btAxesColor;
	private Label lineStyle;
	protected FlowPanel axesOptionsPanel;
	private Label axesOptionsTitle;
	private PopupMenuButtonW axesStylePopup;
	protected Label backgroundColorLabel;
	protected MyCJButton btBackgroundColor;
	CheckBox cbShowMouseCoords;
	private FormLabel tooltips;
	private FormLabel rightAngleStyleLabel;
	protected Label miscTitle;
	private Label consProtocolTitle;
	private FlowPanel consProtocolPanel;
	CheckBox cbShowNavbar;
	CheckBox cbNavPlay;
	CheckBox cbOpenConsProtocol;
	protected Label lblAxisLabelStyle;
	protected CheckBox cbAxisLabelSerif;
	protected CheckBox cbAxisLabelBold;
	protected CheckBox cbAxisLabelItalic;
	protected FlowPanel miscPanel;
	private EuclidianOptionsModel model;
	ListBox rightAngleStyleListBox;
	ListBox lbTooltips;

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
				model.applyMinMax(tf.getText(), type);
				optionsEuclidianW.updateView();
			}
		});

		tf.addBlurHandler(event -> {
			model.applyMinMax(tf.getText(), type);
			optionsEuclidianW.updateView();
		});
	}

	protected double parseDouble(String text) {
		if (text == null || "".equals(text)) {
			return Double.NaN;
		}
		return optionsEuclidianW.app.getKernel().getAlgebraProcessor()
				.evaluateToDouble(text);
	}

	void applyAxesRatio() {
		optionsEuclidianW.model.applyAxesRatio(
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

		imgLock = new Image(MaterialDesignResources.INSTANCE.lock_black()
						.getSafeUri().asString(), 0, 0, 18, 18);
		imgUnlock = new Image(MaterialDesignResources.INSTANCE.lock_open_black()
						.getSafeUri().asString(), 0, 0, 18, 18);

		tbLockRatio = new GToggleButton(imgLock);
		tbLockRatio.setValue(optionsEuclidianW.view.isLockedAxesRatio());
		tbLockRatio.setEnabled(optionsEuclidianW.view.isZoomable());

		axesRatioLabel = new Label("");

		dimPanel = new FlowPanel();
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

		tbLockRatio.addClickHandler(event -> {
			if (tbLockRatio.getValue()) {
				model.applyLockRatio(parseDouble(tfAxesRatioX.getText())
						/ parseDouble(tfAxesRatioY.getText()));
			} else {
				model.applyLockRatio(-1);
			}
		});
	}

	protected int setDimension() {
		return 4;
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
		cbShowAxes = new CheckBox(
				optionsEuclidianW.loc.getMenu("ShowAxes"));

		// show bold checkbox
		cbBoldAxes = new CheckBox(optionsEuclidianW.loc.getMenu("Bold"));

		// axes color
		colorLabel = new Label(
				optionsEuclidianW.loc.getMenu("Color") + ":");

		lblAxisLabelStyle = new Label(
				optionsEuclidianW.loc.getMenu("LabelStyle") + ":");
		// show axis label bold checkbox
		cbAxisLabelBold = new CheckBox(
				optionsEuclidianW.loc.getMenu("Bold"));

		cbAxisLabelSerif = new CheckBox(
				optionsEuclidianW.loc.getMenu("Serif"));

		// show axis label italic checkbox
		cbAxisLabelItalic = new CheckBox(
				optionsEuclidianW.loc.getMenu("Italic"));

		btAxesColor = new MyCJButton();

		btAxesColor.addClickHandler(event -> optionsEuclidianW.getDialogManager()
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
						+ (cbBoldAxes.getValue()
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
		cbShowAxes.addClickHandler(event -> {
			setShowAxes(cbShowAxes.getValue());
			optionsEuclidianW.updateView();
			optionsEuclidianW.app.storeUndoInfo();
		});

		cbBoldAxes.addClickHandler(event -> {
			model.applyBoldAxes(cbBoldAxes.getValue(),
					cbShowAxes.getValue());
			optionsEuclidianW.updateView();
		});

		cbAxisLabelSerif.addClickHandler(
				event -> model.setAxesLabelsSerif(cbAxisLabelSerif.getValue()));

		cbAxisLabelBold.addClickHandler(event -> model.setAxisFontBold(cbAxisLabelBold.getValue()));

		cbAxisLabelItalic.addClickHandler(
				event -> model.setAxisFontItalic(cbAxisLabelItalic.getValue()));

		indent(axesOptionsPanel);
	}

	protected void setShowAxes(Boolean value) {
		optionsEuclidianW.model.showAxes(value);
		optionsEuclidianW.xAxisTab.setShowAxis(value);
		optionsEuclidianW.yAxisTab.setShowAxis(value);
	}

	protected void fillAxesOptionsPanel() {
		axesOptionsPanel.add(LayoutUtilW.panelRow(cbShowAxes, cbBoldAxes));
		axesOptionsPanel.add(LayoutUtilW.panelRow(colorLabel, btAxesColor,
				lineStyle, axesStylePopup));
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
		consProtocolPanel = new FlowPanel();

		cbShowNavbar = new CheckBox();

		consProtocolPanel.add(cbShowNavbar);

		cbNavPlay = new CheckBox();

		cbOpenConsProtocol = new CheckBox();

		cbShowNavbar.setStyleName("checkBoxPanel");

		FlowPanel buttons = new FlowPanel();
		buttons.setStyleName("panelIndent");
		cbNavPlay.setStyleName("checkBoxPanel");
		cbOpenConsProtocol.setStyleName("checkBoxPanel");
		buttons.add(cbNavPlay);
		buttons.add(cbOpenConsProtocol);
		consProtocolPanel.add(buttons);

		add(consProtocolTitle);
		indent(consProtocolPanel);

		cbShowNavbar.addClickHandler(event -> {
			optionsEuclidianW.app
					.toggleShowConstructionProtocolNavigation(
							optionsEuclidianW.view
									.getViewID());
			cbNavPlay.setEnabled(cbShowNavbar.getValue());
			cbOpenConsProtocol.setEnabled(cbShowNavbar.getValue());
		});

		cbNavPlay.addClickHandler(event -> togglePlayButton());

		cbOpenConsProtocol.addClickHandler(event -> toggleConsProtButton());
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
		optionsEuclidianW.model.applyBackgroundColor(viewIdx, color);
	}

	protected void addMiscPanel() {
		miscTitle = new Label();
		miscTitle.setStyleName("panelTitle");
		// background color panel
		backgroundColorLabel = new Label(
				optionsEuclidianW.loc.getMenu("BackgroundColor") + ":");

		btBackgroundColor = new MyCJButton();

		// show mouse coords
		cbShowMouseCoords = new CheckBox();

		// show tooltips
		lbTooltips = new ListBox();
		fillTooltipCombo();
		tooltips = new FormLabel(
				optionsEuclidianW.loc.getMenu("Tooltips") + ":")
						.setFor(lbTooltips);

		rightAngleStyleListBox = new ListBox();
		updateRightAngleCombo();
		rightAngleStyleListBox
				.setSelectedIndex(optionsEuclidianW.view.getRightAngleStyle());
		rightAngleStyleLabel = new FormLabel(
				optionsEuclidianW.loc.getMenu("RightAngleStyle") + ":")
						.setFor(rightAngleStyleListBox);
		miscPanel = new FlowPanel();
		add(miscTitle);

		fillMiscPanel();

		indent(miscPanel);

		btBackgroundColor.addClickHandler(event -> {
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
									updateBackgroundColorButton(color);
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
			// model.applyBackgroundColor();
		});

		cbShowMouseCoords.addClickHandler(
				event -> model.applyMouseCoords(cbShowMouseCoords.getValue()));

		lbTooltips.addChangeHandler(event -> model.applyTooltipMode(
				lbTooltips.getSelectedIndex()));

		rightAngleStyleListBox.addChangeHandler(event -> model.applyRightAngleStyle(
				rightAngleStyleListBox.getSelectedIndex()));
	}

	private void updateRightAngleCombo() {
		rightAngleStyleListBox.clear();
		for (String s : model.fillRightAngleCombo()) {
			rightAngleStyleListBox.addItem(s);
		}
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
		cbShowAxes.setText(optionsEuclidianW.loc.getMenu("ShowAxes"));
		cbBoldAxes.setText(optionsEuclidianW.loc.getMenu("Bold"));
		colorLabel.setText(optionsEuclidianW.loc.getMenu("Color") + ":");
		lineStyle
				.setText(optionsEuclidianW.loc.getMenu("LineStyle") + ":");

		miscTitle.setText(optionsEuclidianW.loc.getMenu("Miscellaneous"));
		backgroundColorLabel.setText(
				optionsEuclidianW.loc.getMenu("BackgroundColor") + ":");
		int index = lbTooltips.getSelectedIndex();

		fillTooltipCombo();
		lbTooltips.setSelectedIndex(index);
		cbShowMouseCoords.setText(
				optionsEuclidianW.loc.getMenu("ShowMouseCoordinates"));

		index = rightAngleStyleListBox.getSelectedIndex();

		updateRightAngleCombo();
		rightAngleStyleListBox.setSelectedIndex(index);

		consProtocolTitle.setText(optionsEuclidianW.loc
				.getMenu("ConstructionProtocolNavigation"));

		cbShowNavbar.setText(optionsEuclidianW.loc.getMenu("Show"));
		cbNavPlay.setText(optionsEuclidianW.loc.getMenu("PlayButton"));
		cbOpenConsProtocol.setText(optionsEuclidianW.loc
				.getMenu("ConstructionProtocolButton"));

		lblAxisLabelStyle.setText(
				optionsEuclidianW.loc.getMenu("LabelStyle") + ":");
		cbAxisLabelSerif.setText(optionsEuclidianW.loc.getMenu("Serif"));
		cbAxisLabelBold.setText(optionsEuclidianW.loc.getMenu("Bold"));
		cbAxisLabelItalic.setText(optionsEuclidianW.loc.getMenu("Italic"));
		tooltips.setText(optionsEuclidianW.loc.getMenu("Tooltips") + ":");

		rightAngleStyleLabel.setText(
				optionsEuclidianW.loc.getMenu("RightAngleStyle") + ":");
	}

	private void fillTooltipCombo() {
		lbTooltips.clear();
		for (String item : model.fillTooltipCombo()) {
			lbTooltips.addItem(item);
		}
	}

	/**
	 * @param value
	 *            axes ratio
	 */
	public void enableAxesRatio(boolean value) {
		tfAxesRatioX.getTextBox().setEnabled(value);
		tfAxesRatioY.getTextBox().setEnabled(value);
		// tbLockRatio.getDownFace().setImage(value ? imgUnlock : imgLock);
		if (tbLockRatio != null) {
			tbLockRatio.getUpFace().setImage(value ? imgUnlock : imgLock);
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

	/**
	 * @param minX
	 *            min x
	 * @param maxX
	 *            max x
	 * @param minY
	 *            min y
	 * @param maxY
	 *            max y
	 * @param minZ
	 *            min z
	 * @param maxZ
	 *            max z
	 */
	public void setMinMaxText(String minX, String maxX, String minY,
			String maxY, String minZ, String maxZ) {
		dimField[0].setText(minX);
		dimField[1].setText(maxX);
		dimField[2].setText(minY);
		dimField[3].setText(maxY);
		dimField[4].setText(minZ);
		dimField[5].setText(maxZ);
	}

	/**
	 * Updates color, visible and bold checkboxes using current view settings.
	 * 
	 * @param color
	 *            axes color override for axis color
	 */
	public final void updateAxes(GColor color) {
		// btAxesColor.setForeground(new GColorW(view.getAxesColor()));
		cbShowAxes.setValue(optionsEuclidianW.view.getShowXaxis()
				&& optionsEuclidianW.view.getShowYaxis());
		cbBoldAxes.setValue(optionsEuclidianW.view.areAxesBold());
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

	/**
	 * @param color
	 *            background color
	 */
	public void updateBackgroundColorButton(GColor color) {
		ImageOrText content = new ImageOrText();
		content.setBgColor(color);
		btBackgroundColor.setIcon(content);
	}

	/**
	 * Update construction protocol navigation settings.
	 * 
	 * @param isVisible
	 *            whether the checkboxes should be enabled
	 */
	public void updateConsProtocolPanel(boolean isVisible) {
		// cons protocol panel
		cbShowNavbar.setValue(isVisible);
		ConstructionProtocolNavigation cpn = optionsEuclidianW.app
				.getGuiManager().getCPNavigationIfExists();
		cbNavPlay.setValue(cpn == null || cpn.isPlayButtonVisible());
		cbOpenConsProtocol
				.setValue(cpn == null || cpn.isConsProtButtonVisible());

		cbNavPlay.setEnabled(isVisible);
		cbOpenConsProtocol.setEnabled(isVisible);
	}

	public void showMouseCoords(boolean value) {
		cbShowMouseCoords.setValue(value);
	}

	public void selectAxesStyle(int index) {
		axesStylePopup.setSelectedIndex(index);
	}

	public void enabeLock(boolean value) {
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

	/**
	 * Update xmin, xmax, ymin, ymax and scale inputs
	 */
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
}