package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * Extension used for extrusion
 * 
 * @author Mathieu
 *
 */
public class AlgoQuadricLimitedConicHeightConeForExtrusion
		extends AlgoQuadricLimitedConicHeightCone implements AlgoForExtrusion {

	private ExtrusionComputer extrusionComputer;

	/**
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            labels
	 * @param bottom
	 *            bottom side
	 * @param height
	 *            height
	 */
	public AlgoQuadricLimitedConicHeightConeForExtrusion(Construction c,
			String[] labels, GeoConicND bottom, GeoNumberValue height) {
		super(c, labels, bottom, height);
	}

	@Override
	public void setExtrusionComputer(ExtrusionComputer extrusionComputer) {
		this.extrusionComputer = extrusionComputer;
	}

	@Override
	public void compute() {
		super.compute();
		if (extrusionComputer != null) {
			extrusionComputer.compute();
		}
	}

	@Override
	public GeoElement getGeoToHandle() {
		return getTopFace();
	}

	@Override
	public void setOutputPointsEuclidianVisible(boolean b) {
		super.setOutputPointsEuclidianVisible(b);
	}
}
