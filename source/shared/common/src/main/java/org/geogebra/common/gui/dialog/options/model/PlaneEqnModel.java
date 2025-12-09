/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.dialog.options.model;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

public class PlaneEqnModel extends MultipleOptionsModel {

	private List<LinearEquationRepresentable.Form> eqnValues;

	public PlaneEqnModel(App app) {
		super(app);

		eqnValues = Arrays.asList(LinearEquationRepresentable.Form.IMPLICIT,
				LinearEquationRepresentable.Form.USER);
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
		if (geo instanceof LinearEquationRepresentable) {
			if (isUserInput) {
				return equationBehaviour.getLinearAlgebraInputEquationForm() != null
						&& !equationBehaviour.allowsChangingEquationFormsByUser();
			}
		}
		return false;
	}

	public static boolean isValid(GeoElement geo) {
		return (geo instanceof GeoPlaneND) && (geo.getDefinition() != null);
	}

	private GeoPlaneND getLineAt(int index) {
		return (GeoPlaneND) getObjectAt(index);
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
				.setSelectedIndex(equalMode ? value0 : -1);
	}

	@Override
	public List<String> getChoices(Localization loc) {
		return Arrays.asList(loc.getMenu("ImplicitLineEquation"), // index 1
				loc.getMenu("InputForm"));
	}

	@Override
	protected void apply(int index, int value) {
		if (value >= 0) {
			getLineAt(index).setEquationForm(eqnValues.get(value));
			getGeoAt(index).updateRepaint();
		}
	}

	@Override
	public int getValueAt(int index) {
		LinearEquationRepresentable.Form equationForm = getLineAt(index).getEquationForm();
		return equationForm == null ? -1 : eqnValues.indexOf(equationForm);
	}

	@Override
	public String getTitle() {
		return "Equation";
	}

}
