package org.geogebra.common.properties.impl.distribution;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.geogebra.common.gui.view.probcalculator.ResultPanel;
import org.geogebra.common.gui.view.probcalculator.model.resultpanel.AbstractResultModel;
import org.geogebra.common.gui.view.probcalculator.model.resultpanel.IntervalResultModel;
import org.geogebra.common.gui.view.probcalculator.model.resultpanel.LeftResultModel;
import org.geogebra.common.gui.view.probcalculator.model.resultpanel.RightResultModel;
import org.geogebra.common.gui.view.probcalculator.model.resultpanel.TwoTailedResultModel;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractProperty;

public class ProbabilityResultProperty extends AbstractProperty implements ResultPanel {

	private AbstractResultModel model;

	private Map<Class, AbstractResultModel> modelMap;

	/**
	 * Constructs an abstract property.
	 * @param localization this is used to localize the name
	 * @param name the name to be localized
	 */
	public ProbabilityResultProperty(Localization localization, String name) {
		super(localization, name);
		modelMap = new HashMap<>();
	}

	public AbstractResultModel getModel() {
		return model;
	}

	@Override
	public void showInterval() {
		model = getOrCreateModel(
				IntervalResultModel.class, () -> new IntervalResultModel(getLocalization()));
	}

	private <T extends AbstractResultModel> T getOrCreateModel(
			Class<T> modelClass, Supplier<T> modelSupplier) {

		T model = (T) modelMap.get(modelClass);
		if (model == null) {
			model = modelSupplier.get();
			modelMap.put(modelClass, model);
		}
		return model;
	}

	@Override
	public void showTwoTailed() {
		model = getOrCreateModel(
				TwoTailedResultModel.class, () -> new TwoTailedResultModel(getLocalization()));
	}

	@Override
	public void showTwoTailedOnePoint() {
		showTwoTailed();
		TwoTailedResultModel model = (TwoTailedResultModel) this.model;
		model.setGreaterThan();
	}

	@Override
	public void showLeft() {
		model = getOrCreateModel(
				LeftResultModel.class, () -> new LeftResultModel(getLocalization()));
	}

	@Override
	public void showRight() {
		model = getOrCreateModel(
				RightResultModel.class, () -> new RightResultModel(getLocalization()));
	}

	@Override
	public void setResultEditable(boolean value) {
		// models know whether they should have editable results
	}

	@Override
	public void updateResult(String text) {
		getModel().updateResult(text);
	}

	@Override
	public void updateLowHigh(String low, String high) {
		AbstractResultModel model = getModel();
		model.updateLow(low);
		model.updateHigh(high);
	}

	@Override
	public void updateTwoTailedResult(String low, String high) {
		// the model updates the two tailed result when values change
	}

	@Override
	public boolean isFieldLow(Object source) {
		return false;
	}

	@Override
	public boolean isFieldHigh(Object source) {
		return false;
	}

	@Override
	public boolean isFieldResult(Object source) {
		return false;
	}

	@Override
	public void setGreaterThan() {
		TwoTailedResultModel model = (TwoTailedResultModel) getModel();
		model.setGreaterThan();
	}

	@Override
	public void setGreaterOrEqualThan() {
		TwoTailedResultModel model = (TwoTailedResultModel) getModel();
		model.setGreaterThanOrEqualTo();
	}
}
