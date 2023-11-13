/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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
