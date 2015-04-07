package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * @author ggb3D
 *
 */
public abstract class AlgoQuadricPointPointNumber extends
		AlgoQuadricPointNumber {

	/**
	 * @param c
	 *            construction
	 * @param label
	 * @param origin
	 * @param secondPoint
	 * @param r
	 * @param computer
	 */
	public AlgoQuadricPointPointNumber(Construction c, String label,
			GeoPointND origin, GeoPointND secondPoint, NumberValue r,
			AlgoQuadricComputer computer) {
		super(c, label, origin, (GeoElement) secondPoint, r, computer);
	}

	@Override
	protected Coords getDirection() {
		return ((GeoPointND) getSecondInput()).getInhomCoordsInD3().sub(
				getOrigin().getInhomCoordsInD3());
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain(getPlainName(), getOrigin().getLabel(tpl),
				getSecondInput().getLabel(tpl), getNumber().getLabel(tpl));

	}

}
