package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * @author Victor Franco Espino
 * @version 11-02-2007
 * 
 *          This class calculate affine ratio of 3 points: (A,B,C) = (t(C)-t(A))
 *          : (t(C)-t(B))
 */

public class AlgoAffineRatio extends AlgoElement {

	private GeoPointND A, B, C; // input
	private GeoNumeric M; // output

	public AlgoAffineRatio(Construction cons, String label, GeoPointND A,
			GeoPointND B, GeoPointND C) {
		super(cons);
		this.A = A;
		this.B = B;
		this.C = C;
		// create new GeoNumeric Object to return the result
		M = new GeoNumeric(cons);
		setInputOutput();
		compute();
		M.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.AffineRatio;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = A.toGeoElement();
		input[1] = B.toGeoElement();
		input[2] = C.toGeoElement();

		setOnlyOutput(M);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return M;
	}

	@Override
	public final void compute() {
		// Check if the points are aligned
		if (GeoPoint.collinearND(A, B, C)) {
			if (B.isEqualPointND(C)) {
				M.setValue(1.0); // changed, was undefined
			} else {
				M.setValue(GeoPoint.affineRatio(A, B, C));
			}
		} else {
			M.setUndefined();
		}
	}

	
}