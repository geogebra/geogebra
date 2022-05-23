package org.geogebra.web.full.gui.view.probcalculator;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.probcalculator.ProbabilityManager;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.util.ProbabilityModeGroup;
import org.geogebra.web.html5.gui.util.ListBoxApi;
import org.geogebra.web.html5.gui.util.ToggleButton;

import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class DistributionPanel extends FlowPanel implements ChangeHandler, InsertHandler {
	private ProbabilityCalculatorViewW view;
	private Localization loc;
	private ListBox comboDistribution;
	private HandlerRegistration comboDistributionHandler;
	private ToggleButton cumulativeWidget;
	private Label[] lblParameterArray;
	private MathTextFieldW[] fldParameterArray;
	protected ProbabilityModeGroup modeGroup;
	protected ResultPanelW resultPanel;

	/**
	 * costructor
	 * @param view - prob calc view
	 * @param loc - localization
	 */
	public DistributionPanel(ProbabilityCalculatorViewW view, Localization loc) {
		this.view = view;
		this.loc = loc;
		addStyleName("distrPanel");
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

	private void buildParameterPanel(FlowPanel parent) {
		initParamFields();

		FlowPanel parameterPanel = new FlowPanel();
		parameterPanel.addStyleName("parameterPanel");
		for (int i = 0; i < view.maxParameterCount; i++) {
			parameterPanel.add(lblParameterArray[i]);
			parameterPanel.add(fldParameterArray[i]);
		}

		parent.add(parameterPanel);
	}

	private void initParamFields() {
		lblParameterArray = new Label[ view.maxParameterCount];
		fldParameterArray = new MathTextFieldW[ view.maxParameterCount];

		for (int i = 0; i < view.maxParameterCount; i++) {
			lblParameterArray[i] = new Label();
			fldParameterArray[i] = new MathTextFieldW(view.getApp());
			fldParameterArray[i].setPxWidth(64);
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
				lblParameterArray[i].setText(view.getParameterLabels()[view.getSelectedDist()
						.ordinal()][i]);
				// set field
				fldParameterArray[i].setText(view.format(view.getParameters()[i]));
			}
		}
	}

	/**
	 * init cumulative widget
	 */
	public void initCumulativeWidget() {
		cumulativeWidget = new ToggleButton(GuiResources.INSTANCE.cumulative_distribution());
		cumulativeWidget.addFastClickHandler((e) ->
				view.setCumulative(cumulativeWidget.isSelected()));
	}

	/**
	 * init and fill the distribution drop-down
	 * @param parent - parent panel
	 */
	public void buildDistrComboBox(FlowPanel parent) {
		comboDistribution = new ListBox();
		comboDistribution.addStyleName("comboDistribution");
		comboDistributionHandler = comboDistribution.addChangeHandler(this);

		setDistributionComboBoxMenu();
		parent.add(comboDistribution);
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (comboDistribution.getSelectedIndex() > -1) {
			view.changeDistribution(comboDistribution);
		}
	}

	/**
	 * update distribution combo-box
	 */
	public void setDistributionComboBoxMenu() {
		comboDistributionHandler.removeHandler();
		comboDistribution.clear();
		comboDistribution.addItem(getDistribution(ProbabilityCalculatorSettings.Dist.NORMAL));
		comboDistribution.addItem(getDistribution(ProbabilityCalculatorSettings.Dist.STUDENT));
		comboDistribution.addItem(getDistribution(ProbabilityCalculatorSettings.Dist.CHISQUARE));
		comboDistribution.addItem(getDistribution(ProbabilityCalculatorSettings.Dist.F));
		comboDistribution.addItem(getDistribution(ProbabilityCalculatorSettings.Dist.EXPONENTIAL));
		comboDistribution.addItem(getDistribution(ProbabilityCalculatorSettings.Dist.CAUCHY));
		comboDistribution.addItem(getDistribution(ProbabilityCalculatorSettings.Dist.WEIBULL));
		comboDistribution.addItem(getDistribution(ProbabilityCalculatorSettings.Dist.GAMMA));
		comboDistribution.addItem(getDistribution(ProbabilityCalculatorSettings.Dist.LOGNORMAL));
		comboDistribution.addItem(getDistribution(ProbabilityCalculatorSettings.Dist.LOGISTIC));

		comboDistribution.addItem(view.SEPARATOR);
		NodeList<OptionElement> options = SelectElement.as(comboDistribution.getElement())
				.getOptions();
		options.getItem(options.getLength() - 1)
				.setAttribute("disabled", "disabled");
		comboDistribution.addItem(getDistribution(ProbabilityCalculatorSettings.Dist.BINOMIAL));
		comboDistribution.addItem(getDistribution(ProbabilityCalculatorSettings.Dist.PASCAL));
		comboDistribution.addItem(getDistribution(ProbabilityCalculatorSettings.Dist.POISSON));
		comboDistribution.addItem(getDistribution(
				ProbabilityCalculatorSettings.Dist.HYPERGEOMETRIC));

		ListBoxApi.select(getDistribution(view.getSelectedDist()),
				comboDistribution);
		comboDistribution.addChangeHandler(this);
	}

	private String getDistribution(ProbabilityCalculatorSettings.Dist dist) {
		return view.getDistributionMap().get(dist);
	}

	/**
	 * update the whole gui
	 */
	public void updateGUI() {
		setDistributionComboBoxMenu();
		updateCumulative();
		updateParameters();
		modeGroup.setMode(view.getProbMode());
	}

	protected void updateCumulative() {
		cumulativeWidget.setSelected(view.isCumulative());
	}

	/**
	 * update translation
	 */
	public void setLabels() {
		setDistributionComboBoxMenu();
		cumulativeWidget.setTitle(loc.getMenu("Cumulative"));
		for (int i = 0; i < ProbabilityManager.getParmCount(view.getSelectedDist()); i++) {
			lblParameterArray[i]
					.setText(view.getParameterLabels()[view.getSelectedDist().ordinal()][i]);
		}
		modeGroup.setLabels();
		resultPanel.setLabels();
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
			if (!Double.isNaN(value)) {
				source.resetError();
			}
			if (getResultPanel().isFieldLow(source)) {
				checkBounds(numericValue, intervalCheck, false);
			}

			else if (getResultPanel().isFieldHigh(source)) {
				checkBounds(numericValue, intervalCheck, true);
			}

			// handle inverse probability
			else if (getResultPanel().isFieldResult(source)) {
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
								view.updateOutput();
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
		boolean valid = high ? view.isValidInterval(view.getLow(), value.getDouble())
				: view.isValidInterval(value.getDouble(), view.getHigh());

		if (valid) {
			if (high) {
				view.setHigh(value);
			} else {
				view.setLow(value);
			}
			view.setXAxisPoints();
		}
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
}
