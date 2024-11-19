package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author ggb3D
 *
 */
public class AlgoConePointLineAngle extends AlgoQuadricPointNumber {

	/**
	 * @param c
	 *            construction
	 * @param label
	 *            output label
	 * @param origin
	 *            vertex
	 * @param axis
	 *            axis
	 * @param angle
	 *            angle
	 */
	public AlgoConePointLineAngle(Construction c, String label,
			GeoPointND origin, GeoLineND axis, GeoNumberValue angle) {
		super(c, label, origin, axis, angle,
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
