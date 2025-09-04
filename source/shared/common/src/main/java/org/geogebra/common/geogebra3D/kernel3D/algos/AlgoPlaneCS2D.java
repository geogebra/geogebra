package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.matrix.CoordSys;

/**
 * Create a plane containing a 2D coord sys
 * 
 * @author ggb3D
 *
 */
public class AlgoPlaneCS2D extends AlgoElement3D {

	/** the 2D coord sys created */
	protected GeoCoordSys2D cs;

	/** polygon */
	private GeoCoordSys2D csInput;

	/**
	 * @param c
	 *            construction
	 * @param csInput
	 *            contained polygon / conic
	 */
	public AlgoPlaneCS2D(Construction c, GeoCoordSys2D csInput) {
		super(c);

		this.csInput = csInput;

		cs = new GeoPlane3D(c);

		// set input and output
		setInputOutput(new GeoElement[] { (GeoElement) csInput },
				new GeoElement[] { (GeoElement) cs });

	}

	@Override
	public void compute() {

		CoordSys coordsys = cs.getCoordSys();

		if (!csInput.isDefined()) {
			coordsys.setUndefined();
			return;
		}

		// copy the coord sys
		coordsys.set(csInput.getCoordSys());

		// recalc equation vector (not existing for polygons, ...)
		if (coordsys.isDefined()) {
			coordsys.makeEquationVector();
		}

	}

	/**
	 * return the cs
	 * 
	 * @return the cs
	 */
	public GeoCoordSys2D getCoordSys() {
		return cs;
	}

	@Override
	public Commands getClassName() {
		return Commands.Plane;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("PlaneContainingA",
				csInput.getLabel(tpl));

	}

}
