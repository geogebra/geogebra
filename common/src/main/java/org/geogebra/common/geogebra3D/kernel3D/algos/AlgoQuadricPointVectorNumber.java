package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * @author ggb3D
 *
 */
public abstract class AlgoQuadricPointVectorNumber extends
		AlgoQuadricPointNumber {

	/**
	 * @param c
	 *            construction
	 * @param label
	 * @param origin
	 * @param direction
	 * @param r
	 */
	public AlgoQuadricPointVectorNumber(Construction c, String label,
			GeoPointND origin, GeoVectorND direction, NumberValue r,
			AlgoQuadricComputer computer) {
		super(c, label, origin, (GeoElement) direction, r, computer);
	}

	@Override
	protected Coords getDirection() {
		return ((GeoVectorND) getSecondInput()).getCoordsInD3();
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain(getPlainName(), getOrigin().getLabel(tpl),
				getSecondInput().getLabel(tpl), getNumber().getLabel(tpl));

	}

}
