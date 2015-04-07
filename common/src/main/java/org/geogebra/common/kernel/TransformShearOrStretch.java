package org.geogebra.common.kernel;

import org.geogebra.common.kernel.algos.AlgoShearOrStretch;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoVec3D;

/**
 * Shear or stretch
 * 
 * @author kondr
 * 
 */
public class TransformShearOrStretch extends Transform {

	private boolean shear;
	private GeoVec3D line;
	private NumberValue num;

	/**
	 * @param cons
	 *            construction
	 * @param line
	 *            line determining shear/stretch direction
	 * @param num
	 *            shear/stretch ratio
	 * @param shear
	 *            true to shear, false to stretch
	 */
	public TransformShearOrStretch(Construction cons, GeoVec3D line,
			GeoNumeric num, boolean shear) {
		this.shear = shear;
		this.line = line;
		this.num = num;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoShearOrStretch algo = new AlgoShearOrStretch(cons, geo, line, num,
				shear);
		return algo;
	}

	@Override
	public boolean isSimilar() {
		return false;
	}

	@Override
	public boolean changesOrientation() {
		return !shear && num.getDouble() < 0;
	}

}
