package org.geogebra.common.properties.impl.distribution;

import javax.annotation.Nullable;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;

/**
 * The parameter property of the distribution view.
 */
public class ParameterProperty extends AbstractNumericProperty {

	private final ProbabilityCalculatorView view;
	private final String localizedName;
	private final int parameterIndex;

	/**
	 * @param localization localization
	 * @param algebraProcessor processor
	 * @param view view
	 * @param parameterIndex index of the parameter
	 * @param localizedName localized name of the parameter
	 */
	public ParameterProperty(Localization localization, AlgebraProcessor algebraProcessor,
			ProbabilityCalculatorView view, int parameterIndex,
			String localizedName) {
		super(algebraProcessor, localization, localizedName);
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
		if (view.getParameters().length > parameterIndex
				&& view.isValidParameterChange(value.getDouble(), parameterIndex)) {
			GeoNumberValue[] parameters = view.getParameters();
			parameters[parameterIndex] = value;
			view.onParameterUpdate();
		}
	}

	@Override
	protected NumberValue getNumberValue() {
		if (view.getParameters().length > parameterIndex) {
			return view.getParameters()[parameterIndex];
		}
		return parseNumberValue("0");
	}

	@Nullable
	@Override
	public String validateValue(String value) {
		if (super.validateValue(value) != null) {
			return "";
		}
		return null;
	}
}
