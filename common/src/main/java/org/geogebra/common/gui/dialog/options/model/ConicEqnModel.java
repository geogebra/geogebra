package org.geogebra.common.gui.dialog.options.model;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.main.Localization;

public class ConicEqnModel extends MultipleOptionsModel {


	private Localization loc;
	int implicitIndex, explicitIndex, specificIndex;

	public ConicEqnModel(Localization loc) {
		this.loc = loc;
	}

	@Override
	public boolean isValidAt(int index) {
		return (getObjectAt(index) instanceof GeoConic);
	}

	private GeoConic getConicAt(int index) {
		return (GeoConic)getObjectAt(index);
	}

	@Override
	public void updateProperties() {
		// check if all conics have same type and mode
		// and if specific, explicit is possible
		GeoConic temp, geo0 = getConicAt(0);
		boolean equalType = true;
		boolean equalMode = true;
		boolean specificPossible = geo0.isSpecificPossible();
		boolean explicitPossible = geo0.isExplicitPossible();
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
		}

		// specific can't be shown because there are different types
		if (!equalType)
			specificPossible = false;

		specificIndex = -1;
		explicitIndex = -1;
		implicitIndex = -1;
		int counter = -1;
		if (specificPossible) {
			getListener().addItem(geo0.getSpecificEquation());
			specificIndex = ++counter;
		}
		if (explicitPossible) {
			getListener().addItem(loc.getPlain("ExplicitConicEquation"));
			explicitIndex = ++counter;
		}
		implicitIndex = ++counter;
		getListener().addItem(loc.getPlain("ImplicitConicEquation"));

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
		GeoConic geo = getConicAt(index);
		if (value == specificIndex) {
			geo.setToSpecific();
		} else if (value == explicitIndex) {
			geo.setToExplicit();
		} else if (value == implicitIndex) {
			geo.setToImplicit();
		}
		geo.updateRepaint();
	}

	@Override
	public int getValueAt(int index) {
		// Not used
		return 0;
	}

}
