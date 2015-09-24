package org.geogebra.common.kernel.algos;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.SingularValueDecomposition;
import org.apache.commons.math.linear.SingularValueDecompositionImpl;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.GeoClass;

/**
 * @author csilla
 *
 */
public class AlgoSVD extends AlgoElement {

	private GeoList listOfLines; // input
	private int rows = 0; // rows in M
	private int columns = 0; // columns in M
	private RealMatrix M = null;
	private RealMatrix U, S, V; // decomposition M=USV*
	private GeoList listOfMatrices; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param listOfLines
	 *            input matrix
	 */
	public AlgoSVD(Construction cons, String label, GeoList listOfLines) {
		super(cons);
		this.listOfLines = listOfLines;
		listOfMatrices = new GeoList(cons);
		setInputOutput();
		compute();
		listOfMatrices.setLabel(label);
	}

	@Override
	public final Commands getClassName() {
		return Commands.SVD;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = listOfLines;

		super.setOnlyOutput(listOfMatrices);
		setDependencies();

	}

	/**
	 * @return list of matrices U, S, V
	 */
	public GeoList getResult() {
		return listOfMatrices;
	}

	@Override
	public void compute() {
		// get number of rows and columns
		if (listOfLines.isDefined() && listOfLines.getListDepth() == 2) {
			rows = listOfLines.size();
			if (rows > 0 && listOfLines.get(0) instanceof GeoList) {
				columns = ((GeoList) listOfLines.get(0)).size();
			} else {
				listOfMatrices.setUndefined();
				return;
			}
		} else {
			listOfMatrices.setUndefined();
			return;
		}

		if (!listOfLines.getElementType().equals(GeoClass.LIST)) {
			listOfMatrices.setUndefined();
			return;
		}

		try {
			// make matrix from input list
			if (!makeMatrixes()) {
				listOfMatrices.setUndefined();
				return;
			}

			// get decomposition
			SingularValueDecomposition svd = new SingularValueDecompositionImpl(
					M);

			U = svd.getU();
			S = svd.getS();
			V = svd.getV();

			// make list from elements of decomposition
			makeListOfMatrices();

		} catch (Throwable t) {
			listOfMatrices.setUndefined();
			t.printStackTrace();
		}


	}

	// convert list into matrix
	private final boolean makeMatrixes() {
		GeoElement geo = null;
		GeoList row = null;
		M = new Array2DRowRealMatrix(rows, columns);

		for (int r = 0; r < rows; r++) {
			geo = listOfLines.get(r);
			if (!geo.isGeoList()) {
				return false;
			}
			row = (GeoList) geo;
			for (int c = 0; c < columns; c++) {
				M.setEntry(r, c, row.get(c).evaluateDouble());
			}

		}

		return true;

	}

	private final void makeListOfMatrices() {

		GeoList matrixUlist = matrix2list(U);
		GeoList matrixSlist = matrix2list(S);
		GeoList matrixVlist = matrix2list(V);
		listOfMatrices.clear();
		listOfMatrices.add(matrixUlist);
		listOfMatrices.add(matrixSlist);
		listOfMatrices.add(matrixVlist);
	}

	// convert matrix into list
	private final GeoList matrix2list(RealMatrix matrix) {
		double[][] data = matrix.getData();
		GeoList list = new GeoList(cons);
		for (int i = 0; i < data.length; i++) {
			GeoList curRow = new GeoList(cons);
			for (int j = 0; j < data[i].length; j++) {
				curRow.add(new GeoNumeric(cons, data[i][j]));
			}
			list.add(curRow);
		}
		return list;
	}

}
