package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;

/**
 * Extension of algo when used for extrusion
 * 
 * @author Mathieu
 *
 */
public class AlgoPolyhedronPointsPyramidForExtrusion
		extends AlgoPolyhedronPointsPyramid implements AlgoForExtrusion {

	private ExtrusionComputer extrusionComputer;

	/**
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            labels
	 * @param polygon
	 *            polygon
	 * @param height
	 *            height
	 */
	public AlgoPolyhedronPointsPyramidForExtrusion(Construction c,
			String[] labels, GeoPolygon polygon, NumberValue height) {
		super(c, labels, polygon, height);
	}

	/**
	 * sets the extrusion computer
	 * 
	 * @param extrusionComputer
	 *            extrusion computer
	 */
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
		return outputPolygonsSide.getElement(0);
	}

	@Override
	public void setOutputPointsEuclidianVisible(boolean visible) {
		super.setOutputPointsEuclidianVisible(visible);
	}
}
