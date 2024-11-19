package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author ggb3D
 *
 */
public class AlgoCylinderAxisRadius extends AlgoQuadric {

	private GeoLineND axis;

	/**
	 * @param c
	 *            construction
	 */
	public AlgoCylinderAxisRadius(Construction c, String label, GeoLineND axis,
			GeoNumberValue r) {
		super(c, axis, r, new AlgoQuadricComputerCylinder());

		this.axis = axis;

		setInputOutput(new GeoElement[] { (GeoElement) axis, (GeoElement) r },
				new GeoElement[] { getQuadric() });
		compute();

		getQuadric().setLabel(label);
	}

	@Override
	public void compute() {

		if (!((GeoElement) axis).isDefined()) {
			getQuadric().setUndefined();
			return;
		}

		Coords o = axis.getPointInD(3, 0).getInhomCoordsInSameDimension();
		Coords d = axis.getPointInD(3, 1).sub(o);

		if (d.equalsForKernel(0, Kernel.STANDARD_PRECISION)) {
			getQuadric().setUndefined();
			return;
		}

		// check number
		double r = getComputer().getNumber(getNumber().getDouble());
		if (Double.isNaN(r)) {
			getQuadric().setUndefined();
			return;
		}

		// compute the quadric
		d.normalize();

		getQuadric().setDefined();

		getQuadric().setCylinder(o, d, null, r, r);

	}

	@Override
	protected Coords getDirection() {
		return axis.getPointInD(3, 1).getInhomCoordsInSameDimension()
				.sub(axis.getPointInD(3, 0).getInhomCoordsInSameDimension());
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("CylinderWithAxisARadiusB",
				axis.getLabel(tpl), getNumber().getLabel(tpl));
	}

	@Override
	public Commands getClassName() {
		return Commands.CylinderInfinite;
	}

}
