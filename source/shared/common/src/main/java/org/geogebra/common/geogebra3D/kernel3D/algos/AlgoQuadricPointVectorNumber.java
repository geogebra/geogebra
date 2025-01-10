package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author ggb3D
 *
 */
public abstract class AlgoQuadricPointVectorNumber
		extends AlgoQuadricPointNumber {

	/**
	 * @param c
	 *            construction
	 * @param label
	 *            output label
	 * @param origin
	 *            bottom center
	 * @param direction
	 *            direction
	 * @param r
	 *            cylinder radius or cone angle
	 * @param computer
	 *            quadric computer
	 */
	public AlgoQuadricPointVectorNumber(Construction c, String label,
			GeoPointND origin, GeoVectorND direction, GeoNumberValue r,
			AlgoQuadricComputer computer) {
		super(c, label, origin, direction, r, computer);
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
