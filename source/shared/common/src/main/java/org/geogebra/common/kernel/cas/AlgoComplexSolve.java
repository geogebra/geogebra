package org.geogebra.common.kernel.cas;

import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoSymbolic;

/**
 * Algo for CAS commands CSolve and CSolutions
 */
public class AlgoComplexSolve extends AlgoSolve {

	/**
	 * @param c Construction
	 * @param eq Equation or list thereof
	 * @param hint Variables or variable = initial value
	 * @param type Whether to use CSolve/CSolutions
	 */
	public AlgoComplexSolve(Construction c, GeoElement eq, GeoElement hint, Commands type) {
		super(c, eq, hint, type);
	}

	@Override
	protected void convertOutputToSymbolic(GeoList raw) {
		List<GeoSymbolic> geoSymbolics = raw.elements().map(geo -> {
			GeoSymbolic symbolic = new GeoSymbolic(cons);
			symbolic.setDefinition(geo.getDefinition());
			return symbolic;
		}).collect(Collectors.toList());
		raw.clear();
		geoSymbolics.forEach(raw::add);
	}
}
