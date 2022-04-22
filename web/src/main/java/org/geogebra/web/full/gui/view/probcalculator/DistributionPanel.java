package org.geogebra.web.full.gui.view.probcalculator;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.util.ToggleButton;
import org.geogebra.web.html5.gui.util.ListBoxApi;

import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class DistributionPanel extends FlowPanel implements ChangeHandler {
	private ProbabilityCalculatorViewW view;
	private Localization loc;
	private ListBox comboDistribution;
	private HandlerRegistration comboDistributionHandler;
	private Widget cumulativeWidget;

	public DistributionPanel(ProbabilityCalculatorViewW view, Localization loc) {
		this.view = view;
		this.loc = loc;
		buildCumulativeWidget();
		buildDistrComboBox();
	}

	public void buildCumulativeWidget() {
		cumulativeWidget = new ToggleButton(GuiResources.INSTANCE.cumulative_distribution());
		((ToggleButton) cumulativeWidget).addFastClickHandler((e) ->
				view.setCumulative(((ToggleButton) cumulativeWidget).isSelected()));
		add(cumulativeWidget);
	}

	private void buildDistrComboBox() {
		comboDistribution = new ListBox();
		comboDistribution.addStyleName("comboDistribution");
		comboDistributionHandler = comboDistribution.addChangeHandler(this);

		setDistributionComboBoxMenu();

		add(comboDistribution);
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (comboDistribution.getSelectedIndex() > -1) {
			view.changeDistribution(comboDistribution);
		}
	}

	private void setDistributionComboBoxMenu() {
		comboDistributionHandler.removeHandler();
		comboDistribution.clear();
		comboDistribution.addItem(view.getDistributionMap().get(ProbabilityCalculatorSettings.Dist.NORMAL));
		comboDistribution.addItem(view.getDistributionMap().get(ProbabilityCalculatorSettings.Dist.STUDENT));
		comboDistribution.addItem(view.getDistributionMap().get(ProbabilityCalculatorSettings.Dist.CHISQUARE));
		comboDistribution.addItem(view.getDistributionMap().get(ProbabilityCalculatorSettings.Dist.F));
		comboDistribution.addItem(view.getDistributionMap().get(ProbabilityCalculatorSettings.Dist.EXPONENTIAL));
		comboDistribution.addItem(view.getDistributionMap().get(ProbabilityCalculatorSettings.Dist.CAUCHY));
		comboDistribution.addItem(view.getDistributionMap().get(ProbabilityCalculatorSettings.Dist.WEIBULL));
		comboDistribution.addItem(view.getDistributionMap().get(ProbabilityCalculatorSettings.Dist.GAMMA));
		comboDistribution.addItem(view.getDistributionMap().get(ProbabilityCalculatorSettings.Dist.LOGNORMAL));
		comboDistribution.addItem(view.getDistributionMap().get(ProbabilityCalculatorSettings.Dist.LOGISTIC));

		comboDistribution.addItem(view.SEPARATOR);
		NodeList<OptionElement> options = SelectElement.as(comboDistribution.getElement())
				.getOptions();
		options.getItem(options.getLength() - 1)
				.setAttribute("disabled", "disabled");
		comboDistribution.addItem(view.getDistributionMap().get(ProbabilityCalculatorSettings.Dist.BINOMIAL));
		comboDistribution.addItem(view.getDistributionMap().get(ProbabilityCalculatorSettings.Dist.PASCAL));
		comboDistribution.addItem(view.getDistributionMap().get(ProbabilityCalculatorSettings.Dist.POISSON));
		comboDistribution.addItem(view.getDistributionMap().get(ProbabilityCalculatorSettings.Dist.HYPERGEOMETRIC));

		ListBoxApi.select(view.getDistributionMap().get(view.getSelectedDist()),
				comboDistribution);
		comboDistribution.addChangeHandler(this);
	}


	public void updateGUI() {
		setDistributionComboBoxMenu();
		((ToggleButton) cumulativeWidget).setSelected(view.isCumulative());
	}

	public void setLabels() {
		setDistributionComboBoxMenu();
		cumulativeWidget.setTitle(loc.getMenu("Cumulative"));
	}
}
