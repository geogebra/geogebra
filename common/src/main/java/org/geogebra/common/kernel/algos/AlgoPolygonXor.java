package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoPolygon;

/**
 * @author thilina
 *
 */
public class AlgoPolygonXor extends AlgoPolygonOperation {

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels for the output
	 * @param inPoly0
	 *            first input polygon
	 * @param inPoly1
	 *            second input polygon
	 */
	public AlgoPolygonXor(Construction cons, String[] labels,
			GeoPolygon inPoly0, GeoPolygon inPoly1) {

		super(cons, labels, inPoly0, inPoly1, PolyOperation.XOR);
	}

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels for the output
	 * @param inPoly0
	 *            first input polygon
	 * @param inPoly1
	 *            second input polygon
	 * @param outputSizes
	 *            output sizes of the results of the operation. Consists of
	 *            polygon size, point size, and segment size
	 */
	public AlgoPolygonXor(Construction cons, String[] labels,
			GeoPolygon inPoly0, GeoPolygon inPoly1, int[] outputSizes) {

		super(cons, labels, inPoly0, inPoly1, PolyOperation.XOR);
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Xor;
	}

}
