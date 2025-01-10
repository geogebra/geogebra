package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author ggb3D
 *
 */
public abstract class AlgoQuadricPointPointNumber
		extends AlgoQuadricPointNumber {

	/**
	 * @param c
	 *            construction
	 * @param label
	 *            output label
	 * @param origin
	 *            base center
	 * @param secondPoint
	 *            second point
	 * @param r
	 *            radius
	 * @param computer
	 *            conic computer
	 */
	public AlgoQuadricPointPointNumber(Construction c, String label,
			GeoPointND origin, GeoPointND secondPoint, GeoNumberValue r,
			AlgoQuadricComputer computer) {
		super(c, label, origin, secondPoint, r, computer);
	}

	@Override
	protected Coords getDirection() {
		return ((GeoPointND) getSecondInput()).getInhomCoordsInD3()
				.sub(getOrigin().getInhomCoordsInD3());
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain(getPlainName(), getOrigin().getLabel(tpl),
				getSecondInput().getLabel(tpl), getNumber().getLabel(tpl));

	}

}
