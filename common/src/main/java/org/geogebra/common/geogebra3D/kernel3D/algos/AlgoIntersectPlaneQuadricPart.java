package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

public class AlgoIntersectPlaneQuadricPart
		extends AlgoIntersectPlaneQuadricLimited {

	private GeoConicND bottom;
	private GeoConicND top;

	private AlgoQuadricEnds algoEnds = null;

	public AlgoIntersectPlaneQuadricPart(Construction cons, GeoPlane3D plane,
			GeoQuadricND quadric) {
		super(cons, plane, quadric);
	}

	@Override
	protected void end() {

		if (quadric.getParentAlgorithm() instanceof AlgoQuadricSide) {
			// use quadric limited parent ends
			GeoQuadric3DLimited parent = ((AlgoQuadricSide) quadric
					.getParentAlgorithm()).getInputQuadric();
			bottom = parent.getBottom();
			top = parent.getTop();
		} else {
			algoEnds = new AlgoQuadricEnds(cons, quadric);
			cons.removeFromConstructionList(algoEnds);
			bottom = algoEnds.getSection1();
			top = algoEnds.getSection2();

		}

		super.end();
	}

	@Override
	public void compute() {

		if (algoEnds != null) {
			algoEnds.compute();
		}

		super.compute();
	}

	@Override
	protected GeoConicND getBottom() {
		return bottom;
	}

	@Override
	protected GeoConicND getTop() {
		return top;
	}

	@Override
	protected GeoQuadric3DPart getSide() {
		return (GeoQuadric3DPart) quadric;
	}

	@Override
	protected double getBottomParameter() {
		return ((GeoQuadric3DPart) quadric).getBottomParameter();
	}

	@Override
	protected double getTopParameter() {
		return ((GeoQuadric3DPart) quadric).getTopParameter();
	}

}
