package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * @author ggb3D
 *
 */
public class AlgoQuadricSide extends AlgoQuadric {

	private boolean isHelperAlgo;

	/**
	 * @param c
	 *            construction
	 * @param inputQuadric
	 */
	public AlgoQuadricSide(Construction c, GeoQuadric3DLimited inputQuadric,
			boolean isHelperAlgo) {
		super(c, inputQuadric, null, new AlgoQuadricComputerSide());

		this.isHelperAlgo = isHelperAlgo;

		setInputOutput(new GeoElement[] { inputQuadric },
				new GeoElement[] { getQuadric() });

		compute();
	}

	public AlgoQuadricSide(Construction c, String label,
			GeoQuadric3DLimited inputQuadric) {

		this(c, inputQuadric, false);
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

		// compute the quadric
		getQuadric().setDefined();
		getQuadric().setType(getInputQuadric().getType());
		getComputer()
				.setQuadric(getQuadric(), getInputQuadric().getOrigin(),
						getInputQuadric().getDirection(),
						getInputQuadric().getRadius());
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
