package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;

public class AlgoDimension extends AlgoElement {

	private GeoList matrixDimension;
	private GeoNumeric firstDimension, secondDimension;
	private boolean matrix;
	private GeoList list;
	private GeoElement point;

	public AlgoDimension(Construction cons, String label, GeoList geoList) {
		super(cons);
		list = geoList;

		firstDimension = new GeoNumeric(cons);
		matrix = list.isMatrix();
		if (matrix) {
			matrixDimension = new GeoList(cons);
			secondDimension = new GeoNumeric(cons);
			matrixDimension.add(firstDimension);
			matrixDimension.add(secondDimension);
		}

		setInputOutput();
		compute();
		getResult().setLabel(label);

	}

	public AlgoDimension(Construction cons, String label, GeoElement geoList) {
		super(cons);
		point = geoList;

		firstDimension = new GeoNumeric(cons);
		matrix = false;

		setInputOutput();
		compute();
		getResult().setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = point == null ? list : point.toGeoElement();

		if (matrix) {
			setOnlyOutput(matrixDimension);
		} else {
			setOnlyOutput(firstDimension);
		}
		setDependencies();
	}

	@Override
	public void compute() {
		if (point != null) {
			if (!point.isDefined()) {
				firstDimension.setUndefined();
				return;
			}
			firstDimension.setValue(point instanceof GeoPoint
					|| point instanceof GeoVector ? 2 : 3);
			return;
		}

		if (!list.isDefined()) {
			getResult().setUndefined();
			return;
		}

		int size = list.size();
		firstDimension.setValue(size);
		if (matrix) {
			matrixDimension.setDefined(true);
			if (!list.get(0).isGeoList()) {
				matrixDimension.setUndefined();
				return;
			}
			int n = ((GeoList) list.get(0)).size();
			for (int i = 0; i < size; i++) {
				if (!list.get(i).isGeoList()
						|| ((GeoList) list.get(i)).size() != n) {
					matrixDimension.setUndefined();
					return;
				}
			}
			secondDimension.setValue(n);
		}

	}

	public GeoElement getResult() {
		return matrix ? matrixDimension : firstDimension;
	}

	@Override
	public Commands getClassName() {
		return Commands.Dimension;
	}

	

}
