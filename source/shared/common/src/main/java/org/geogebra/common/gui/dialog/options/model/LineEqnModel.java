package org.geogebra.common.gui.dialog.options.model;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.statistics.AlgoFitLineX;
import org.geogebra.common.kernel.statistics.AlgoFitLineY;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

public class LineEqnModel extends MultipleOptionsModel {

	private List<Integer> eqnValues;

	public LineEqnModel(App app) {
		super(app);

		eqnValues = Arrays.asList(LinearEquationRepresentable.Form.IMPLICIT.rawValue,
				LinearEquationRepresentable.Form.EXPLICIT.rawValue, LinearEquationRepresentable.Form.PARAMETRIC.rawValue,
				LinearEquationRepresentable.Form.GENERAL.rawValue, LinearEquationRepresentable.Form.USER.rawValue);

	}

	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		if (forceInputForm(geo)) {
			return false;
		}
		return isValid(geo);
	}

	/**
	 * For user equations force input form, for command equations
	 * either don't show them (conic) or force command output (line)
	 * 
	 * @param geo
	 *            equation
	 * @return whether to force input form
	 */
	public static boolean forceInputForm(GeoElementND geo) {
		EquationBehaviour equationBehaviour = geo.getKernel().getEquationBehaviour();
		if (geo instanceof LinearEquationRepresentable) {
			boolean isUserInput = geo.getParentAlgorithm() == null;
			if (isUserInput) {
				return equationBehaviour.getLinearAlgebraInputEquationForm() != null
						&& !equationBehaviour.allowsChangingEquationFormsByUser();
			}
			if (geo instanceof GeoLine) {
				AlgoElement algo = geo.getParentAlgorithm();
				boolean isFitLineOutput = (algo instanceof AlgoFitLineX)
						|| (algo instanceof AlgoFitLineY);
				if (isFitLineOutput) {
					return equationBehaviour.getFitLineCommandEquationForm() != null
							&& !equationBehaviour.allowsChangingEquationFormsByUser();
				}
				return equationBehaviour.getLineCommandEquationForm() != null
						&& !equationBehaviour.allowsChangingEquationFormsByUser();
			}
		}
		return false;
	}

	public static boolean isValid(GeoElement geo) {
		if (geo instanceof GeoSegment) {
			return false;
		}
		return geo instanceof GeoLine;
	}

	private GeoLine getLineAt(int index) {
		return (GeoLine) getObjectAt(index);
	}

	@Override
	public void updateProperties() {
		int value0 = getValueAt(0);
		boolean equalMode = true;
		for (int i = 0; i < getGeosLength(); i++) {
			if (getValueAt(i) != value0) {
				equalMode = false;
			}
		}

		getListener()
				.setSelectedIndex(equalMode ? eqnValues.indexOf(value0) : -1);
	}

	@Override
	public List<String> getChoices(Localization loc) {

		return Arrays.asList(loc.getMenu("ImplicitLineEquation"), // index 0
				loc.getMenu("ExplicitLineEquation"), // index 1
				loc.getMenu("ParametricForm"), // index 2
				loc.getMenu("GeneralLineEquation"), // index 3
				loc.getMenu("InputForm"));

	}

	@Override
	protected void apply(int index, int value) {
		getLineAt(index).setMode(eqnValues.get(value));
		getGeoAt(index).updateRepaint();

	}

	@Override
	public int getValueAt(int index) {
		return getLineAt(index).getToStringMode();
	}

}
