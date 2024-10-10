package org.geogebra.web.full.gui.view.probcalculator;

import org.geogebra.common.exam.ExamListener;
import org.geogebra.common.exam.ExamState;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.probcalculator.ProbabilityManager;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.properties.impl.distribution.DistributionTypeProperty;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.components.CompDropDown;
import org.geogebra.web.full.gui.util.ProbabilityModeGroup;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

public class DistributionPanel extends FlowPanel implements InsertHandler, ExamListener {
	private ProbabilityCalculatorViewW view;
	private Localization loc;
	private CompDropDown distributionDropDown;
	private ToggleButton cumulativeWidget;
	private Label[] lblParameterArray;
	private MathTextFieldW[] fldParameterArray;
	protected ProbabilityModeGroup modeGroup;
	protected ResultPanelW resultPanel;
	private DistributionTypeProperty distTypeProperty;

	/**
	 * costructor
	 * @param view - prob calc view
	 * @param loc - localization
	 */
	public DistributionPanel(ProbabilityCalculatorViewW view, Localization loc) {
		this.view = view;
		this.loc = loc;
		addStyleName("distrPanel");
		GlobalScope.examController.addListener(this);
		buildGUI();
	}

	/**
	 * build distribution panel
	 */
	public void buildGUI() {
		initCumulativeWidget();
		FlowPanel comboParamPanel = new FlowPanel();
		comboParamPanel.addStyleName("comboParamPanel");
		comboParamPanel.add(cumulativeWidget);
		buildDistrComboBox(comboParamPanel);
		add(comboParamPanel);

		resultPanel = new ResultPanelW(view.getApp(), this);
		buildParameterPanel(comboParamPanel);
		buildModeGroupWithResult();
		add(resultPanel);
	}

	public ProbabilityCalculatorViewW getView() {
		return view;
	}

	protected void buildModeGroupWithResult() {
		modeGroup = new ProbabilityModeGroup(loc);
		modeGroup.add(ProbabilityCalculatorView.PROB_LEFT, GuiResources.INSTANCE.interval_left(),
				"LeftProb");
		modeGroup.add(ProbabilityCalculatorView.PROB_INTERVAL,
				GuiResources.INSTANCE.interval_between(), "IntervalProb");
		modeGroup.add(ProbabilityCalculatorView.PROB_TWO_TAILED,
				GuiResources.INSTANCE.interval_two_tailed(), "TwoTailedProb");
		modeGroup.add(ProbabilityCalculatorView.PROB_RIGHT,
				GuiResources.INSTANCE.interval_right(), "RightProb");
		modeGroup.endGroup();
		modeGroup.addFastClickHandler((source) -> {
			if (modeGroup.handle(source) && !view.isCumulative()) {
				view.changeProbabilityType();
				view.updateProbabilityType(resultPanel);
				view.updateGUI();
			}
		});

		add(modeGroup);
	}

	/**
	 * @param disable whether to disable or not
	 */
	public void disableInterval(boolean disable) {
		if (modeGroup != null) {
			modeGroup.disableButtons(disable);
			Dom.toggleClass(modeGroup, "disabled", disable);
		}
	}

	/**
	 * add parameter label and input field to UI
	 * @param parent - parent holder div
	 */
	public void buildParameterPanel(FlowPanel parent) {
		initParamFields();

		FlowPanel parameterPanel = new FlowPanel();
		parameterPanel.addStyleName("parameterPanel");
		for (int i = 0; i < view.maxParameterCount; i++) {
			FlowPanel holderPanel = new FlowPanel();
			holderPanel.addStyleName("holder");
			holderPanel.add(lblParameterArray[i]);
			holderPanel.add(fldParameterArray[i]);
			parameterPanel.add(holderPanel);
		}

		parent.add(parameterPanel);
	}

	private void initParamFields() {
		lblParameterArray = new Label[ view.maxParameterCount];
		fldParameterArray = new MathTextFieldW[ view.maxParameterCount];

		for (int i = 0; i < view.maxParameterCount; i++) {
			lblParameterArray[i] = BaseWidgetFactory.INSTANCE.newSecondaryText("");
			fldParameterArray[i] = new MathTextFieldW(view.getApp());
			resultPanel.addInsertHandler(fldParameterArray[i]);
		}
	}

	/**
	 * update parameter fields
	 */
	public void updateParameters() {
		for (int i = 0; i < view.maxParameterCount; ++i) {

			boolean hasParm = i < ProbabilityManager.getParmCount(view.getSelectedDist());

			lblParameterArray[i].setVisible(hasParm);
			fldParameterArray[i].setVisible(hasParm);

			if (hasParm) {
				// set label
				lblParameterArray[i].setVisible(true);
				lblParameterArray[i].setText(getParamLabel(i));
				// set field
				fldParameterArray[i].setText(view.format(view.getParameters()[i]));
				resetError(fldParameterArray[i]);
			}
		}
	}

	private void resetError(MathTextFieldW field) {
		Widget parent = field.asWidget().getParent();
		if (parent != null) {
			parent.removeStyleName("errorStyle");
			parent.removeStyleName("errorStyle");
		}
	}

	private String getParamLabel(int index) {
		return view.getParameterLabels()[view.getSelectedDist().ordinal()][index];
	}

	/**
	 * init cumulative widget
	 */
	public void initCumulativeWidget() {
		cumulativeWidget = new ToggleButton(GuiResources.INSTANCE.cumulative_distribution());
		cumulativeWidget.addFastClickHandler((e) -> {
				view.setCumulative(cumulativeWidget.isSelected());
				disableInterval(cumulativeWidget.isSelected());
		});
	}

	/**
	 * init and fill the distribution drop-down
	 * @param parent - parent panel
	 */
	public void buildDistrComboBox(FlowPanel parent) {
		distTypeProperty = new DistributionTypeProperty(loc, view);
		GlobalScope.propertiesRegistry.register(distTypeProperty, getApp());
		String comboLbl = getApp().getConfig().hasDistributionView() ? "Distribution" : null;
		distributionDropDown = new CompDropDown(getApp(), comboLbl, distTypeProperty);
		if (getApp().getConfig().hasDistributionView()) {
			distributionDropDown.setFullWidth(true);
		}
		distributionDropDown.addStyleName("comboDistribution");
		parent.add(distributionDropDown);
	}

	private AppW getApp() {
		return (AppW) view.getApp();
	}

	/**
	 * update the whole gui
	 */
	public void updateGUI() {
		updateCumulative();
		updateParameters();
		distributionDropDown.resetFromModel();
		modeGroup.setMode(view.getProbMode());
	}

	protected void updateCumulative() {
		cumulativeWidget.setSelected(view.isCumulative());
	}

	/**
	 * update translation
	 */
	public void setLabels() {
		distributionDropDown.setLabels();
		if (cumulativeWidget != null) {
			cumulativeWidget.setTitle(loc.getMenu("Cumulative"));
		}
		for (int i = 0; i < ProbabilityManager.getParmCount(view.getSelectedDist()); i++) {
			lblParameterArray[i]
					.setText(getParamLabel(i));
		}
		modeGroup.setLabels();
		view.updateProbabilityType(resultPanel);
	}

	public int getModeGroupValue() {
		return modeGroup.getValue();
	}

	@Override
	public void doTextFieldActionPerformed(MathTextFieldW source, boolean intervalCheck) {
		if (view.isIniting()) {
			return;
		}
		String inputText = source.getText().trim();
		boolean update = true;

		if (!"".equals(inputText)) {
			Kernel kernel = view.getApp().getKernel();
			// allow input such as sqrt(2)
			GeoNumberValue nv = kernel.getAlgebraProcessor().evaluateToNumeric(
					inputText, intervalCheck ? source : ErrorHelper.silent());
			GeoNumberValue numericValue = nv != null
					? nv : new GeoNumeric(kernel.getConstruction(), Double.NaN);
			double value = numericValue.getDouble();

			if (Double.isNaN(value)) {
				source.asWidget().getParent().addStyleName("errorStyle");
				return;
			} else {
				resetError(source);
			}
			if (getResultPanel().isFieldLow(source)) {
				checkBounds(numericValue, intervalCheck, false);
			} else if (getResultPanel().isFieldHigh(source)) {
				checkBounds(numericValue, intervalCheck, true);
			} else if (getResultPanel().isFieldResult(source)) {
				update = false;
				if (value < 0 || value > 1) {
					if (!intervalCheck) {
						updateLowHigh();
						return;
					}
					updateGUI();
				} else {
					if (view.getProbMode() == ProbabilityCalculatorView.PROB_LEFT) {
						view.setHigh(view.inverseProbability(value));
					}
					if (view.getProbMode() == ProbabilityCalculatorView.PROB_RIGHT) {
						view.setLow(view.inverseProbability(1 - value));
					}
					updateLowHigh();
					view.setXAxisPoints();
				}
			} else {
				// handle parameter entry
				for (int i = 0; i < view.getParameters().length; ++i) {
					if (source == fldParameterArray[i]) {
						if (view.isValidParameterChange(value, i)) {
							view.getParameters()[i] = numericValue;
							if (intervalCheck) {
								view.updateAll(true);
							} else {
								view.updateOutput(false);
								view.updateLowHighResult();
							}
						}

					}
				}
			}
			if (intervalCheck) {
				view.updateIntervalProbability();
				if (update) {
					updateGUI();
				}
			}
		}
	}

	private void updateLowHigh() {
		getResultPanel().updateLowHigh(view.format(view.getLow()), view.format(view.getHigh()));
	}

	private void checkBounds(GeoNumberValue value, boolean intervalCheck, boolean high) {
		if (high) {
			view.setHigh(value);
		} else {
			view.setLow(value);
		}

		view.setXAxisPoints();

		if (intervalCheck) {
			updateGUI();
			if (view.isTwoTailedMode()) {
				view.updateGreaterSign(getResultPanel());
			}
		} else {
			view.updateIntervalProbability();
			if (view.isTwoTailedMode()) {
				getResultPanel().updateTwoTailedResult(view.getProbabilityText(
						view.getLeftProbability()),
						view.getProbabilityText(view.getRightProbability()));
				getResultPanel().updateResult(view.getProbabilityText(view.getLeftProbability()
						+ view.getRightProbability()));
				view.updateGreaterSign(getResultPanel());
			} else {
				getResultPanel().updateResult(view.getProbabilityText(view.getProbability()));
			}
		}
	}

	public ResultPanelW getResultPanel() {
		return resultPanel;
	}

	@Override
	public void examStateChanged(ExamState newState) {
		if (newState == ExamState.ACTIVE || newState == ExamState.IDLE) {
			distributionDropDown.setProperty(distTypeProperty);
			distributionDropDown.resetFromModel();
		}
	}
}
