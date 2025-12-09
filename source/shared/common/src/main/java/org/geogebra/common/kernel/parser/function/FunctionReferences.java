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

package org.geogebra.common.kernel.parser.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.arithmetic.filter.OperationFilter;
import org.geogebra.common.plugin.Operation;

class FunctionReferences {

	private static final int MAX_ARGS = 4;

	private final List<Map<String, Operation>> functionMap = new ArrayList<>();
	private final Set<String> reservedFunctions = new HashSet<>();
	private final List<OperationSyntax> syntaxes = new ArrayList<>();

	FunctionReferences() {
		initFunctionMap();
	}

	private void initFunctionMap() {
		for (int i = 0; i <= MAX_ARGS; i++) {
			functionMap.add(new HashMap<String, Operation>());
		}
	}

	void put(int size, String name, Operation op, String arg) {
		reservedFunctions.add(name);
		if (arg != null) {
			syntaxes.add(new OperationSyntax(op, name + arg));
		}
		if (size <= MAX_ARGS && size >= 0) {
			functionMap.get(size).put(name, op);
		}
	}

	void putReserved(String name) {
		reservedFunctions.add(name);
	}

	Operation get(String name, int size) {
		if (size > MAX_ARGS || size < 0) {
			return null;
		}
		return functionMap.get(size).get(name);
	}

	boolean isReserved(String s) {
		return reservedFunctions.contains(s);
	}

	void getCompletions(String prefix, Set<String> completions, Set<Operation> ops,
			@CheckForNull OperationFilter operationFilter) {
		for (OperationSyntax operationSyntax : syntaxes) {
			if (operationFilter != null && !operationFilter.isAllowed(operationSyntax.operation)) {
				continue;
			}
			if (operationSyntax.syntax.startsWith(prefix)) {
				completions.add(operationSyntax.syntax);
				ops.add(operationSyntax.operation);
			}
		}
	}

	private static class OperationSyntax {
		final Operation operation;
		final String syntax;

        private OperationSyntax(Operation operation, String syntax) {
            this.operation = operation;
            this.syntax = syntax;
        }
    }
}
