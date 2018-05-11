package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.GgbMat;

/**
 * Computes rank of a matrix.
 */
public class AlgoMatrixRank extends AlgoElement {

	private GeoList inputList;
	private GeoNumeric rank;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param matrix
	 *            matrix
	 */
	public AlgoMatrixRank(Construction cons, String label, GeoList matrix) {
		super(cons);
		this.inputList = matrix;

		rank = new GeoNumeric(cons);

		setInputOutput();
		compute();
		rank.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(rank);
		input = new GeoElement[] { inputList };
		setDependencies();
	}

	@Override
	public void compute() {
		GgbMat matrix = new GgbMat(inputList);

		if (matrix.isUndefined()) {
			rank.setUndefined();
			return;
		}

		matrix.reducedRowEchelonFormImmediate();
		int rows = matrix.getRowDimension();
		int cols = matrix.getColumnDimension();
		for (int i = 0; i < rows; i++) {
			boolean onlyZeros = true;
			for (int j = 0; j < cols; j++) {
				if (!DoubleUtil.isZero(matrix.getEntry(i, j))) {
					onlyZeros = false;
					break;
				}
			}
			if (onlyZeros) {
				rank.setValue(i);
				return;
			}
		}
		rank.setValue(rows);

	}

	@Override
	public Commands getClassName() {
		return Commands.MatrixRank;
	}

	/**
	 * @return matrix rank
	 */
	public GeoNumeric getResult() {
		return rank;
	}

}
