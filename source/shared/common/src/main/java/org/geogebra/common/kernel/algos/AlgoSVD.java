/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.algos;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.debug.Log;

/**
 * @author csilla
 *
 */
public class AlgoSVD extends AlgoElement {

	private GeoList listOfLines; // input
	private int rows = 0; // rows in M
	private int columns = 0; // columns in M
	private RealMatrix M = null;
	// decomposition M=USV*
	private RealMatrix U;
	private RealMatrix S;
	private RealMatrix V;
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
			if (!makeMatrices()) {
				listOfMatrices.setUndefined();
				return;
			}

			// get decomposition
			SingularValueDecomposition svd = new SingularValueDecomposition(M);

			U = svd.getU();
			S = svd.getS();
			V = svd.getV();

			// make list from elements of decomposition
			makeListOfMatrices();

		} catch (Throwable t) {
			listOfMatrices.setUndefined();
			Log.debug(t);
		}

	}

	// convert list into matrix
	private boolean makeMatrices() {
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
