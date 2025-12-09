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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Product of square matrices {A,B,C} returns A*B*C
 * 
 * @author Michael Borcherds
 */

public class AlgoProductMatrices extends AlgoElement {

	private GeoList geoList; // input
	private GeoList result; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param geoList
	 *            list of matrices
	 */
	public AlgoProductMatrices(Construction cons, String label,
			GeoList geoList) {
		super(cons);
		this.geoList = geoList;

		// make sure output is same type as input (GeoVector, GeoPoint,
		// GeoPoint3D)
		result = new GeoList(cons);

		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Product;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geoList;

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting matrix
	 */
	public GeoElement getResult() {
		return result;
	}

	@Override
	public final void compute() {

		int size = geoList.size();

		if (!geoList.isDefined() || size == 0 || !geoList.get(0).isMatrix()) {
			result.setUndefined();
			return;
		}

		GeoList matrix = (GeoList) geoList.get(0);

		if (size == 1) {
			result.set(matrix);
			return;
		}

		double[][] working = new double[matrix.size()][matrix.size()];
		double[][] working2 = new double[matrix.size()][matrix.size()];

		// fill initial matrix
		for (int r = 0; r < matrix.size(); r++) {
			for (int c = 0; c < matrix.size(); c++) {
				working2[r][c] = matrix.get(r, c).evaluateDouble();
				// Log.debug(working2[r][c]);
			}
		}

		for (int i = 1; i < size; i++) {
			GeoElement p = geoList.get(i);

			if (!p.isMatrix()) {
				result.setUndefined();
				return;
			}

			matrix = (GeoList) p;

			// check matrix is square
			if (((GeoList) matrix.get(0)).size() != matrix.size()) {
				result.setUndefined();
				return;
			}

			// fill working matrix
			for (int r = 0; r < matrix.size(); r++) {
				for (int c = 0; c < matrix.size(); c++) {
					working[r][c] = working2[r][c];
					// Log.debug(working2[r][c]);
				}
			}

			// do the multiplication for one matrix
			for (int r = 0; r < matrix.size(); r++) {
				for (int c = 0; c < matrix.size(); c++) {

					double count = 0;
					// Log.debug(" ");

					for (int n = 0; n < matrix.size(); n++) {
						count += working[r][n]
								* matrix.get(n, c).evaluateDouble();
						// Log.debug(working[r][n] +" * "+ matrix.get(n,
						// c).evaluateDouble());
					}

					working2[r][c] = count;
				}
			}
		}

		// put result back in a GeoList
		GeoList ret = new GeoList(cons);
		for (int r = 0; r < matrix.size(); r++) {
			GeoList row = new GeoList(cons);
			for (int c = 0; c < matrix.size(); c++) {
				row.add(new GeoNumeric(cons, working2[r][c]));
			}
			ret.add(row);
		}

		result.set(ret);
	}

}
