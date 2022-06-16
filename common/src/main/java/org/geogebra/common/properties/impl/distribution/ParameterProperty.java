package org.geogebra.common.properties.impl.distribution;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.properties.impl.AbstractNumericProperty;

/**
 * The parameter property of the distribution view.
 */
public class ParameterProperty extends AbstractNumericProperty {

	private final ProbabilityCalculatorView view;
	private final String localizedName;
	private final int parameterIndex;

	/**
	 * @param algebraProcessor processor
	 * @param view view
	 * @param parameterIndex index of the parameter
	 * @param localizedName localized name of the parameter
	 */
	public ParameterProperty(AlgebraProcessor algebraProcessor, ProbabilityCalculatorView view,
			int parameterIndex, String localizedName) {
		super(algebraProcessor, null, null);
		this.view = view;
		this.parameterIndex = parameterIndex;
		this.localizedName = localizedName;
	}

	@Override
	public String getName() {
		return localizedName;
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		if (view.isValidParameterChange(value.getDouble(), parameterIndex)) {
			GeoNumberValue[] parameters = view.getParameters();
			parameters[parameterIndex] = value;
			view.setProbabilityCalculator(view.getSelectedDist(), parameters, view.isCumulative());
			view.updateResult();
		}
	}

	@Override
	protected NumberValue getNumberValue() {
		return view.getParameters()[parameterIndex];
	}
}
