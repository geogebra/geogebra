package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * angle for three points, oriented
 * 
 * @author mathieu
 */
public class AlgoAnglePoints3DOrientation extends AlgoAnglePoints3D {

	private GeoDirectionND orientation;

	AlgoAnglePoints3DOrientation(Construction cons, String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation) {
		super(cons, label, A, B, C, orientation);
	}

	public AlgoAnglePoints3DOrientation(Construction cons,
			GeoDirectionND orientation) {
		super(cons);
		this.orientation = orientation;
	}

	@Override
	protected void setInput(GeoPointND A, GeoPointND B, GeoPointND C,
			GeoDirectionND orientation) {

		super.setInput(A, B, C, orientation);
		this.orientation = orientation;
	}

	@Override
	protected GeoAngle newGeoAngle(Construction cons1) {
		return GeoAngle3D.newAngle3DWithDefaultInterval(cons1);
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[4];
		input[0] = (GeoElement) getA();
		input[1] = (GeoElement) getB();
		input[2] = (GeoElement) getC();
		input[3] = (GeoElement) orientation;

		setOutputLength(1);
		setOutput(0, getAngle());
		setDependencies(); // done by AlgoElement
	}

	@Override
	public void compute() {

		super.compute();

		if (orientation == kernel.getSpace()) { // no orientation with space
			return;
		}

		if (!getAngle().isDefined() || Kernel.isZero(getAngle().getValue())) {
			return;
		}
		
		checkOrientation(vn, orientation, getAngle());
	}

	@Override
	protected void setForceNormalVector() {
		vn = v1.crossProduct4(v2);

		if (vn.isZero()) { // v1 and v2 are dependent
			if (orientation == kernel.getSpace()) { // no orientation with space
				vn = crossXorY(v1);
			} else {
				vn = orientation.getDirectionInD3().copyVector();
			}
		}

		vn.normalize();

	}

	@Override
	public String toString(StringTemplate tpl) {

		return getLoc().getPlain("AngleBetweenABC", getA().getLabel(tpl),
				getB().getLabel(tpl), getC().getLabel(tpl));
	}

}
