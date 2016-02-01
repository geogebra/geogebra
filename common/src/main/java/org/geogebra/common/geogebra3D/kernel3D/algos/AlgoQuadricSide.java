package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * @author ggb3D
 *
 */
public class AlgoQuadricSide extends AlgoQuadric {

	private boolean isHelperAlgo;
	private GeoConicND bottom;

	/**
	 * @param c
	 *            construction
	 * @param inputQuadric
	 */
	public AlgoQuadricSide(Construction c, GeoQuadric3DLimited inputQuadric,
			boolean isHelperAlgo, GeoConicND bottom) {
		super(c, inputQuadric, null, new AlgoQuadricComputerSide());

		this.isHelperAlgo = isHelperAlgo;
		this.bottom = bottom;
		setInputOutput(new GeoElement[] { inputQuadric },
				new GeoElement[] { getQuadric() });

		compute();
	}

	public AlgoQuadricSide(Construction c, String label,
			GeoQuadric3DLimited inputQuadric) {

		this(c, inputQuadric, false, null);
		getQuadric().setLabel(label);
	}

	public GeoQuadric3DLimited getInputQuadric() {
		return (GeoQuadric3DLimited) getSecondInput();
	}

	@Override
	public void compute() {

		// check origin
		if (!getInputQuadric().isDefined()) {
			getQuadric().setUndefined();
			return;
		}
		double r1 = getInputQuadric().getRadius(), r2;
		Coords eigen = null;
		if (bottom == null) {
			r2 = r1;
		} else {
			r2 = r1 * bottom.getHalfAxis(1) / bottom.getHalfAxis(0);
			eigen = bottom.getEigenvec3D(0).normalize();
		}
		// compute the quadric
		getQuadric().setDefined();
		getQuadric().setType(getInputQuadric().getType());
		getComputer()
				.setQuadric(getQuadric(), getInputQuadric().getOrigin(),
				getInputQuadric().getDirection(), eigen,
 r1, r2);
		((GeoQuadric3DPart) getQuadric()).setLimits(getInputQuadric()
				.getBottomParameter(), getInputQuadric().getTopParameter());

		((GeoQuadric3DPart) getQuadric()).calcArea();

	}

	@Override
	public void remove() {
		if (removed)
			return;
		super.remove();
		if (isHelperAlgo)
			getInputQuadric().remove();
	}

	@Override
	protected Coords getDirection() {
		return null;
	}

	/*
	 * final public String toString() { return
	 * loc.getPlain("SideOfABetweenBC",((GeoElement)
	 * getInputQuadric()).getLabel(),point.getLabel(),pointThrough.getLabel());
	 * }
	 */

	@Override
	public Commands getClassName() {
		return Commands.QuadricSide;
	}

}
