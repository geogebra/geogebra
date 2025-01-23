package org.geogebra.common.gui.dialog.options.model;

import java.util.List;

import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoQuadric3DInterface;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;

/**
 * Equation type setting for quadrics and conics
 */
@Deprecated // replace with QuadraticEquationFormProperty
public class ConicEqnModel extends MultipleOptionsModel {

	private Localization loc;
	private int implicitIndex, explicitIndex, specificIndex, parametricIndex,
			userIndex, vertexformIndex, conicformIndex;

	/**
	 * @param app
	 *            application
	 */
	public ConicEqnModel(App app) {
		super(app);
		this.loc = app.getLocalization();
	}

	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		if (forceInputForm(geo)) {
			return false;
		}
		return isValid(geo);
	}

	public static boolean forceInputForm(GeoElementND geo) {
		EquationBehaviour equationBehaviour = geo.getKernel().getEquationBehaviour();
		boolean isUserInput = geo.getParentAlgorithm() == null;
		if (geo instanceof QuadraticEquationRepresentable) {
			if (isUserInput) {
				return equationBehaviour.getConicAlgebraInputEquationForm() != null
						&& !equationBehaviour.allowsChangingEquationFormsByUser();
			}
		}
		return false;
	}

	/**
	 * @param geo
	 *            element
	 * @return whether given geo is a quadric with different equation types
	 */
	public static boolean isValid(GeoElement geo) {
		return geo instanceof GeoConic || geo instanceof GeoQuadric3DInterface;
	}

	private GeoQuadricND getConicAt(int index) {
		return (GeoQuadricND) getObjectAt(index);
	}

	@Override
	public void updateProperties() {
		// check if all conics have same type and mode
		// and if specific, explicit is possible
		GeoQuadricND temp, geo0 = getConicAt(0);
		boolean equalType = true;
		boolean equalMode = true;
		boolean specificPossible = geo0.isSpecificFormPossible();
		boolean explicitPossible = geo0.isExplicitFormPossible();
		boolean userPossible = geo0.getDefinition() != null;
		boolean vertexformPossible = geo0.isVertexFormPossible();
		boolean conicformPossible = geo0.isConicFormPossible();
		for (int i = 1; i < getGeosLength(); i++) {
			temp = getConicAt(i);
			// same type?
			if (geo0.getType() != temp.getType()) {
				equalType = false;
			}
			// same mode?
			if (geo0.getToStringMode() != temp.getToStringMode()) {
				equalMode = false;
			}
			// specific equation possible?
			if (!temp.isSpecificFormPossible()) {
				specificPossible = false;
			}
			// explicit equation possible?
			if (!temp.isExplicitFormPossible()) {
				explicitPossible = false;
			}
			if (temp.getDefinition() == null) {
				userPossible = false;
			}
			if (!temp.isVertexFormPossible()) {
				vertexformPossible = false;
			}
			if (!temp.isConicFormPossible()) {
				conicformPossible = false;
			}
		}

		// specific can't be shown because there are different types
		if (!equalType) {
			specificPossible = false;
		}

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
			getListener().addItem(geo0.getSpecificEquationLabel());
			specificIndex = ++counter;
		}
		if (explicitPossible) {
			getListener().addItem(loc.getMenu("ExplicitConicEquation"));
			explicitIndex = ++counter;
		}
		if (userPossible) {
			getListener().addItem(loc.getMenu("InputForm"));
			userIndex = ++counter;
		}
		implicitIndex = ++counter;
		getListener().addItem(getImplicitEquation(geo0, loc, false));
		if (vertexformPossible) {
			getListener().addItem(loc.getMenu("ParabolaVertexForm"));
			vertexformIndex = ++counter;
		}
		if (conicformPossible) {
			getListener().addItem(loc.getMenu("ParabolaConicForm"));
			conicformIndex = ++counter;
		}
		if (geo0 instanceof GeoConic) {
			getListener().addItem(loc.getMenu("ParametricForm"));
			this.parametricIndex = ++counter;
		}
		int mode;
		if (equalMode) {
			mode = geo0.getToStringMode();
		} else {
			mode = -1;
		}
		switch (mode) {
		case QuadraticEquationRepresentable.Form.CONST_SPECIFIC:
			if (specificIndex > -1) {
				getListener().setSelectedIndex(specificIndex);
			}
			break;

		case QuadraticEquationRepresentable.Form.CONST_EXPLICIT:
			if (explicitIndex > -1) {
				getListener().setSelectedIndex(explicitIndex);
			}
			break;

		case QuadraticEquationRepresentable.Form.CONST_IMPLICIT:
			getListener().setSelectedIndex(implicitIndex);
			break;
		case QuadraticEquationRepresentable.Form.CONST_PARAMETRIC:
			getListener().setSelectedIndex(parametricIndex);
			break;
		case QuadraticEquationRepresentable.Form.CONST_USER:
			getListener().setSelectedIndex(userIndex);
			break;
		case QuadraticEquationRepresentable.Form.CONST_VERTEX:
			if (vertexformIndex > -1) {
				getListener().setSelectedIndex(vertexformIndex);
			}
			break;
		case QuadraticEquationRepresentable.Form.CONST_CONICFORM:
			if (conicformIndex > -1) {
				getListener().setSelectedIndex(conicformIndex);
			}
			break;

		default:
			getListener().setSelectedIndex(-1);
		}
	}

	/**
	 * @param geo0
	 *            element
	 * @param loc2
	 *            localization
	 * @param prefix
	 *            whether to add "Equation"
	 * @return for quadrics "Expanded Form", for conics "Equation? a x^2 + ... "
	 */
	public static String getImplicitEquation(GeoQuadricND geo0,
			Localization loc2, boolean prefix) {
		if (geo0 instanceof GeoQuadric3DInterface) {
			return loc2.getMenu("ExpandedForm");
		}
		return (prefix ? loc2.getMenu("Equation") + ' ' : "")
						+ loc2.getMenu("ImplicitConicEquation");
	}

	@Override
	public List<String> getChoices(Localization localization) {
		// Not used
		return null;
	}

	@Override
	protected void apply(int index, int value) {
		GeoQuadricND quad = getConicAt(index);
		Log.debug(value + ":" + parametricIndex);
		if (value == specificIndex) {
			quad.setToSpecificForm();
		} else if (value == explicitIndex) {
			quad.setToExplicit();
		} else if (value == implicitIndex) {
			quad.setToImplicitForm();
		} else if (value == userIndex) {
			quad.setToUserForm();
		} else if (value == parametricIndex) {
			quad.setToParametricForm(null);
		} else if (value == vertexformIndex) {
			quad.setToVertexForm();
		} else if (value == conicformIndex) {
			quad.setToConicForm();
		}
		quad.updateRepaint();
	}

	@Override
	public int getValueAt(int index) {
		// Not used
		return 0;
	}

}
