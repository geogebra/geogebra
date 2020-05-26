package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoFunction;

public class AlgoRemovableDiscontinuity extends AlgoHolesPolynomial {

	public AlgoRemovableDiscontinuity(Construction cons, String label, GeoFunction f) {
		super(cons, label, f, false);
	}

	@Override
	public Commands getClassName() {
		return Commands.RemovableDiscontinuity;
	}
}
