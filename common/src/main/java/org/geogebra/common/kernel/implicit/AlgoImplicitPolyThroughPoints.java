package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Computes implicit polynomial through given points
 *
 */
public class AlgoImplicitPolyThroughPoints extends AlgoElement {
	private GeoList P; // input points
	private GeoImplicit implicitPoly; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param p
	 *            points on polynomial
	 */
	public AlgoImplicitPolyThroughPoints(Construction cons, String label,
			GeoList p) {
		super(cons);
		this.P = p;

		implicitPoly = kernel.newImplicitPoly(cons);

		setInputOutput();
		compute();

		implicitPoly.setLabel(label);
	}

	/**
	 * @return resulting polynomial
	 */
	public GeoImplicit getImplicitPoly() {
		return implicitPoly;
	}

	/**
	 * @return input list of points
	 */
	public GeoList getP() {
		return P;
	}

	@Override
	protected void setInputOutput() {
		input = P.getGeoElements();
		setOnlyOutput(implicitPoly);
		setDependencies();
	}

	@Override
	public void compute() {
		implicitPoly.throughPoints(P);
	}

	@Override
	public Commands getClassName() {
		return Commands.ImplicitCurve;
	}

	

}
