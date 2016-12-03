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
 *          This class calculate cross ratio of 4 points like the division of 2
 *          affine ratio's: CrossRatio(A,B,C,D) = affineRatio(B, C, D) /
 *          affineRatio(A, C, D)
 */

public class AlgoCrossRatio extends AlgoElement {

	private GeoPointND A, B, C, D; // input
	private GeoNumeric M; // output

	public AlgoCrossRatio(Construction cons, String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoPointND D) {
		super(cons);
		this.A = A;
		this.B = B;
		this.C = C;
		this.D = D;
		M = new GeoNumeric(cons);
		setInputOutput();
		compute();
		M.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.CrossRatio;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[4];
		input[0] = A.toGeoElement();
		input[1] = B.toGeoElement();
		input[2] = C.toGeoElement();
		input[3] = D.toGeoElement();

		super.setOnlyOutput(M);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return M;
	}

	@Override
	public final void compute() {
		// Check if the points are aligned
		if (!(A.isEqualPointND(D)) && !(B.isEqualPointND(C))
				&& GeoPoint.collinearND(B, C, D)
				&& GeoPoint.collinearND(A, C, D)) {
			M.setValue(GeoPoint.affineRatio(B, C, D)
					/ GeoPoint.affineRatio(A, C, D));
		} else {
			M.setUndefined();
		}
	}

	
}