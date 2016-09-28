package org.geogebra.common.gui.dialog.options.model;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;

public class ConicEqnModel extends MultipleOptionsModel {


	private Localization loc;
	int implicitIndex, explicitIndex, specificIndex, parametricIndex,
			userIndex, vertexformIndex, conicformIndex;

	public ConicEqnModel(App app) {
		super(app);
		this.loc = app.getLocalization();
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
		boolean vertexformPossible = geo0.isVertexformPossible();
		boolean conicformPossible = geo0.isConicformPossible();
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
			if (!temp.isVertexformPossible()) {
				vertexformPossible = false;
			}
			if (!temp.isConicformPossible()) {
				conicformPossible = false;
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
		vertexformIndex = -1;
		conicformIndex = -1;
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
		if (vertexformPossible && app.has(Feature.MORE_DISPLAY_FORMS)) {
			getListener().addItem(loc.getPlain("ParabolaVertexForm"));
			vertexformIndex = ++counter;
		}
		if (conicformPossible && app.has(Feature.MORE_DISPLAY_FORMS)) {
			getListener().addItem(loc.getPlain("ParabolaConicForm"));
			conicformIndex = ++counter;
		}

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
		case GeoConicND.EQUATION_VERTEX:
			if (vertexformIndex > -1)
				getListener().setSelectedIndex(vertexformIndex);
			break;
		case GeoConicND.EQUATION_CONICFORM:
			if (conicformIndex > -1)
				getListener().setSelectedIndex(conicformIndex);
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
		} else if (value == vertexformIndex) {
			geo.setToVertexform();
		} else if (value == conicformIndex) {
			geo.setToConicform();
		}

		geo.updateRepaint();
	}

	@Override
	public int getValueAt(int index) {
		// Not used
		return 0;
	}

}
