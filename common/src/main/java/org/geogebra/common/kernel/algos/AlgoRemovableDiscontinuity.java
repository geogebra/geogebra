package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoFunction;

public class AlgoRemovableDiscontinuity extends AlgoHolesPolynomial {

	public AlgoRemovableDiscontinuity(Construction cons, GeoFunction f, String[] labels) {
		super(cons, f, labels, false, true);
	}

	public AlgoRemovableDiscontinuity(Construction cons, GeoFunction f, String[] labels,
			boolean setLabels) {
		super(cons, f, labels, false, setLabels);
	}

	@Override
	public Commands getClassName() {
		return Commands.RemovableDiscontinuity;
	}
}
