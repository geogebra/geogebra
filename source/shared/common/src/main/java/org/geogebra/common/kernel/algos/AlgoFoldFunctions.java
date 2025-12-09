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
import org.geogebra.common.plugin.Operation;

/**
 * Sum of functions, may take whole list or just several first elements
 */
public class AlgoFoldFunctions extends AlgoElement {

	private GeoList geoList; // input
	private GeoNumeric truncate; // input
	private GeoElement resultFun;
	private Operation op;
	private FoldComputer foldComputer;

	/**
	 * Creates labeled function sum algo for truncated list (or whole list if
	 * truncate == null)
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param geoList
	 *            list
	 * @param truncate
	 *            number of elements to take
	 * @param op
	 *            operation
	 * @param foldComputer
	 *            fold helper
	 */
	public AlgoFoldFunctions(Construction cons, String label, GeoList geoList,
			GeoNumeric truncate, Operation op, FoldComputer foldComputer) {
		super(cons);
		this.geoList = geoList;
		this.truncate = truncate;
		this.op = op;
		this.foldComputer = foldComputer;
		resultFun = foldComputer.getTemplate(cons, geoList.getElementType());

		setInputOutput();
		compute();
		resultFun.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		if (truncate == null) {
			input = new GeoElement[1];
			input[0] = geoList;
		} else {
			input = new GeoElement[2];
			input[0] = geoList;
			input[1] = truncate;
		}

		setOnlyOutput(resultFun);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns result
	 * 
	 * @return sum of functions
	 */
	public GeoElement getResult() {
		return resultFun;
	}

	@Override
	public final void compute() {
		// Sum[{x^2,x^3}]
		int n = truncate == null ? geoList.size() : (int) truncate.getDouble();

		if (n <= 0 || n > geoList.size()
				|| !foldComputer.check(geoList.get(0))) {
			resultFun.setUndefined();
			return;
		}

		foldComputer.setFrom(geoList.get(0), kernel);

		for (int i = 1; i < n; i++) {

			if (!foldComputer.check(geoList.get(i))) {
				resultFun.setUndefined();
				return;
			}
			this.foldComputer.add(geoList.get(i), op);
		}
		foldComputer.finish();
	}

	@Override
	public Commands getClassName() {
		return op == Operation.PLUS ? Commands.Sum : Commands.Product;
	}

}
