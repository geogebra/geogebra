package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * @author ggb3D
 *
 */
public abstract class AlgoQuadricPointNumber extends AlgoQuadric {

	private GeoPointND origin;

	/**
	 * @param c
	 *            construction
	 */
	public AlgoQuadricPointNumber(Construction c, String label,
			GeoPointND origin, GeoElement secondInput, NumberValue r,
			AlgoQuadricComputer computer) {
		super(c, secondInput, r, computer);

		this.origin = origin;

		setInputOutput(new GeoElement[] { (GeoElement) origin, secondInput,
				(GeoElement) r }, new GeoElement[] { getQuadric() });

		compute();

		getQuadric().setLabel(label);
	}

	@Override
	public void compute() {

		// check origin
		if (!((GeoElement) origin).isDefined() || origin.isInfinite()) {
			getQuadric().setUndefined();
			return;
		}

		// check direction
		Coords d = getDirection();

		if (d.equalsForKernel(0, Kernel.STANDARD_PRECISION)) {
			getQuadric().setUndefined();
			return;
		}

		// check number
		double r = getComputer().getNumber(
				((NumberValue) getNumber()).getDouble());
		if (Double.isNaN(r)) {
			getQuadric().setUndefined();
			return;
		}

		// compute the quadric
		getQuadric().setDefined();
		getComputer().setQuadric(getQuadric(), origin.getInhomCoordsInD3(), d,
				r);

	}

	/**
	 * 
	 * @return origin point
	 */
	protected GeoPointND getOrigin() {
		return origin;
	}

	/**
	 * 
	 * @return plain name
	 */
	abstract protected String getPlainName();

}
