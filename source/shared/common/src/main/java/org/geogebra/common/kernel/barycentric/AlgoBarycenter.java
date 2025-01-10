package org.geogebra.common.kernel.barycentric;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author Darko Drakulic
 * @version 17-10-2011
 * 
 *          This class make point with given weights respevt to given polygon.
 * 
 */

public class AlgoBarycenter extends AlgoElement {

	private GeoList poly; // input
	private GeoList list; // input
	private GeoPointND point; // output

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param A
	 *            list of points
	 * @param B
	 *            list of weights
	 */
	public AlgoBarycenter(Construction cons, String label, GeoList A,
			GeoList B) {
		super(cons);
		this.poly = A;
		this.list = B;
		int dim = 2;
		for (int i = 0; i < A.size(); i++) {
			if (A.get(i).isGeoElement3D()) {
				dim = 3;
				break;
			}
		}
		point = kernel.getGeoFactory().newPoint(dim, cons);
		setInputOutput();
		compute();
		point.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Barycenter;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = poly;
		input[1] = list;

		setOnlyOutput(point);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the resulting point
	 * 
	 * @return the resulting point
	 */
	public GeoPointND getResult() {
		return point;
	}

	@Override
	public final void compute() {

		int size = list.size();
		if (!list.isDefined() || size == 0) {
			point.setUndefined();
			return;
		}
		if (list.size() != poly.size()) {
			point.setUndefined();
			return;
		}
		if (!list.getGeoElementForPropertiesDialog().isGeoNumeric()
				|| !poly.getGeoElementForPropertiesDialog().isGeoPoint()) {
			point.setUndefined();
			return;
		}

		int numberOfVertices = poly.size();
		double sum = list.get(0).evaluateDouble();
		Coords sumCoords = ((GeoPointND) poly.get(0)).getInhomCoordsInD3()
				.copy().mulInside(sum);
		for (int i = 1; i < numberOfVertices; i++) {
			double w = list.get(i).evaluateDouble();
			sumCoords.addInsideMul(
					((GeoPointND) poly.get(i)).getInhomCoordsInD3(), w);
			sum += w;

		}

		point.setCoords(sumCoords.mulInside(1 / sum), false);
	}

}