package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * @author ggb3D
 *
 */
public class AlgoConePointLineAngle extends AlgoQuadricPointNumber {

	/**
	 * @param c
	 *            construction
	 * @param label
	 * @param origin
	 * @param axis
	 * @param angle
	 */
	public AlgoConePointLineAngle(Construction c, String label,
			GeoPointND origin, GeoLineND axis, NumberValue angle) {
		super(c, label, origin, (GeoElement) axis, angle,
				new AlgoQuadricComputerCone());
	}

	@Override
	protected Coords getDirection() {
		GeoLineND axis = (GeoLineND) getSecondInput();
		return axis.getPointInD(3, 1).getInhomCoordsInSameDimension()
				.sub(axis.getPointInD(3, 0).getInhomCoordsInSameDimension());
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain(getPlainName(), getOrigin().getLabel(tpl),
				getSecondInput().getLabel(tpl), getNumber().getLabel(tpl));

	}

	@Override
	final protected String getPlainName() {
		return "ConeWithCenterAAxisParallelToBAngleC";
	}

	@Override
	public Commands getClassName() {
		return Commands.ConeInfinite;
	}

}
