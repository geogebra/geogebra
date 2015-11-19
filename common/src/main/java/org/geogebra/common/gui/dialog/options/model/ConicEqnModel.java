package org.geogebra.common.gui.dialog.options.model;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.main.Localization;

public class ConicEqnModel extends MultipleOptionsModel {


	private Localization loc;
	int implicitIndex, explicitIndex, specificIndex, parametricIndex,
			userIndex;

	public ConicEqnModel(Localization loc) {
		this.loc = loc;
	}

	@Override
	public boolean isValidAt(int index) {
		return (getObjectAt(index) instanceof GeoConic);
	}

	private GeoConicND getConicAt(int index) {
		return (GeoConicND) getObjectAt(index);
	}

	@Override
	public void updateProperties() {
		// check if all conics have same type and mode
		// and if specific, explicit is possible
		GeoConicND temp, geo0 = getConicAt(0);
		boolean equalType = true;
		boolean equalMode = true;
		boolean specificPossible = geo0.isSpecificPossible();
		boolean explicitPossible = geo0.isExplicitPossible();
		boolean userPossible = geo0.getDefinition() != null;
		for (int i = 1; i < getGeosLength(); i++) {
			temp = getConicAt(i);
			// same type?
			if (geo0.getType() != temp.getType())
				equalType = false;
			// same mode?
			if (geo0.getToStringMode() != temp.getToStringMode())
				equalMode = false;
			// specific equation possible?
			if (!temp.isSpecificPossible())
				specificPossible = false;
			// explicit equation possible?
			if (!temp.isExplicitPossible())
				explicitPossible = false;
			if (temp.getDefinition() == null) {
				userPossible = false;
			}
		}

		// specific can't be shown because there are different types
		if (!equalType)
			specificPossible = false;

		specificIndex = -1;
		explicitIndex = -1;
		implicitIndex = -1;
		userIndex = -1;
		parametricIndex = -1;
		int counter = -1;
		getListener().clearItems();
		if (specificPossible) {
			getListener().addItem(geo0
					.getSpecificEquation());
			specificIndex = ++counter;
		}
		if (explicitPossible) {
			getListener().addItem(loc
					.getPlain("ExplicitConicEquation"));
			explicitIndex = ++counter;
		}
		if (userPossible) {
			getListener().addItem(loc
					.getPlain("InputForm"));
			userIndex = ++counter;
		}
		implicitIndex = ++counter;
		getListener().addItem(loc
				.getPlain("ImplicitConicEquation"));

		int mode;
		if (equalMode)
			mode = geo0.getToStringMode();
		else
			mode = -1;
		switch (mode) {
		case GeoConicND.EQUATION_SPECIFIC:
			if (specificIndex > -1)
				getListener().setSelectedIndex(specificIndex);
			break;

		case GeoConicND.EQUATION_EXPLICIT:
			if (explicitIndex > -1)
				getListener().setSelectedIndex(explicitIndex);
			break;

		case GeoConicND.EQUATION_IMPLICIT:
			getListener().setSelectedIndex(implicitIndex);
			break;
		case GeoConicND.EQUATION_PARAMETRIC:
			getListener().setSelectedIndex(parametricIndex);
			break;
		case GeoConicND.EQUATION_USER:
			getListener().setSelectedIndex(userIndex);
			break;

		default:
			getListener().setSelectedIndex(-1);
		}
	}
	
	@Override
	public List<String> getChoiches(Localization loc) {
		// Not used
		return null;
	}

	@Override
	protected void apply(int index, int value) {
		GeoConicND geo = getConicAt(index);
		if (value == specificIndex) {
			geo.setToSpecific();
		} else if (value == explicitIndex) {
			geo.setToExplicit();
		} else if (value == implicitIndex) {
			geo.setToImplicit();
		} else if (value == userIndex) {
			geo.setToUser();
		} else if (value == parametricIndex) {
			geo.setToParametric();
		}

		geo.updateRepaint();
	}

	@Override
	public int getValueAt(int index) {
		// Not used
		return 0;
	}

}
