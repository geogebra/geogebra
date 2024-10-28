package org.geogebra.common.gui.dialog.options.model;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.EquationForm;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

public class LineEqnModel extends MultipleOptionsModel {

	private List<Integer> eqnValues;

	public LineEqnModel(App app) {
		super(app);

		eqnValues = Arrays.asList(EquationForm.Linear.IMPLICIT.rawValue,
				EquationForm.Linear.EXPLICIT.rawValue, EquationForm.Linear.PARAMETRIC.rawValue,
				EquationForm.Linear.GENERAL.rawValue, EquationForm.Linear.USER.rawValue);

	}

	@Override
	public boolean isValidAt(int index) {
		boolean valid = true;
		GeoElement geo = getGeoAt(index);
		if (forceInputForm(app, geo)) {
			return false;
		}
		if (!(geo instanceof GeoLine) || geo instanceof GeoSegment) {
			valid = false;
		}

		return valid;
	}

	/**
	 * For user equations force input form, for command equations
	 * either don't show them (conic) or force command output (line)
	 * 
	 * @param app
	 *            app
	 * @param geo
	 *            equation
	 * @return whether to force input form
	 */
	public static boolean forceInputForm(App app, GeoElementND geo) {
		// TODO APPS-5867 replace with kernel.getEquationBehaviour()
		boolean isEnforcedLineEquationForm =
				geo instanceof GeoLine && app.getConfig().getEnforcedLineEquationForm() != -1;
		boolean isEnforcedConicEquationForm =
				geo instanceof GeoConicND && app.getConfig().getEnforcedConicEquationForm() != -1;
		boolean isEnforcedEquationForm = isEnforcedLineEquationForm || isEnforcedConicEquationForm;
		boolean isCasDisabled = !app.getSettings().getCasSettings().isEnabled();
		boolean isEquationValue = geo instanceof EquationValue;
		return (isCasDisabled && isEquationValue) && isEnforcedEquationForm;
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
