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

package org.geogebra.common.kernel.arithmetic;

import java.util.List;

import org.geogebra.common.plugin.Operation;

/**
 * Checks and counts the given operator in the expression.
 */
public class OperationCountChecker implements Inspecting {
	private List<Operation> operations;
	private int count = 0;

	/**
	 * @param ops to count.
	 */
	public OperationCountChecker(Operation... ops) {
		this.operations = List.of(ops);
		this.count = 0;
	}

	@Override
	public boolean check(ExpressionValue v) {
		if (operations.contains(v.wrap().getOperation())) {
			count++;
		}

		return false;
	}

	/**
	 *
	 * @return the actual count of operator
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Reset counter
	 */
	public void reset() {
		count = 0;
	}
}
